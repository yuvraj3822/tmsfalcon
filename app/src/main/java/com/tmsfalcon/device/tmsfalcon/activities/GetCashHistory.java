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
import com.tmsfalcon.device.tmsfalcon.adapters.CashHistoryAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.CashHistoryModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GetCashHistory extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    CashHistoryAdapter adapter;
    ArrayList<CashHistoryModel> cash_history_arrayList = new ArrayList<>();

    int is_first_data_call = 1;

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_get_cash_history, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        initIds();
        if(networkValidator.isNetworkConnected()){

            getData(10);
            setScrollEvent();

        }
        else {
            Toast.makeText(GetCashHistory.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }

    }

    public void setScrollEvent(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
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

                        getData(10);
                        loading = true;
                    }
                }
                /*if (!loading && (lastInScreen == totalItemCount)) {
                    if(is_first_data_call != 1 && manual_call_done == true){

                        if (sent_request_offset != totalItemCount){
                            getData(10);
                            sent_request_offset = totalItemCount;
                        }
                        loading = true;
                    }
                }*/

            }
        });
    }

    public void initIds(){
        networkValidator = new NetworkValidator(GetCashHistory.this);
        session = new SessionManager(GetCashHistory.this);
        footer_layout = findViewById(R.id.footer_layout);
        footer_text = findViewById(R.id.footer_text);
    }

    public void getData(int limit) {
        // Tag used to cancel the request
        currentPage += 1;
        String tag_json_obj = "cash_history";
        String url = UrlController.CASH_HISTORY;
        showProgessBar();
        Map<String, Integer> params = new HashMap<>();
        params.put("limit", limit);
        params.put("offset", cash_history_arrayList.size());
        Log.e("offset",""+cash_history_arrayList.size());
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
                                total_count = response.getInt("total");

                                if(data_json !=null && data_json.length()>0){
                                    for(int i = 0; i<data_json.length(); i++){
                                        JSONObject jEmpObj = data_json.getJSONObject(i);
                                        CashHistoryModel model = new CashHistoryModel(jEmpObj.getString("driver_cash_id"),
                                                                        jEmpObj.getString("trip_id"),
                                                                        jEmpObj.getString("cash_for"),
                                                                        jEmpObj.getString("updated_at"),
                                                                        jEmpObj.getString("amount"),
                                                                        jEmpObj.getString("code"),
                                                                        jEmpObj.getString("status"));
                                        cash_history_arrayList.add(model);

                                    }
                                    footer_layout.setVisibility(View.VISIBLE);
                                    footer_text.setText(setFooterText(cash_history_arrayList.size(),total_count));
                                    if(is_first_data_call == 1){
                                        adapter = new CashHistoryAdapter(GetCashHistory.this,cash_history_arrayList);
                                        listView.setAdapter(adapter);
                                        is_first_data_call = 0;
                                    }
                                    else{
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                                else{
                                    if(cash_history_arrayList.size() == 0){
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
                ErrorHandler.setVolleyMessage(GetCashHistory.this,error);
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

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @Bind(R.id.cash_history)
    ListView listView;
}
