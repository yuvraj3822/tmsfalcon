package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.FuelFragmentModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class FuelRebateAdapter extends BaseAdapter {

    private ArrayList<FuelFragmentModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public FuelRebateAdapter(Activity activity, ArrayList<FuelFragmentModel> data) {
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
        FuelRebateAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        FuelFragmentModel model = mData.get(position);

        if (convertView == null) {

            holder = new FuelRebateAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout./*list_item_fuel_rebate*/individual_item_fuel_rebate, null);
            convertView.setId(Integer.parseInt(model.getId()));
            holder.rebateIdTextView = convertView.findViewById(R.id.rebate_id);
            holder.rebateScheduleTextView = convertView.findViewById(R.id.rebate_schedule);
            holder.fuelStationTextView = convertView.findViewById(R.id.fuel_stations);
            holder.rebatePerGallonTextView = convertView.findViewById(R.id.rebate_per_gallon);
            holder.rebate_method = convertView.findViewById(R.id.rebate_method);
            convertView.setTag(holder);

        } else {

            holder = (FuelRebateAdapter.ViewHolder)convertView.getTag();
            convertView.setId(Integer.parseInt(model.getId()));

        }
        if(model.getMethod() != null && model.getMethod() != "" && model.getMethod().length() > 0){
            if(model.getMethod().equals("rebate_per_gallon")){
                Log.e("in","rebate_per_gallon");
                holder.rebate_method.setText("Rebate Per Gallon");
                holder.rebatePerGallonTextView.setText("$"+model.getRebate_per_gallon());
            }
            else if(model.getMethod().equals("percentage")){
                Log.e("in","percentage");
                holder.rebate_method.setText("Percentage");
                holder.rebatePerGallonTextView.setText("Varies");
            }

        }
        holder.rebateIdTextView.setText("#"+model.getId());
        CustomValidator.setCustomText(holder.rebateScheduleTextView,
                CustomValidator.capitalizeFirstLetter(model.getRebate_schedule()),
                (View) holder.rebatePerGallonTextView.getParent());
        CustomValidator.setCustomText(holder.fuelStationTextView,
                model.getFuel_stations(),
                (View) holder.fuelStationTextView.getParent());
       // CustomValidator.setCustomText(holder.rebatePerGallonTextView,model.getRebate_per_gallon(), (View) holder.rebatePerGallonTextView.getParent());
        /*holder.rebateScheduleTextView.setText(model.getRebate_schedule());
        holder.fuelStationTextView.setText(model.getFuel_stations());
        holder.rebatePerGallonTextView.setText(model.getRebate_per_gallon());*/


        return convertView;
    }
    public static class ViewHolder {
        TextView rebateScheduleTextView,fuelStationTextView,rebatePerGallonTextView,rebateIdTextView,rebate_method;

    }

}
