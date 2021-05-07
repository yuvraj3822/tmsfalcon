package com.tmsfalcon.device.tmsfalcon.fragments;


import android.Manifest;
import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.UploadedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.adapters.DocumentAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrailerDocumentList extends Fragment implements DocumentAdapter.progressCallback {

    int trailer_id;
    SessionManager session;
    NetworkValidator networkValidator;
    ArrayList<DocumentModel> doc_arrayList = new ArrayList<>();
    ListView docListView;
    DocumentAdapter documentAdapter;
    TextProgressBar textProgressBar;
    LinearLayout progress_layout;
    TextView no_data_textview;

    public TrailerDocumentList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trailer_document_list, container, false);
        initIds(view);
        Bundle bundle = getActivity().getIntent().getExtras();
        trailer_id = bundle.getInt("trailer_id");
        checkPermissions();
        if(networkValidator.isNetworkConnected()){
            getDocumentsList();
        }
        else {
            Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        return view;
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

                } else {
                    Toast.makeText(getActivity(), "Allow external storage reading", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void showError() {
        Toast.makeText(getContext(), "Allow permissions to view files.", Toast.LENGTH_SHORT).show();
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        docListView = view.findViewById(R.id.docListView);
        progress_layout = view.findViewById(R.id.progress_layout);
        textProgressBar = view.findViewById(R.id.progress_bar_percent);
        no_data_textview = view.findViewById(R.id.no_data_textview);
    }
    private void getDocumentsList() {
        // Tag used to cancel the request

        String tag_json_obj = "trailer_documents_tag";
        String url = UrlController.TRAILER_DOCUMENTS+"/"+trailer_id;
        final ProgressDialog pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.show();

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(trailer_id));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
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
                                if(dataArray !=null && dataArray.length()>0){
                                    for(int i = 0; i < dataArray.length(); i++){

                                        JSONObject document_single = (JSONObject) dataArray.get(i);
                                        String docment_id = document_single.getString("documents_id");
                                        String document_file_name = document_single.getString("document_file_name");
                                        String document_type = document_single.getString("document_type");
                                        String u_id = document_single.getString("u_id");
                                        String tracking = document_single.getString("tracking");
                                        JSONObject view_json = document_single.getJSONObject("view");
                                        String url = view_json.getString("url");
                                        String file_type = view_json.getString("type");
                                        String thumb = document_single.getString("thumb");
                                        String expiry_date = document_single.getString("expiry_date");
                                        String tracking_status = document_single.getString("tracking_status");
                                        String tracking_type = document_single.getString("tracking_type");
                                        DocumentModel model = new DocumentModel(docment_id,document_file_name,document_type,u_id,tracking,url,file_type,thumb,expiry_date,tracking_status,tracking_type);
                                        doc_arrayList.add(model);

                                    }
                                    documentAdapter = new DocumentAdapter(getActivity(),doc_arrayList);
                                    documentAdapter.registerProgressCallback(TrailerDocumentList.this);
                                    docListView.setAdapter(documentAdapter);
                                }
                                else{
                                    no_data_textview.setVisibility(View.VISIBLE);
                                }
                            }

                        } catch (JSONException e) {
                             Log.e("exception ", String.valueOf(e));
                        }
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(getActivity(),error);
                pDialog.dismiss();

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
