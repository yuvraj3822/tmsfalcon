package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.FuelFragmentModel;
import com.tmsfalcon.device.tmsfalcon.fragments.FuelScheduleFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.FuelSummaryFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FuelRebateActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ArrayList<FuelFragmentModel> arraylist_fuel = new ArrayList<>();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_fuel_rebate);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_fuel_rebate, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);

        initIds();
        if(networkValidator.isNetworkConnected()){
            getFuelRebateData();
        }
        else {
            Toast.makeText(FuelRebateActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void initIds(){
        bundle = new Bundle();
        networkValidator = new NetworkValidator(FuelRebateActivity.this);
        session = new SessionManager(FuelRebateActivity.this);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
    }

    public void setTabs(){
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
    }


    public  void getFuelRebateData(){
        // Tag used to cancel the request
        String tag_json_obj = "fuel_rebates_tag";

        String url = UrlController.FUEL_REBATE_SCHEDULE;
        arraylist_fuel.clear();

        showProgessBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        JSONArray rebate_schedule,rebate_summary;

                        Log.e("Response ", response.toString());
                        try {
                            status = response.getBoolean("status");

                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){
                                    rebate_schedule = data_json.getJSONArray("rebate_schedule");
                                    if(rebate_schedule != null && rebate_schedule.length()>0){
                                        for(int i = 0; i<rebate_schedule.length(); i++){
                                            JSONObject jEmpObj = rebate_schedule.getJSONObject(i);
                                            FuelFragmentModel model = new FuelFragmentModel(jEmpObj.getString("id"),
                                                    jEmpObj.getString("company_id"),
                                                    jEmpObj.getString("fuel_stations"),
                                                    jEmpObj.getString("driver_id"),
                                                    jEmpObj.getString("rebate_schedule"),
                                                    jEmpObj.getString("fuel_station"),
                                                    jEmpObj.getString("rebate_per_gallon"),
                                                    jEmpObj.getString("method")
                                            );
                                            arraylist_fuel.add(model);

                                        }
                                        bundle.putParcelableArrayList("rebate_schedule",arraylist_fuel);
                                        setTabs();
                                    }
                                    else{
                                        no_data_textview.setVisibility(View.VISIBLE);
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
                ErrorHandler.setVolleyMessage(FuelRebateActivity.this,error);
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

    private void setupViewPager(ViewPager viewPager) {

        FuelScheduleFragment fuelScheduleFragment = new FuelScheduleFragment();
        fuelScheduleFragment.setArguments(bundle);

        FuelSummaryFragment fuelSummaryFragment = new FuelSummaryFragment();
        fuelSummaryFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fuelSummaryFragment, " Summary");
        adapter.addFragment(fuelScheduleFragment, " Schedule");
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
