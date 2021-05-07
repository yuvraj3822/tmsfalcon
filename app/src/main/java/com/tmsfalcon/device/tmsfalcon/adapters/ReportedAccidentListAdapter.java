package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.AccidentDetailWebView;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.ReportedAccidentListModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class ReportedAccidentListAdapter extends BaseAdapter {

    private ArrayList<ReportedAccidentListModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    SessionManager sessionManager;
    ViewHolder finalHolder;

    public ReportedAccidentListAdapter(Activity activity, ArrayList<ReportedAccidentListModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);

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
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ReportedAccidentListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final ReportedAccidentListModel model = mData.get(position);
        if (convertView == null) {
            holder = new ReportedAccidentListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_accident_list, null);

            holder.accident_date_textview = convertView.findViewById(R.id.accident_date_textview);
            holder.accident_time_textview = convertView.findViewById(R.id.accident_time_textview);
            holder.accident_location_textview = convertView.findViewById(R.id.accident_location_textview);
            holder.accident_status_textview = convertView.findViewById(R.id.accident_status_textview);
            holder.accident_detail_textview = convertView.findViewById(R.id.accident_detail_textview);

            holder.status_circle_image = convertView.findViewById(R.id.status_circle_image);

            convertView.setTag(holder);
        } else {

            holder = (ReportedAccidentListAdapter.ViewHolder)convertView.getTag();

        }

        holder.accident_date_textview.setText(model.getAccident_date());
        holder.accident_time_textview.setText(model.getAccident_time());
        if(!CustomValidator.checkNullState(model.getAccident_location())){
            holder.accident_location_textview.setText(model.getAccident_location());
        }
        else{
            holder.accident_location_textview.setText("NA");
        }

        if(model.getAccident_status() != null && model.getAccident_status() != "" && model.getAccident_status().length() > 0){
            holder.accident_status_textview.setText("#"+model.getAccident_id()+" : "+CustomValidator.capitalizeFirstLetter(model.getAccident_status()));
        }

        holder.accident_detail_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, AccidentDetailWebView.class);
                i.putExtra("url",model.getAccident_detail_url());
                activity.startActivity(i);

//                Toast.makeText(activity, "Under Development", Toast.LENGTH_SHORT).show();

            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView accident_date_textview,accident_time_textview,accident_location_textview,accident_status_textview,accident_detail_textview;
        ImageView status_circle_image;



    }

}
