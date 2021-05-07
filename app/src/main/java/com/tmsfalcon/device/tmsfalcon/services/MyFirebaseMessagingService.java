package com.tmsfalcon.device.tmsfalcon.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tmsfalcon.device.tmsfalcon.Login;
import com.tmsfalcon.device.tmsfalcon.Profile;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.TrailerDetail;
import com.tmsfalcon.device.tmsfalcon.TruckDetail;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.activities.CompanyDispatcherActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.activities.DocumentDetail;
import com.tmsfalcon.device.tmsfalcon.activities.ExpiredDocumentActivity;
import com.tmsfalcon.device.tmsfalcon.activities.PdfWebViewActivity;
import com.tmsfalcon.device.tmsfalcon.activities.RequestedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 5/30/2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.e("token: ",s);
        SessionManager.getInstance().saveDeviceToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.e(TAG, "From: " + remoteMessage.getFrom());
       /* Log.e(TAG, "Notification  Message Body: " + remoteMessage.getNotification().getBody());*/
        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Message data payload: " + remoteMessage.getData());

            /* String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");
            String type = remoteMessage.getData().get("type");
            String event_id = remoteMessage.getData().get("event_id");
            String resource_type = remoteMessage.getData().get("resource_type");
            String trip_id = remoteMessage.getData().get("trip_id");*/
            String count = remoteMessage.getData().get("count");
            //int resource_id = Integer.parseInt(remoteMessage.getData().get("resource_id"));
            String res_str = "";
            int resource_id = 0;
            res_str = remoteMessage.getData().get("resource_id");
            try{
                resource_id = Integer.parseInt(res_str);
            }
            catch(Exception e){

            }
            SessionManager.getInstance().storeNotificationCount(count);
            handleNotification(this,remoteMessage,resource_id);
            /*handleNotification(this,event_id,type,resource_type,title,message,trip_id,count,resource_id);*/

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void createNotification(String title,String message,Intent intent,String count) {
        NotificationManager notifManager = null;
        final int NOTIFY_ID = 0; // ID of notification
        String id = "notif_channel_id"; // default_channel_id
        String channel_title = "notif_channel"; // Default Channel
        PendingIntent pendingIntent = null;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(intent != null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, channel_title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(getApplicationContext(), id);
            
            builder.setContentTitle(title)
                    .setContentText(message)// required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        }
        else {
            builder = new NotificationCompat.Builder(getApplicationContext(), id);
            
            builder.setContentTitle(title)                            // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)   // required
                    .setContentText(message) // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(title)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    private void sendNotification(String title,String message,Intent intent,String count){
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification_view);
        remoteViews.setImageViewResource(R.id.image_icon, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.text_title, title);
        remoteViews.setTextViewText(R.id.text_message, message);

        // Create aappn explicit intent for an Activity in your
        PendingIntent pendingIntent =  null;
        if(intent != null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);


           /* NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setCustomContentView(remoteViews)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    // Set the intent that will fire when the user taps the notification
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).bigText(message))
                    //.setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    //.setStyle(new Notification.DecoratedCustomViewStyle())  //Requires Minimum Api level 24
                    .setAutoCancel(true);*/

        //Vibration
        notification.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //LED
        notification.setLights(Color.BLUE, 3000, 3000);

        //Ton
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.setSound(alarmSound);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE); // To create unique notification id
        manager.notify(m, notification.build());
    }

   /* private void handleNotification(final Context context, String event_id, final String type, final String resource_type, final String title,final String notification_message,final String trip_id_string,final String count,final int resource_id){*/
   private void handleNotification(final Context context, RemoteMessage remoteMessage,final int resource_id){
       String title = remoteMessage.getData().get("title");
       String notification_message = remoteMessage.getData().get("message");
       String type = remoteMessage.getData().get("type");
       String event_id = remoteMessage.getData().get("event_id");
       String resource_type = remoteMessage.getData().get("resource_type");
       String trip_id_string = remoteMessage.getData().get("trip_id");
       String count = remoteMessage.getData().get("count");
       int trip_int_id  = 0;
        Intent inner_intent = null;
        try{
            trip_int_id = Integer.parseInt(trip_id_string);
        }
        catch(NumberFormatException e){

        }
        if(type != "" || type != null || type != "null"){
            if(type.trim().equals("NewUploadedDocumentEvent")){
                inner_intent = new Intent(context, Profile.class);
                inner_intent.putExtra("open_fragment",1);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("DriverAssignedEvent") ){
                inner_intent = new Intent(context, CompanyDispatcherActivity.class);
                inner_intent.putExtra("open_fragment",1);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
                                   /* else if(type.trim().equals("ChangePasswordEvent")){
                                        intent = new Intent(this,Dashboard.class);
                                    }*/
            else if(type.trim().equals("TripDriverAssignedEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("TripAssignedTruckEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("TripAssignedTrailerEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("AccountDisabledEvent")){
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(context, "Your Account has been Disabled.Please contact Administrator.", Toast.LENGTH_LONG).show();
                    }
                });

                SessionManager.getInstance().logoutUserForNotifications();
                inner_intent = new Intent(context,Login.class);
            }
            else if(type.trim().equals("AssignResoucesDriverEvent")){
                if(resource_type.trim().equalsIgnoreCase("Trailer")){
                    inner_intent = new Intent(context,TrailerDetail.class);
                    inner_intent.putExtra("trailer_id",resource_id);

                }
                else{
                    inner_intent = new Intent(context,TruckDetail.class);
                    inner_intent.putExtra("truck_id",resource_id);
                }

                inner_intent.setAction(Long.toString(System.currentTimeMillis()));

            }
            else if(type.trim().equals("RequestedDocumentEvent")){
                String doc_id = remoteMessage.getData().get("encrypted_doc_id");
                String doc_name = remoteMessage.getData().get("docName");
                String doc_type = remoteMessage.getData().get("doc_type");
                String doc_title = remoteMessage.getData().get("doc_title");
                String due_date = remoteMessage.getData().get("due_date");
                String comment = remoteMessage.getData().get("comment");
                String status = remoteMessage.getData().get("status");
                String key = remoteMessage.getData().get("key");
                String doc_belongs_to = remoteMessage.getData().get("document_belongs_to");

//                inner_intent = new Intent(context, CaptureDocument.class);
                inner_intent = new Intent(context, RequestedDocumentsActivity.class);
                inner_intent.putExtra("document_request_id",doc_id);
                inner_intent.putExtra("document_type",doc_type);
                inner_intent.putExtra("document_name",doc_name);
                inner_intent.putExtra("load_number",doc_title);
                inner_intent.putExtra("due_date",due_date);
                inner_intent.putExtra("comment",comment);
                inner_intent.putExtra("status",status);
                inner_intent.putExtra("key",key);
                inner_intent.putExtra("is_expired","0");
                inner_intent.putExtra("document_belongs_to",doc_belongs_to);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("CreateSettlementEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("TripDispatchedEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("TripCompletedEvent")){
                inner_intent = new Intent(context, TripDetail.class);
                inner_intent.putExtra("trip_id",trip_int_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("CompletedMaintenanceEvent")){
                if(resource_type.trim().equalsIgnoreCase("Trailer")){
                    inner_intent = new Intent(context,TrailerDetail.class);
                    inner_intent.putExtra("trailer_id",resource_id);
                }
                else{
                    inner_intent = new Intent(context,TruckDetail.class);
                    inner_intent.putExtra("truck_id",resource_id);
                }
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("SuspendedMaintenanceEvent")){
                if(resource_type.trim().equalsIgnoreCase("Trailer")){
                    inner_intent = new Intent(context,TrailerDetail.class);
                    inner_intent.putExtra("trailer_id",resource_id);
                }
                else{
                    inner_intent = new Intent(context,TruckDetail.class);
                    inner_intent.putExtra("truck_id",resource_id);
                }
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("AddMaintenanceEvent")){
                if(resource_type.trim().equalsIgnoreCase("Trailer")){
                    inner_intent = new Intent(context,TrailerDetail.class);
                    inner_intent.putExtra("trailer_id",resource_id);
                }
                else{
                    inner_intent = new Intent(context,TruckDetail.class);
                    inner_intent.putExtra("truck_id",resource_id);
                }
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("DocumentRequestRejectedEvent")){
                String doc_id = remoteMessage.getData().get("docId");
                Log.e("docid","docid"+doc_id);
                inner_intent = new Intent(context,DocumentDetail.class);
                inner_intent.putExtra("module","DocumentRequestRejectedEvent");
                inner_intent.putExtra("doc_id",doc_id);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
                /*inner_intent = new Intent(context,DocumentRequestActivity.class);
                inner_intent.putExtra("open_fragment",0);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));*/
            }
            else if(type.trim().equals("ExpiresDocumentsEvent")){
                if(resource_type.trim().equalsIgnoreCase("Trailer")){
                    inner_intent = new Intent(context,TrailerDetail.class);
                    inner_intent.putExtra("trailer_id",resource_id);
                }
                else if(resource_type.trim().equalsIgnoreCase("Truck")){
                    inner_intent = new Intent(context,TruckDetail.class);
                    inner_intent.putExtra("truck_id",resource_id);
                }
                else if(resource_type.trim().equalsIgnoreCase("driver")){
                    inner_intent = new Intent(context,Profile.class);
                    inner_intent.putExtra("open_fragment",1);
                }
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));

            }
            else if(type.trim().equals("ExpiredDocumentsEvent")){
                inner_intent = new Intent(context,ExpiredDocumentActivity.class);
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));
            }
            else if(type.trim().equals("SuggestedCalllogEvent")){
                Log.e("public_link n"," is "+remoteMessage.getData().get("public_link"));
                inner_intent = new Intent(context, CallLogDetail.class);
                inner_intent.putExtra("public_link",remoteMessage.getData().get("public_link"));
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));

            }
            else if(type.trim().equals("BatchSettlementEvent")){
                inner_intent = new Intent(context, PdfWebViewActivity.class);
                inner_intent.putExtra("pdf_url",remoteMessage.getData().get("public_link"));
                inner_intent.setAction(Long.toString(System.currentTimeMillis()));

            }
            else {
                inner_intent = new Intent(context, DashboardActivity.class);
            }

        }
        else{
            inner_intent = new Intent(context, DashboardActivity.class);
        }
        createNotification(title,notification_message,inner_intent,count);
        /*if(!type.trim().equals("AccountDisabledEvent")) {
            updateSeenAt(context, event_id);
        }*/
    }

    private void updateSeenAt(final Context context, String event_id) {

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
                                Toast.makeText(context,message,Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(MyFirebaseMessagingService.this,error);

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

}
