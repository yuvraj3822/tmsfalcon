package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;

import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.CompanyFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.DispatcherFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CompanyDispatcherActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    private Context context = CompanyDispatcherActivity.this;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int open_fragment = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_company_dispatcher);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_company_dispatcher, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        open_fragment = getIntent().getIntExtra("open_fragment",0);
        Log.e("open fragment activity ",""+open_fragment);
        initIds();
        if(!networkValidator.isNetworkConnected()){
            Toast.makeText(context,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }
    private void initIds() {
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);

        viewPager =  findViewById(R.id.viewpager);

        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(2);
        if(open_fragment == 1){
            viewPager.setCurrentItem(1);
        }
        else{
            viewPager.setCurrentItem(0);
        }
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    /*@Override
    protected void onResume() {

        super.onResume();
        //Set up Badge
        Log.e("count ",""+SessionManager.getInstance()Count.getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CompanyFragment(), "Company");
        adapter.addFragment(new DispatcherFragment(), "Dispatcher");
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

   /* @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on) ImageView lights_on;*/
    @Bind(R.id.no_data_textview)
    TextView no_data_textview;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;

    @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(CompanyDispatcherActivity.this, NotificationActivity.class);
        startActivity(i);
    }
    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(CompanyDispatcherActivity.this);
    }*/

}
