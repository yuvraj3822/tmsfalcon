package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.TrailerDetail;
import com.tmsfalcon.device.tmsfalcon.TruckDetail;
import com.tmsfalcon.device.tmsfalcon.activities.directUploadModule.DirectUploadCameraScreen;
import com.tmsfalcon.device.tmsfalcon.adapters.TrailerListAdapter;
import com.tmsfalcon.device.tmsfalcon.adapters.TruckListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.TrailerModel;
import com.tmsfalcon.device.tmsfalcon.entities.TruckModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TrailerActivity extends NavigationBaseActivity {

    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    private Context context = TrailerActivity.this;
    NetworkValidator networkValidator;
    ListView listViewTrailer;
    TrailerListAdapter mAdapter;
    ArrayList<TrailerModel> arrayTrailers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_trailer);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = null;
        if (inflater != null) {
            contentView = inflater.inflate(R.layout.activity_trailer, null, false);
            drawer.addView(contentView, 0);
        }

        ButterKnife.bind(this);
       /* lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        if(networkValidator.isNetworkConnected()){
            getTrailersAction();
            listViewTrailer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    int trailer_id = view.getId();
                    Bundle bundle = new Bundle();
                    bundle.putInt("trailer_id",trailer_id);
                    Intent i = new Intent(context, TrailerDetail.class);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            });
        }
        else {
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    /*@Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/
    private void initIds() {
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        listViewTrailer = findViewById(R.id.listViewTrailer);
    }
    private void getTrailersAction() {
        // Tag used to cancel the request
        String tag_json_obj = "trailers_tag";

        String url = UrlController.RESOURCES;
        arrayTrailers.clear();

        showProgessBar();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONArray trailer_json;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");
                            if(status){
                                data_json = response.getJSONObject("data");
                                trailer_json = data_json.getJSONArray("trailers");
                                if(trailer_json !=null && trailer_json.length() > 0){

                                    if(trailer_json.length() == 1){
                                        JSONObject jEmpObj = trailer_json.getJSONObject(0);
                                        String trailer_id = jEmpObj.getString ("company_trailer_id");
                                        Bundle bundle = new Bundle();
                                        bundle.putInt("trailer_id", Integer.parseInt(trailer_id));
                                        Intent showTruckDetail = new Intent(TrailerActivity.this, TrailerDetail.class);
                                        showTruckDetail.putExtras(bundle);
                                        startActivity(showTruckDetail);
                                    }
                                    else{
                                        for (int i = 0; i < trailer_json.length(); i++) {
                                            JSONObject jEmpObj = trailer_json.getJSONObject(i);
                                            TrailerModel trailerModel = new TrailerModel(
                                                    jEmpObj.getString("company_trailer_id"),
                                                    jEmpObj.getString("thumb"),
                                                    jEmpObj.getString("make"),
                                                    jEmpObj.getString("unit_number"),
                                                    jEmpObj.getString("model"),
                                                    jEmpObj.getString("vin")
                                            );
                                            arrayTrailers.add(trailerModel);

                                        }
                                        mAdapter = new TrailerListAdapter(TrailerActivity.this, arrayTrailers);
                                        listViewTrailer.setAdapter(mAdapter);
                                    }

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
                ErrorHandler.setVolleyMessage(TrailerActivity.this,error);
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
        Intent i = new Intent(TrailerActivity.this, NotificationActivity.class);
        startActivity(i);
    }


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(TrailerActivity.this);
    }*/

}
