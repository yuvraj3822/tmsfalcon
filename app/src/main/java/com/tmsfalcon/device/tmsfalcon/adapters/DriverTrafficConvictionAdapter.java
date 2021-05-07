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
import com.tmsfalcon.device.tmsfalcon.entities.DriverTraficConvictionModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/14/2017.
 */

public class DriverTrafficConvictionAdapter extends BaseAdapter {

    private ArrayList<DriverTraficConvictionModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public DriverTrafficConvictionAdapter(Activity activity,ArrayList<DriverTraficConvictionModel> data) {
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
        DriverTrafficConvictionAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);
        DriverTraficConvictionModel model = mData.get(position);
        if (convertView == null) {
            holder = new DriverTrafficConvictionAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_traffic_conviction, null);
            holder.conviction_dateTextView = convertView.findViewById(R.id.conviction_date);
            holder.locationTextView = convertView.findViewById(R.id.location);
            holder.chargeTextView = convertView.findViewById(R.id.charge);
            holder.penaltyTextView = convertView.findViewById(R.id.penalty);
            convertView.setTag(holder);
        } else {
            holder = (DriverTrafficConvictionAdapter.ViewHolder)convertView.getTag();
        }
        CustomValidator.setCustomText(holder.conviction_dateTextView,model.getConviction_date(), (View) holder.conviction_dateTextView.getParent());
        CustomValidator.setCustomText(holder.locationTextView,model.getLocation(), (View) holder.locationTextView.getParent());
        CustomValidator.setCustomText(holder.chargeTextView,model.getCharge(), (View) holder.chargeTextView.getParent());
        CustomValidator.setCustomText(holder.penaltyTextView,model.getPenalty(), (View) holder.penaltyTextView.getParent());
        return convertView;
    }
    public static class ViewHolder {
        TextView conviction_dateTextView,locationTextView,chargeTextView,penaltyTextView;

    }

}


