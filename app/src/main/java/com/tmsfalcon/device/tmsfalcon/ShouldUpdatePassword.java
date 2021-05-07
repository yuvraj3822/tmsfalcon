package com.tmsfalcon.device.tmsfalcon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ShouldUpdatePassword extends AppCompatActivity {

    EditText passwordEditText,confirmPasswordEditText;
    String password,confirm_pasword;
    Button login_btn,update_btn;
    boolean isFormValid ;
    NetworkValidator networkValidator;
    CustomValidator customValidator;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_should_update_password);
        ButterKnife.bind(this);
        networkValidator = new NetworkValidator(ShouldUpdatePassword.this);
        customValidator = new CustomValidator(ShouldUpdatePassword.this);
        session = new SessionManager(ShouldUpdatePassword.this);
        passwordEditText = findViewById(R.id.input_password);
        confirmPasswordEditText = findViewById(R.id.input_confirm_password);
        login_btn = findViewById(R.id.redirectlogin);
        update_btn = findViewById(R.id.update_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent redirectLogin = new Intent(ShouldUpdatePassword.this,Login.class);
                startActivity(redirectLogin);
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(networkValidator.isNetworkConnected()){
                    if(validateForm()){
                        performUpdateAction();
                    }
                    else{

                    }
                }
                else {
                    Toast.makeText(ShouldUpdatePassword.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    public boolean validateForm() {

        password = passwordEditText.getText().toString().trim();
        confirm_pasword = confirmPasswordEditText.getText().toString().trim();
        Log.e("valid password ", String.valueOf(customValidator.isValidPassword(password)));
        if(!customValidator.setRequired(password)){
            passwordEditText.setError(getResources().getString(R.string.new_password_error));
            return isFormValid = false;
        }
        if(!customValidator.setRequired(confirm_pasword)){
            confirmPasswordEditText.setError(getResources().getString(R.string.confirm_password_error));
            return isFormValid = false;
        }
        if(!password.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$") ){
            passwordEditText.setError(getResources().getString(R.string.new_password_regex_error));
            return isFormValid = false;
        }
        if(!customValidator.setEquality(password,confirm_pasword)){
            confirmPasswordEditText.setError(getResources().getString(R.string.password_equality_error));
            return isFormValid = false;
        }

        return isFormValid = true;
    }

    private void performUpdateAction() {
        // Tag used to cancel the request
        String tag_json_obj = "update_password_tag";

        String url = UrlController.SHOULD_UPDATE_PASSWORD_URL;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("password", password);
        params.put("confirm_password", confirm_pasword);

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
                            if(status){
                                SessionManager.getInstance().clearSession();
                                Intent goToLogin = new Intent(ShouldUpdatePassword.this,Login.class);
                                startActivity(goToLogin);
                            }
                            Toast.makeText(ShouldUpdatePassword.this,messages,Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }

                        Log.e("Response", response.toString());
                        pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorHandler.setVolleyMessage(ShouldUpdatePassword.this,error);

                pDialog.hide();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Token",SessionManager.getInstance().get_token());
                Log.e("session ",SessionManager.getInstance().get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    @SuppressWarnings("unused")
    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("unused")
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

}
