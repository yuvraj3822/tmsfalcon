package com.tmsfalcon.device.tmsfalcon.fragments;


import android.app.Activity;
import android.content.Context;
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
import com.tmsfalcon.device.tmsfalcon.adapters.HosViolationAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.HosViolationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class HosViolationFragment extends Fragment {

    NetworkValidator networkValidator;
    @SuppressWarnings("unused")
    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    Context context;
    JSONArray violationJsonArray;
    ListView listViewHosViolation;
    HosViolationAdapter mAdapter;
    TextView no_data_textview;
    ProgressBar progressBar;

    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;
    private boolean call_manual = false;
    private boolean manual_call_done = false;
    private int sent_request_offset = 0;
    private int currentPage = 0;

    ArrayList<HosViolationModel> arrayList = new ArrayList<>();


    public HosViolationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_hos_violation, container, false);
        initIds(view);
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){
                arrayList.clear();
                getViolationRecords(10);


            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
        return view;
    }

    public void setScrollEvent(){
        listViewHosViolation.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                            getViolationRecords(10);
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
            arrayList.clear();
            if(networkValidator.isNetworkConnected()){
                getViolationRecords(10);
                setScrollEvent();
            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }

    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }

    private void getViolationRecords(int limit){
        currentPage += 1;
        String tag_json_obj = "violation_tag";
        String url = UrlController.HOS_VIOLATION;
        showProgessBar();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", limit);
        params.put("page_no", arrayList.size());
        Log.e("session ",SessionManager.getInstance().get_token());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");

                            if(status){
                                sent_request_offset = 0;
                                data_json = response.getJSONObject("data");

                                if(data_json !=null && data_json.length()>0){
                                    JSONArray hos_violationsJson = data_json.getJSONArray("hos_violations");
                                    JSONObject paginationJson = data_json.getJSONObject("pagination");
                                    total_count = paginationJson.getInt("total");

                                    if(hos_violationsJson !=null && hos_violationsJson.length()>0){
                                        for(int i = 0; i<hos_violationsJson.length(); i++){
                                            JSONObject jEmpObj = hos_violationsJson.getJSONObject(i);
                                            JSONObject violation_json = jEmpObj.getJSONObject("hos_violation");
                                            HosViolationModel model = new HosViolationModel(violation_json.getString("name"),
                                                    violation_json.getString("start_time"),
                                                    violation_json.getString("end_time"));
                                            arrayList.add(model);
                                            //Log.e("id",""+jEmpObj.getInt("trip_id")+" size : "+arrayList.size());
                                        }
                                        footer_layout.setVisibility(View.VISIBLE);
                                        footer_text.setText(setFooterText(arrayList.size(),total_count));
                                        if(is_first_data_call == 1){
                                            mAdapter = new HosViolationAdapter(getActivity(),arrayList);
                                            listViewHosViolation.setAdapter(mAdapter);
                                            is_first_data_call = 0;
                                            call_manual = true;
                                        }
                                        else{
                                            mAdapter.notifyDataSetChanged();
                                            manual_call_done = true;
                                        }

                                    }
                                    else{
                                        if(arrayList.size() == 0){
                                            no_data_textview.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    if(total_count > arrayList.size() && call_manual == true){
                                        //makeManualCall();
                                        call_manual = false;
                                    }
                                }
                                else{
                                    no_data_textview.setVisibility(View.VISIBLE);
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

    private void initIds(View view){
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        listViewHosViolation = view.findViewById(R.id.listViewHosViolation);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        progressBar = view.findViewById(R.id.progress_bar);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

}
