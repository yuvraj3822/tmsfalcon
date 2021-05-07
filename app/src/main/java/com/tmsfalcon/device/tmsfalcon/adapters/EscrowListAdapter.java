package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.EscrowDetailActivity;
import com.tmsfalcon.device.tmsfalcon.activities.LoanInstallments;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.EscrowModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class EscrowListAdapter extends BaseAdapter {

    private ArrayList<EscrowModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public EscrowListAdapter(Activity activity, ArrayList<EscrowModel> data) {
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
        EscrowListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final EscrowModel model = mData.get(position);
        if (convertView == null) {
            holder = new EscrowListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_escrow, null);

            holder.escrow_label = convertView.findViewById(R.id.escrow_label);
            holder.periodic_amount = convertView.findViewById(R.id.periodic_amount);
            holder.schedule = convertView.findViewById(R.id.payment_schedule);
            holder.start_date = convertView.findViewById(R.id.start_date);
            holder.end_date = convertView.findViewById(R.id.end_date);
            holder.balance = convertView.findViewById(R.id.balance);
            //holder.opening_balance = convertView.findViewById(R.id.opening_balance);
            holder.details_button = convertView.findViewById(R.id.details_button);


        convertView.setTag(holder);
        } else {

            holder = (EscrowListAdapter.ViewHolder)convertView.getTag();

        }
        convertView.setId(Integer.parseInt(model.getId()));

        String label_text = "";
        if(CustomValidator.checkNullState(model.getTitle())){
            label_text = "#"+model.getId();
        }
        else{
            label_text = "#"+model.getId()+" "+CustomValidator.capitalizeFirstLetter(model.getTitle());
        }
        holder.escrow_label.setText(label_text);
        holder.periodic_amount.setText("$"+model.getPeriodic_amount());
        holder.schedule.setText(model.getPayment_schedule());
        holder.start_date.setText(model.getStart_date());
        holder.end_date.setText(model.getEnd_date());
        holder.balance.setText("$"+model.getBalance());
        //holder.opening_balance.setText("$"+model.getOpening_balance());


        holder.details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, EscrowDetailActivity.class);
                i.putExtra("id",model.getId());
                activity.startActivity(i);
            }
        });

        return convertView;
    }
    public static class ViewHolder {
        TextView escrow_label,schedule,start_date,end_date,balance,opening_balance,periodic_amount;
        LinearLayout details_button;

    }

}
