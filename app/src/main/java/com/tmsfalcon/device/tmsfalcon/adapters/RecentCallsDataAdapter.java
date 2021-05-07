package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.RecentCallsModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Android on 7/18/2017.
 */

public class RecentCallsDataAdapter extends BaseAdapter {

    private List<RecentCallsModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context activity;

    public RecentCallsDataAdapter(Context activity,List<RecentCallsModel> data) {
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
        RecentCallsDataAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final RecentCallsModel model = mData.get(position);

        if (convertView == null) {

            holder = new RecentCallsDataAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_recent_calls, null);
            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.phoneNumberTextView = convertView.findViewById(R.id.phone_number);
            holder.timeTextView = convertView.findViewById(R.id.time_texview);

            convertView.setTag(holder);

        } else {

            holder = (RecentCallsDataAdapter.ViewHolder)convertView.getTag();

        }
        if(model.getDialer_name().length() == 0){
            holder.titleTextView.setText(model.getDialer_phone());
        }
        else {
           holder.titleTextView.setText(model.getDialer_name());
        }
        holder.phoneNumberTextView.setText(model.getDialer_phone());
        String date = model.getDialer_time();
        //Log.e("date",""+date);
        Date d = null;

        try {
             d = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(d != null){
            String str = (String) DateUtils.getRelativeDateTimeString(

                    activity, // Suppose you are in an activity or other Context subclass

                    d.getTime(), // The time to display

                    DateUtils.MINUTE_IN_MILLIS, // The resolution. This will display only
                    // minutes (no "3 seconds ago")


                    DateUtils.WEEK_IN_MILLIS, // The maximum resolution at which the time will switch
                    // to default date instead of spans. This will not
                    // display "3 weeks ago" but a full date instead

                    0); // Eventual flag
            holder.timeTextView.setText(str);
        }

        return convertView;
    }
    public static class ViewHolder {
        TextView titleTextView,phoneNumberTextView,timeTextView;
        LinearLayout callLayout;
        ImageView typeImageView;
    }
}
