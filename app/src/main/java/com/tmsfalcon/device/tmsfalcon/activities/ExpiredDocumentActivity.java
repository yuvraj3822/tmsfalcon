package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.DocumentAdapter;
import com.tmsfalcon.device.tmsfalcon.adapters.ExpiredDocumentAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.ExpiredDocumentModel;
import com.tmsfalcon.device.tmsfalcon.widgets.TextProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExpiredDocumentActivity extends NavigationBaseActivity implements DocumentAdapter.progressCallback{

    SessionManager session;
    NetworkValidator networkValidator;
    ArrayList<ExpiredDocumentModel> doc_arrayList = new ArrayList<>();
    ListView docListView;
    ExpiredDocumentAdapter documentAdapter;
    TextProgressBar textProgressBar;
    LinearLayout progress_layout;

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_expired_document);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //inflate your activity layout here!
        View contentView = inflater.inflate(R.layout.activity_expired_document, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        if(networkValidator.isNetworkConnected()){
            getDocumentsList(10);
            setScrollEvent();
        }
        else {
            Toast.makeText(ExpiredDocumentActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
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
                        getDocumentsList(10);
                        loading = true;
                    }
                }
            }
        });
    }

    private void getDocumentsList(int limit) {
        currentPage += 1;
        // Tag used to cancel the request
        String tag_json_obj = "expired_documents_tag";
        String url = UrlController.EXPIRED_DOCUMENTS;
        progressBar.setVisibility(View.VISIBLE);
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
                                session.storeExpiredDocumentsCount(total_count);
                                if(dataArray != null && dataArray.length() > 0){
                                    for(int i = 0; i < dataArray.length(); i++){

                                        JSONObject document_single = (JSONObject) dataArray.get(i);
                                        String docment_id = document_single.getString("document_id");
                                        String e_document_id = document_single.getString("e_document_id");
                                        String document_file_name = document_single.getString("document_file_name");
                                        String doc_belongs_to = document_single.getString("document_belongs_to");
                                        String document_type = document_single.getString("document_type");
                                        String u_id = document_single.getString("u_id");
                                        String document_code = document_single.getString("document_code");
                                        JSONObject view_json = document_single.getJSONObject("view");
                                        String url = view_json.getString("url");
                                        String file_type = view_json.getString("type");
                                        String thumb = document_single.getString("thumb");
                                        String expiry_date = document_single.getString("expiry_date");
                                        String doc_status = document_single.getString("status");
                                        ExpiredDocumentModel model = new ExpiredDocumentModel(e_document_id,document_file_name,document_type,document_code,u_id,url,file_type,thumb,expiry_date,doc_belongs_to,doc_status);
                                        doc_arrayList.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(doc_arrayList.size(),total_count));
                                    if(is_first_data_call == 1){
                                        documentAdapter = new ExpiredDocumentAdapter(ExpiredDocumentActivity.this,doc_arrayList);
                                        documentAdapter.registerProgressCallback(ExpiredDocumentActivity.this);
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
                ErrorHandler.setVolleyMessage(ExpiredDocumentActivity.this,error);
                progressBar.setVisibility(View.GONE);
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

    public void initIds(){

        networkValidator = new NetworkValidator(ExpiredDocumentActivity.this);
        session = new SessionManager(ExpiredDocumentActivity.this);
        docListView = findViewById(R.id.docListView);
        progress_layout = findViewById(R.id.progress_layout);
        textProgressBar = findViewById(R.id.progress_bar_percent);
        footer_layout = findViewById(R.id.footer_layout);
        footer_text = findViewById(R.id.footer_text);

    }

    @Override
    public void showProgressCallback() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress_layout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void hideProgressCallback() {
        runOnUiThread(new Runnable() {
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(textProgressBar != null){
                    textProgressBar.setProgress(percentage);
                    textProgressBar.setText(""+percentage+" %");
                }

            }
        });
    }

   /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());
    }*/

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
   /* @Bind(R.id.cart_badge)
    TextView cartBadgeTextView;

    @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(ExpiredDocumentActivity.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(ExpiredDocumentActivity.this);
    }*/
}
