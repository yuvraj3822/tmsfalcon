package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.os.Bundle;
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
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.ProgressIntroScreen;
import com.tmsfalcon.device.tmsfalcon.adapters.ReportedAccidentListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.ReportedAccidentListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReportedAccidentList extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView accidentListView;
    ReportedAccidentListAdapter mAdapter;
    ArrayList<ReportedAccidentListModel> arrayList_accidents = new ArrayList<>();

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
        View contentView = inflater.inflate(R.layout.activity_reported_accident_list, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        checkInternet(true);

    }

    public void setScrollEvent(){
        accidentListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        getAccidentData(true);
                        loading = true;
                    }
                }
            }
        });
    }

    private void getAccidentData(boolean showProgressBar) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "get_accident_records_tag";
        String url = UrlController.ACCIDENT_RECORDS;
        if(showProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }

        //doc_arrayList.clear();
        Map<String, String> params = new HashMap<>();
        params.put("length", "10");
        params.put("start", String.valueOf(arrayList_accidents.size()));

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
                                dataArray = response.getJSONArray("accidents");
                                total_count = response.getInt("total_count");
                                // System.out.print(response.getString("q"));

                                if(dataArray != null && dataArray.length()>0){

                                    for(int i = 0; i < dataArray.length(); i++){

                                        JSONObject document_single = (JSONObject) dataArray.get(i);
                                        String accident_date = document_single.getString("accident_date");
                                        String accident_time = document_single.getString("accident_time");
                                        String view_url = document_single.getString("view_url");
                                        String location_name = document_single.getString("location_name");
                                        String accident_status = document_single.getString("accident_status");
                                        String accident_id = document_single.getString("accident_id");

                                        ReportedAccidentListModel model = new ReportedAccidentListModel(accident_id,accident_date,accident_time,location_name,accident_status,view_url);
                                        arrayList_accidents.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(arrayList_accidents.size(),total_count));
                                    if(is_first_data_call == 1){
                                        mAdapter = new ReportedAccidentListAdapter(ReportedAccidentList.this,arrayList_accidents);
                                        accidentListView.setAdapter(mAdapter);

                                        is_first_data_call = 0;
                                    }
                                    else{
                                        mAdapter.notifyDataSetChanged();
                                    }


                                }
                                else{
                                    if(arrayList_accidents.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }

                                }

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
                ErrorHandler.setVolleyMessage(ReportedAccidentList.this,error);
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

    private void checkInternet(boolean showProgressBar){

        if(networkValidator.isNetworkConnected()){
            getAccidentData(showProgressBar);
            setScrollEvent();
        }
        else {
            Toast.makeText(ReportedAccidentList.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }

    public void initIds(){

        networkValidator = new NetworkValidator(ReportedAccidentList.this);
        session = new SessionManager(ReportedAccidentList.this);
        accidentListView = findViewById(R.id.listViewAccident);
        footer_layout = findViewById(R.id.footer_layout);
        footer_text = findViewById(R.id.footer_text);

    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @OnClick(R.id.fab)
    void onClickFab(){
        Intent i = new Intent(ReportedAccidentList.this, ProgressIntroScreen.class);
        startActivity(i);
    }
}
