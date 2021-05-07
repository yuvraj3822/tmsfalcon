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
import com.tmsfalcon.device.tmsfalcon.entities.DriverAccidentRecordModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/14/2017.
 */

public class DriverAccidentRecordAdapter extends BaseAdapter {

    private ArrayList<DriverAccidentRecordModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    @SuppressWarnings("unused")
    private Activity activity;

    public DriverAccidentRecordAdapter(Activity activity,ArrayList<DriverAccidentRecordModel> data) {
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
        DriverAccidentRecordAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        DriverAccidentRecordModel model = mData.get(position);
        if (convertView == null) {
            holder = new DriverAccidentRecordAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_accident_record, null);
            holder.accident_natureTextView = convertView.findViewById(R.id.accident_nature);
            holder.accident_dateTextView = convertView.findViewById(R.id.accident_date);
            holder.fatalitiesTextView = convertView.findViewById(R.id.fatalities);
            holder.injuriesTextView = convertView.findViewById(R.id.injuries);

            convertView.setTag(holder);
        } else {
            holder = (DriverAccidentRecordAdapter.ViewHolder)convertView.getTag();
        }
        CustomValidator.setCustomText(holder.accident_natureTextView,model.getAccident_nature(), (View) holder.accident_natureTextView.getParent());
        CustomValidator.setCustomText(holder.accident_dateTextView,model.getAccident_date(),(View)holder.accident_dateTextView.getParent());
        CustomValidator.setCustomText(holder.fatalitiesTextView,model.getFatalities(), (View) holder.fatalitiesTextView.getParent());
        CustomValidator.setCustomText(holder.injuriesTextView,model.getInjuries(), (View) holder.injuriesTextView.getParent());

        return convertView;
    }
    public static class ViewHolder {
        TextView accident_natureTextView,accident_dateTextView,fatalitiesTextView,injuriesTextView;

    }

}


