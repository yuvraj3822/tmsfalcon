package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.tmsfalcon.device.tmsfalcon.adapters.LoanListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.LoanModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LoanActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView loanListView;
    LoanListAdapter mAdapter;
    ArrayList<LoanModel> arrayList_loan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_loan);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_loan, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        if(networkValidator.isNetworkConnected()){
            getLoanData();
        }
        else {
            Toast.makeText(LoanActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void initIds(){

        networkValidator = new NetworkValidator(LoanActivity.this);
        session = new SessionManager(LoanActivity.this);
        loanListView = findViewById(R.id.listViewLoan);

    }

    public void getLoanData() {
        // Tag used to cancel the request
        String tag_json_obj = "loans_tag";
        String url = UrlController.LOAN;
        arrayList_loan.clear();
        showProgessBar();
        Log.e("session ",SessionManager.getInstance().get_token());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
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

                                if(data_json != null && data_json.length() > 0){
                                    for (int i = 0; i < data_json.length(); i++) {
                                        JSONObject jEmpObj = data_json.getJSONObject(i);

                                        LoanModel childmodel = new LoanModel(jEmpObj.getString("id"),
                                                jEmpObj.getString("company_id"),
                                                jEmpObj.getString("driver_id"),
                                                jEmpObj.getString("total_loan_amount"),
                                                jEmpObj.getString("down_payment"),
                                                jEmpObj.getString("last_installment"),
                                                jEmpObj.getString("no_installment"),
                                                jEmpObj.getString("installment_amount"),
                                                jEmpObj.getString("no_recieved_installment"),
                                                jEmpObj.getString("amount_paid"),
                                                jEmpObj.getString("start_date"),
                                                jEmpObj.getString("expiring_date"),
                                                jEmpObj.getString("notes"),
                                                jEmpObj.getString("payment_schedule"),
                                                jEmpObj.getInt("deferred"),
                                                jEmpObj.getString("loan_title"));
                                        arrayList_loan.add(childmodel);

                                    }
                                    mAdapter = new LoanListAdapter(LoanActivity.this, arrayList_loan);
                                    loanListView.setAdapter(mAdapter);
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

                ErrorHandler.setVolleyMessage(LoanActivity.this,error);
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
    /*@Override
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
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/

    /*@OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(LoanActivity.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(LoanActivity.this);
    }*/

}
