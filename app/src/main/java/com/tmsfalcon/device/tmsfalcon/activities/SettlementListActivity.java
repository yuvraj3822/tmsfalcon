package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.DevTllSettlementFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.NewSettlementFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class SettlementListActivity extends NavigationBaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    int open_fragment = 0;
    Drawable drawable;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_settlement_list, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        open_fragment = getIntent().getIntExtra("open_fragment",0);
        Log.e("open fragment activity ",""+open_fragment);
        initIds();

    }

    private void initIds() {
        /*toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        session = new SessionManager(SettlementListActivity.this);
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
        adapter.addFragment(new NewSettlementFragment(), "Settlement Batches");

        if(session.get_company_id() == 81){
            adapter.addFragment(new DevTllSettlementFragment(), "Old Settlements");
        }
        else{
            Log.e("SettlementListActivity","Not in company id 81");
        }

        viewPager.setAdapter(adapter);

        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
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


}
