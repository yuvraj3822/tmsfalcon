package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
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
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GetCashActivity extends NavigationBaseActivity {

    ArrayList load_arrayList = new ArrayList();
    ArrayList cash_for_arrayList = new ArrayList();
    private ClipboardManager myClipboard;
    private ClipData myClip;
    SessionManager session;
    NetworkValidator networkValidator;
    boolean isFormValid ;
    CustomValidator customValidator;
    String current_trip_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_get_cash, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        initIds();

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if(networkValidator.isNetworkConnected()){
            getOptions();
        }
        else {
            Toast.makeText(GetCashActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }



        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int progress_value = seekBar.getProgress();
                seek_bar_progress_textview.setText(""+progress_value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        load_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("result: "," ");
                current_trip_id = (String) tripHash.get(adapterView.getItemAtPosition(i));
                current_trip_id.trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void initIds(){
        networkValidator = new NetworkValidator(GetCashActivity.this);
        session = new SessionManager(GetCashActivity.this);
        customValidator = new CustomValidator(GetCashActivity.this);
    }

    public void requestCashApi(){
        String tag_json_obj = "get_cash";
        String url = UrlController.GET_CASH;



        Map<String, String> params = new HashMap<>();
        params.put("cash_for",cash_for_spinner.getSelectedItem().toString());
        params.put("trip_id",current_trip_id);
        params.put("amount",""+seekBar.getProgress());

        showProgessBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response "," is "+response);
                        Boolean status = null;
                        JSONObject data_json;
                        hideProgressBar();
                        try {
                            status = response.getBoolean("status");
                            //collapseAllViews();
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){

                                    String comdata_code = data_json.getString("code");
                                    if(comdata_code != "" && comdata_code != null){
                                        comdata_code_textview.setText(comdata_code);
                                        comdata_layout.setVisibility(View.VISIBLE);
                                    } else{
                                        String errors = "";
                                        JSONArray json_errors = response.optJSONArray("messages");
                                        if(json_errors != null){
                                            for(int i = 0 ; i < json_errors.length(); i++){
                                                errors += json_errors.get(i)+"\n";
                                            }
                                        }
                                        Toast.makeText(GetCashActivity.this,errors,Toast.LENGTH_LONG).show();
                                    }

                                }
                                else{


                                }

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("in","error response");
                ErrorHandler.setVolleyMessage(GetCashActivity.this,error);
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

//    private ArrayList demoTripList(){
//        ArrayList list = new ArrayList();
//        list.add("1235");
//        list.add("475586");
//        list.add("87646");
//        list.add("5354343");
//        list.add("55454");
//        return list;
//    }

    ArrayList tripNoList = new ArrayList();
    HashMap tripHash = new HashMap();

    private void createTripList(String trip,String tripNo){
//        tripNoList.add("# TRIP-"+tripNo);
        tripHash.put("# TRIP-"+tripNo,trip);

    }

    public void getOptions(){
        Log.e("called","called");
        tripNoList.clear();
        String tag_json_obj = "get_cash_options";
        String url = UrlController.GET_CASH_OPTIONS;

        showProgessBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response "," is "+response);
                        Boolean status = null;
                        JSONObject data_json;
                        hideProgressBar();
                        try {
                            status = response.getBoolean("status");
                            //collapseAllViews();
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){

                                    mainlinearLayout.setVisibility(View.VISIBLE);

                                    current_trip_id = data_json.getString("trip");

//                                    load_arrayList.add("# TRIP-"+current_trip_id);
//                                    load_arrayList.addAll(demoTripList());




                                    current_trip_id = data_json.getString("trip");
                                    JSONArray jsonArray = data_json.getJSONArray("trips");

                                    for (int i = 0; i < jsonArray.length(); i++ ){

                                        String trip = jsonArray.getJSONObject(i).getString("trip");
                                        String tripNo = jsonArray.getJSONObject(i).getString("trip_number");
                                        createTripList(tripNo,trip);

                                    }


//                                    load_arrayList.add("# TRIP-"+current_trip_id);



                                    load_arrayList.addAll(tripHash.keySet());

                                    cash_for_arrayList.add(""+data_json.getString("cash_for"));







                                    seekBar.setMax(data_json.getInt("max_limit"));
                                    seekBar.setProgress(data_json.getInt("min_limit"));
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        //seekBar.setMin(data_json.getInt("min_limit"));
                                    }

                                    ArrayAdapter load_adapter = new ArrayAdapter(GetCashActivity.this,R.layout.custom_textview_to_spinner,load_arrayList);
                                    load_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Setting the ArrayAdapter data on the Spinner
                                    load_spinner.setAdapter(load_adapter);

                                    ArrayAdapter cash_for_adapter = new ArrayAdapter(GetCashActivity.this,R.layout.custom_textview_to_spinner,cash_for_arrayList);
                                    cash_for_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Setting the ArrayAdapter data on the Spinner
                                    cash_for_spinner.setAdapter(cash_for_adapter);

                                }
                                else{
                                    mainlinearLayout.setVisibility(View.GONE);
                                    no_trip_textview.setVisibility(View.VISIBLE);
                                    no_trip_textview.setText("No Current Trip was Found to request Cash.");

                                }

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorHandler.setVolleyMessage(GetCashActivity.this,error);
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


    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.load_spinner)
    AppCompatSpinner load_spinner;

    @Bind(R.id.cash_for_spinner)
    AppCompatSpinner cash_for_spinner;

    @Bind(R.id.comdata_code)
    TextView comdata_code_textview;

    @Bind(R.id.seek_bar)
    AppCompatSeekBar seekBar;

    @Bind(R.id.seek_bar_progress_textview)
    TextView seek_bar_progress_textview;

    @Bind(R.id.main_layout)
    LinearLayout mainlinearLayout;

    @Bind(R.id.no_data_textview)
    TextView no_trip_textview;

    @Bind(R.id.comdata_layout)
    LinearLayout comdata_layout;

    @OnClick(R.id.comdata_code)
    void copyText(){
        String textview_text = comdata_code_textview.getText().toString();
        myClip = ClipData.newPlainText("text", textview_text);
        myClipboard.setPrimaryClip(myClip);

        Toast.makeText(getApplicationContext(), "Text Copied to Clipboard.",
                Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.history_icon)
    void openHistoryActivity(){
        Intent i = new Intent(GetCashActivity.this,GetCashHistory.class);
        startActivity(i);
    }

    @OnClick(R.id.history_text)
    void openActivity(){
        Intent i = new Intent(GetCashActivity.this,GetCashHistory.class);
        startActivity(i);
    }

    @OnClick(R.id.get_cash_btn)
    void getCash(){
        //validateForm();
        if(networkValidator.isNetworkConnected()){
            requestCashApi();
            /*if(isFormValid){
                requestCashApi();
            }*/
        }
        else {
            Toast.makeText(GetCashActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }

    }
}
