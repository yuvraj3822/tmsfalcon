package com.tmsfalcon.device.tmsfalcon.customtools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.entities.DestinationLocationModel;
import com.tmsfalcon.device.tmsfalcon.services.GpsTrackerService;
import com.tmsfalcon.device.tmsfalcon.services.LocationService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.RequestBody;

/**
 * Created by Dell on 7/31/2018.
 */

public class Utils {

    public static final int LOCATION_INTERVAL = 1000*60; //For Location Tracking Service
    public static final int FASTEST_LOCATION_INTERVAL = 1000 *30; //For Location Tracking Service

    public static final int DATABASE_VERSION = 9; //db version to be used in every table

    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";
    public static final String BROADCAST_DETECTED_ACTIVITY_TO_SERVICE = "DetectedActivitiesIntentServiceToLocationService";
    public static final String ACTION_LOCATION_BROADCAST = "LocationServiceLocationBroadcast";

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 60 * 1000;

    public static final int CONFIDENCE = 70;

    public static boolean urlExist; // to check if url exist for documents

    public static String TASK_UPLOAD_REQUESTED_DOCUMENTS = "task_upload_requested_documents"; //to be used in job intent service for uploading
    public static String Task_UPLOAD_CAPTURED_DOCUMENTS = "task_upload_captured_documents";
    public static boolean cancel_call = false; // for job intent
    public static boolean pause_call = false; // for job intent
    public static boolean confirm_cancel_call = false; // for job intent dialog
    public static String SHOW_PROGRESS_BAR = "show_progress_bar";
    public static String CANCEL_UPLOADING = "cancel_uploading";
    public static String HIDE_PROGRESS_BAR = "hide_progress_bar";
    public static String SHOW_PROGRESS_TEXT = "show_progress_text";
    public static Context activity_context;
    public static String Task_Type;
    public static Map<String, RequestBody> Service_Hashmap;
    public static Map<String, String> Service_EMAIL_Hashmap;
    public static ArrayList<File> Service_Files_Array;
    public static ProgressRequestBodyMultiple.UploadCallbacks Service_Upload_Callback;
    public static final String storageConnectionString = "DefaultEndpointsProtocol=https;"
            + "AccountName=tmsfalconapp;"
            + "AccountKey=eC7dfzmkPVUJfmRBBrV6hwJkRi2aPpwmq2d8XeJXLvdamGCbHnOrvI+FO0Ga4vqxyzrTwo1LOinZnV23/ghKZA==";

    //public static SessionManager session;

    public static void setNotificationCount(TextView textview, String count){
        if(count != null && ((Integer.parseInt(count)) > 0)) {
            textview.setText(""+count);
        }
        else{
            textview.setVisibility(View.GONE);
            //textview.setText("");
        }

    }
    public static boolean isSameDomain(String url, String url1) {
        return getRootDomainUrl(url.toLowerCase()).equals(getRootDomainUrl(url1.toLowerCase()));
    }

    private static String getRootDomainUrl(String url) {
        String[] domainKeys = url.split("/")[2].split("\\.");
        int length = domainKeys.length;
        int dummy = domainKeys[0].equals("www") ? 1 : 0;
        if (length - dummy == 2)
            return domainKeys[length - 2] + "." + domainKeys[length - 1];
        else {
            if (domainKeys[length - 1].length() == 2) {
                return domainKeys[length - 3] + "." + domainKeys[length - 2] + "." + domainKeys[length - 1];
            } else {
                return domainKeys[length - 2] + "." + domainKeys[length - 1];
            }
        }
    }

    public static String getDestinationCities(String destinations){
        Type type = new TypeToken<List<DestinationLocationModel>>(){}.getType();
        Gson gson = new Gson();
        List<DestinationLocationModel> destination_list = gson.fromJson(destinations, type);
        ArrayList<String> cities = new ArrayList<>();
        //String cities = "";
        if(destination_list != null && !destination_list.isEmpty()) {
            for (int i = 0; i < destination_list.size(); i++) {
                cities.add(destination_list.get(i).getDestination_city());
                //cities = cities+","
            }
        }
        String cities_string = TextUtils.join(",", cities);
        return  cities_string;

    }

    public static String getDestinationStates(String destinations){
        Type type = new TypeToken<List<DestinationLocationModel>>(){}.getType();
        Gson gson = new Gson();
        List<DestinationLocationModel> destination_list = gson.fromJson(destinations, type);
        ArrayList<String> states = new ArrayList<>();
        if(destination_list != null && !destination_list.isEmpty()){
            for(int i = 0; i < destination_list.size(); i++){
                states.add(destination_list.get(i).getDestination_state());
            }
        }
        String states_string = TextUtils.join(",", states);
        return  states_string;

    }

    public static String convertDecimalToTwoDigits(String num){
        String str = "";
        if(!CustomValidator.checkNullState(num)){
            str = String.format("%.2f", Double.parseDouble(num));
        }
        return str;
    }

    public static String convertNumToAccountingFormat(String num){
        String str = "";
        if(!CustomValidator.checkNullState(num)){
            if(isNumber(num)){
                str = NumberFormat.getNumberInstance(Locale.getDefault()).format(Double.parseDouble(num));
            }
            else if(isFloat(num)){
                str = NumberFormat.getNumberInstance(Locale.getDefault()).format(Double.parseDouble(num));
            }
            else {
                str = "";
            }

        }
        return str;
    }

    private static boolean isNumber(String string) {
        try {
            int amount = Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isFloat(String string) {
        try {
            float amount = Float.parseFloat(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isUrlReachable(Context context, final String url) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = connMan.getActiveNetworkInfo();

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    if (netInfo != null && netInfo.isConnected()) {
                        try {
                            URL urlServer = new URL(url);
                            HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                            urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                            urlConn.connect();
                            if (urlConn.getResponseCode() == 200) {
                                Log.e("in","getResponseCode 200");
                                urlExist = true;
                            } else {
                                urlExist = false;
                            }
                        } catch (MalformedURLException e1) {
                            urlExist = false;
                        } catch (IOException e) {
                            urlExist = false;
                        }
                    }
                    urlExist = false;
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        return urlExist;
    }


    public static boolean isGooglePlayServicesAvailable(Activity context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(context);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(context, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
    public static Date getUTCToLocalDate(String date) {
        Date inputDate = new Date();
        if (date != null && !date.isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                inputDate = simpleDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return inputDate;
    }
    public static String getLocalToUTCDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date time = calendar.getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return outputFmt.format(time);
    }

    // function to convert speed
    // in km/hr to m/sec
    public static int kmph_to_mps(double kmph)
    {
        return(int) (0.277778 * kmph);
    }

    // function to convert speed
    // in m/sec to km/hr
    public static int mps_to_kmph(double mps)
    {
        return(int) (3.6 * mps);
    }

    public static double convertMetreToMile(float metres){
        return metres*0.000621;
    }

    public static void goToDashboard(Context context){
        Intent intent = new Intent(context, DashboardActivity.class);
        context.startActivity(intent);
    }

    public static void logoutApi(final Context context){

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Logging Out...");
        if(!((Activity) context ).isFinishing())
        {
            pd.show();
        }


        final SessionManager session = new SessionManager(context);
        String tag_json_obj = "logout_tag";
        String url = UrlController.LOGOUT;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        if(pd != null){
                            pd.dismiss();
                        }

                        Log.e("Response", response.toString());
                        try {
                            status = response.getBoolean("status");

                            if(status){
                                context.stopService(new Intent(context, GpsTrackerService.class));
                                context.stopService(new Intent(context, LocationService.class));
                                session.deleteLoadBoardSettings();
                                session.logoutUser();
                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if(pd != null){
                    pd.dismiss();
                }
                VolleyLog.e("Error Response ", "Error: " + error.getMessage());
                NetworkResponse errorRes = error.networkResponse;
                String stringData = "";
                Log.e("error ", String.valueOf(error));

                if(errorRes != null && errorRes.data != null){
                    try {
                        stringData = new String(errorRes.data,"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.e("exception ", String.valueOf(e));
                    }
                }
                Log.e("Error",stringData);
            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                if(pd != null){
                    pd.dismiss();
                }

                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                Log.e("session token ",session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
