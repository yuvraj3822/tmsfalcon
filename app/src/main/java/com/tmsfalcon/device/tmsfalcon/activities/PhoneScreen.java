package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
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

public class PhoneScreen extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_phone_screen);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_phone_screen, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        if(networkValidator.isNetworkConnected()){
            getRingCentralData();
        }
        else {
            Toast.makeText(PhoneScreen.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void getRingCentralData() {
        // Tag used to cancel the request
        String tag_json_obj = "rincentral_data";
        String url = UrlController.RINGCENTRAL;
        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        String phone,extension;
                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");

                            if(status){
                                data_json = response.getJSONObject("data");
                                phone = data_json.getString("phone");
                                extension = data_json.getString("extension");
                                if(phone != null && phone.length() > 0){
                                    mainBlock.setVisibility(View.VISIBLE);
                                   // callingBlock.setVisibility(View.VISIBLE);
                                    RingcentralPhone.setText(phone);
                                    RingcentralExtension.setText(extension);
                                    callRequestLayout.setVisibility(View.VISIBLE);

                                }
                                else{
                                    no_data_textview.setVisibility(View.VISIBLE);
                                    mainBlock.setVisibility(View.GONE);
                                    //callingBlock.setVisibility(View.GONE);
                                    callRequestLayout.setVisibility(View.GONE);
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
                ErrorHandler.setVolleyMessage(PhoneScreen.this,error);
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

    public void initIds(){

        networkValidator = new NetworkValidator(PhoneScreen.this);
        session = new SessionManager(PhoneScreen.this);

    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    /*@Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }
*/
   /* @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/
    @Bind(R.id.ringcentral_phone)
    TextView RingcentralPhone;
    @Bind(R.id.ringcentral_extension)
    TextView RingcentralExtension;
    @Bind(R.id.main_block)
    LinearLayout mainBlock;
    @Bind(R.id.call_request_layout)
    LinearLayout callRequestLayout;

    @OnClick(R.id.call_request_layout)
    void goToRingOutScreen(){
        Intent i = new Intent(PhoneScreen.this,RingoutScreen.class);
        i.putExtra("intent_phone","");
        i.putExtra("intent_ext","");
        startActivity(i);
    }

   /* @Bind(R.id.calling_block)
    LinearLayout callingBlock;*/

   /* @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(P jg kg hg kgh khg kjhoneScreen.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(PhoneScreen.this);
    }*/
}
