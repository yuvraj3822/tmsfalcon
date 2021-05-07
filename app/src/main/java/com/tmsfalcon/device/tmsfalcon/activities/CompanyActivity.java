package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class CompanyActivity extends AppCompatActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    TextView nameTextView,emailTextView,phoneTextView,faxTextView,addressTextView,stateTextView,zipcodeTextView;
    CircleImageView company_thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);
        ButterKnife.bind(this);
        lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);
        initIds();
        int colorCodeDark = Color.parseColor("#071528");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        if(networkValidator.isNetworkConnected()){
            getCompanyDetail();
        }
        else {
            Toast.makeText(CompanyActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }
    private void initIds() {
        networkValidator = new NetworkValidator(CompanyActivity.this);
        session = new SessionManager(CompanyActivity.this);
        nameTextView = findViewById(R.id.full_name);
        emailTextView = findViewById(R.id.email);
        phoneTextView = findViewById(R.id.phone);
        addressTextView = findViewById(R.id.address);
        faxTextView = findViewById(R.id.alternative_fax);
        stateTextView = findViewById(R.id.state_country);
        zipcodeTextView = findViewById(R.id.zipcode);
        company_thumb = findViewById(R.id.company_thumb);
    }

    private void getCompanyDetail() {
        // Tag used to cancel the request
        String tag_json_obj = "company_detail_tag";

        String url = UrlController.COMPANY_INFO;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                JSONObject data = response.getJSONObject("data");


                                Glide.with(CompanyActivity.this)
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(company_thumb);
                                CustomValidator.setStringData(nameTextView,data.getString("company_name"));
                                CustomValidator.setStringData(emailTextView,data.getString("email"));
                                CustomValidator.setStringData(phoneTextView,data.getString("phone_number"));
                                CustomValidator.setStringData(faxTextView,data.getString("alternative_fax"));
                                CustomValidator.setStringData(addressTextView,data.getString("address"));
                                CustomValidator.setStringData(zipcodeTextView,data.getString("zip_code"));
                                CustomValidator.setCombinedStringData(stateTextView,"/",data.getString("state"),data.getString("country"));

                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                       hideProgressBar();
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
                                Toast.makeText(CompanyActivity.this,getResources().getString(R.string.token_error),Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(CompanyActivity.this,errors,Toast.LENGTH_LONG).show();
                            }

                        }
                    }
                    else{
                        Toast.makeText(CompanyActivity.this,getResources().getString(R.string.null_response),Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    Log.e("exception ", String.valueOf(e));
                    Log.e("My App", getResources().getString(R.string.malformed_json)+ stringData );
                    Toast.makeText(CompanyActivity.this,getResources().getString(R.string.malformed_json)+ stringData,Toast.LENGTH_LONG).show();
                }
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }
}
