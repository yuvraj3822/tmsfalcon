package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.Responses.GpsDataResponse;
import com.tmsfalcon.device.tmsfalcon.activities.DialogActivity;
import com.tmsfalcon.device.tmsfalcon.database.Driver;
import com.tmsfalcon.device.tmsfalcon.entities.GpsLocationParams;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Dell on 1/26/2019.
 */

public class GpsApiCalls {

    /*public static void callFirstApi(final Context context){
        RestClient.get().fetchGpsData(SessionManager.getInstance().get_token()).enqueue(new Callback<GpsDataResponse>() {
            @Override
            public void onResponse(Call<GpsDataResponse> call, Response<GpsDataResponse> response) {

                Log.e("gps response ", new Gson().toJson(response));
                Log.e("in"," response is "+response.isSuccessful());
                if(response.body() != null || response.isSuccessful()){
                    Log.e("in","first if");
                    if(response.body().getStatus()){
                         Log.e("gps response body", " is "+new Gson().toJson(response.body()));
                         GpsDataResponse.Datum data = response.body().getData();
                         int off_duty = data.getOff_duty();]
                         int on_duty = data.getOn_duty();
                         int interval = data.getInterval();
                         String current = data.getCurrent();
                         Boolean location_status = data.getLocation_status();
                         SessionManager.getInstance().storeGpsParameters(off_duty,on_duty,interval,current,location_status);
                         Log.e("gps data","off duty=>"+SessionManager.getInstance().getKeyGpsOffDuty()+
                                                    "on duty=>"+SessionManager.getInstance().getKeyGpsOnDuty()+
                                                    "interval=>"+SessionManager.getInstance().getKeyGpsInterval()+
                                                    "current=>"+SessionManager.getInstance().getKeyGpsCurrent()+
                                                    "location_status=>"+SessionManager.getInstance().getKeyGpsLocationStatus());

                    }
                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(context,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<GpsDataResponse> call, Throwable t) {
                Log.e("server call error",t.getMessage());
            }
        });
    }*/

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    public static RequestBody convertsingleLocation(String dt,String lat,String lng,String alt,String angle ,String speed,String loc_valid,String params){
        ArrayList<GpsLocationParams> list_postFields = new ArrayList<>();

        GpsLocationParams gpsLocationParams = new GpsLocationParams();
        gpsLocationParams.setDt(dt);
        gpsLocationParams.setLat(lat);
        gpsLocationParams.setLng(lng);
        gpsLocationParams.setAltitude(alt);
        gpsLocationParams.setAngle(angle);
        gpsLocationParams.setSpeed(speed);
        gpsLocationParams.setLoc_valid(loc_valid);
        gpsLocationParams.setParams(params);

        list_postFields.add(gpsLocationParams);

        String json = serialize(list_postFields, "data");
        //Log.e("single loc json",json);
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return body;
    }

    public static RequestBody convertMultipleLocations(Context context){

        Driver data = new Driver(context);
        List<GpsLocationParams> records = data.getAllLocations();

        String json = serialize(records, "data");
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        return body;
    }


    public static void sendLocation(final Context context, RequestBody body, final boolean deleteDbData){

        Log.e("response","initial: ");

//        uncomment

//        RestClient.get().sendGpsLocationParams(SessionManager.getInstance().get_token(),body).enqueue(new Callback<GpsDataResponse>() {
//            @Override
//            public void onResponse(Call<GpsDataResponse> call, Response<GpsDataResponse> response) {
//                if(response.body() != null || response.isSuccessful()){
//                    if(response.body().getStatus()){
//                        //Log.e("gps response body", " is "+new Gson().toJson(response.body()));
//                        GpsDataResponse.Datum datum = response.body().getData();
//                        GpsDataResponse.GpsIntervalSettings data = datum.getGpsIntervalSettings();
//                        int off_duty_interval = data.getOff_duty_interval();
//                        int on_duty_interval = data.getOn_duty_interval();
//                        int off_duty_metres = data.getOff_duty_metres();
//                        int on_duty_metres = data.getOn_duty_metres();
//                        String current_status = data.getCurrent_status();
//                        Boolean location_status = data.getLocation_status();
//                        SessionManager.getInstance().storeGpsParameters(off_duty_interval,on_duty_interval,off_duty_metres,on_duty_metres,current_status,location_status);
//                        if(deleteDbData){
//
//                            Driver driver = new Driver(context);
//                            driver.deleteAllLocations();
//                            driver.showLocations(driver);
//
//                        }
//                    }
//                }
//                else{
//                    try {
//                        String error_string = response.errorBody().string();
//                       // Log.e("error string ",""+error_string);
//
//                        //ErrorHandler.setRestClientMessageForService(context,error_string);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GpsDataResponse> call, Throwable t) {
//               // Log.e("server call error",""+t.getMessage());
//            }
//        });
//
    }

    public static String serialize(List<GpsLocationParams> objects, String arrKey) {
        JsonArray ja = new JsonArray();
        for (Object object: objects) {
            Gson gson = new Gson();
            JsonElement je = gson.toJsonTree(object);
            /*JsonObject jo = new JsonObject();
            jo.add(objKey, je);*/
            ja.add(je);
        }

        JsonObject objMain = new JsonObject();
        objMain.add(arrKey,ja);

        return objMain.toString();

    }
}
