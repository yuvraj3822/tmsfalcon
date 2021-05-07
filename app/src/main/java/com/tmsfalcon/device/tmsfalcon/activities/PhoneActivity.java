package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.ContactsTabFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.KeypadFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.RecentCallsTabFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.SettingsTabFragmnet;
import com.tmsfalcon.device.tmsfalcon.fragments.VoiceMailTabFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class PhoneActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    private Context context = PhoneActivity.this;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;
    int open_fragment = 0;
    String intent_phone = "";
    String intent_ext = "";
    String intent_name = "";
    KeypadFragment keypadFragment;
    RecentCallsTabFragment recentCallsTabFragment;
    ContactsTabFragment contactsTabFragment;
    SettingsTabFragmnet settingsTabFragmnet;
    VoiceMailTabFragment voiceMailTabFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_phone);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_phone, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        if(getIntent().hasExtra("open_fragment"))
            open_fragment = getIntent().getExtras().getInt("open_fragment",0);
        if(getIntent().hasExtra("intent_phone"))
            intent_phone = getIntent().getExtras().getString("intent_phone","");
        if(getIntent().hasExtra("intent_ext"))
            intent_ext = getIntent().getExtras().getString("intent_ext","");
        if(getIntent().hasExtra("intent_name"))
            intent_name = getIntent().getExtras().getString("intent_name","");
        initIds();
        session.checkLogin();
        setTabs();
    }

    private void initIds() {
        bundle = new Bundle();
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
    }

    public void setTabs(){
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(open_fragment);
        View headerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_phone_tabs, null, false);
//        LinearLayout linearLayoutOne = headerView.findViewById(R.id.first_tab);
//        LinearLayout linearLayoutTwo =  headerView.findViewById(R.id.second_tab);
        LinearLayout linearLayoutThird = headerView.findViewById(R.id.third_tab);
//        LinearLayout linearLayoutFourth = headerView.findViewById(R.id.fourth_tab);
//        LinearLayout linearLayoutFifth = headerView.findViewById(R.id.fifth_tab);

//        tabLayout.getTabAt(0).setCustomView(linearLayoutOne);
//        tabLayout.getTabAt(0).setCustomView(linearLayoutTwo);
        tabLayout.getTabAt(0).setCustomView(linearLayoutThird);
//        tabLayout.getTabAt(2).setCustomView(linearLayoutFourth);
//        tabLayout.getTabAt(3).setCustomView(linearLayoutFifth);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle keypadBundle = new Bundle();
        keypadBundle.putString("intent_phone",intent_phone);
        keypadBundle.putString("intent_ext",intent_ext);
        keypadBundle.putString("intent_name",intent_name);

//        keypadFragment = new KeypadFragment();
//        keypadFragment.setArguments(keypadBundle);
//
//        adapter.addFragment(keypadFragment, "Keypad");

        recentCallsTabFragment = new RecentCallsTabFragment();

        contactsTabFragment = new ContactsTabFragment();

        settingsTabFragmnet = new SettingsTabFragmnet();

        voiceMailTabFragment = new VoiceMailTabFragment();

//        adapter.addFragment(recentCallsTabFragment, "Recents");

        adapter.addFragment(contactsTabFragment, "Contacts");

//        adapter.addFragment(settingsTabFragmnet, "Settings");

//        adapter.addFragment(voiceMailTabFragment, "Voicemails");

        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        recentCallsTabFragment.onDetach();
        voiceMailTabFragment.onDetach();
        keypadFragment.onDetach();
        contactsTabFragment.onDetach();
        settingsTabFragmnet.onDetach();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /*recentCallsTabFragment.onDetach();
        voiceMailTabFragment.onDetach();
        keypadFragment.onDetach();
        contactsTabFragment.onDetach();
        settingsTabFragmnet.onDetach();*/
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


}
