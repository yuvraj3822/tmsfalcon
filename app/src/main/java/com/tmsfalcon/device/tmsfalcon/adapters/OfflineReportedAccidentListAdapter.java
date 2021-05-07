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

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.AccidentDetailWebView;
import com.tmsfalcon.device.tmsfalcon.activities.UpdateBasicAccidentData;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.AccidentModule.KotAccidentFinalScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;
import com.tmsfalcon.device.tmsfalcon.entities.ReportedAccidentListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Android on 8/23/2017.
 */
public class OfflineReportedAccidentListAdapter extends BaseAdapter{

    private ArrayList<ReportedAccidentListModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    SessionManager sessionManager;
    ViewHolder finalHolder;

    AccidentBasicDetails db;
    AccidentWitnessTable witnessTable;
    AccidentCaptureImageTable document_db;
    SessionManager session;
    Map<String, File> document_hashmap = null;

    public OfflineReportedAccidentListAdapter(Activity activity, ArrayList<ReportedAccidentListModel> data) {
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
        OfflineReportedAccidentListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final ReportedAccidentListModel model = mData.get(position);
        if (convertView == null) {
            holder = new OfflineReportedAccidentListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_offline_accident_list, null);

            holder.accident_date_textview = convertView.findViewById(R.id.accident_date_textview);
            holder.accident_time_textview = convertView.findViewById(R.id.accident_time_textview);
            holder.accident_location_textview = convertView.findViewById(R.id.accident_location_textview);
            holder.accident_status_textview = convertView.findViewById(R.id.accident_status_textview);
            holder.accident_update_textview = convertView.findViewById(R.id.accident_update_textview);
            holder.accident_upload_textview = convertView.findViewById(R.id.accident_upload_textview);

            holder.status_circle_image = convertView.findViewById(R.id.status_circle_image);


            convertView.setTag(holder);
        } else {

            holder = (OfflineReportedAccidentListAdapter.ViewHolder)convertView.getTag();

        }

        holder.accident_date_textview.setText(model.getAccident_date());
        holder.accident_time_textview.setText(model.getAccident_time());
        if(!CustomValidator.checkNullState(model.getAccident_location())){
            holder.accident_location_textview.setText(model.getAccident_location());
        }
        else{
            holder.accident_location_textview.setText("NA");
        }

        holder.accident_status_textview.setText("#"+model.getAccident_id());

        holder.accident_update_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(activity, UpdateBasicAccidentData.class);
                i.putExtra("accident_id",model.getAccident_id());
                activity.startActivity(i);
            }
        });

        holder.accident_upload_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.accident_report_id = Integer.parseInt(model.getAccident_id());
                Intent i = new Intent(activity, KotAccidentFinalScreen.class);
                activity.startActivity(i);
            }
        });

        return convertView;
    }
    public static class ViewHolder {
        TextView accident_date_textview,accident_time_textview,accident_location_textview,accident_status_textview,accident_update_textview,accident_upload_textview;
        ImageView status_circle_image;

    }
}
