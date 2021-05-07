package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import androidx.appcompat.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.entities.LoadPrefereneSpinnerModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class LoadPreferenceSpinnerAdapter extends BaseAdapter {

    private ArrayList<LoadPrefereneSpinnerModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public LoadPreferenceSpinnerAdapter(Activity activity, ArrayList<LoadPrefereneSpinnerModel> data) {
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
        LoadPreferenceSpinnerAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final LoadPrefereneSpinnerModel model = mData.get(position);

        if (convertView == null) {
            holder = new LoadPreferenceSpinnerAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.load_preference_custom_spinner, null);
            holder.trailer_type_textview = convertView.findViewById(R.id.trailer_type_textview);
            holder.trailer_type_short_name = convertView.findViewById(R.id.trailer_type_short_name);
            holder.checkBox = convertView.findViewById(R.id.trailer_type_checkbox);

            convertView.setTag(holder);
        } else {
            holder = (LoadPreferenceSpinnerAdapter.ViewHolder)convertView.getTag();

        }

        if(position == 0){
            holder.checkBox.setVisibility(View.GONE);
        }
        else{
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        holder.trailer_type_textview.setText(model.getTrailer_type());
        holder.trailer_type_short_name.setText(model.getTrailer_type_short_name());


        return convertView;
    }
    public static class ViewHolder {

        TextView trailer_type_textview,trailer_type_short_name;
        AppCompatCheckBox checkBox;

    }

}
