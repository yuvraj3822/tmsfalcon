package com.tmsfalcon.device.tmsfalcon.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class DispatcherActivity extends AppCompatActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    TextView nameTextView,emailTextView,phoneTextView;
    CircleImageView dispatcher_thumb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatcher);
        ButterKnife.bind(this);
        lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);
        initIds();
       /* int colorCodeDark = Color.parseColor("#071528");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));*/
        if(networkValidator.isNetworkConnected()){
            getDispatcherDetail();
        }
        else {
            Toast.makeText(DispatcherActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }
    private void initIds() {
        networkValidator = new NetworkValidator(DispatcherActivity.this);
        session = new SessionManager(DispatcherActivity.this);
        nameTextView = findViewById(R.id.full_name);
        emailTextView = findViewById(R.id.email);
        phoneTextView = findViewById(R.id.phone);
        dispatcher_thumb = findViewById(R.id.dispatcher_thumb);
    }

    private void getDispatcherDetail() {
        // Tag used to cancel the request
        String tag_json_obj = "dispatcher_detail_tag";

        String url = UrlController.DISPACHER_INFO;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        Boolean dispatcher = null;
                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            dispatcher = response.getBoolean("dispatcher");
                            if(status){
                                JSONObject data = response.getJSONObject("data");
                                dispatcherDataLinearLayout.setVisibility(View.VISIBLE);
                                Glide.with(DispatcherActivity.this)
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(dispatcher_thumb);
                               /* Picasso.with(DispatcherActivity.this)
                                        .load(data.getString("thumb"))
                                        .error(R.drawable.no_image)
                                        .into(dispatcher_thumb);*/
                                CustomValidator.setCombinedStringData(nameTextView," ",data.getString("first_name"),data.getString("last_name"));
                                CustomValidator.setStringData(emailTextView,data.getString("email"));
                                CustomValidator.setStringData(phoneTextView,data.getString("phone_number"));
                            }
                            else{
                                if(!dispatcher){
                                    dispatcherDataLinearLayout.setVisibility(View.GONE);
                                    no_dispatcher_textview.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(DispatcherActivity.this,error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
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
    @Bind(R.id.dispatcher_data)
    LinearLayout dispatcherDataLinearLayout;
    @Bind(R.id.no_dispatcher_textview)
    TextView no_dispatcher_textview;

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }
}
