package com.tmsfalcon.device.tmsfalcon;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.DriverViolationFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.HosViolationFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.ProfileDocumentFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class Profile extends NavigationBaseActivity{

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int open_fragment = 0;
    Drawable drawable;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_profile);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_profile, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/

        open_fragment = getIntent().getIntExtra("open_fragment",0);
        Log.e("open fragment activity ",""+open_fragment);
        initIds();
        Log.e("driver id ",""+session.get_driver_id());
    }

    private void initIds() {
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        session = new SessionManager(Profile.this);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        viewPager.setCurrentItem(open_fragment);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BasicInfoFragment(), "Basic Info");
        adapter.addFragment(new ProfileDocumentFragment(), "Documents");
        adapter.addFragment(new EmploymentHistoryFragment(), "Employment History");
        adapter.addFragment(new AccidentRecordFragment(), "Accident Record");
        adapter.addFragment(new ExperienceRecordFragment(), "Experience Record");
        adapter.addFragment(new TrafficConvictionFragment(), "Traffic Conviction");
        adapter.addFragment(new HosViolationFragment(), "HOS Violation");
        adapter.addFragment(new DriverViolationFragment(), "Driver's Violation");

        viewPager.setAdapter(adapter);

        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }

    /*@Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+ SessionManager.getInstance().getNotificationCount());
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
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/

    /*@OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(Profile.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }
*/
    /*@OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(Profile.this);
    }*/

}
