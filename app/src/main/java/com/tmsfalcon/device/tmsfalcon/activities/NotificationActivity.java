package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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
import com.tmsfalcon.device.tmsfalcon.adapters.NotificationListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NotificationActivity extends NavigationBaseActivity {

    SessionManager session;
    private Context context = NotificationActivity.this;
    NetworkValidator networkValidator;
    ListView listViewNotification;
    NotificationListAdapter mAdapter;
    ArrayList<NotificationModel> arrayNotifications = new ArrayList<>();

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
        //setContentView(R.layout.activity_notification);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_notification, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        bell_layout.setVisibility(View.GONE);
        initIds();
        if (networkValidator.isNetworkConnected()) {
            getNotifications(10);
            setScrollEvent();

        } else {
            Toast.makeText(context, getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
        }
    }
    public void setScrollEvent(){
        listViewNotification.setOnScrollListener(new AbsListView.OnScrollListener() {
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
                        getNotifications(10);
                        loading = true;
                    }
                }
            }
        });
    }

    private void getNotifications(int limit) {
        currentPage += 1;
        String tag_json_obj = "fetch_all_notifications";
        String url = UrlController.FETCH_ALL_NOTIFICATIONS;
        //arrayNotifications.clear();
        showProgessBar();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", limit);
        params.put("offset", arrayNotifications.size());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray data_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                data_json = response.getJSONArray("data");
                                String count = response.getString("total_count");
                                String unseen_count = response.getString("unseen_count");
                                total_count = Integer.parseInt(count);
                                SessionManager.getInstance().storeNotificationCount(unseen_count);

                                if(data_json != null && data_json.length() > 0){
                                    for (int i = 0; i < data_json.length(); i++) {
                                        JSONObject jEmpObj = data_json.getJSONObject(i);
                                        NotificationModel model = new NotificationModel(jEmpObj.getString("sent_time"),
                                                jEmpObj.getString("message"),
                                                jEmpObj.getString("type"),
                                                jEmpObj.getString("resource_type"),
                                                jEmpObj.getString("event_id"),
                                                jEmpObj.getString("trip_id"),
                                                jEmpObj.getString("resource_id"),
                                                jEmpObj.getString("seen_status"),
                                                jEmpObj.getString("docId"),
                                                jEmpObj.getString("docName"),
                                                jEmpObj.getString("doc_title"),
                                                jEmpObj.getString("doc_type"),
                                                jEmpObj.getString("due_date"),
                                                jEmpObj.getString("comment"),
                                                jEmpObj.getString("status"),
                                                jEmpObj.getString("key"),
                                                jEmpObj.getString("document_belongs_to"),
                                                jEmpObj.optString("public_link"));

                                        arrayNotifications.add(model);

                                    }

                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(arrayNotifications.size(),total_count));
                                    if(is_first_data_call == 1){
                                        mAdapter = new NotificationListAdapter(NotificationActivity.this,arrayNotifications);
                                        listViewNotification.setAdapter(mAdapter);

                                        is_first_data_call = 0;
                                    }
                                    else{
                                        mAdapter.notifyDataSetChanged();
                                    }

                                }
                                else{
                                    if(arrayNotifications.size() == 0){
                                        no_data_textview.setVisibility(View.VISIBLE);
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
                ErrorHandler.setVolleyMessage(NotificationActivity.this,error);
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

    private void initIds() {
        networkValidator = new NetworkValidator(NotificationActivity.this);
        session = new SessionManager(NotificationActivity.this);
        listViewNotification = findViewById(R.id.listViewNotifications);

        footer_layout = findViewById(R.id.footer_layout);
        footer_text = findViewById(R.id.footer_text);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;
    @Bind(R.id.bell_layout)
    RelativeLayout bell_layout;*/

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    /*@OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(NotificationActivity.this);
    }


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }*/
}
