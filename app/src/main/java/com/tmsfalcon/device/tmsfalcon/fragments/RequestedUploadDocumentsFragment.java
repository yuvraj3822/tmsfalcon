package com.tmsfalcon.device.tmsfalcon.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.tmsfalcon.device.tmsfalcon.activities.UploadedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.adapters.RequestedUploadedDocumentsAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
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
public class RequestedUploadDocumentsFragment extends Fragment implements RequestedUploadedDocumentsAdapter.progressCallback{

    SessionManager session;
    PermissionManager permissionsManager = new PermissionManager();
    NetworkValidator networkValidator;
    RequestedUploadedDocumentsAdapter documentAdapter;
    ArrayList<DocumentRequestModel> doc_arrayList = new ArrayList<>();
    public static final int PERMISSIONS_REQUEST_CODE = 123;
    private Context context = getActivity();

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;

    public RequestedUploadDocumentsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_requested_upload_documents, container, false);
        ButterKnife.bind(RequestedUploadDocumentsFragment.this,view);
        initIds(view);

        //checkPermissions();

        if (getUserVisibleHint()) {
            checkInternet(true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkInternet(false);
            }
        });
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() && (is_first_data_call != 0)) { // fragment is visible and have created
            checkInternet(true);

        }
    }

    public void setScrollEvent(){
        docListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        getUploadedDocuments(10,true);
                        loading = true;
                    }
                }
            }
        });
    }

    private void checkInternet(boolean showProgressBar){

        if(networkValidator.isNetworkConnected()){

            getUploadedDocuments(10,showProgressBar);
            setScrollEvent();
        }
        else {
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }
    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
        /*int colorCodeDark = Color.parseColor("#EEEEEE");
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));*/
    }

    private void checkPermissions() {

        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, UploadedDocumentsActivity.PERMISSIONS_REQUEST_CODE);

        } else {

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case UploadedDocumentsActivity.PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkInternet(true);

                } else {
                    Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void getUploadedDocuments(int limit,boolean showProgressBar) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "uploaded_documents_tag";
        String url = UrlController.UPLOADED_DOCUMENTS;
        if(showProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }

        //doc_arrayList.clear();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", limit);
        params.put("offset", doc_arrayList.size());
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
                                if(dataArray != null && dataArray.length()>0){
                                    for(int i = 0; i < dataArray.length(); i++){

                                        JSONObject document_single = (JSONObject) dataArray.get(i);
                                        String docment_id = document_single.getString("document_id");
                                        String document_code = document_single.getString("document_code");
                                        String document_request_id = document_single.getString("document_request_id");
                                        String document_belongs_to = document_single.getString("document_belongs_to");
                                        String documents = document_single.getString("documents");
                                        String title = document_single.getString("title");
                                        String request_document_from_type = document_single.getString("request_document_from_type");
                                        String request_method = document_single.getString("request_method");
                                        String comment = document_single.getString("comment");
                                        String document_due_date = document_single.getString("document_due_date");
                                        String doc_status = document_single.getString("status");
                                        String updated_at = document_single.getString("updated_at");
                                        String u_id = document_single.getString("u_id");

                                        JSONObject view = document_single.getJSONObject("view");
                                        String file_url = view.getString("url");
                                        String file_type = view.getString("type");

                                        DocumentRequestModel model = new DocumentRequestModel(document_code,document_request_id,document_belongs_to,documents,title,
                                                request_document_from_type,request_method,comment,document_due_date,doc_status,docment_id,updated_at,u_id,file_url,file_type);
                                        doc_arrayList.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(doc_arrayList.size(),total_count));
                                    if(is_first_data_call == 1){
                                        documentAdapter = new RequestedUploadedDocumentsAdapter(getActivity(),doc_arrayList);
                                        documentAdapter.registerProgressCallback(RequestedUploadDocumentsFragment.this);
                                        docListView.setAdapter(documentAdapter);

                                        is_first_data_call = 0;
                                    }
                                    else{
                                        documentAdapter.notifyDataSetChanged();
                                    }

                                }
                                else{
                                    if(doc_arrayList.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }
                                    else{
                                        no_data_textview.setVisibility(View.GONE);
                                    }

                                }
                                swipeRefreshLayout.setRefreshing(false);
                                /*if(total_count > doc_arrayList.size()){
                                    makeManualCall();
                                }*/
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
                ErrorHandler.setVolleyMessage(getActivity(),error);
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

    public void makeManualCall(){
        //getUploadedDocuments(100);
    }

    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }

    @Bind(R.id.uploadedDocsListView)
    ListView docListView;
    @Bind(R.id.progress_bar_percent)
    TextProgressBar textProgressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @Bind(R.id.progress_layout)
    LinearLayout progress_layout;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;

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

}
