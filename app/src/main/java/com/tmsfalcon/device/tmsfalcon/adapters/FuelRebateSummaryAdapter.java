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
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.FuelFragmentModel;
import com.tmsfalcon.device.tmsfalcon.entities.FuelSummaryModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */
public class FuelRebateSummaryAdapter extends BaseAdapter {

    private ArrayList<FuelSummaryModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public FuelRebateSummaryAdapter(Activity activity, ArrayList<FuelSummaryModel> data) {
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
        FuelRebateSummaryAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        FuelSummaryModel model = mData.get(position);

        if (convertView == null) {

            holder = new FuelRebateSummaryAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout./*list_item_fuel_rebate_summary*/individual_item_fuel_summary, null);
            convertView.setId(Integer.parseInt(model.getId()));
            holder.rebate_id_textview = convertView.findViewById(R.id.rebate_id);
            holder.rebate_transaction_textview = convertView.findViewById(R.id.fuel_transaction);
            holder.fuel_station_texview = convertView.findViewById(R.id.fuel_station);
            holder.rebate_amount_textview = convertView.findViewById(R.id.rebate_amount);
            holder.date_textview = convertView.findViewById(R.id.date);
            holder.rebate_method_textview = convertView.findViewById(R.id.rebate_method);
            holder.rebate_method_name_textview = convertView.findViewById(R.id.rebate_method_name_textview);
            convertView.setTag(holder);

        } else {

            holder = (FuelRebateSummaryAdapter.ViewHolder)convertView.getTag();
            convertView.setId(Integer.parseInt(model.getId()));

        }
        holder.rebate_id_textview.setText("#"+model.getId());
        holder.rebate_transaction_textview.setText(model.getTransaction_id());
        holder.fuel_station_texview.setText(model.getFuel_station_name());
        holder.rebate_amount_textview.setText("$"+model.getRebate_amount());
        holder.date_textview.setText(model.getTransaction_date());
        if(model.getRebate_method() != null && model.getRebate_method().equalsIgnoreCase("rebate_per_gallon")){
            holder.rebate_method_name_textview.setText("Gallons");
        }
        /*else{
            holder.rebate_method_name_textview.setText("Percentage");
        }*/
        if(model.getTotal_galloons() != null){
            holder.rebate_method_textview.setText(Utils.convertDecimalToTwoDigits(model.getTotal_galloons()));
        }

       /* holder.rebate_method_textview.setText(model.getRebate_method()+":"+
                model.getRebate_method_value()+"*"+String.format("%.2f", Double.parseDouble(model.getTotal_galloons())));*/


        return convertView;
    }
    public static class ViewHolder {
        TextView rebate_id_textview,rebate_transaction_textview,date_textview,
                fuel_station_texview,rebate_method_textview,rebate_amount_textview,rebate_method_name_textview;

    }

}
