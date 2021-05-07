package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.TripDetailResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.TripItineraryModel;
import com.tmsfalcon.device.tmsfalcon.fragments.TripDetailDocumentFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.TripDetailItineraryFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.TripSettlementFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tmsfalcon.device.tmsfalcon.customtools.AppController.math;

public class TripDetail extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    private Context context = TripDetail.this;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Integer trip_id = 0;
    Bundle bundle;
    String fragment_name = "";
    LinearLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_trip_detail);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_trip_detail, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
       /* lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        first_layout.setVisibility(View.GONE);
        tabLayout.setVisibility(View.GONE);

        int colorCodeDark = Color.parseColor("#071528");
        //progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        if(!networkValidator.isNetworkConnected()){
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        else{
            trip_id = getIntent().getExtras().getInt("trip_id");
            fragment_name  = getIntent().getExtras().getString("fragment_name");
            Log.e("trip detail",""+trip_id);
            fetchData();
        }


        if (fragment_name.equalsIgnoreCase("completed_trips"))
            mainView.setBackgroundColor(ContextCompat.getColor(TripDetail.this,R.color.selected_color));
        else
            mainView.setBackgroundColor(ContextCompat.getColor(TripDetail.this,R.color.unselected_color));

    }



    public void fetchData(){
        showProgessBar();
        Log.e("session ","is "+SessionManager.getInstance().get_token());
        RestClient.get().fetchTripDetail(SessionManager.getInstance().get_token(),trip_id).enqueue(new Callback<TripDetailResponse>() {
            @Override
            public void onResponse(Call<TripDetailResponse> call, Response<TripDetailResponse> response) {
                first_layout.setVisibility(View.VISIBLE);
                tabLayout.setVisibility(View.VISIBLE);
                Log.e("response:-"," "+response.toString());

                if(response.body() != null || response.isSuccessful()){
                    if (response.body().getStatus()) {

                        List<TripDetailResponse.Datum> data = response.body().getData();
                        for (int i = 0;i<data.size();i++){
                            TripDetailResponse.Datum model = data.get(i);
                            if(model.getType().trim().equalsIgnoreCase("ltl")||model.getType().trim().equalsIgnoreCase("tl")){
                                load_type.setText("TL");
                                load_type_image.setBackground(getResources().getDrawable(R.drawable.ltl_trip_image));

                            } else {
                                load_type.setText(model.getType());
                                load_type_image.setBackground(getResources().getDrawable(R.drawable.ftl_trip_image));

                            }
                            origin.setText(CustomValidator.properCase(model.getStart_location()));
                            destination.setText(CustomValidator.properCase(model.getEnd_location()));
                            start_time.setText(model.getStart_date());
                            end_time.setText(model.getEnd_date());
                            distance.setText(math(model.getDistance())+" Miles");
                            stops.setText(model.getStops().toString());
                            TripNoTextView.setText("#"+model.getTrip_number());
                            ArrayList<TripItineraryModel> locations = model.getLocations();

                            locations.add(0,new TripItineraryModel("","","","","","","","","",
                                                                        0,0,"","","","","","","","","","",""));

                            locations.add(locations.size(),new TripItineraryModel("","","","","","","","","",
                                    0,0,"","","","","","","","","","",""));


                            bundle.putParcelableArrayList("itinerary", locations);
                            bundle.putInt("trip_id",model.getTrip_id());
                            bundle.putString("distance", String.valueOf(model.getDistance()));

                           int value =  model.getIsBatchSettlement();// 0 show (not settled) or 1 not show(settled) for batchsettlement
                            setTabs(value);
                        }

                        //Toast.makeText(DayOffActivity.this, ""+messages, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("in","status false body "+response.body());
                    }

                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(TripDetail.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<TripDetailResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void initIds() {
        bundle = new Bundle();
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        mainView = findViewById(R.id.first_block);


    }

    public void setTabs(int value){
        setupViewPager(viewPager,value);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(context, R.color.light_blue);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(context, R.color.white_greyish);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.location_light_blue);
        tabLayout.getTabAt(1).setIcon(R.drawable.dollar_light_blue);
        Log.e("settle show: ",": "+value);
        if(fragment_name != null && fragment_name.equals("completed_trips")){
            if (value == 0) {// 0 show (not settled) or 1 don't show(settled) for batchsettlement
                tabLayout.getTabAt(2).setIcon(R.drawable.document_light_blue);
            }
        }

//        intilisetab color
        int tabIconColor = ContextCompat.getColor(context, R.color.light_blue);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);



//        tabLayout.getTabAt(2).setIcon(R.drawable.ic_tab_document);


//        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
//                .inflate(R.layout.custom_trip_detail_tab, null, false);
//        LinearLayout linearLayoutOne = headerView.findViewById(R.id.first_tab);
//        LinearLayout linearLayoutTwo = headerView.findViewById(R.id.third_tab);
//        LinearLayout linearLayoutThird = headerView.findViewById(R.id.second_tab);
//
//        tabLayout.getTabAt(0).setCustomView(linearLayoutOne);
//        tabLayout.getTabAt(1).setCustomView(linearLayoutTwo);
//        if(fragment_name != null && fragment_name.equals("completed_trips")){
//            tabLayout.getTabAt(2).setCustomView(linearLayoutThird);
//        }

    }

    private void setupViewPager(ViewPager viewPager, int value) {

        TripDetailItineraryFragment tripDetailItineraryFragment = new TripDetailItineraryFragment();
        tripDetailItineraryFragment.setArguments(bundle);

        TripDetailDocumentFragment tripDetailDocumentFragment = new TripDetailDocumentFragment();
        tripDetailDocumentFragment.setArguments(bundle);

        TripSettlementFragment tripSettlementFragment = new TripSettlementFragment();
        tripSettlementFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(tripDetailItineraryFragment, "Itinerary");
        adapter.addFragment(tripDetailDocumentFragment, "Documents");

        if(fragment_name != null && fragment_name.equals("completed_trips")) {

            if (value == 0) {// 0 show (not settled) or 1 don't show(settled) for batchsettlement
//                tabLayout.getTabAt(2).setIcon(R.drawable.document_light_blue);
                adapter.addFragment(tripSettlementFragment, "Settlement");

            }

        }

            viewPager.setAdapter(adapter);
    }

    /*@Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/

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

   /* @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on) ImageView lights_on;*/
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.load_type) TextView load_type;
    @Bind(R.id.trip_no) TextView TripNoTextView;
    @Bind(R.id.origin) TextView origin;
    @Bind(R.id.distance) TextView distance;
    @Bind(R.id.destination) TextView destination;
    @Bind(R.id.stops) TextView stops;
    @Bind(R.id.load_type_image) ImageView load_type_image;
    @Bind(R.id.start_time) TextView start_time;
    @Bind(R.id.end_time) TextView end_time;
    @Bind(R.id.trip_first_block)
    LinearLayout first_layout;
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;

    @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(TripDetail.this, NotificationActivity.class);
        startActivity(i);
    }


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }*/

}
