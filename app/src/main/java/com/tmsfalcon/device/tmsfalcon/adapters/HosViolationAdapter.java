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
import com.tmsfalcon.device.tmsfalcon.entities.DayOffModel;
import com.tmsfalcon.device.tmsfalcon.entities.HosViolationModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class HosViolationAdapter extends BaseAdapter {

    private ArrayList<HosViolationModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public HosViolationAdapter(Activity activity, ArrayList<HosViolationModel> data) {
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

        HosViolationModel model = mData.get(position);

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_hos_violation, null);
            holder.violationTextView = convertView.findViewById(R.id.violation);
            holder.startTextView = convertView.findViewById(R.id.start);
            holder.endTextView = convertView.findViewById(R.id.end);
            convertView.setTag(holder);

        } else {

            holder = (HosViolationAdapter.ViewHolder)convertView.getTag();

        }

        CustomValidator.setCustomText(holder.violationTextView,model.getViolation(), (View) holder.violationTextView.getParent());
        CustomValidator.setCustomText(holder.startTextView,model.getStart(), (View) holder.startTextView.getParent());
        CustomValidator.setCustomText(holder.endTextView,model.getViolation(), (View) holder.endTextView.getParent());


        return convertView;
    }
    public static class ViewHolder {
        TextView violationTextView,startTextView,endTextView;
    }
}
