package com.tmsfalcon.device.tmsfalcon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android on 7/6/2017.
 */

public class ForgotPassword extends AppCompatActivity {

    NetworkValidator networkValidator;
    CustomValidator customValidator;
    Button submitBtn,login;
    EditText username_editText;
    String input_username;
    boolean isFormValid ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        networkValidator = new NetworkValidator(ForgotPassword.this);
        customValidator = new CustomValidator(ForgotPassword.this);
        submitBtn = findViewById(R.id.submit_btn);
        login = findViewById(R.id.login);
        username_editText = findViewById(R.id.input_username);
        submitBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(networkValidator.isNetworkConnected()){
                    validateForm();
                    if(isFormValid ){
                        performForgotAction();
                    }
                }
                else {
                    Toast.makeText(ForgotPassword.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent redirectLogin = new Intent(ForgotPassword.this,Login.class);
                startActivity(redirectLogin);
            }
        });
    }
    public boolean validateForm() {

        input_username = username_editText.getText().toString();
        if(!customValidator.setRequired(input_username)){
            username_editText.setError(getResources().getString(R.string.username_error));
            return isFormValid = false;
        }

        return isFormValid = true;
    }
    private void performForgotAction() {
        // Tag used to cancel the request

        String tag_json_obj = "forgot_password_tag";

        String url = UrlController.FORGOT_PASSWORD_URL;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("username", input_username);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
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

                            for(int i =0 ; i < json_messages.length(); i++){
                                messages += json_messages.get(i)+"\n";
                            }
                            Toast.makeText(ForgotPassword.this,messages,Toast.LENGTH_LONG).show();
                            Intent goToLogin = new Intent(ForgotPassword.this,Login.class);
                            startActivity(goToLogin);

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }
                        if(status){

                        }

                        Log.e("Response", response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(ForgotPassword.this,error);

                pDialog.hide();
            }
        }) {

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}
