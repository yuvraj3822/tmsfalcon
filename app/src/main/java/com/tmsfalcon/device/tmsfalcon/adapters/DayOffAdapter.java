package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.entities.DayOffModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class DayOffAdapter extends BaseAdapter {

    private ArrayList<DayOffModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public DayOffAdapter(Activity activity, ArrayList<DayOffModel> data) {
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
        DayOffAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        DayOffModel model = mData.get(position);

        if (convertView == null) {

            holder = new DayOffAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_day_off, null);
            holder.date = convertView.findViewById(R.id.date);
            holder.reason = convertView.findViewById(R.id.reason);
            holder.status = convertView.findViewById(R.id.status);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            holder.time = convertView.findViewById(R.id.time);
            holder.time_layout = convertView.findViewById(R.id.time_layout);
            convertView.setTag(holder);

        } else {

            holder = (DayOffAdapter.ViewHolder)convertView.getTag();

        }
        if(model.getIs_multiple() != null && model.getIs_multiple().equals("1")){
            Log.e("in","1");
            holder.date.setText(model.getStart()+" to "+model.getEnd());
            holder.time_layout.setVisibility(View.GONE);
        }
        else{
            if(model.getIs_full_off() == 0){
                holder.time_layout.setVisibility(View.VISIBLE);
                holder.time.setText(model.getStart()+" to "+model.getEnd());
            }
            else{
                holder.time_layout.setVisibility(View.GONE);
            }

            holder.date.setText(model.getDate());
        }
        holder.reason.setText(model.getReason());


        if(model.getStatus().equals("Accepted")){
            holder.status.setText(model.getStatus());
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        }
        else if(model.getStatus().equals("Rejected")){
            holder.status.setText(model.getStatus());
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equals("Pending")){
            holder.status.setText("Pending");
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
        }

        return convertView;
    }
    public static class ViewHolder {
        TextView date,reason,status,time;
        View status_circle;
        LinearLayout time_layout;
    }
}
