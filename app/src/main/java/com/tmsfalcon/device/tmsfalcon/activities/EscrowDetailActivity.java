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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.EscrowScheduleFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.EscrowTransactionFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class EscrowDetailActivity extends NavigationBaseActivity {

    String id = "";
    SessionManager session;
    NetworkValidator networkValidator;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_escrow_detail);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_escrow_detail, null, false);
        drawer.addView(contentView, 0);
        id = getIntent().getExtras().getString("id");
        Log.e("id ",""+id);
        ButterKnife.bind(this);
        initIds();
        mainLinear.setVisibility(View.GONE);
        if(!networkValidator.isNetworkConnected()){
            Toast.makeText(EscrowDetailActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        else {
            getData();
        }
    }

    public void initIds(){

        bundle = new Bundle();

        networkValidator = new NetworkValidator(EscrowDetailActivity.this);

        session = new SessionManager(EscrowDetailActivity.this);

        viewPager = findViewById(R.id.viewpager);

        tabLayout = findViewById(R.id.tabs);

    }

    public void setTabs(){
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getData() {
        showProgessBar();
        Log.e("session token ",SessionManager.getInstance().get_token());
        // Tag used to cancel the request
        RestClient.get().fetchEscrowDetail(SessionManager.getInstance().get_token(), Integer.parseInt(id)).enqueue(new Callback<EscrowDetailResponse>() {
            @Override
            public void onResponse(Call<EscrowDetailResponse> call, retrofit2.Response<EscrowDetailResponse> response) {

                if(response.body() != null || response.isSuccessful()){

                    if (response.body().getStatus()) {
                        mainLinear.setVisibility(View.VISIBLE);
                        EscrowDetailDataModel escrowDetailDataModel = response.body().getData();

                        ArrayList<EscrowDetailDataModel.EscrowStatement> escrow_statements = escrowDetailDataModel.getEscrowStatement();
                        ArrayList<EscrowDetailDataModel.EscrowTransaction> escrow_transactions = escrowDetailDataModel.getEscrowTransaction();
                        escrow_amount.setText(""+escrowDetailDataModel.getBalance());
                        escrow_id.setText("#"+id);
                        escrow_label.setText(CustomValidator.capitalizeFirstLetter(escrowDetailDataModel.getTitle()));
                        bundle.putParcelableArrayList("escrow_statements", escrow_statements);
                        bundle.putParcelableArrayList("transactions",escrow_transactions);
                        bundle.putString("periodic_amount",escrowDetailDataModel.getBalance());
                        bundle.putParcelableArrayList("escrow_transactions",escrow_transactions);
                        setTabs();

                        //Toast.makeText(DayOffActivity.this, ""+messages, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("in","status false body "+response.body());
                    }


                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(EscrowDetailActivity.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<EscrowDetailResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {

        EscrowScheduleFragment escrowScheduleFragment = new EscrowScheduleFragment();
        escrowScheduleFragment.setArguments(bundle);

        EscrowTransactionFragment escrowTransactionFragment = new EscrowTransactionFragment();
        escrowTransactionFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(escrowScheduleFragment, "Payment Schedule");
        adapter.addFragment(escrowTransactionFragment, "Transactions");
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

    @Bind(R.id.escrow_amount)
    TextView escrow_amount;

    @Bind(R.id.escrow_id)
    TextView escrow_id;

    @Bind(R.id.escrow_label)
    TextView escrow_label;

    @Bind(R.id.main_content)
    LinearLayout mainLinear;


}
