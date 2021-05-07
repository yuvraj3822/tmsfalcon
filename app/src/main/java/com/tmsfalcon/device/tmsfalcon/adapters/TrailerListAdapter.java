package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;

import android.util.Log;
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
import com.tmsfalcon.device.tmsfalcon.entities.TrailerModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class TrailerListAdapter extends BaseAdapter {

    private ArrayList<TrailerModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public TrailerListAdapter(Activity activity,ArrayList<TrailerModel> data) {
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
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TrailerListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        Log.e("here","here");
        TrailerModel model = mData.get(position);
        if (convertView == null) {
            holder = new TrailerListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_trailer, null);
            convertView.setId(Integer.parseInt(model.getId()));
            holder.makeTextView = convertView.findViewById(R.id.trailer_make);
            holder.unitTextView = convertView.findViewById(R.id.trailer_unit);
            holder.vinTextView = convertView.findViewById(R.id.trailer_vin);
            holder.trailer_thumb = convertView.findViewById(R.id.trailer_image);
            holder.first_block = convertView.findViewById(R.id.first_block);
            holder.second_block = convertView.findViewById(R.id.second_block);
            holder.imageProgress = convertView.findViewById(R.id.image_progress);
            holder.imageProgressLayout = convertView.findViewById(R.id.image_progress_layout);
            convertView.setTag(holder);
        } else {
            holder = (TrailerListAdapter.ViewHolder)convertView.getTag();
            convertView.setId(Integer.parseInt(model.getId()));
        }
        if(model.getModel().length() > 0){
            holder.makeTextView.setText(model.getMake()+","+model.getModel());
        }
        else{
            holder.makeTextView.setText(model.getMake());
        }

        if (position%5 == 0){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_one));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_one));
        } else if (position%5 == 1){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_two));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_two));
        } else if (position%5 == 2){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_three));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_three));
        } else if (position%5 == 3){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_four));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_four));
        }
        else if (position%5 == 4){
            convertView.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_five));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_five));
        }
        //holder.makeTextView.setText(model.getMake());
        holder.unitTextView.setText("#"+model.getUnit());
        holder.vinTextView.setText(model.getVin());
        String url = UrlController.HOST+model.getThumb();
        final ViewHolder finalHolder = holder;
        Glide.with(activity)
                .load(url)
                .override(200,200)
                .error(R.drawable.default_trailer)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.trailer_thumb.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.trailer_thumb.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(holder.trailer_thumb);
        /*Picasso.with(activity)
                .load(url)
                .error(R.drawable.default_trailer)
                .into(holder.trailer_thumb, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //rotate.cancel();
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.trailer_thumb.setVisibility(View.VISIBLE);
                        //do smth when picture is loaded successfully
                    }

                    @Override
                    public void onError() {
                        finalHolder.imageProgressLayout.setVisibility(View.GONE);
                        finalHolder.trailer_thumb.setVisibility(View.VISIBLE);
                        //do smth when there is picture loading error
                    }
                });*/
        return convertView;
    }
    public static class ViewHolder {
        TextView makeTextView,unitTextView,vinTextView;
        ImageView trailer_thumb;
        LinearLayout first_block;
        LinearLayout second_block;
        ProgressBar imageProgress;
        LinearLayout imageProgressLayout;

    }

}
