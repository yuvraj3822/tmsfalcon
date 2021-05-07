package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanDetailFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;
    LoanDetailDataModel loan_detail;
    String received_installments = "";
    String installment_amount_intent = "";
    String amount_paid = "";


    public LoanDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loan_detail, container, false);
        ButterKnife.bind(LoanDetailFragment.this,view);
        initIds(view);
        if (getUserVisibleHint()) {

            bundle = this.getArguments();
            loan_detail = bundle.getParcelable("detail_data");
            received_installments = bundle.getString("received_installments");
            installment_amount_intent = bundle.getString("installment_amount_intent");

            amount_paid = bundle.getString("amount_paid");

            Log.e("loan_detail ","" +loan_detail);
            putData();

        }
        return view;
    }

    public void putData(){

        CustomValidator.setCustomText(interest,loan_detail.getInterest(), (View) interest.getParent().getParent());

        CustomValidator.setCustomText(loan_date,loan_detail.getLoan_date(), (View) loan_date.getParent().getParent());

        CustomValidator.setCustomText(payment_schedule,loan_detail.getPayment_schedule(), (View) payment_schedule.getParent().getParent());

        if(!CustomValidator.checkNullState(loan_detail.getTotal_Interest_due())){
            CustomValidator.setCustomSalaryText(total_interest_due,loan_detail.getTotal_Interest_due().substring(1), (View) total_interest_due.getParent().getParent());

        }

        CustomValidator.setCustomText(no_installment,loan_detail.getNo_installment(), (View) no_installment.getParent().getParent());

        if(!CustomValidator.checkNullState(loan_detail.getInstallment_amount())){

            CustomValidator.setCustomSalaryText(installment_amount,loan_detail.getInstallment_amount().substring(1), (View) installment_amount.getParent().getParent());

        }

        CustomValidator.setCustomText(first_payment_due,loan_detail.getFirst_payment_due(), (View) first_payment_due.getParent().getParent());

        CustomValidator.setCustomText(last_payment_due,loan_detail.getLast_payment_due(), (View) last_payment_due.getParent().getParent());

//        Double paid_loan_amount = Double.parseDouble(received_installments) * Double.parseDouble(installment_amount_intent);


        Double paid_loan_amount = Double.parseDouble(amount_paid);


        String paid_loan_amount_s = NumberFormat.getNumberInstance(Locale.getDefault()).format(paid_loan_amount);
        CustomValidator.setCustomText(total_payments,"$"+paid_loan_amount_s, (View) total_payments.getParent().getParent());


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() ) { // fragment is visible and have created
            bundle = this.getArguments();
            loan_detail = bundle.getParcelable("detail_data");
            received_installments = bundle.getString("received_installments");
            installment_amount_intent = bundle.getString("installment_amount_intent");
            Log.e("loan_detail ","" +loan_detail);
            putData();
        }
    }

    public void initIds(View view){
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        //progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    @Bind(R.id.interest)
    TextView interest;

    @Bind(R.id.loan_date)
    TextView loan_date;

    @Bind(R.id.payment_schedule)
    TextView payment_schedule;

    @Bind(R.id.total_interest_due)
    TextView total_interest_due;

    @Bind(R.id.no_installment)
    TextView no_installment;

    @Bind(R.id.installment_amount)
    TextView installment_amount;

    @Bind(R.id.first_payment_due)
    TextView first_payment_due;

    @Bind(R.id.last_payment_due)
    TextView last_payment_due;

    @Bind(R.id.total_payments)
    TextView total_payments;

}
