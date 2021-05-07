package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.SuggestedLoadsModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android on 8/23/2017.
 */
public class LoadBoardListAdapter extends BaseAdapter {

    private ArrayList<SuggestedLoadsModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    boolean is_action_button_disabled = false; //to control the clicks on action buttons
    SessionManager sessionManager;
    ListView listView;

    public LoadBoardListAdapter(Activity activity,ArrayList<SuggestedLoadsModel> data,ListView list) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
        this.listView = list;
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
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LoadBoardListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final SuggestedLoadsModel model = mData.get(position);
        if (convertView == null) {
            holder = new LoadBoardListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_load_board, null);

            holder.call_log_id_textview = convertView.findViewById(R.id.call_log_id);
            holder.origin_textview = convertView.findViewById(R.id.origin);
            holder.destination_textview = convertView.findViewById(R.id.destination);
            holder.price_textview = convertView.findViewById(R.id.price);
            holder.ppm_textview = convertView.findViewById(R.id.price_per_mile);
            holder.accept_textview = convertView.findViewById(R.id.accept);
            holder.reject_textview = convertView.findViewById(R.id.reject);
            holder.accept_linear_layout = convertView.findViewById(R.id.accept_linear_layout);
            holder.reject_linear_layout = convertView.findViewById(R.id.reject_linear_layout);
            holder.detail_layout = convertView.findViewById(R.id.detail_layout);
            holder.miles_textview = convertView.findViewById(R.id.miles);
            holder.bottom_combined_layout = convertView.findViewById(R.id.bottom_combined_layout);
            holder.bottom_response_layout = convertView.findViewById(R.id.bottom_status_layout);
            holder.response_textview = convertView.findViewById(R.id.response);

            convertView.setTag(holder);
        } else {

            holder = (LoadBoardListAdapter.ViewHolder)convertView.getTag();

        }
        //convertView.setId(Integer.parseInt(model.getId()));

        holder.call_log_id_textview.setText("#"+model.getCall_log_id());
        holder.origin_textview.setText(model.getOrigin());
        holder.destination_textview.setText(model.getDestination());
        holder.price_textview.setText("$"+model.getPrice());
        holder.ppm_textview.setText("$"+model.getPm());
        holder.miles_textview.setText(model.getMiles());

        if(model.isIs_action_visible()){
            is_action_button_disabled = false;
            holder.bottom_combined_layout.setVisibility(View.VISIBLE);
            holder.bottom_response_layout.setVisibility(View.GONE);
        }
        else{
            holder.bottom_combined_layout.setVisibility(View.GONE);
            holder.bottom_response_layout.setVisibility(View.VISIBLE);
            is_action_button_disabled = true;
            if(model.getMy_response().equals("interested")){
                holder.response_textview.setText("Marked as Interested");
                holder.response_textview.setTextColor(activity.getResources().getColor(R.color.green_dark));
            }
            else if(model.getMy_response().equals("rejected")){
                holder.response_textview.setText("Marked as Rejected");
                holder.response_textview.setTextColor(activity.getResources().getColor(R.color.red_dark));
            }
            else if(model.isIs_awarded()){
                holder.response_textview.setText("Offer Awarded");
                holder.response_textview.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            }
        }
        holder.detail_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, CallLogDetail.class);
                Log.e("plublic_link",": "+model.getPublic_link());
                i.putExtra("public_link",model.getPublic_link());
                activity.startActivity(i);
            }
        });

        if(!is_action_button_disabled){
            final ViewHolder finalHolder = holder;
            holder.accept_linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoadResponse("interested",model.getCall_log_id(), finalHolder);
                }
            });

            final ViewHolder finalHolder1 = holder;
            holder.reject_linear_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setLoadResponse("rejected",model.getCall_log_id(), finalHolder1);
                }
            });
        }


        return convertView;
    }
    public static class ViewHolder {
        TextView call_log_id_textview,origin_textview,destination_textview,price_textview,ppm_textview,accept_textview,
                reject_textview,miles_textview,response_textview;
        LinearLayout accept_linear_layout,reject_linear_layout,detail_layout,bottom_combined_layout,bottom_response_layout;

    }

    private void sendBroadcastMessage(String message) {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("action_load_board_adapter");
        // You can also include some extra data.
        intent.putExtra("action", message);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    public void setLoadResponse(final String response_status, final String call_log_id, final ViewHolder viewHolder){
        String tag_json_obj = "set_load_offer_response_tag";
        String url = UrlController.SET_LOAD_OFFER_RESPONSE;
        sendBroadcastMessage("show_progress_bar");
        String current_lat = sessionManager.getKeyCurrentLatitude();
        String current_long = sessionManager.getKeyCurrentLongitude();
        Log.e("loc","lat "+current_lat+" long "+current_long);
        Map<String, String> params = new HashMap<>();
        params.put("response", response_status);
        params.put("calllog_id", call_log_id);
        params.put("lat", current_lat);
        params.put("lng", current_long);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response", String.valueOf(response));
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                viewHolder.bottom_combined_layout.setVisibility(View.GONE);
                                viewHolder.bottom_response_layout.setVisibility(View.VISIBLE);
                                is_action_button_disabled = true;
                                if(response_status.equals("interested")){
                                    viewHolder.response_textview.setText("Marked as Interested");
                                    viewHolder.response_textview.setTextColor(activity.getResources().getColor(R.color.green_dark));
                                }
                                else if(response_status.equals("rejected")){
                                    viewHolder.response_textview.setText("Marked as Rejected");
                                    viewHolder.response_textview.setTextColor(activity.getResources().getColor(R.color.red_dark));
                                }
                                Toast.makeText(activity,"Call-log "+call_log_id+" Marked as "+response_status,Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(activity,"Something went wrong.Try Again Later.",Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        sendBroadcastMessage("hide_progress_bar");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(activity,error);
                sendBroadcastMessage("hide_progress_bar");
            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", sessionManager.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


}
