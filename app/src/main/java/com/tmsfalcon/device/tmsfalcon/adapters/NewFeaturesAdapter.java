package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.entities.NewFeatureModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class NewFeaturesAdapter extends BaseAdapter {

    private ArrayList<NewFeatureModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public NewFeaturesAdapter(Activity activity, ArrayList<NewFeatureModel> data) {
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
        ViewHolder holder = null;

        final NewFeatureModel model = mData.get(position);

        if (convertView == null) {
            holder = new NewFeaturesAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_new_feature, null);
            holder.title = convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (NewFeaturesAdapter.ViewHolder)convertView.getTag();
        }

        holder.title.setText(model.getFeature());



        return convertView;
    }

    public static class ViewHolder {
        TextView title;
    }

}
