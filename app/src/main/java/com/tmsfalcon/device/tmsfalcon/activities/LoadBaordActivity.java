package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.tmsfalcon.device.tmsfalcon.adapters.LoadBoardListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.SuggestedLoadsModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadBaordActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView loadListView;
    LoadBoardListAdapter mAdapter;
    ArrayList<SuggestedLoadsModel> arrayList_load = new ArrayList<>();

    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_load_baord, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("action_load_board_adapter"));
        initIds();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternet(false);
            }
        });
        checkInternet(true);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("action");
            Log.e("action","is "+message);
            if(message != null && message.equals("show_progress_bar")){
                showProgessBar();
            }
            else if(message != null &&  message.equals("hide_progress_bar")){
                hideProgressBar();
            }
            /*else if(message != null &&  message.equals("notify_data_set_changed")){
                mAdapter.notifyDataSetChanged();
            }*/
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    public void setScrollEvent(){
        loadListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.e("in", "firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount);
                int lastInScreen = firstVisibleItem + visibleItemCount;

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        currentPage++;
                    }
                }
                if (!loading && (lastInScreen == totalItemCount)) {
                    if(is_first_data_call != 1){
                        getLoadData(true);
                        loading = true;
                    }
                }
            }
        });
    }

    private void checkInternet(boolean showProgressBar){

        if(networkValidator.isNetworkConnected()){
            getLoadData(showProgressBar);
            setScrollEvent();
        }
        else {
            Toast.makeText(LoadBaordActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    private void getLoadData(boolean showProgressBar) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "get_suggested_loads_tag";
        String url = UrlController.FETCH_SUGGESTED_LOADS;
        if(showProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }

        //doc_arrayList.clear();
        Map<String, String> params = new HashMap<>();
        params.put("length", "10");
        params.put("start", String.valueOf(arrayList_load.size()));

        /*if(session.get_mark_available() != null && session.get_mark_available().equals("true")){
            Log.e("in","if yes");
            params.put("mark_available","1");
        }
        else{
            Log.e("in","else yes");
            params.put("mark_available","0");
        }*/
        params.put("mark_available","1");
        params.put("lat",session.getKeyCurrentLatitude());
        params.put("long",session.getKeyCurrentLongitude());
        params.put("trailer_type",session.get_trailer_type());
        params.put("max_weight",session.get_trailer_weight());
        params.put("trailer_length",session.get_trailer_length());
        params.put("trailer_width",session.get_trailer_width());
        params.put("origin_state",session.get_origin_state());
        params.put("origin_zipcode",session.get_origin_zipcode());
        params.put("dispatcher_name",session.get_agent());
        params.put("dispatcher_id",session.get_agent_id());
        params.put("pickup_date",session.get_origin_pickup_date());
        params.put("pickup_end_date",session.get_origin_pickup_enddate());
        params.put("delivery_date",session.get_delivery_date());
        params.put("delivery_end_date",session.get_delivery_enddate());
        params.put("origin_radius",session.get_origin_radius());
        params.put("destination_radius",session.get_destination_radius());
        params.put("max_miles",session.get_max_miles());
        params.put("min_per_mile",session.get_min_per_mile());
        params.put("load_type",session.get_load_type());
        params.put("post_age",session.get_post_age());
        if(session.get_display_load_not_having_rate() != null && session.get_display_load_not_having_rate() == "true"){
            params.put("display_loads_not_having_rate","1");
        }
        else{
            params.put("display_loads_not_having_rate","0");
        }
        params.put("destination_zipcode",session.get_destination_zipcode());
        params.put("destination_state", (Utils.getDestinationStates(session.get_destination_location_arraylist())).toString());
        params.put("destination_city", (Utils.getDestinationCities(session.get_destination_location_arraylist())).toString());
        Log.e("destination_zipcode"," is "+session.get_destination_zipcode());
        Log.e("params"," "+params);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response", response.toString());
                        try {
                            status = response.getBoolean("status");
                            JSONArray dataArray = new JSONArray();
                            if(status){
                                dataArray = response.getJSONArray("data");
                                total_count = response.getInt("total");
                               // System.out.print(response.getString("q"));

                                if(dataArray != null && dataArray.length()>0){
                                    for(int i = 0; i < dataArray.length(); i++){

                                        JSONObject document_single = (JSONObject) dataArray.get(i);
                                        String call_log_id = document_single.getString("calllog_id");
                                        String origin = document_single.getString("origin");
                                        String destination = document_single.getString("destination");
                                        String price = document_single.getString("price");
                                        String pm = document_single.getString("pm");
                                        String miles = document_single.getString("miles");
                                        String call_log_status = document_single.getString("status");
                                        String my_response = document_single.getString("my_response");
                                        String public_link = document_single.getString("public_link");
                                        boolean is_action_visible = document_single.getBoolean("is_action_visible");
                                        boolean is_awarded = document_single.getBoolean("is_awarded");


                                        SuggestedLoadsModel model = new SuggestedLoadsModel(call_log_id,origin,destination,price,pm,miles,call_log_status
                                                ,my_response,public_link,is_action_visible,is_awarded);
                                        arrayList_load.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(arrayList_load.size(),total_count));
                                    if(is_first_data_call == 1){
                                        mAdapter = new LoadBoardListAdapter(LoadBaordActivity.this,arrayList_load,loadListView);
                                        loadListView.setAdapter(mAdapter);

                                        is_first_data_call = 0;
                                    }
                                    else{
                                        mAdapter.notifyDataSetChanged();
                                    }

                                }
                                else{
                                    if(arrayList_load.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }

                                }
                                swipeRefreshLayout.setRefreshing(false);

                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(LoadBaordActivity.this,error);
                if(progressBar != null){
                    progressBar.setVisibility(View.GONE);
                }

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


    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void initIds(){

        networkValidator = new NetworkValidator(LoadBaordActivity.this);
        session = new SessionManager(LoadBaordActivity.this);
        loadListView = findViewById(R.id.listViewLoad);
        footer_layout = findViewById(R.id.footer_layout);
        footer_text = findViewById(R.id.footer_text);

    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

    @OnClick(R.id.load_settings_icon)
    void openSettings(){
        Intent i = new Intent(LoadBaordActivity.this,LoadBoardPreferences.class);
        startActivity(i);

    }

}
