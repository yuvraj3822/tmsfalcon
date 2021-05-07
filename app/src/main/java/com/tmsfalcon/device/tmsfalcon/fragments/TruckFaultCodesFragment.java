package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.FaultCodeAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.FaultCodeModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TruckFaultCodesFragment extends Fragment {

    int truck_id;
    SessionManager session;
    NetworkValidator networkValidator;
    TextView no_data_textview;
    ProgressBar progressBar;
    FaultCodeAdapter mAdapter;
    ListView listViewFaultCodes;

    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;
    private int sent_request_offset = 0;
    private int currentPage = 1;
    AlertDialog sortByAlertDialog;
    String sort_status = "open";
    RadioButton radioClosed;
    RadioButton radioOpen;
     RadioButton radioAll;

    ArrayList<FaultCodeModel> arrayList = new ArrayList<>();

    public TruckFaultCodesFragment() {
        // Required empty public constructor
    }

    public void refreshVariables(){
        previousTotal = 0;
        loading = true;
       // is_first_data_call = 1;
        total_count = 0;
        sent_request_offset = 0;
        currentPage = 1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_truck_fault_codes, container, false);
        initIds(view);
        ButterKnife.bind(this,view);
        Bundle bundle = getActivity().getIntent().getExtras();
        truck_id = bundle.getInt("truck_id");
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){
                arrayList.clear();
                getFaultCodes(10,"open",false);


            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
        return view;
    }

    public void setScrollEvent(){
        listViewFaultCodes.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        if (sent_request_offset != totalItemCount){
                            getFaultCodes(10,sort_status,false);
                            sent_request_offset = totalItemCount;
                        }
                        loading = true;
                    }
                }
            }
        });
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        no_data_textview = view.findViewById(R.id.no_data_textview);
        progressBar = view.findViewById(R.id.progress_bar);
        listViewFaultCodes = view.findViewById(R.id.listViewFaultCodes);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && (is_first_data_call != 0)) { // fragment is visible and have created
            arrayList.clear();
            if(networkValidator.isNetworkConnected()){
                getFaultCodes(10,"open",false);
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

    private void getFaultCodes(int limit,String status,boolean refresh_data) {
        // Tag used to cancel the request
        String tag_json_obj = "truck_fault_codes_tag";
        String url = UrlController.FAULT_CODES;
        progressBar.setVisibility(View.VISIBLE);

        if(refresh_data){
            arrayList.clear();
            refreshVariables();
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(truck_id));
        params.put("limit", String.valueOf(limit));
        params.put("status", status);
        params.put("page_no", String.valueOf(currentPage));
        Log.e("page_no "," is "+currentPage);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response", response.toString());
                        try {
                            status = response.getBoolean("status");
                            JSONObject dataJson = new JSONObject();
                            if(status){

                                dataJson = response.optJSONObject("data");
                                if(dataJson != null && dataJson.length() > 0){
                                    no_data_textview.setVisibility(View.GONE);
                                    JSONArray faultCodesArray = dataJson.getJSONArray("fault_codes");
                                    total_count = dataJson.getInt("total");
                                    if(faultCodesArray != null && faultCodesArray.length() > 0){
                                        no_data_textview.setVisibility(View.GONE);
                                        for(int i = 0;i<faultCodesArray.length();i++){
                                            JSONObject single_object = (JSONObject) faultCodesArray.get(i);
                                            JSONObject faultCodeObject = single_object.getJSONObject("fault_code");
                                            FaultCodeModel model = new FaultCodeModel(faultCodeObject.getString("code_label"),
                                                    faultCodeObject.getString("code_description"),
                                                    faultCodeObject.getString("source_address_label"),
                                                    faultCodeObject.getString("status"),
                                                    faultCodeObject.getString("type"),
                                                    faultCodeObject.getString("first_observed_at"),
                                                    faultCodeObject.getString("last_observed_at"));
                                            arrayList.add(model);
                                        }
                                        footer_layout.setVisibility(View.VISIBLE);
                                        footer_text.setText(setFooterText(arrayList.size(), (total_count)));
                                        if(is_first_data_call == 1){
                                            mAdapter = new FaultCodeAdapter(getActivity(),arrayList);
                                            listViewFaultCodes.setAdapter(mAdapter);
                                            is_first_data_call = 0;
                                        }
                                        else{
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                    else{
                                        if(arrayList.size() == 0){
                                            no_data_textview.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                else{
                                    if(arrayList.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(getActivity(),error);
                progressBar.setVisibility(View.GONE);

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @OnClick(R.id.sort_icon)
    void openSortDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.filter_sort_by_dialog, null);
        dialogBuilder.setView(dialogView);

       // final EditText subject = dialogView.findViewById(R.id.subject);
        radioClosed = dialogView.findViewById(R.id.radio_closed);
        radioOpen = dialogView.findViewById(R.id.radio_open);
        radioAll = dialogView.findViewById(R.id.radio_all);

        if (sort_status.equals("closed")){
            radioClosed.setChecked(true);
            radioOpen.setChecked(false);
            radioAll.setChecked(false);

        } else if (sort_status.equals("open")){
            radioOpen.setChecked(true);
            radioClosed.setChecked(false);
            radioAll.setChecked(false);

        } else {
            radioAll.setChecked(true);
            radioOpen.setChecked(false);
            radioClosed.setChecked(false);
        }

//
//        if(sort_status != "" && sort_status.equals("closed")){
//            radioClosed.setChecked(true);
//        }
//        else if(sort_status != "" && sort_status.equals("open")){
//            radioOpen.setChecked(true);
//        } else if (sort_status != "" && sort_status.equals("")){
//
//        }
//        else{
//            radioAll.setChecked(true);
//        }

        radioClosed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioAll.setChecked(false);
                    radioOpen.setChecked(false);
                    sortByAlertDialog.dismiss();
                    sort_status = "closed";
                    getFaultCodes(10,sort_status,true);

                }
            }
        });
        radioOpen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioAll.setChecked(false);
                    radioClosed.setChecked(false);
                    sortByAlertDialog.dismiss();
                    sort_status = "open";
                    getFaultCodes(10,sort_status,true);
                }
            }
        });
        radioAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    radioClosed.setChecked(false);
                    radioOpen.setChecked(false);
                    sortByAlertDialog.dismiss();
                    sort_status = "";
                    getFaultCodes(10,sort_status,true);
                }
            }
        });

        sortByAlertDialog = dialogBuilder.create();
        sortByAlertDialog.show();

    }

}
