package com.tmsfalcon.device.tmsfalcon;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tmsfalcon.device.tmsfalcon.activities.DashboardActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.Driver;
import com.tmsfalcon.device.tmsfalcon.entities.DriverModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Android on 6/30/2017.
 */

public class Login extends AppCompatActivity implements View.OnClickListener{

    EditText username_editText,password_ediIext;
    String username,password;
    Button login_btn,forgot_password_btn;
    boolean isLoginValid ;
    NetworkValidator networkValidator;
    CustomValidator customValidator;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.bind(this);
        InitIds();
        session.storeNotificationCount(session.getNotificationCount());
        login_btn.setOnClickListener(this);
        forgot_password_btn.setOnClickListener(this);
    }

    public void InitIds(){
        username_editText = findViewById(R.id.input_name);
        password_ediIext = findViewById(R.id.input_password);
        login_btn = findViewById(R.id.login_btn);
        forgot_password_btn = findViewById(R.id.forgot_password);
        networkValidator = new NetworkValidator(Login.this);
        customValidator = new CustomValidator(Login.this);
        // Session Manager
        session = new SessionManager(Login.this);




    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                if(networkValidator.isNetworkConnected()){
                    if(validateLoginForm()){
                        performLoginAction();
                    }
                }
                else {
                    Toast.makeText(Login.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                }

                break;
            case R.id.forgot_password:
                Intent goToForgotPassworScreen = new Intent(Login.this,ForgotPassword.class);
                startActivity(goToForgotPassworScreen);
                break;
        }

    }

    private void performLoginAction() {
        // Tag used to cancel the request
        String tag_json_obj = "login_tag";
        AppController.first_time_login = 1;

        final String url = UrlController.LOGIN_URL;

        showProgessBar();

        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        String token = "";
                        int driver_id,should_update_password;
                        String uid,thumb,nick_name,full_name,type,company_id,company_name,gender;
                        JSONObject dataObj;
                        Driver driver = new Driver(Login.this);

                        try {
;                            status = response.getBoolean("status");

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }
                        if(status){
                            try {
                                token = response.getString("token");
                                AppController.token = token;
                                dataObj = response.getJSONObject("data");
                                driver_id = dataObj.getInt("driver_id");
                                uid = dataObj.getString("uid");
                                gender = dataObj.getString("gender");
                                thumb = dataObj.getString("thumb");
                                nick_name = dataObj.getString("nick_name");
                                should_update_password = dataObj.getInt("should_update_password");
                                full_name = dataObj.getString("full_name");
                                type = dataObj.getString("type");
                                company_id = dataObj.getString("company_id");
                                company_name = dataObj.getString("company_name");
                                String topicToSubscribe = company_id+"-"+type;
                                String topicAll = company_id+"-all";

                                if(should_update_password == 1){

                                    Log.e("thumb",""+thumb);

                                    FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe);
                                    FirebaseMessaging.getInstance().subscribeToTopic(topicAll);

                                    session.createLoginSession(driver_id,full_name,nick_name,uid,thumb,token,type,company_name,gender, Integer.parseInt(company_id));
                                    Intent goToShouldUpdatePasswordScreen = new Intent(Login.this,ShouldUpdatePassword.class);
                                    startActivity(goToShouldUpdatePasswordScreen);
                                }
                                else{
                                    /*FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe);
                                    FirebaseMessaging.getInstance().subscribeToTopic(topicAll);*/

                                    long result = driver.addDriverBasic(new DriverModel(driver_id, full_name, should_update_password,nick_name,  thumb, uid, "", "", "","","","","","", "","","", "","", "", "", "", "", "","", "", "", "", "", "", "", "", "", "", "", "", "",token));
                                    if(result != -1){
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_successful),Toast.LENGTH_LONG).show();
                                        AppController.driver_id = driver_id;
                                        session.createLoginSession(driver_id,full_name,nick_name,uid,thumb,token,type,company_name,gender, Integer.parseInt(company_id));
                                        //GpsApiCalls.callFirstApi(Login.this);
                                        Intent goToDashBoard = new Intent(Login.this, DashboardActivity.class);
                                        startActivity(goToDashBoard);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_db_error),Toast.LENGTH_LONG).show();
                                    }

                                }
                            } catch (JSONException e) {
                                 Log.e("exception ", String.valueOf(e));
                            }
                        }

                        Log.e("Response", response.toString());
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(Login.this,error);
                hideProgressBar();
            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void showProgessBar() {
        if(progressBar != null){
            progressBar.setVisibility(View.VISIBLE);
        }

    }

    public void hideProgressBar() {
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
        }

    }

    public boolean validateLoginForm() {
        username = username_editText.getText().toString();
        password = password_ediIext.getText().toString();
        if(!customValidator.setRequired(username)){
            username_editText.setError(getResources().getString(R.string.username_error));
            return isLoginValid = false;
        }
        if(!customValidator.setRequired(password)){
            password_ediIext.setError(getResources().getString(R.string.password_error));
            return isLoginValid = false;
        }
        return isLoginValid = true;
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
}