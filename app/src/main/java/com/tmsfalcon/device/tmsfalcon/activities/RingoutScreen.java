package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
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

public class RingoutScreen extends NavigationBaseActivity implements View.OnClickListener,View.OnLongClickListener{

    SessionManager session;
    NetworkValidator networkValidator;
    String intent_phone,intent_ext;
    public String input_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_ringout_screen, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        //setContentView(R.layout.activity_contacts);
        intent_phone = getIntent().getExtras().getString("intent_phone","");
        intent_ext = getIntent().getExtras().getString("intent_ext","");
        Log.e("intent data ","intent_phone "+intent_phone+" intent_ext "+intent_ext );
        initIds();
        registerClickListener();
        inputTextView.setText(intent_ext+" "+intent_phone);

        if(networkValidator.isNetworkConnected()){
            //getContactsData();
        }
        else {
            Toast.makeText(RingoutScreen.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void registerClickListener(){
        oneLayout.setOnClickListener(this);
        twoLayout.setOnClickListener(this);
        threeLayout.setOnClickListener(this);
        fourLayout.setOnClickListener(this);
        fiveLayout.setOnClickListener(this);
        sixLayout.setOnClickListener(this);
        sevenLayout.setOnClickListener(this);
        eightLayout.setOnClickListener(this);
        nineLayout.setOnClickListener(this);
        zeroLayout.setOnClickListener(this);
        aestrickLayout.setOnClickListener(this);
        hashLayout.setOnClickListener(this);
        stepBackImageView.setOnClickListener(this);
        zeroLayout.setOnLongClickListener(this);
        callLayout.setOnClickListener(this);
    }

    public void initIds(){

        networkValidator = new NetworkValidator(RingoutScreen.this);
        session = new SessionManager(RingoutScreen.this);

    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        input_number = inputTextView.getText().toString();
        switch (id){
            case R.id.one_layout:
                input_number = input_number+""+getResources().getString(R.string.one);
                break;
            case R.id.two_layout:
                input_number = input_number+""+getResources().getString(R.string.two);
                break;
            case R.id.three_layout:
                input_number = input_number+""+getResources().getString(R.string.three);
                break;
            case R.id.four_layout:
                input_number = input_number+""+getResources().getString(R.string.four);
                break;
            case R.id.five_layout:
                input_number = input_number+""+getResources().getString(R.string.five);
                break;
            case R.id.six_layout:
                input_number = input_number+""+getResources().getString(R.string.six);
                break;
            case R.id.seven_layout:
                input_number = input_number+""+getResources().getString(R.string.seven);
                break;
            case R.id.eight_layout:
                input_number = input_number+""+getResources().getString(R.string.eight);
                break;
            case R.id.nine_layout:
                input_number = input_number+""+getResources().getString(R.string.nine);
                break;
            case R.id.zero_layout:
                input_number = input_number+""+getResources().getString(R.string.zero);
                break;
            case R.id.aestrick_layout:
                input_number = input_number+""+getResources().getString(R.string.aestrick);
                break;
            case R.id.hash_layout:
                input_number = input_number+""+getResources().getString(R.string.hash);
                break;
            case R.id.step_back_image:
                input_number = removeLastChar(input_number);
                break;
            case R.id.call_layout:
                makeCall();
                break;
        }
        Log.e("input_number ",""+input_number);
        inputTextView.setText(input_number);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void makeCall(){
        //String phoneNumber = "+"+inputTextView.getText();
        String phoneNumber = inputTextView.getText().toString();

        // Tag used to cancel the request
        String tag_json_obj = "ringout";

        String url = UrlController.RINGOUT;

        showProgessBar();

        Map<String, String> params = new HashMap<>();

        params.put("to",phoneNumber);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request
                .Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("Response", response.toString());

                        Boolean status = null;
                        JSONArray json_messages ;
                        String messages = "";

                        try {
                            status = response.getBoolean("status");
                            json_messages = response.optJSONArray("messages");

                            if(json_messages != null && json_messages.length() >0){
                                for(int i = 0 ; i < json_messages.length(); i++){
                                    messages += json_messages.get(i)+"\n";
                                }

                                Toast.makeText(RingoutScreen.this,messages,Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        if(status){
                            inputTextView.setText("");
                            Toast.makeText(RingoutScreen.this,"Your call request has been generated.",Toast.LENGTH_LONG).show();
                        }

                        Log.e("Response", response.toString());
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(RingoutScreen.this,error);
                hideProgressBar();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Token",session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }


    @Override
    public boolean onLongClick(View v) {
        int id = v.getId();
        input_number = inputTextView.getText().toString();
        switch (id){
            case R.id.zero_layout:
                input_number = input_number+""+getResources().getString(R.string.plus);
                break;
        }
        inputTextView.setText(input_number);
        return true;
    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }

    @Bind(R.id.input_number)
    TextView inputTextView;

    @Bind(R.id.one_layout)
    LinearLayout oneLayout;

    @Bind(R.id.two_layout)
    LinearLayout twoLayout;

    @Bind(R.id.three_layout)
    LinearLayout threeLayout;

    @Bind(R.id.four_layout)
    LinearLayout fourLayout;

    @Bind(R.id.five_layout)
    LinearLayout fiveLayout;

    @Bind(R.id.six_layout)
    LinearLayout sixLayout;

    @Bind(R.id.seven_layout)
    LinearLayout sevenLayout;

    @Bind(R.id.eight_layout)
    LinearLayout eightLayout;

    @Bind(R.id.nine_layout)
    LinearLayout nineLayout;

    @Bind(R.id.zero_layout)
    LinearLayout zeroLayout;

    @Bind(R.id.aestrick_layout)
    LinearLayout aestrickLayout;

    @Bind(R.id.hash_layout)
    LinearLayout hashLayout;

    @Bind(R.id.step_back_image)
    ImageView stepBackImageView;

    @Bind(R.id.call_layout)
    LinearLayout callLayout;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;


}
