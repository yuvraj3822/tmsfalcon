package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.activities.LoanInstallments;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class EscrowScheduleListAdapter extends BaseAdapter {

    private ArrayList<EscrowDetailDataModel.EscrowStatement> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    String periodic_amount;

    public EscrowScheduleListAdapter(Activity activity, ArrayList<EscrowDetailDataModel.EscrowStatement> data,String periodic_amount) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.periodic_amount = periodic_amount;
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
        EscrowScheduleListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        EscrowDetailDataModel.EscrowStatement model = mData.get(position);
        if (convertView == null) {
            holder = new EscrowScheduleListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_escrow_schedule, null);

            holder.schedule_id_textview = convertView.findViewById(R.id.schedule_id);
            holder.date_textview = convertView.findViewById(R.id.schedule_date);
            holder.status_textview = convertView.findViewById(R.id.schedule_status);
            holder.payment_textview = convertView.findViewById(R.id.schedule_payment);
            holder.status_circle = convertView.findViewById(R.id.status_circle);

        convertView.setTag(holder);
        } else {

            holder = (EscrowScheduleListAdapter.ViewHolder)convertView.getTag();

        }
        //convertView.setId(Integer.parseInt(model.getSchedule_id()));
        holder.schedule_id_textview.setText("#"+model.getSchedule_id());
        holder.date_textview.setText(model.getDue_date_start()+" - "+model.getDue_date_end());
        holder.payment_textview.setText("$"+periodic_amount);

        if(model.getStatus().equalsIgnoreCase("skipped")){
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equalsIgnoreCase("paid") ||
                model.getStatus().equalsIgnoreCase("pending") ||
                model.getStatus().equalsIgnoreCase("late payment")){
            if(model.getStatus().equalsIgnoreCase("paid")){
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            }
            else{
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
            }
        }

        holder.status_textview.setText(model.getStatus());



        return convertView;
    }
    public static class ViewHolder {
        TextView schedule_id_textview,date_textview,status_textview,payment_textview;
        View status_circle;

    }

}
