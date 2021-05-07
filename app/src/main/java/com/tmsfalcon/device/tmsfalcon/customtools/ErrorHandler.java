package com.tmsfalcon.device.tmsfalcon.customtools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.DialogActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Dell on 9/20/2018.
 */

public class ErrorHandler {

    public static void setVolleyMessage(Context context, VolleyError error){
        Boolean error_status = null;
        JSONArray json_errors ;
        String errors = "";
        Log.e("error", " is "+String.valueOf(error));
        VolleyLog.e("Error Response ", "Error: " + error.getMessage());
        NetworkResponse errorRes = error.networkResponse;
        String stringData = "";
        boolean is_disabled = false;
        boolean is_another_user = false;

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
                json_errors = obj.optJSONArray("messages");
                is_disabled = obj.optBoolean("is_disabled");
                is_another_user = obj.optBoolean("is_another_user");

                Log.e("json_errors",""+json_errors);

                if(json_errors != null){
                    for(int i = 0 ; i < json_errors.length(); i++){
                        errors += json_errors.get(i)+"\n";
                    }
                }
                if(!error_status){
                    Log.e("Volley Error ","Status Code => "+errorRes.statusCode +" is_disabled => "+is_disabled+" Is another User => "+is_another_user);
                    if((json_errors != null && json_errors.length() > 0 && json_errors.get(0).toString().trim().equalsIgnoreCase("Expired token")) || is_disabled || is_another_user){
                        Utils.logoutApi(context);
                        //Toast.makeText(context,context.getResources().getString(R.string.token_error),Toast.LENGTH_LONG).show();
                    }
                    Toast.makeText(context,errors,Toast.LENGTH_LONG).show();

                }
            }
            else{
                Toast.makeText(context,context.getResources().getString(R.string.null_response),Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            Log.e("exception ", String.valueOf(e));
            Log.e("My App", context.getResources().getString(R.string.malformed_json)+ stringData );
            Toast.makeText(context,context.getResources().getString(R.string.malformed_json)+ stringData,Toast.LENGTH_LONG).show();
        }
    }

    public static void setRestClientMessage(Context context, String error_string){

        try {
            if(error_string != null && error_string.length() > 0){
                JSONObject jObjError = new JSONObject(error_string);
                if(jObjError != null && jObjError.length() > 0){

                    JSONArray messagesList = jObjError.getJSONArray("messages");
                    String messages = "";
                    for(int i = 0; i<messagesList.length() ;i++){
                        messages += messagesList.get(i);
                    }

                    boolean is_another_user = jObjError.optBoolean("is_another_user");
                    boolean is_disabled = jObjError.optBoolean("is_disabled");
                    if(messagesList.get(0).equals("Expired token") ||is_another_user || is_disabled){
                        Utils.logoutApi(context);
                    }
                    Toast.makeText(context,messages,Toast.LENGTH_LONG).show();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void setRestClientMessageForService(final Context context, String error_string){

        try {
            if(error_string != null && error_string.length() > 0){
                final JSONObject jObjError = new JSONObject(error_string);
                if(jObjError != null && jObjError.length() > 0){

                    final JSONArray messagesList = jObjError.getJSONArray("messages");
                    String messages = "";
                    for(int i = 0; i<messagesList.length() ;i++){
                        messages += messagesList.get(i);
                    }

                    boolean is_another_user = jObjError.optBoolean("is_another_user");
                    boolean is_disabled = jObjError.optBoolean("is_disabled");
                    try {
                        if(messagesList.get(0).equals("Expired token") ||is_another_user || is_disabled || messagesList.get(0).equals("GPS Device Id Not found")){
                            Log.e("in","conditions");
                            Intent startDialogActivity = new Intent(context, DialogActivity.class);
                            startDialogActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startDialogActivity.putExtra("is_location_dialog",false);
                            startDialogActivity.putExtra("messageList",messagesList.toString());
                            startDialogActivity.putExtra("messages",messages);
                            context.startActivity(startDialogActivity);
                            //Utils.logoutApiForService(context);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
