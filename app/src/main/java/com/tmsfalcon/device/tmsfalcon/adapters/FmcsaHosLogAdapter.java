package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.FmcsaHosLogModel;
import com.tmsfalcon.device.tmsfalcon.entities.HosViolationModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Android on 7/18/2017.
 */

public class FmcsaHosLogAdapter extends BaseAdapter {

    private ArrayList<FmcsaHosLogModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public FmcsaHosLogAdapter(Activity activity, ArrayList<FmcsaHosLogModel> data) {
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
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("unused")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        FmcsaHosLogModel model = mData.get(position);

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_fmcsa_hos_log_violation, null);
            holder.report_textview = convertView.findViewById(R.id.report_textview);
            holder.state_textview = convertView.findViewById(R.id.state_textview);
            holder.location_textview = convertView.findViewById(R.id.location_textview);
            holder.section_textview = convertView.findViewById(R.id.section_textview);
            holder.description_textview = convertView.findViewById(R.id.description_textview);
            holder.date_time_textview = convertView.findViewById(R.id.date_time_textview);
            holder.resource_type_textview = convertView.findViewById(R.id.resource_type_textview);
            holder.unit_no_textview = convertView.findViewById(R.id.unit_no_textview);
            holder.vehicle_layout = convertView.findViewById(R.id.vehicle_layout);

            convertView.setTag(holder);

        } else {

            holder = (FmcsaHosLogAdapter.ViewHolder)convertView.getTag();

        }
        holder.report_textview.setText(model.getReport());
        holder.state_textview.setText(model.getState());
        holder.location_textview.setText(model.getLocation());
        holder.section_textview.setText(model.getSection());
        holder.description_textview.setText(model.getDescription());
        holder.date_time_textview.setText(model.getDatetime());
        if(model.getRequest_type_vehicle() == 1){
            holder.vehicle_layout.setVisibility(View.VISIBLE);
            holder.unit_no_textview.setText(model.getUnit_number());
            holder.resource_type_textview.setText(model.getResource_type());
        }
        else{
            holder.vehicle_layout.setVisibility(View.GONE);
        }


        return convertView;
    }
    public static class ViewHolder {
        LinearLayout vehicle_layout;
        TextView report_textview,location_textview,state_textview,section_textview,
                date_time_textview,description_textview,resource_type_textview,unit_no_textview;
    }
}
