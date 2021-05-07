package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.TruckModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class TruckListAdapter extends BaseAdapter {

    private ArrayList<TruckModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public TruckListAdapter(Activity activity,ArrayList<TruckModel> data) {
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
        TruckListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        TruckModel model = mData.get(position);

        if (convertView == null) {
            holder = new TruckListAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_truck, null);
            convertView.setId(Integer.parseInt(model.getId()));
            holder.makeTextView = convertView.findViewById(R.id.truck_make);
            holder.unitTextView = convertView.findViewById(R.id.truck_unit);
            holder.truck_vin = convertView.findViewById(R.id.truck_vin);
            holder.truck_thumb = convertView.findViewById(R.id.truck_image);
            holder.first_block = convertView.findViewById(R.id.first_block);
            holder.second_block = convertView.findViewById(R.id.second_block);
            holder.imageProgress = convertView.findViewById(R.id.image_progress);
            holder.imageProgressLayout = convertView.findViewById(R.id.image_progress_layout);

            convertView.setTag(holder);
        } else {
            holder = (TruckListAdapter.ViewHolder)convertView.getTag();
            convertView.setId(Integer.parseInt(model.getId()));
        }
        if(model.getModel().length() > 0){
            holder.makeTextView.setText(model.getMake()+","+model.getModel());
        }
        else{
            holder.makeTextView.setText(model.getMake());
        }

       /* if (position%5 == 0){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_one));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_one));
        } else if (position%5 == 1){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_two));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_two));
        } else if (position%5 == 2){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_three));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_three));
        } else if (position%5 == 3){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_four));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_four));
        }
        else if (position%5 == 4){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_five));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_five));
        }*/

        holder.unitTextView.setText("#"+model.getUnit());
        holder.truck_vin.setText(model.getVin());
        String url = UrlController.HOST+model.getThumb();
        final ViewHolder finalHolder = holder;
        Glide.with(activity)
                .load(url)
                .override(200,200)
                .error(R.drawable.default_truck)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.truck_thumb.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.truck_thumb.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.truck_thumb);
       /* Picasso.with(activity)
                .load(url)
                .resize(200,200)
                .centerCrop()
                .error(R.drawable.default_truck)
                .into(holder.truck_thumb, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //rotate.cancel();
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.truck_thumb.setVisibility(View.VISIBLE);
                        //do smth when picture is loaded successfully
                    }

                    @Override
                    public void onError() {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.truck_thumb.setVisibility(View.VISIBLE);
                        //do smth when there is picture loading error
                    }
                });*/
        return convertView;
    }
    public static class ViewHolder {
        TextView makeTextView,unitTextView,truck_vin;
        ImageView truck_thumb;
        LinearLayout first_block;
        LinearLayout second_block;
        ProgressBar imageProgress;
        LinearLayout imageProgressLayout;

    }

}
