package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.LoanInstallments;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.LoanModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class LoanListAdapter extends BaseAdapter {

    private ArrayList<LoanModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public LoanListAdapter(Activity activity,ArrayList<LoanModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return mData.size();
    }

    @Override
    public Object getItem(int position) {

        return mData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LoanListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final LoanModel model = mData.get(position);
        if (convertView == null) {

            holder = new LoanListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_loan, null);

            holder.down_payment = convertView.findViewById(R.id.down_payment);
            holder.last_installment = convertView.findViewById(R.id.last_installment);
            holder.installments = convertView.findViewById(R.id.installments);
            holder.duration = convertView.findViewById(R.id.duration);
            holder.installment_amount = convertView.findViewById(R.id.installment_amount);
            holder.amount_paid = convertView.findViewById(R.id.amount_paid);
            holder.notes = convertView.findViewById(R.id.notes);
            holder.payment_schedule = convertView.findViewById(R.id.payment_schedule);
            holder.loanAmount = convertView.findViewById(R.id.loan_amount);
            holder.loan_label = convertView.findViewById(R.id.loan_label);
            holder.installment_button = convertView.findViewById(R.id.installment_button);
            convertView.setTag(holder);

        } else {

            holder = (LoanListAdapter.ViewHolder)convertView.getTag();

        }
        Double total_loan_amount = Double.parseDouble(model.getTotal_loan_amount());
        Double installment_amount = Double.parseDouble(model.getInstallment_amount());
        Double no_received_installments = Double.parseDouble(model.getNo_recieved_installment());

        Double deferred = Double.parseDouble(String.valueOf(model.getDeferred()));



        Double amount_paid;

//        if (model.getAmount_paid() != null || !model.getAmount_paid().equalsIgnoreCase("null"))
//            amount_paid = Double.parseDouble(model.getAmount_paid());
//         else amount_paid = Double.valueOf(0);


        if (model.getAmount_paid() != null){
            if (model.getAmount_paid().equalsIgnoreCase("null")){
                amount_paid = Double.valueOf(0);
            }else {
                amount_paid = Double.parseDouble(model.getAmount_paid());
            }
        } else {
            amount_paid = Double.valueOf(0);
        }


        Double down_payment = Double.parseDouble(model.getDown_payment());


//        Double current_balance = total_loan_amount - (no_received_installments*installment_amount);



//        Double current_balance = total_loan_amount - amount_paid - down_payment;

//        Double current_balance = total_loan_amount - amount_paid - down_payment;

        Double current_balance = total_loan_amount - amount_paid + (deferred * installment_amount);

Log.e("currentbal:  ",""+current_balance+ "= "+ total_loan_amount+"- "+amount_paid+"+ ("+deferred+"* "+installment_amount+")" );
        final String current_balance_str = Utils.convertDecimalToTwoDigits(String.valueOf(current_balance));

        convertView.setId(Integer.parseInt(model.getId()));
        CustomValidator.setCustomSalaryText(holder.down_payment,model.getDown_payment(), (View) holder.down_payment.getParent().getParent());
        CustomValidator.setCustomSalaryText(holder.last_installment,model.getDown_payment(), (View) holder.down_payment.getParent().getParent());
        CustomValidator.setCustomSalaryText(holder.installment_amount,model.getInstallment_amount(), (View) holder.installment_amount.getParent().getParent());
        CustomValidator.setCustomSalaryText(holder.amount_paid,model.getAmount_paid(), (View) holder.amount_paid.getParent().getParent());
        CustomValidator.setCustomSalaryText(holder.loanAmount,current_balance_str, (View) holder.loanAmount.getParent().getParent());
        CustomValidator.setCustomText(holder.notes,model.getNotes(), (View) holder.notes.getParent().getParent());
        CustomValidator.setCustomText(holder.payment_schedule,model.getPayment_schedule(), (View) holder.payment_schedule.getParent().getParent());

        holder.installments.setText(model.getNo_recieved_installment()+" / "+model.getNo_installment());
        holder.duration.setText(model.getStart_date()+" - "+model.getExpiring_date());
        String label_text = "";
        if(CustomValidator.checkNullState(model.getLoan_title())){
            label_text = "#"+model.getId();
        }
        else{
            label_text = "#"+model.getId()+" "+model.getLoan_title();
        }
        holder.loan_label.setText(label_text);

        holder.installment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,LoanInstallments.class);
                i.putExtra("id",model.getId());
                i.putExtra("received_installments",model.getNo_recieved_installment());
                i.putExtra("current_balance",current_balance_str);
                i.putExtra("installment_amount_intent",model.getInstallment_amount());
                i.putExtra("amount_paid",model.getAmount_paid());
                activity.startActivity(i);
            }
        });

        return convertView;
    }
    public static class ViewHolder {
        TextView down_payment,last_installment,installments,duration,
                installment_amount,amount_paid,notes,payment_schedule,loanAmount,loan_label;
        LinearLayout installment_button;

    }

}
