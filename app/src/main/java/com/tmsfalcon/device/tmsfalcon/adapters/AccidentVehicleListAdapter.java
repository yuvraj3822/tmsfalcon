package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.AccidentWitnessTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class AccidentVehicleListAdapter extends BaseAdapter{

    private ArrayList<AccidentVehicleDetailsModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    SessionManager sessionManager;
    ViewHolder holder = null;


   /* public AccidentVehicleListAdapter(Activity activity, ArrayList<AccidentVehicleDetailsModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
    }*/
    public AccidentVehicleListAdapter(Activity activity) {
        this.activity = activity;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);

    }

    @Override
    public int getCount() {
        return 2;
        //return mData.size();
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

        int type = getItemViewType(position);

        //final AccidentVehicleDetailsModel model = mData.get(position);
        if (convertView == null) {
            holder = new AccidentVehicleListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_accident_vehicle, null);
            holder.dotNo = convertView.findViewById(R.id.dotno);
            holder.licnesePlate = convertView.findViewById(R.id.license_no);
            holder.licenseState = convertView.findViewById(R.id.license_state);
            holder.companyName = convertView.findViewById(R.id.company_names);
            holder.policyNo = convertView.findViewById(R.id.policy_no);
//            holder.policyHolderName = convertView.findViewById(R.id.);
            holder.ownerName = convertView.findViewById(R.id.owner_name);
            holder.ownerPhoneNo = convertView.findViewById(R.id.phone_no);
            holder.ownerAddress = convertView.findViewById(R.id.address);
            holder.towingCompanyName = convertView.findViewById(R.id.towing_company_name);
            holder.towingPhoneNo = convertView.findViewById(R.id.towing_phone_no);
            convertView.setTag(holder);
        } else {
            holder = (AccidentVehicleListAdapter.ViewHolder)convertView.getTag();
        }






        return convertView;
    }
    public static class ViewHolder {
        TextView dotNo,licnesePlate,licenseState,companyName,policyNo,policyHolderName,ownerName,ownerPhoneNo,ownerAddress,towingCompanyName,towingPhoneNo;

    }

}
