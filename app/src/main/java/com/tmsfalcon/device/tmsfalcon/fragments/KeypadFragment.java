package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.RecentCallsTable;
import com.tmsfalcon.device.tmsfalcon.entities.RecentCallsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class KeypadFragment extends Fragment implements View.OnClickListener,View.OnLongClickListener {

    SessionManager session;
    NetworkValidator networkValidator;
    String intent_phone,intent_ext,intent_name;
    public String input_number;
    Bundle bundle;
    RecentCallsTable table;

    public KeypadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keypad, container, false);
        ButterKnife.bind(KeypadFragment.this,view);
        //setContentView(R.layout.activity_contacts);
        table = new RecentCallsTable(getActivity());
        initIds();
        if (getUserVisibleHint()) {

            bundle = this.getArguments();
            Log.e("bundle", String.valueOf(bundle));

            intent_phone = bundle.getString("intent_phone","");
            intent_ext = bundle.getString("intent_ext","");
            intent_name = bundle.getString("intent_name","");
            Log.e("intent data ","intent_phone "+intent_phone+" intent_ext "+intent_ext+" intent_name "+intent_name );
            registerClickListener();
            if(intent_ext != ""){
                inputTextView.setText(" "+intent_phone+"*"+intent_ext);
            }
            else{
                inputTextView.setText(" "+intent_phone);
            }

            dialer_name_textview.setText(""+intent_name);

        }


        if(networkValidator.isNetworkConnected()){
            //getContactsData();
        }
        else {
            Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created


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

        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());

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
                dialer_name_textview.setVisibility(View.GONE);
                break;
            case R.id.call_layout:
                insertData();
                makeCall();
                break;
        }
        //Log.e("input_number ",""+input_number);
        inputTextView.setText(input_number);
    }

    public void insertData(){
        String phoneNumber = inputTextView.getText().toString();
        //Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy HH:mm");
        String currentDateandTime = sdf.format(new Date());
        long inserted_status = table.insertCallRecord(new RecentCallsModel(intent_name,phoneNumber,intent_ext,"outgoing",currentDateandTime.toString()));
        table.showRecords(table);

        if(inserted_status != -1){


        }
        else{
            Toast.makeText(getActivity(),"Record Not Inserted",Toast.LENGTH_LONG).show();
        }
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
        dialer_name_textview.setVisibility(View.GONE);
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

                                Toast.makeText(getActivity(),messages,Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        if(status){
                            inputTextView.setText("");
                            Toast.makeText(getActivity(),"Your call request has been generated.",Toast.LENGTH_LONG).show();
                        }

                        Log.e("Response", response.toString());
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(getActivity(),error);
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

    @Bind(R.id.dialer_name)
    TextView dialer_name_textview;

}
