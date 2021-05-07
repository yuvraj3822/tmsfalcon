package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.DriverExperienceModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/13/2017.
 */

public class DriverExperienceRecordAdapter extends BaseAdapter {

    private ArrayList<DriverExperienceModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    public DriverExperienceRecordAdapter(Activity activity,ArrayList<DriverExperienceModel> data) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DriverExperienceRecordAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        DriverExperienceModel model = mData.get(position);
        if (convertView == null) {
            holder = new DriverExperienceRecordAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_experience_record, null);
            holder.equipmentClassTextView = convertView.findViewById(R.id.equipment_class);
            holder.equipmentTypeTextView = convertView.findViewById(R.id.equipment_type);
            holder.durationTextView = convertView.findViewById(R.id.duration);
            holder.totalMilesTextView = convertView.findViewById(R.id.total_miles);

            convertView.setTag(holder);
        } else {
            holder = (DriverExperienceRecordAdapter.ViewHolder)convertView.getTag();
        }
        String duration = model.getFrom_date()+" to "+model.getTo_date();

        CustomValidator.setCustomText(holder.equipmentClassTextView,model.getEquipment_class(), (View) holder.equipmentClassTextView.getParent());
        CustomValidator.setCustomText(holder.equipmentTypeTextView,model.getEquipment_type(), (View) holder.equipmentTypeTextView.getParent());
        CustomValidator.setCustomText(holder.durationTextView,duration, (View) holder.durationTextView.getParent());
        CustomValidator.setCustomText(holder.totalMilesTextView,model.getTotal_miles(), (View) holder.totalMilesTextView.getParent());
        return convertView;
    }
    public static class ViewHolder {
        TextView equipmentClassTextView,equipmentTypeTextView,durationTextView,totalMilesTextView;

    }

}

