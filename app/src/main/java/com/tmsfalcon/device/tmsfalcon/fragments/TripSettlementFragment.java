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
import com.tmsfalcon.device.tmsfalcon.adapters.SettlementDocumentAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.SettlementModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripSettlementFragment extends Fragment implements SettlementDocumentAdapter.progressCallback {

    SessionManager session;
    NetworkValidator networkValidator;
    int trip_id = 0;
    Bundle bundle;
    SettlementDocumentAdapter documentAdapter;
    ArrayList<SettlementModel> doc_arrayList = new ArrayList<>();
    ListView docListView;
    TextProgressBar textProgressBar;
    LinearLayout progress_layout;
    TextView no_data_textview;
    ProgressBar progressBar;

    public TripSettlementFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_settlement, container, false);
        initIds(view);
        checkPermissions();
        if (getUserVisibleHint()) {

            bundle = this.getArguments();
            trip_id = bundle.getInt("trip_id");
            fetchData();

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
        progressBar = view.findViewById(R.id.progress_bar);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            bundle = this.getArguments();
            trip_id = bundle.getInt("trip_id");
            fetchData();

        }
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void fetchData() {
        // Tag used to cancel the request
        String tag_json_obj = "trip_settlement_tag";
        String url = UrlController.TRIP_SETTLEMENT+"/?trip_id="+trip_id;

        showProgessBar();
        doc_arrayList.clear();
        Map<String, String> params = new HashMap<>();
        params.put("trip_id", String.valueOf(trip_id));
        Log.e("trip_id", String.valueOf(trip_id));

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;

                        Log.e("Response", response.toString());
                        try {
                            status = response.getBoolean("status");
                            JSONObject dataObject = new JSONObject();
                            if(status){
                                dataObject = response.getJSONObject("data");
                                if(dataObject != null && dataObject.length() > 0 ){

                                    int trip_id = dataObject.getInt("trip_id");
                                    int total_settled_amount = dataObject.getInt("settlement_total");
                                    String start_location  = dataObject.getString("start_location");
                                    String end_location  = dataObject.getString("end_location");
                                    String download_url  = dataObject.getString("download_url");
                                    String settlement_date  = dataObject.getString("settlement_date");
                                    String thumb = dataObject.getString("thumb");

                                    SettlementModel model = new SettlementModel(trip_id,total_settled_amount,start_location,end_location,settlement_date,download_url,thumb);
                                    doc_arrayList.add(model);

                                    documentAdapter = new SettlementDocumentAdapter(getActivity(),doc_arrayList);
                                    documentAdapter.registerProgressCallback(TripSettlementFragment.this);
                                    docListView.setAdapter(documentAdapter);
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
