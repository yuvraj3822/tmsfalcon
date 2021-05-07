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
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanInstallmentModel;
import com.tmsfalcon.device.tmsfalcon.fragments.LoanDetailFragment;
import com.tmsfalcon.device.tmsfalcon.fragments.LoanInstallmentFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

public class LoanInstallments extends NavigationBaseActivity {

    String id = "";
    SessionManager session;
    NetworkValidator networkValidator;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Bundle bundle;
    String received_installments = "";
    String current_balance = "";
    String installment_amount_intent = "";
    String amount_paid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_loan_installments);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_loan_installments, null, false);
        drawer.addView(contentView, 0);
        id = getIntent().getExtras().getString("id");
        received_installments = getIntent().getExtras().getString("received_installments");
        current_balance = getIntent().getExtras().getString("current_balance");
        installment_amount_intent = getIntent().getExtras().getString("installment_amount_intent");
        amount_paid = getIntent().getExtras().getString("amount_paid");


        Log.e("id ",""+id);
        ButterKnife.bind(this);
        /*lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        mainLinear.setVisibility(View.GONE);
        if(!networkValidator.isNetworkConnected()){
            Toast.makeText(LoanInstallments.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        else {
            getData();
        }
    }

    public void initIds(){

        bundle = new Bundle();

        networkValidator = new NetworkValidator(LoanInstallments.this);

        session = new SessionManager(LoanInstallments.this);

        viewPager = findViewById(R.id.viewpager);

        tabLayout = findViewById(R.id.tabs);

    }

    public void setTabs(){
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void getData() {
        showProgessBar();
        Log.e("session token ",SessionManager.getInstance().get_token());
        // Tag used to cancel the request
        RestClient.get().fetchLoanDetail(SessionManager.getInstance().get_token(), Integer.parseInt(id)).enqueue(new Callback<LoanDetailModel>() {
            @Override
            public void onResponse(Call<LoanDetailModel> call, retrofit2.Response<LoanDetailModel> response) {

                if(response.body() != null || response.isSuccessful()){

                    if (response.body().getStatus()) {
                        mainLinear.setVisibility(View.VISIBLE);
                        LoanDetailDataModel loan_detail_data = response.body().getData();

                        ArrayList<LoanInstallmentModel> loanInstallments = loan_detail_data.getInstallments();
                        //loan_total_amount.setText(""+loan_detail_data.getTotal_loan_amount().substring(1));
                        total_amount_paid.setText(Utils.convertNumToAccountingFormat(current_balance));
                        loan_id.setText("#"+id);
                        loan_label.setText(loan_detail_data.getLoan_title());
                        bundle.putParcelable("detail_data", loan_detail_data);
                        //bundle.putParcelableArrayList("installment_data",loanInstallments);
                        setTabs();

                        //Toast.makeText(DayOffActivity.this, ""+messages, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("in","status false body "+response.body());
                    }


                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(LoanInstallments.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<LoanDetailModel> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });

    }

    private void setupViewPager(ViewPager viewPager) {

        LoanDetailFragment loanDetailFragment = new LoanDetailFragment();
        bundle.putString("received_installments",received_installments);
        bundle.putString("installment_amount_intent",installment_amount_intent);

        bundle.putString("amount_paid",amount_paid);


        loanDetailFragment.setArguments(bundle);

        LoanInstallmentFragment loanInstallmentFragment = new LoanInstallmentFragment();
        bundle.putString("loan_id",id);
        loanInstallmentFragment.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(loanDetailFragment, "Loan Details");
        adapter.addFragment(loanInstallmentFragment, "Loan Installments");
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

   /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/

   /* @Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/

    @Bind(R.id.total_amount_paid)
    TextView total_amount_paid;

    @Bind(R.id.loan_id)
    TextView loan_id;

    @Bind(R.id.loan_label)
    TextView loan_label;

    @Bind(R.id.main_content)
    LinearLayout mainLinear;

  /*  @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(LoanInstallments.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }

    @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(LoanInstallments.this);
    }*/
}
