package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsTabFragmnet extends Fragment {

    String[] values;
    SessionManager session;
    NetworkValidator networkValidator;

    public SettingsTabFragmnet() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_tab_fragmnet, container, false);
        ButterKnife.bind(SettingsTabFragmnet.this,view);
        values = getActivity().getResources().getStringArray(R.array.rc_status_options);
        initIds();
        if (getUserVisibleHint()) {
            setData();
        }

        return view;
    }
    public void setData(){
        if(networkValidator.isNetworkConnected()){
            getRCInfo();
            setSwitchFunction();
        }
        else {
            Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            setData();

        }
    }

    public void getRCInfo(){
        // Tag used to cancel the request
        String tag_json_obj = "get_rc_info";

        String url = UrlController.RINGCENTRAL;

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        int is_account,notification_status;
                        JSONObject userDetailJson,userStatusJson;
                        String name,phone,ext;
                        String call_status,call_dnd_status;
                       // Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            is_account = response.getInt("is_account");
                            if(status){
                                if(is_account == 1){
                                    main_layout.setVisibility(View.VISIBLE);
                                    userDetailJson = response.getJSONObject("user_details");
                                    userStatusJson = response.getJSONObject("user_status");
                                    name = userDetailJson.optString("name");
                                    phone = userDetailJson.optString("businessPhone");
                                    ext = userDetailJson.optString("extensionNumber");
                                    notification_status = response.optInt("notification_status");
                                    if(notification_status == 1){
                                        notification_switch.setChecked(true);
                                        notification_textview.setText("On");
                                    }
                                    else{
                                        notification_switch.setChecked(false);
                                        notification_textview.setText("Off");
                                    }

                                    call_status = userStatusJson.getString("userStatus");
                                    call_dnd_status = userStatusJson.getString("dndStatus");

                                    rc_name_textview.setText(""+name);
                                    rc_phone_textview.setText(""+phone);
                                    rc_extension_textview.setText(""+ext);

                                    if(Objects.equals(call_dnd_status,"DoNotAcceptAnyCalls")){
                                        dnd_switch.setChecked(false);
                                        dnd_type_text.setText(getActivity().getResources().getString(R.string.not_available));
                                        if(getActivity() != null){
                                            status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_dnd));
                                        }

                                    }
                                    else if(Objects.equals(call_dnd_status,"TakeAllCalls")){
                                        dnd_switch.setChecked(true);
                                        if(getActivity() != null){
                                            dnd_type_text.setText(getActivity().getResources().getString(R.string.available));
                                            status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_green));
                                        }

                                    }

                                }
                                else{
                                    main_layout.setVisibility(View.GONE);
                                    no_data_textview.setVisibility(View.VISIBLE);
                                }


                            }
                            else{
                                no_data_textview.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                String stringData = "";
                int is_account;
                NetworkResponse errorRes = error.networkResponse;
                JSONObject obj = null;
                if(errorRes != null && errorRes.data != null){
                    try {
                        stringData = new String(errorRes.data,"UTF-8");
                        obj = new JSONObject(stringData);
                        if(obj != null && obj.length() > 0) {
                            is_account = obj.getInt("is_account");
                            if(is_account == 0){
                                no_data_textview.setVisibility(View.VISIBLE);
                            }

                        }
                    } catch (UnsupportedEncodingException e) {
                        Log.e("exception ", String.valueOf(e));
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


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
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

   /* @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Toast.makeText(getActivity(),
                "selected value " + values[newVal], Toast.LENGTH_SHORT).show();
        status_textview.setText(""+values[newVal]);
        showStatusCircle(newVal);
    }*/

    /*public void showStatusCircle(int value){
        switch (value){
            case 0:
                status_circle_text_layout.setBackground(getActivity().getDrawable(R.drawable.circle_green));
                status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_green));
                dnd_layout.setVisibility(View.GONE);
                break;
            case 1:
                status_circle_text_layout.setBackground(getActivity().getDrawable(R.drawable.circle_red));
                status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_red));
                dnd_layout.setVisibility(View.GONE);
                break;
            case 2:
                status_circle_text_layout.setBackground(getActivity().getDrawable(R.drawable.circle_dnd));
                status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_dnd));
                dnd_layout.setVisibility(View.VISIBLE);
                setSwitchFunction();
                break;
            case 3:
                status_circle_text_layout.setBackground(getActivity().getDrawable(R.drawable.circle_grey));
                status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_grey));
                dnd_layout.setVisibility(View.GONE);
                break;
        }
    }

    public void showRCStatusPicker(){
        RCStatusPicker newFragment = new RCStatusPicker();
        newFragment.setValueChangeListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "time picker");
    }*/

    private void updateUserStatus(boolean switchIsChecked) {

        // Tag used to cancel the request
        String tag_json_obj = "rc_user_status_update";

        String url = UrlController.UPDATE_USER_PRESENCE_STATUS;

        showProgessBar();

        Map<String, String> params = new HashMap<>();
       // params.put("userStatus", "");
        if(switchIsChecked){
            params.put("dndStatus", "TakeAllCalls");
        }
        else{
            params.put("dndStatus", "DoNotAcceptAnyCalls");
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject dataObject;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                dataObject = response.getJSONObject("data");
                                if(dataObject != null && dataObject.length() > 0){
                                    String dndType = dataObject.getString("dndStatus");
                                    if(getActivity() != null){
                                        if(Objects.equals(dndType,"DoNotAcceptAnyCalls")){
                                            dnd_switch.setChecked(false);
                                            dnd_type_text.setText(getActivity().getResources().getString(R.string.not_available));
                                            status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_dnd));
                                        }
                                        else if(Objects.equals(dndType,"TakeAllCalls")){
                                            dnd_switch.setChecked(true);
                                            dnd_type_text.setText(getActivity().getResources().getString(R.string.available));
                                            status_circle_image.setBackground(getActivity().getDrawable(R.drawable.circle_green));

                                        }
                                    }

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
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void updateNotificationStatus(final boolean switchIsChecked) {

        // Tag used to cancel the request
        String tag_json_obj = "rc_notification_status_update";

        String url = UrlController.UPDATE_NOTIFICATION_STATUS;

        showProgessBar();

        Map<String, Integer> params = new HashMap<>();
        // params.put("userStatus", "");
        if(switchIsChecked){
            params.put("notification_status", 1);
        }
        else{
            params.put("notification_status", 0);
        }


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject dataObject;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                notification_switch.setChecked(switchIsChecked);
                                if(switchIsChecked){
                                    notification_textview.setText("On");
                                }
                                else{
                                    notification_textview.setText("Off");
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
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public void setSwitchFunction(){
        dnd_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               updateUserStatus(isChecked);
            }
        });
        notification_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateNotificationStatus(isChecked);
            }
        });

    }

    public void initIds(){

        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());

    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

   /* @OnClick(R.id.status_layout)
    void performFunction(){
        showRCStatusPicker();
    }
    @Bind(R.id.status_text)
    TextView status_textview;

    @Bind(R.id.status_circle_text)
    LinearLayout status_circle_text_layout;*/

    @Bind(R.id.dnd_layout)
    LinearLayout dnd_layout;

    @Bind(R.id.dnd_switch)
    SwitchCompat dnd_switch;

    @Bind(R.id.dnd_type_text)
    TextView dnd_type_text;

    @Bind(R.id.status_circle_image)
    LinearLayout status_circle_image;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @Bind(R.id.main_layout)
    RelativeLayout main_layout;

    @Bind(R.id.rc_name)
    TextView rc_name_textview;

    @Bind(R.id.rc_phone)
    TextView rc_phone_textview;

    @Bind(R.id.rc_extension)
    TextView rc_extension_textview;

    @Bind(R.id.notification_switch)
    SwitchCompat notification_switch;

    @Bind(R.id.notification_text)
    TextView notification_textview;
}
