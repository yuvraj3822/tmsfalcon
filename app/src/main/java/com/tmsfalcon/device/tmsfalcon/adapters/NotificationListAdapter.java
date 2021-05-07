package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.Login;
import com.tmsfalcon.device.tmsfalcon.Profile;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.TrailerDetail;
import com.tmsfalcon.device.tmsfalcon.TruckDetail;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.activities.CaptureDocument;
import com.tmsfalcon.device.tmsfalcon.activities.CompanyDispatcherActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DocumentDetail;
import com.tmsfalcon.device.tmsfalcon.activities.ExpiredDocumentActivity;
import com.tmsfalcon.device.tmsfalcon.activities.PdfWebViewActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android on 7/18/2017.
 */

public class NotificationListAdapter extends BaseAdapter {

    private ArrayList<NotificationModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    private static final String NOTIFICATION_COUNT = "notification_count";

    public NotificationListAdapter(Activity activity, ArrayList<NotificationModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NotificationListAdapter.ViewHolder holder = null;

        final NotificationModel model = mData.get(position);

        if (convertView == null) {
            holder = new NotificationListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_notification, null);
            holder.title = convertView.findViewById(R.id.title);
            holder.time = convertView.findViewById(R.id.time);
            holder.seen_status_image = convertView.findViewById(R.id.seen_status_image);

            convertView.setTag(holder);
        } else {
            holder = (NotificationListAdapter.ViewHolder)convertView.getTag();
        }
        final String type = model.getNotification_event();

        holder.title.setText(model.getNotification_message());
        holder.time.setText(model.getNotification_time());
        convertView.setId(Integer.parseInt(model.getNotification_event_id()));
        if(model.getSeen_status().trim().equals("1")){
            holder.seen_status_image.setImageResource(R.drawable.check);
        }
        else{
            holder.seen_status_image.setImageResource(R.drawable.untick);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int resource_id = 0;
                try{
                    resource_id = Integer.parseInt(model.getResource_id());
                }
                catch(Exception e){

                }
                handleNotification(model,resource_id);
                //handleNotification(model.getNotification_event_id(),model.getNotification_event(),model.getNotification_resource_type(),model.getNotification_trip_id(),resource_id);
            }
        });
        return convertView;
    }
    /*private void handleNotification(String event_id, final String type, final String resource_type,final String trip_id_string,final int resource_id){*/
    private void handleNotification(NotificationModel model,final int resource_id){
        int trip_int_id  = 0;
        String type = model.getNotification_event();
        String event_id = model.getNotification_event_id();
        String resource_type = model.getNotification_resource_type();
        String trip_id_string = model.getNotification_trip_id();
        String public_link = model.getPublic_link(); //for viewing call log in a web view
        try{
            trip_int_id = Integer.parseInt(model.getNotification_trip_id());
            Log.e("trip id ",""+trip_int_id);
        }
        catch(NumberFormatException e){

        }
        String count = SessionManager.getInstance().getNotificationCount();
        int new_count = 0;
        if(count != null){
            if((Integer.parseInt(count)) > 0){
                new_count = Integer.parseInt(count) - 1;
            }

        }
        if(!type.trim().equals("AccountDisabledEvent")) {
            updateSeenAt(event_id);
        }

        SessionManager.getInstance().storeNotificationCount(""+new_count);
        if(type.trim().equals("NewUploadedDocumentEvent")){

            Intent intent = new Intent(activity, Profile.class);
            intent.putExtra("open_fragment",1);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("DriverAssignedEvent") ){
            Intent intent = new Intent(activity, CompanyDispatcherActivity.class);
            intent.putExtra("open_fragment",1);
            activity.startActivity(intent);
        }
        /*else if(type.trim().equals("ChangePasswordEvent")){
            Intent intent = new Intent(activity,Dashboard.class);
            activity.startActivity(intent);
        }*/
        else if(type.trim().equals("TripDriverAssignedEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("TripAssignedTruckEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("TripAssignedTrailerEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("AccountDisabledEvent")){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Your Account has been Disabled.Please contact Administrator.", Toast.LENGTH_LONG).show();
                }
            });

            SessionManager.getInstance().logoutUserForNotifications();
            Intent intent = new Intent(activity,Login.class);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("AssignResoucesDriverEvent")){

            if(resource_type.trim().equals("Trailer")){
                Intent intent = new Intent(activity,TrailerDetail.class);
                intent.putExtra("trailer_id",resource_id);
                activity.startActivity(intent);
            }
            else{
                Intent intent = new Intent(activity,TruckDetail.class);
                intent.putExtra("truck_id",resource_id);
                activity.startActivity(intent);
            }

        }
        else if(type.trim().equals("RequestedDocumentEvent")){

//            Intent intent = new Intent(activity, CaptureDocument.class);
            Intent intent = new Intent(activity, TestCameraScreen.class);

            Log.e("document_request_id"," "+model.getDoc_id());
            Log.e("document_type"," "+model.getDoc_type());
            Log.e("document_name"," "+model.getDoc_name());
            Log.e("load_number"," "+model.getDoc_title());
            Log.e("due_date"," "+model.getDoc_due_date());
            Log.e("comment"," "+model.getDoc_comment());
            Log.e("status"," "+model.getDoc_status());
            Log.e("key"," "+model.getDoc_key());
            intent.putExtra("document_request_id",model.getDoc_id());
            intent.putExtra("document_type",model.getDoc_type());
            intent.putExtra("document_name",model.getDoc_name());
            intent.putExtra("load_number",model.getDoc_title());
            intent.putExtra("due_date",model.getDoc_due_date());
            intent.putExtra("comment",model.getDoc_comment());
            intent.putExtra("status",model.getDoc_status());
            intent.putExtra("key",model.getDoc_key());
            intent.putExtra("is_expired","0");
            intent.putExtra("document_belongs_to",model.getDoc_belongs_to());

            activity.startActivity(intent);
        }
        else if(type.trim().equals("CreateSettlementEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("TripDispatchedEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("TripCompletedEvent")){
            Intent intent = new Intent(activity, TripDetail.class);
            intent.putExtra("trip_id",trip_int_id);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("CompletedMaintenanceEvent")){
            Log.e("in","CompletedMaintenanceEvent");
            Log.e("resource_id",""+resource_id);
            if(resource_type.trim().equalsIgnoreCase("Trailer")){
                Intent intent = new Intent(activity,TrailerDetail.class);
                intent.putExtra("trailer_id",resource_id);
                activity.startActivity(intent);
            }
            else{
                Intent intent = new Intent(activity,TruckDetail.class);
                intent.putExtra("truck_id",resource_id);
                activity.startActivity(intent);
            }
        }
        else if(type.trim().equals("SuspendedMaintenanceEvent")){
            Log.e("in","SuspendedMaintenanceEvent");
            Log.e("resource_id",""+resource_id);
            if(resource_type.trim().equalsIgnoreCase("Trailer")){
                Intent intent = new Intent(activity,TrailerDetail.class);
                intent.putExtra("trailer_id",resource_id);
                activity.startActivity(intent);
            }
            else{
                Intent intent = new Intent(activity,TruckDetail.class);
                intent.putExtra("truck_id",resource_id);
                activity.startActivity(intent);
            }
        }
        else if(type.trim().equals("AddMaintenanceEvent")){
            Log.e("in","AddMaintenanceEvent");
            Log.e("resource_id",""+resource_id);
            Log.e("resource_type",""+resource_type);
            if(resource_type.trim().equalsIgnoreCase("Trailer")){
                Intent intent = new Intent(activity,TrailerDetail.class);
                intent.putExtra("trailer_id",resource_id);
                activity.startActivity(intent);
            }
            else{
                Intent intent = new Intent(activity,TruckDetail.class);
                intent.putExtra("truck_id",resource_id);
                activity.startActivity(intent);
            }
        }
        else if(type.trim().equals("DocumentRequestRejectedEvent")){
            Intent intent = new Intent(activity, DocumentDetail.class);
            intent.putExtra("module","DocumentRequestRejectedEvent");
            intent.putExtra("doc_id",model.getDoc_id());
            /*Intent intent = new Intent(activity,DocumentRequestActivity.class);
            intent.putExtra("open_fragment",0);*/
            activity.startActivity(intent);
        }
        else if(type.trim().equals("ExpiredDocumentsEvent")){
            Intent intent = new Intent(activity,ExpiredDocumentActivity.class);
            activity.startActivity(intent);
        }
        else if(type.trim().equals("ExpiresDocumentsEvent")){
            Log.e("in","resuorce type => "+resource_type);
            if(resource_type.trim().equalsIgnoreCase("Trailer")){
                Log.e("in","trailer");
                Intent intent = new Intent(activity,TrailerDetail.class);
                intent.putExtra("trailer_id",resource_id);
                activity.startActivity(intent);
            }
            else if(resource_type.trim().equalsIgnoreCase("Truck")){
                Log.e("in","Truck");
                Intent intent = new Intent(activity,TruckDetail.class);
                intent.putExtra("truck_id",resource_id);
                activity.startActivity(intent);
            }
            else if(resource_type.trim().equalsIgnoreCase("driver")){
                Log.e("in","driver");
                Intent intent = new Intent(activity,Profile.class);
                intent.putExtra("open_fragment",1);
                activity.startActivity(intent);
            }
        }
        else if(type.trim().equals("SuggestedCalllogEvent")){
            Intent i = new Intent(activity, CallLogDetail.class);
            i.putExtra("public_link",public_link);
            activity.startActivity(i);
        }
        else if(type.trim().equals("BatchSettlementEvent")){
            Intent i = new Intent(activity, PdfWebViewActivity.class);
            i.putExtra("pdf_url",public_link);
            activity.startActivity(i);
        }
        else {
            Intent intent = new Intent(activity, DashboardActivity.class);
            activity.startActivity(intent);
        }

    }


    private void updateSeenAt(String event_id) {
        String tag_json_obj = "update_seen_at";
        String url = UrlController.UPDATE_SEEN_AT;
        //String resource_type = resource_event_type;
        Map<String, String> params = new HashMap<>();
        params.put("event_id", event_id);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray data_json;
                        String message = "";

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            message = response.getString("messages");
                            if(status){

                            }
                            else{
                                Toast.makeText(activity,message,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Boolean error_status = null;
                JSONArray json_errors ;
                String errors = "";
                VolleyLog.e("Error Response ", "Error: " + error.getMessage());
                NetworkResponse errorRes = error.networkResponse;
                String stringData = "";

                if(errorRes != null && errorRes.data != null){
                    try {
                        stringData = new String(errorRes.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("exception ", String.valueOf(e));
                    }
                }
                Log.e("Error",stringData);
                try {
                    JSONObject obj = new JSONObject(stringData);
                    if(obj != null && obj.length() > 0){
                        error_status = obj.getBoolean("status");
                        json_errors = obj.getJSONArray("messages");

                        for(int i = 0 ; i < json_errors.length(); i++){
                            errors += json_errors.get(i)+"\n";
                        }
                        if(!error_status){
                            if(json_errors.get(0).equals("Expired token") ){
                               SessionManager.getInstance().logoutUser();
                               Toast.makeText(activity,activity.getResources().getString(R.string.token_error),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(activity,errors,Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    else{
                        Toast.makeText(activity,activity.getResources().getString(R.string.null_response),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("exception ", String.valueOf(e));
                    Log.e("My App", activity.getResources().getString(R.string.malformed_json)+ stringData );
                    Toast.makeText(activity,activity.getResources().getString(R.string.malformed_json)+ stringData,Toast.LENGTH_LONG).show();
                }

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", SessionManager.getInstance().get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public static class ViewHolder {
        TextView title,time;
        ImageView seen_status_image;
    }

}
