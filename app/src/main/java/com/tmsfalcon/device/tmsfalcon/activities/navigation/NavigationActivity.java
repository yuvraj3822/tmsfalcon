package com.tmsfalcon.device.tmsfalcon.activities.navigation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.DayOffRequestActivity;
import com.tmsfalcon.device.tmsfalcon.activities.LoanActivity;
import com.tmsfalcon.device.tmsfalcon.activities.NotificationActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.services.BackgroundDetectedActivitiesService;
import com.tmsfalcon.device.tmsfalcon.services.LocationService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavigationActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    PermissionManager permissionsManager = new PermissionManager();
    static final int FINE_LOCATION = 0x3;
    static final int COARSE_LOCATION = 0x4;
    static final int WAKE_LOCK = 0x5;
    String latitude,longitude,speed,speedInMiles,type,confidence,userActivity;
    Double speedInMilesDouble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_navigation);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_navigation, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        initIds();

    }

    @Override
    protected void onStop() {
        super.onStop();
        /*stopService(new Intent(NavigationActivity.this, LocationService.class));
        stopService(new Intent(NavigationActivity.this, BackgroundDetectedActivitiesService.class));*/
    }

    public void initIds(){
        networkValidator = new NetworkValidator(NavigationActivity.this);
        session = new SessionManager(NavigationActivity.this);
    }

    /*public void startLocationService(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startService(new Intent(NavigationActivity.this, LocationService.class));
            startService(new Intent(NavigationActivity.this, BackgroundDetectedActivitiesService.class));
        } else {
            startForegroundService(new Intent(NavigationActivity.this, LocationService.class));
            startForegroundService(new Intent(NavigationActivity.this, BackgroundDetectedActivitiesService.class));
        }
    }*/

   /* public void registerLocationReceiver(){
        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        // Log.e("intent action ",""+intent.getAction());

                        if(intent.getAction().equals(Utils.ACTION_LOCATION_BROADCAST)){
                            latitude = intent.getStringExtra(LocationService.EXTRA_LATITUDE);
                            longitude = intent.getStringExtra(LocationService.EXTRA_LONGITUDE);
                            speed = intent.getStringExtra(LocationService.EXTRA_SPEED);
                            speedInMilesDouble = Utils.convertMetreToMile(Float.parseFloat(speed));
                            speedInMiles = Double.toString(speedInMilesDouble);

                            type = intent.getStringExtra(LocationService.EXTRA_TYPE );
                            confidence = intent.getStringExtra(LocationService.EXTRA_CONFIDENCE);
                            userActivity = labelUserActivity(Integer.parseInt(type));
                        }

                        Log.e("Data ","Lat => "+latitude+" Long => "+longitude+" Speed => "+speedInMiles+" Type => "+type+" User Activity =>"+userActivity);
                        if (latitude != null && longitude != null) {
                            sendLocationApiRequest(latitude,longitude,speedInMiles,userActivity);
                        }
                    }
                }, new IntentFilter(Utils.ACTION_LOCATION_BROADCAST)
        );
    }*/

   /* private String labelUserActivity(int type) {
        String label = "";

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = "Driving";
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = "Driving";
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = "Driving";
                break;
            }
            case DetectedActivity.RUNNING: {
                label = "Driving";
                break;
            }
            case DetectedActivity.STILL: {
                label = "Stopped";
                break;
            }
            case DetectedActivity.TILTING: {
                label = "Stopped";
                break;
            }
            case DetectedActivity.WALKING: {
                label = "Driving";
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = "Stopped";
                break;
            }
            default:{
                label = "Stopped";
            }
        }
        return label;
    }

    public void sendLocationApiRequest(String latitude,String longitude,String speed,String userActivity){
        // Tag used to cancel the request
        String tag_json_obj = "send_location_tag";

        String url = UrlController.SEND_LOCATION;
        String device = Build.BRAND+"-"+Build.MODEL;
        Log.e("device",device);
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", latitude);
        params.put("long", longitude);
        params.put("speed", speed);
        params.put("status",userActivity);
        params.put("device",device);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request
                .Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray json_messages ;
                        String messages = "";

                        try {
                            status = response.getBoolean("status");
                            json_messages = response.getJSONArray("messages");

                            for(int i = 0 ; i < json_messages.length(); i++){
                                messages += json_messages.get(i)+"\n";
                            }

                            Toast.makeText(NavigationActivity.this,messages,Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        if(status){


                        }

                        Log.e("Response", response.toString());

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

                        for(int i =0 ; i < json_errors.length(); i++){
                            errors += json_errors.get(i)+"\n";
                        }
                        if(!error_status){
                            if(json_errors.get(0).equals("Expired token") ){
                                session.logoutUser();
                                Toast.makeText(NavigationActivity.this,getResources().getString(R.string.token_error),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(NavigationActivity.this,errors,Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    else{
                        Toast.makeText(NavigationActivity.this,getResources().getString(R.string.null_response),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("exception ", String.valueOf(e));
                    Log.e("My App", getResources().getString(R.string.malformed_json)+ stringData );
                    Toast.makeText(NavigationActivity.this,getResources().getString(R.string.malformed_json)+ stringData,Toast.LENGTH_LONG).show();
                }

            }
        }) {
            *//**
             * Passing some request headers
             * *//*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Token",session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case FINE_LOCATION:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();  // Start Location Service for Driver Tracking Purpose
                }
                else{
                    Toast.makeText(NavigationActivity.this,"You need Location Permission to Access User Location.",Toast.LENGTH_LONG).show();
                }
            }
            case COARSE_LOCATION:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationService();  // Start Location Service for Driver Tracking Purpose
                }
                else{
                    Toast.makeText(NavigationActivity.this,"You need Location Permission to Access User Location.",Toast.LENGTH_LONG).show();
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }*/
/*
    @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());*/
        /*if(networkValidator.isNetworkConnected()){
            if(Utils.isGooglePlayServicesAvailable(NavigationActivity.this)){
                Log.e("Google Play Service","Enabled");
                if (!permissionsManager.checkPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) && !permissionsManager.checkPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) && !permissionsManager.checkPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.WAKE_LOCK)) {
                    permissionsManager.askForPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION);
                    permissionsManager.askForPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION);
                    permissionsManager.askForPermission(NavigationActivity.this, NavigationActivity.this, Manifest.permission.WAKE_LOCK, WAKE_LOCK);
                }
                else{
                    if(Utils.isLocationEnabled(NavigationActivity.this)){
                        Log.e("Location Enabled ","True");
                        startLocationService();  // Start Location Service for Driver Tracking Purpose
                        registerLocationReceiver();
                    }
                    else{
                        Log.e("Location Enabled ","False");
                        AlertDialog.Builder builder = new AlertDialog.Builder(NavigationActivity.this);
                        builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                        builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                        builder.setNegativeButton(R.string.no, null);
                        builder.create().show();
                        return;

                    }

                }

            }
            else{
                Log.e("Google Play Service","Disabled");
                Toast.makeText(NavigationActivity.this,getResources().getString(R.string.google_play_services_error),Toast.LENGTH_LONG).show();
            }

        }
        else {
            Toast.makeText(NavigationActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }*/

  //  }


    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

}
