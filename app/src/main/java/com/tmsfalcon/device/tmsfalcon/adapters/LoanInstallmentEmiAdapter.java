package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.media.midi.MidiOutputPort;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.LoanInstallments;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.LoanEmiModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanModel;

import java.util.ArrayList;

/**
 * Created by Dell on 9/26/2018.
 */

public class LoanInstallmentEmiAdapter extends BaseAdapter {

    private ArrayList<LoanEmiModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context activity;

    public LoanInstallmentEmiAdapter(Context activity,ArrayList<LoanEmiModel> data) {
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
        ViewHolder holder = null;
        int type = getItemViewType(position);

        final LoanEmiModel model = mData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_emi, null);

            holder.loan_no = convertView.findViewById(R.id.loan_no);
            holder.loan_payment = convertView.findViewById(R.id.loan_payment);
            holder.loan_principal = convertView.findViewById(R.id.loan_principal);
            holder.loan_principal_left = convertView.findViewById(R.id.loan_principal_left);
            holder.loan_interest = convertView.findViewById(R.id.loan_interest);
            holder.loan_compound_interest = convertView.findViewById(R.id.loan_compound_interest);
            holder.loan_late_payment_charges = convertView.findViewById(R.id.loan_late_payment_charges);
            holder.loan_date = convertView.findViewById(R.id.loan_date);
            holder.loan_status = convertView.findViewById(R.id.loan_status);
            holder.settlement_textview = convertView.findViewById(R.id.settlement_textview);
            holder.settlement_layout = convertView.findViewById(R.id.settlement_layout);
            holder.loan_date_label = convertView.findViewById(R.id.loan_date_label);
            holder.skippedLayout = convertView.findViewById(R.id.skipped_layout);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            convertView.setTag(holder);
        } else {

            holder = (ViewHolder)convertView.getTag();

        }

        holder.settlement_textview.setPaintFlags(holder.settlement_textview.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.loan_no.setText("#"+model.getSindex());
        if(!CustomValidator.checkNullState(model.getEmi())){
            holder.loan_payment.setText("$"+Utils.convertNumToAccountingFormat(model.getEmi().substring(1)));

        }
        if(!CustomValidator.checkNullState(model.getPrincipal())){
            holder.loan_principal.setText("$"+Utils.convertNumToAccountingFormat(model.getPrincipal().substring(1)));

        }
        if(!CustomValidator.checkNullState(model.getPrincipal_left())){
            holder.loan_principal_left.setText("$"+Utils.convertNumToAccountingFormat(model.getPrincipal_left().substring(1)));

        }
        if(!CustomValidator.checkNullState(model.getInterest())){
            holder.loan_interest.setText("$"+Utils.convertNumToAccountingFormat(model.getInterest().substring(1)));

        }

        //holder.loan_principal.setText(Utils.convertNumToAccountingFormat(model.getPrincipal()));
        //holder.loan_principal_left.setText(Utils.convertNumToAccountingFormat(model.getPrincipal_left()));
       // holder.loan_interest.setText(Utils.convertNumToAccountingFormat(model.getInterest()));
        holder.loan_date.setText(model.getPeriod_start());
        holder.loan_status.setText(model.getStatus());
        if(model.getStatus().equalsIgnoreCase("skipped")){
            holder.skippedLayout.setVisibility(View.VISIBLE);
            holder.loan_late_payment_charges.setText(model.getLate_payment_fee());
            holder.loan_compound_interest.setText(model.getCompound_interest());
            holder.loan_date_label.setText("Due Date");
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equalsIgnoreCase("paid") || model.getStatus().equalsIgnoreCase("pending")){
            holder.skippedLayout.setVisibility(View.GONE);
            holder.loan_date_label.setText("Payment Date");
            if(model.getStatus().equalsIgnoreCase("paid")){
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            }
            else{
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
            }
        }


        return convertView;
    }

    public static class ViewHolder {
        TextView loan_no,loan_payment,loan_principal,loan_principal_left,loan_interest,loan_compound_interest,loan_late_payment_charges,
                loan_date,loan_status,settlement_textview,loan_date_label;
        LinearLayout settlement_layout,skippedLayout;
        View status_circle;

    }


}
