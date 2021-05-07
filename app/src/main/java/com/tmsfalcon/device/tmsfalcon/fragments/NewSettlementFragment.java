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
import com.tmsfalcon.device.tmsfalcon.adapters.SettlementBatchListAdapter;
import com.tmsfalcon.device.tmsfalcon.adapters.SettlementListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.SettlementBatchModel;
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
public class NewSettlementFragment extends Fragment implements SettlementListAdapter.progressCallback{

    SessionManager session;
    NetworkValidator networkValidator;
    ListView docListView;
    SettlementListAdapter documentAdapter;
    SettlementBatchListAdapter adapter;
    ArrayList<SettlementBatchModel> doc_arrayList = new ArrayList<>();
    TextProgressBar textProgressBar;
    LinearLayout progress_layout;
    LinearLayout footer_layout;
    TextView footer_text;
    PermissionManager permissionsManager = new PermissionManager();
    public static final int PERMISSIONS_REQUEST_CODE = 123;
    int is_first_data_call = 1;
    int total_count = 0;
    private int currentPage = 0;



    public NewSettlementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_settlement, container, false);
        ButterKnife.bind(this,view);
        initIds(view);
        checkPermissions();
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){
                fetchData(currentPage);
            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }
        }


        return view;
    }

//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser && isResumed() && (is_first_data_call != 0)) { // fragment is visible and have created
//            if(networkValidator.isNetworkConnected()){
//                fetchData();
//                //setScrollEvent();
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
        adapter = new SettlementBatchListAdapter(getActivity(), doc_arrayList);
        docListView.setAdapter(adapter);

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
        // Tag used to cancel the request
        String tag_json_obj = "trip_settlement_list_tag";
        //String url = UrlController.TRIP_SETTLEMENT_LIST;
        String url = UrlController.SETTLEMENT_BATCHES+"?limit="+20+"&offset="+page;
        Log.e("TAG", "fetchData: "+url);
        showProgessBar();
//        doc_arrayList.clear();


        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        try {
                            status = response.getBoolean("status");
                            JSONArray dataObject = new JSONArray();
                            if(status){
                                dataObject = response.getJSONArray("data");
                                total_count = response.getInt("total_count");
                                if(dataObject != null && dataObject.length() > 0){
                                    for (int i = 0; i < dataObject.length(); i++) {
                                        JSONObject jEmpObj = dataObject.getJSONObject(i);

                                        String batch_id = jEmpObj.getString("settlement_batch_id");
                                        String company_id = jEmpObj.getString("company_id");
                                        String settlement_total = jEmpObj.getString("settlement_total");
                                        String date_settled = jEmpObj.getString("date_settled");
                                        String notes  = jEmpObj.getString("notes");
                                        String batch_status  = jEmpObj.getString("status");
                                        String paid_on = jEmpObj.getString("paid_on");
                                        String csv_id = jEmpObj.getString("batch_csv_id");
                                        String u_id = jEmpObj.getString("u_id");

                                        SettlementBatchModel model = new SettlementBatchModel(batch_id,company_id,date_settled,
                                                                                settlement_total,notes,
                                                                                    batch_status,paid_on,csv_id,u_id);
                                        doc_arrayList.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(doc_arrayList.size(),total_count));
                                    adapter.notifyDataSetChanged();

//                                    if (getActivity()!=null) {
//                                        if (!getActivity().isDestroyed() && isAdded()) {
//                                            //adapter.registerProgressCallback(NewSettlementFragment.this);
//                                        }
//                                    }

                                }
//                                else{
//                                    no_data_textview.setVisibility(View.VISIBLE);
//                                }
                            }

                        } catch (JSONException e) {
                            Log.e("Excetion",": "+e);
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

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
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
