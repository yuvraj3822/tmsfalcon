package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.tmsfalcon.device.tmsfalcon.TruckDetail;
import com.tmsfalcon.device.tmsfalcon.adapters.TruckListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.TruckModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TruckActivity extends NavigationBaseActivity {

    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    private Context context = TruckActivity.this;
    NetworkValidator networkValidator;
    @SuppressWarnings("unused")
    JSONArray truckJsonArray;
    ListView listViewTruck;
    TruckListAdapter mAdapter;
    ArrayList<TruckModel> arrayTrucks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_truck);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_truck, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        if(networkValidator.isNetworkConnected()){
            getTrucksAction();
            listViewTruck.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int truck_id = view.getId();
                    Bundle bundle = new Bundle();
                    bundle.putInt("truck_id", truck_id);
                    Intent showTruckDetail = new Intent(TruckActivity.this, TruckDetail.class);
                    showTruckDetail.putExtras(bundle);
                    startActivity(showTruckDetail);
                }
            });
        }
        else {
            Toast.makeText(TruckActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    private void initIds() {
        networkValidator = new NetworkValidator(TruckActivity.this);
        session = new SessionManager(TruckActivity.this);
        listViewTruck = findViewById(R.id.listViewTrucks);
    }

    private void getTrucksAction() {
        // Tag used to cancel the request
        String tag_json_obj = "trucks_tag";

        String url = UrlController.RESOURCES;
        arrayTrucks.clear();

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONArray trucks_json;

                        Log.e(" resources Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                data_json = response.getJSONObject("data");
                                trucks_json = data_json.getJSONArray("trucks");
                                if(trucks_json != null && trucks_json.length() > 0){

                                    if(trucks_json.length() == 1){
                                        JSONObject jEmpObj = trucks_json.getJSONObject(0);
                                        String truck_id = jEmpObj.getString ("company_truck_id");
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("truck_id", Integer.parseInt(truck_id));
                                        Intent showTruckDetail = new Intent(TruckActivity.this, TruckDetail.class);
                                        showTruckDetail.putExtras(bundle);
                                        startActivity(showTruckDetail);
                                    }
                                    else{
                                        for (int i = 0; i < trucks_json.length(); i++) {
                                            JSONObject jEmpObj = trucks_json.getJSONObject(i);
                                            TruckModel truckModel = new TruckModel(
                                                    jEmpObj.getString ("company_truck_id"),
                                                    jEmpObj.getString("thumb"),
                                                    jEmpObj.getString("make"),
                                                    jEmpObj.getString("unit_number"),
                                                    jEmpObj.getString("model"),
                                                    jEmpObj.getString("vin")
                                            );
                                            arrayTrucks.add(truckModel);
                                        }
                                        mAdapter = new TruckListAdapter(TruckActivity.this, arrayTrucks);
                                        listViewTruck.setAdapter(mAdapter);
                                    }

                                }
                                else {
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
                ErrorHandler.setVolleyMessage(TruckActivity.this,error);
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

   /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;

    @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(TruckActivity.this, NotificationActivity.class);
        startActivity(i);
    }


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(TruckActivity.this);
    }*/

}
