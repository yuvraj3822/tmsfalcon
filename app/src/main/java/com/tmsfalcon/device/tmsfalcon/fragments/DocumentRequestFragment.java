package com.tmsfalcon.device.tmsfalcon.fragments;


import android.content.Context;
import android.content.Intent;
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
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.adapters.DocumentRequestAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.PermissionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
import com.tmsfalcon.device.tmsfalcon.interfacess.requestDocumentClick;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static butterknife.ButterKnife.Finder.arrayOf;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentRequestFragment extends Fragment implements DocumentRequestAdapter.progressCallback, requestDocumentClick {

    SessionManager session;
    PermissionManager permissionsManager = new PermissionManager();
    NetworkValidator networkValidator;
    DocumentRequestAdapter documentAdapter;
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

    private int REQUEST_CAMERA_PERMISSION = 0;


    public DocumentRequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_document_request, container, false);
        ButterKnife.bind(DocumentRequestFragment.this,view);

        initIds(view);
        if (getUserVisibleHint()) {
            checkInternet(true);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
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

    public void checkInternet(boolean showProgressBar){
        if(networkValidator.isNetworkConnected()){
            getDocumentRequests(10,showProgressBar);
            setScrollEvent();
        }
        else {
            Toast.makeText(getActivity(),getActivity().getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }



    private Boolean  initPermission(){

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION);
            return false;
        } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION);
            return false;
        } else if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CAMERA_PERMISSION);
            return false;
        }

        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION){
            grantPermisions(grantResults);
        }

    }


    private void  grantPermisions(int[] grantResults ){

        if(grantResults.length == 2) {
            if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Log.e("Granted", "Granted");
//                selectImage(this@TestDashboardActivity)

                Intent i = new Intent(getActivity(), TestCameraScreen.class);
                startActivity(i);

            } else {
                Toast.makeText(getActivity(), "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show();
            }
        }else{
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e("Granted", "Granted");
                //  selectImage(this@TestDashboardActivity)
                Intent i = new Intent(getActivity(), TestCameraScreen.class);
                startActivity(i);

            }else {
                Toast.makeText(getActivity(), "Please provide the permission to access Camera", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void reqClick(DocumentRequestModel model) {

            if (initPermission()){
                Intent goToCapture = new Intent(getActivity(), TestCameraScreen.class);

                goToCapture.putExtra("document_request_id",model.getDocument_request_id());
                goToCapture.putExtra("document_type",model.getDocument_code());
                goToCapture.putExtra("document_name",model.getDocuments());
                goToCapture.putExtra("load_number",model.getTitle());
                goToCapture.putExtra("due_date",model.getDocument_due_date());
                goToCapture.putExtra("comment",model.getComment());
                goToCapture.putExtra("status",model.getStatus());
                goToCapture.putExtra("key",model.getKey());
                goToCapture.putExtra("is_expired","0");
                goToCapture.putExtra("document_belongs_to",model.getDocument_belongs_to());

                startActivity(goToCapture);

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
                        getDocumentRequests(10,true);
                        loading = true;
                    }
                }
            }
        });
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
    }

    /*@SuppressWarnings("unused")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(networkValidator.isNetworkConnected()){
                        getDocumentRequests(10);
                    }
                    else {
                        Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, "Allow external storage reading", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }*/
    private void getDocumentRequests(int limit,boolean showProgressBar) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "document_requests_tag";
        String url = UrlController.DOCUMENTS_REQUESTS;

        if(showProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }

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
                                if(dataArray != null && dataArray.length() >0 ){
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
                                        String key = document_single.getString("key");
                                        String rejected_reason = document_single.getString("reason");
                                        //JSONObject json_previous_doc = document_single.getJSONObject("view");
                                        String previous_doc_url = "";
                                        String previous_doc_type = "";
                                        if(document_single.getJSONObject("view") != null && document_single.getJSONObject("view").length() > 0){
                                            JSONObject json_previous_doc = document_single.getJSONObject("view");
                                            previous_doc_url = json_previous_doc.getString("url");
                                            previous_doc_type = json_previous_doc.getString("type");
                                        }

                                        DocumentRequestModel model = new DocumentRequestModel(document_code,document_request_id,document_belongs_to,documents,title,
                                                request_document_from_type,request_method,comment,document_due_date,doc_status,docment_id,updated_at,key,rejected_reason,previous_doc_url,previous_doc_type);
                                        doc_arrayList.add(model);
                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(doc_arrayList.size(),total_count));
                                    if(is_first_data_call == 1){
                                        documentAdapter = new DocumentRequestAdapter(getActivity(),doc_arrayList,DocumentRequestFragment.this);
                                        documentAdapter.registerProgressCallback(DocumentRequestFragment.this);
                                        docListView.setAdapter(documentAdapter);

                                        is_first_data_call = 0;
                                    }
                                    else{
                                        documentAdapter.notifyDataSetChanged();
                                    }

                                    if(doc_arrayList.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
                                    }

                                }
                                else{
                                    if(doc_arrayList.size() == 0){
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

                if (getActivity()!=null) {
                    if (!getActivity().isDestroyed() && isAdded()) {
                        ErrorHandler.setVolleyMessage(getActivity(), error);
                    }
                }

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
                    progress_layout.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public void progressUpdate(final int percentage) {
        Log.e("in","percentage");
        Log.e("percentage",""+percentage);
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

    @Bind(R.id.docRequestsListView)
    ListView docListView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    @Bind(R.id.swipeContainer)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.progress_bar_percent)
    TextProgressBar textProgressBar;
    @Bind(R.id.progress_layout)
    LinearLayout progress_layout;




}
