package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
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
import com.tmsfalcon.device.tmsfalcon.entities.AccidentDamageDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class AccidentDamageListAdapter extends BaseAdapter{

    private ArrayList<AccidentDamageDetailsModel> mData = new ArrayList<>();
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
    public AccidentDamageListAdapter(Activity activity) {
        this.activity = activity;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
    }

    @Override
    public int getCount() {
        return 1;
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
            holder = new AccidentDamageListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_accident_damage, null);

            convertView.setTag(holder);
        } else {

            holder = (AccidentDamageListAdapter.ViewHolder)convertView.getTag();

        }


        return convertView;
    }
    public static class ViewHolder {
        TextView witness_name_textview,witness_phone_textview,witness_statement_textview;
        LinearLayout audio_layout;
        SeekBar seekBar;
        Button play_img;
        ImageView cancel_img;

    }

}
