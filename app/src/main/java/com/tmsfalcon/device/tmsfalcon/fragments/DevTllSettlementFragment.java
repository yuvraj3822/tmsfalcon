package com.tmsfalcon.device.tmsfalcon.fragments;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
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
import com.tmsfalcon.device.tmsfalcon.adapters.SettlementListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.SettlementModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevTllSettlementFragment extends Fragment implements SettlementListAdapter.progressCallback{

    SessionManager session;
    NetworkValidator networkValidator;
    ListView docListView;
    SettlementListAdapter documentAdapter;
    ArrayList<SettlementModel> doc_arrayList = new ArrayList<>();
    TextProgressBar textProgressBar;
    LinearLayout progress_layout;
    PermissionManager permissionsManager = new PermissionManager();
    public static final int PERMISSIONS_REQUEST_CODE = 123;
    int is_first_data_call = 1;

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

    public DevTllSettlementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_dev_tll_settlement, container, false);
        ButterKnife.bind(this,view);
        initIds(view);
        checkPermissions();

        return view;
    }

//    public void setScrollEvent(){
//        docListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                //Log.e("in", "firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount);
//                int lastInScreen = firstVisibleItem + visibleItemCount;
//                if (loading) {
//                    if (totalItemCount > previousTotal) {
//                        loading = false;
//                        previousTotal = totalItemCount;
//                        currentPage++;
//                    }
//                }
//                if (!loading && (lastInScreen == totalItemCount)) {
//                    if(is_first_data_call != 1){
//
//                        if (sent_request_offset != totalItemCount){
//                            fetchData();
//                            sent_request_offset = totalItemCount;
//                        }
//                        loading = true;
//                    }
//                }
//
//            }
//        });
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && isResumed() && (is_first_data_call != 0)) { // fragment is visible and have created
//            if(networkValidator.isNetworkConnected()){
//                fetchData();
//                setScrollEvent();
//
//            }
//            else {
//                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
//            }
//
//        }
//    }


    private void checkPermissions() {

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSIONS_REQUEST_CODE);
        } else {
            fetchData(currentPage);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if(networkValidator.isNetworkConnected()){
                        fetchData(currentPage);
                    }
                    else {
                        Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void initIds(View view){

        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        docListView = view.findViewById(R.id.listViewSettlements);

        progress_layout = view.findViewById(R.id.progress_layout);
        textProgressBar = view.findViewById(R.id.progress_bar_percent);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);

        documentAdapter = new SettlementListAdapter(getActivity(),doc_arrayList);
        docListView.setAdapter(documentAdapter);
        docListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                Log.e("onScroll ", ": " + i + ", " + i1 + ", " + i2);
                if (doc_arrayList.size() < total_count) {
                    if (i != 0) {
                        int value = i + i1;
                        if (value == i2) {
//                            if ()
                            if (!progressBar.isShown()) {
                                fetchData(currentPage++);
                            }
                        }
                    }
                }
            }
        });

    }

    private void fetchData(int page) {

        currentPage += 1;
        // Tag used to cancel the request
        String tag_json_obj = "dev_tll_settlement_list_tag";
        int start = doc_arrayList.size();
        int length = 20;
        String url = UrlController.DEV_TLL_SETTLEMENT_LIST+"?driver_id="+session.get_driver_uid()+"&start="+start+"&length="+length;

        Log.e("url old setlements",""+url);
        showProgessBar();
        //doc_arrayList.clear();

        Map<String, Integer> params = new HashMap<>();
        params.put("driver_id", (session.get_driver_id()));
        params.put("start",start);
        params.put("length",length);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url,null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("in","onresponse");
                        Boolean status = null;

                        Log.e("Response", response.toString());
                        try {
                            sent_request_offset = 0;
                            //status = response.getBoolean("status");
                            JSONArray dataObject = new JSONArray();
                            dataObject = response.getJSONArray("data");
                            total_count = response.getInt("recordsTotal");

                            if(dataObject != null && dataObject.length() > 0){
                                for (int i = 0; i < dataObject.length(); i++) {
                                    JSONObject jEmpObj = dataObject.getJSONObject(i);

                                    int batch_id = jEmpObj.getInt("batch_id");
                                    String total_settled_amount = jEmpObj.getString("amount");
                                    String download_url;

                                    String settlement_date  = jEmpObj.getString("settlement_date");
                                    boolean fexist = jEmpObj.getBoolean("fexist");
                                    if(fexist){
                                        download_url  = jEmpObj.getString("url_pdf");
                                    }
                                    else{
                                        download_url = "false";
                                    }
                                    String thumb = "http:\\/\\/via.placeholder.com\\/1200x1600";

                                    SettlementModel model = new SettlementModel(batch_id,batch_id,total_settled_amount,settlement_date,download_url,thumb);
                                    doc_arrayList.add(model);

                                }
                                footer_layout.setVisibility(View.VISIBLE);
                                footer_text.setText(setFooterText(doc_arrayList.size(),total_count));
                                documentAdapter.notifyDataSetChanged();


//                                if(is_first_data_call == 1){
//                                    documentAdapter = new SettlementListAdapter(getActivity(),doc_arrayList);
//                                    documentAdapter.registerProgressCallback(DevTllSettlementFragment.this);
//                                    docListView.setAdapter(documentAdapter);
//                                    is_first_data_call = 0;
//                                    call_manual = true;
//                                }
//                                else{
//                                    documentAdapter.notifyDataSetChanged();
//                                    manual_call_done = true;
//                                }


                            }
//                            else{
//                                if(doc_arrayList.size() == 0){
//                                    no_data_textview.setVisibility(View.VISIBLE);
//                                }
//                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //ErrorHandler.setVolleyMessage(getActivity(),error);
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


    @Override
    public void showProgressCallback() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgressCallback() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textProgressBar != null){
                    textProgressBar.setProgress(0);
                    textProgressBar.setText(""+0+" %");
                    progress_layout.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public void progressUpdate(final int percentage) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textProgressBar != null){
                    textProgressBar.setProgress(percentage);
                    textProgressBar.setText(""+percentage+" %");
                }

            }
        });
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

}
