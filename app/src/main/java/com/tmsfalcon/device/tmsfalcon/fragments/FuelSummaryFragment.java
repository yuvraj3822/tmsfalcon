package com.tmsfalcon.device.tmsfalcon.fragments;


import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.tmsfalcon.device.tmsfalcon.adapters.FuelRebateSummaryAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.FuelSummaryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuelSummaryFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView rebateSummaryListView;
    ProgressBar progressBar;
    FuelRebateSummaryAdapter adapter;
    int is_first_data_call = 1;
    ArrayList<FuelSummaryModel> arrayList = new ArrayList<>();
    TextView no_data_textview;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text,current_month_rebate_textview;
    AppCompatSpinner month_spinner;
    String month_rebate = "";
    LinearLayout month_layout;

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean call_manual = false;
    private boolean manual_call_done = false;
    private int sent_request_offset = 0;

    static final String[] Months = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
    int current_year,current_month;
    String selected_duration;
    int selected_month;

    ArrayList<String> month_list = new ArrayList<>();

    static final String[] YEARS = new String[] { "2016", "2017",
            "2018", "2019", "2020", "2021", "2022"};


    public FuelSummaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_fuel_summary, container, false);
        Calendar c = Calendar.getInstance();
        current_year = c.get(Calendar.YEAR);
        current_month = c.get(Calendar.MONTH);
        for(int i = 0 ; i < Months.length ; i++){
//            month_list.add(Months[i]+","+current_year);
            month_list.add(Months[i]);

        }
        initIds(view);
        selected_month = current_month;
        return view;
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        rebateSummaryListView = view.findViewById(R.id.rebateSummaryListview);
        progressBar = view.findViewById(R.id.progress_bar);
        int colorCodeDark = Color.parseColor("#071528");
        // progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        no_data_textview = view.findViewById(R.id.no_data_textview);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
        current_month_rebate_textview = view.findViewById(R.id.current_month_rebate_textview);
        month_spinner = view.findViewById(R.id.month_spinner);
        month_layout = view.findViewById(R.id.month_layout);

        ArrayAdapter month_adapter = new ArrayAdapter(getActivity(),R.layout.custom_textview_to_spinner,month_list);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        month_spinner.setAdapter(month_adapter);

        month_spinner.setSelection(((ArrayAdapter<String>)month_spinner.getAdapter()).getPosition(Months[current_month]+","+current_year));


        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_duration = month_spinner.getSelectedItem().toString();
                selected_month = i;
                if(arrayList.size() > 0){
                    arrayList.clear();
                }

                loading = false;
                previousTotal = 0;
                currentPage = 0;
                getData(10);
                setScrollEvent();
                Log.e("selected_month",""+selected_month);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void setScrollEvent(){
        rebateSummaryListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                            getData(10);
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
                getData(10);
                setScrollEvent();

            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }

    public void getData(int limit) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "fuel_summary_tag";
        String url = UrlController.FUEL_REBATE_SUMMARY;
        showProgessBar();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", limit);
        params.put("start", arrayList.size());
        params.put("selected_month", selected_month+1);
        params.put("selected_year",/*current_year*/2020);
        Log.e("offset",""+arrayList.size());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONArray fuel_summary_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                sent_request_offset = 0;
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){
                                    fuel_summary_json = data_json.getJSONArray("rebate_summary");
                                    total_count = response.getInt("total_count");
                                    month_rebate = response.getString("month_rebate");

                                    if(fuel_summary_json !=null && fuel_summary_json.length()>0){
                                        no_data_textview.setVisibility(View.GONE);
                                        for(int i = 0; i<fuel_summary_json.length(); i++){
                                            JSONObject jEmpObj = fuel_summary_json.getJSONObject(i);
                                            FuelSummaryModel model = new FuelSummaryModel(jEmpObj.getString("rebate_id"),
                                                    jEmpObj.getString("transaction_number"),
                                                    jEmpObj.getString("fuel_station_name"),
                                                    jEmpObj.getString("transaction_date"),
                                                    jEmpObj.getString("rebate_amount"),
                                                    jEmpObj.getString("rebate_method"),
                                                    jEmpObj.getString("rebate_method_value"),
                                                    jEmpObj.getString("total_gallons"));

                                            arrayList.add(model);
                                        }
                                        //current_month_rebate_textview.setText(Months[selected_month]+" : $"+month_rebate);
                                        current_month_rebate_textview.setText("$"+month_rebate);
                                        footer_layout.setVisibility(View.VISIBLE);
                                        footer_text.setText(setFooterText(arrayList.size(),total_count));
                                        if(is_first_data_call == 1){
                                            adapter = new FuelRebateSummaryAdapter(getActivity(),arrayList);
                                            rebateSummaryListView.setAdapter(adapter);
                                            is_first_data_call = 0;

                                        }
                                        else{
                                            adapter.notifyDataSetChanged();
                                        }

                                    }
                                    else{
                                        if(arrayList.size() == 0){
                                            no_data_textview.setVisibility(View.VISIBLE);
                                            month_layout.setVisibility(View.GONE);
                                            //current_month_rebate_textview.setText("");
                                            footer_layout.setVisibility(View.GONE);
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
