package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
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
import com.tmsfalcon.device.tmsfalcon.adapters.EscrowListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.EscrowModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EscrowAccountsActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView EscrowListView;
    EscrowListAdapter mAdapter;
    ArrayList<EscrowModel> arrayList_escrow = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_escrow_accounts, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        if(networkValidator.isNetworkConnected()){
            getEscrowData();
        }
        else {
            Toast.makeText(EscrowAccountsActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }
    public void initIds(){

        networkValidator = new NetworkValidator(EscrowAccountsActivity.this);
        session = new SessionManager(EscrowAccountsActivity.this);
        EscrowListView = findViewById(R.id.listViewEscrow);

    }

    public void getEscrowData() {
        // Tag used to cancel the request
        String tag_json_obj = "escrow_accounts_tag";
        String url = UrlController.ESCROW_ACCOUNTS;
        arrayList_escrow.clear();
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

                                        EscrowModel childmodel = new EscrowModel(jEmpObj.getString("escrow_account_id"),
                                                jEmpObj.getString("title"),
                                                jEmpObj.getString("amount"),
                                                jEmpObj.getString("schedule"),
                                                jEmpObj.getString("start_date"),
                                                jEmpObj.getString("end_date"),
                                                jEmpObj.getString("balance"));
                                        arrayList_escrow.add(childmodel);

                                    }
                                    mAdapter = new EscrowListAdapter(EscrowAccountsActivity.this, arrayList_escrow);
                                    EscrowListView.setAdapter(mAdapter);
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

                ErrorHandler.setVolleyMessage(EscrowAccountsActivity.this,error);
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

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
}
