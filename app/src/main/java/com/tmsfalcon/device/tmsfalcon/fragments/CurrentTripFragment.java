package com.tmsfalcon.device.tmsfalcon.fragments;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.TripAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.TripModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentTripFragment extends Fragment {


    public CurrentTripFragment() {
        // Required empty public constructor
    }


    SessionManager session;
    NetworkValidator networkValidator;
    ListView tripsListView;
    ProgressBar progressBar;
    TripAdapter tripAdapter;
    int is_first_data_call = 1;
    ArrayList<TripModel> arrayList = new ArrayList<>();
    TextView no_data_textview;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean call_manual = false;
    private boolean manual_call_done = false;
    private int sent_request_offset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_current_trip, container, false);
        initIds(view);
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){
                getTripsList(10);
            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
        return view;
    }

    public void setScrollEvent(){
        tripsListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                    if(is_first_data_call != 1&& manual_call_done == true){

                        if (sent_request_offset != totalItemCount){
                            getTripsList(10);
                            sent_request_offset = totalItemCount;
                        }
                        loading = true;
                    }
                }
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && (is_first_data_call != 0)) { // fragment is visible and have created
            if(networkValidator.isNetworkConnected()){
                getTripsList(10);
                //setScrollEvent();
            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }


    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        tripsListView = view.findViewById(R.id.currentTripsListView);
        progressBar = view.findViewById(R.id.progress_bar);
        int colorCodeDark = Color.parseColor("#071528");
        //progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        no_data_textview = view.findViewById(R.id.no_data_textview);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
    }

    public void getTripsList(int limit) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "current_trips_list";
        String url = UrlController.CURRENT_TRIPS;
        showProgessBar();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", 0);
//        Todo because in currentl trip means 1 thats why changed size to 1
//        params.put("offset", arrayList.size());
        params.put("offset", 0);

        //Log.e("offset",""+arrayList.size());
        Log.e("Request: - ",": "+params.toString());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray data_json ;
                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                arrayList.clear();
                                sent_request_offset = 0;
                                data_json = response.getJSONArray("data");
                                total_count = response.getInt("total");

                                if(data_json !=null && data_json.length()>0){
                                    for(int i = 0; i<data_json.length(); i++){
                                        JSONObject jEmpObj = data_json.getJSONObject(i);
                                        TripModel model = new TripModel(jEmpObj.getString("start_location"),
                                                jEmpObj.getString("end_location"),
                                                jEmpObj.getInt("trip_id"),
                                                jEmpObj.getInt("stops"),
                                                jEmpObj.getInt("loads"),
                                                jEmpObj.getString("type"),
                                                jEmpObj.getString("trip_number"),
                                                jEmpObj.getDouble("distance"),
                                                jEmpObj.getString("start_date"),
                                                jEmpObj.getString("end_date"));
                                        arrayList.add(model);
                                        //Log.e("id",""+jEmpObj.getInt("trip_id")+" size : "+arrayList.size());
                                    }
                                     footer_layout.setVisibility(View.GONE);
                                    tripAdapter = new TripAdapter(getActivity(),arrayList,"current_trips");
                                    tripsListView.setAdapter(tripAdapter);
                                    /*footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(arrayList.size(),total_count));
                                    if(is_first_data_call == 1){
                                        tripAdapter = new TripAdapter(getActivity(),arrayList);
                                        tripsListView.setAdapter(tripAdapter);
                                        is_first_data_call = 0;
                                        call_manual = true;
                                    }
                                    else{
                                        tripAdapter.notifyDataSetChanged();
                                        manual_call_done = true;
                                    }*/

                                }
                                else{
                                    if(arrayList.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }
                                }
                               /* if(total_count > arrayList.size() && call_manual == true){
                                    makeManualCall();
                                    call_manual = false;
                                }*/

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
        //jsonObjReq.setShouldCache(false);
    }

    public void makeManualCall(){
        getTripsList(100);

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

}
