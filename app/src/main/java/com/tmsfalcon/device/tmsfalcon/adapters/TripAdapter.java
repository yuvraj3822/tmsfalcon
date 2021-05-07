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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.TripResponse;
import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.TripModel;
import com.tmsfalcon.device.tmsfalcon.entities.TruckModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tmsfalcon.device.tmsfalcon.customtools.AppController.math;

/**
 * Created by Android on 7/18/2017.
 */

public class TripAdapter extends BaseAdapter {

    private ArrayList<TripModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    private String fragment_name;

    public TripAdapter(Activity activity, ArrayList<TripModel> data,String fragment_name) {
        this.activity = activity;
        this.mData = data;
        this.fragment_name = fragment_name;
        mInflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        TripAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final TripModel model = mData.get(position);

        if (convertView == null) {
            holder = new TripAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_trip, null);
            convertView.setId(model.getTrip_id());
            holder.loadType = convertView.findViewById(R.id.load_type);
            holder.tripNo = convertView.findViewById(R.id.trip_no);
            holder.origin = convertView.findViewById(R.id.origin);
            holder.distance = convertView.findViewById(R.id.distance);
            holder.destination = convertView.findViewById(R.id.destination);
            holder.stops = convertView.findViewById(R.id.stops);
            holder.loadImage = convertView.findViewById(R.id.load_type_image);
            holder.start_time = convertView.findViewById(R.id.start_time);
            holder.end_time = convertView.findViewById(R.id.end_time);
            holder.first_block = convertView.findViewById(R.id.first_block);
            holder.second_block =  convertView.findViewById(R.id.second_block);
            holder.proceed = convertView.findViewById(R.id.forward);

            convertView.setTag(holder);
        } else {
            holder = (TripAdapter.ViewHolder)convertView.getTag();
            convertView.setId(model.getTrip_id());
        }


//        Todo if require change from API
//        if(model.getLoads() == 1){
//            holder.loadType.setText("FTL");
//            holder.loadImage.setBackground(activity.getResources().getDrawable(R.drawable.ftl_trip_image));
//
//        }
//        else{
//            holder.loadType.setText("LTL");
//            holder.loadImage.setBackground(activity.getResources().getDrawable(R.drawable.ltl_trip_image));
//        }

        Log.e("seee",": "+model.getTypess());
        if(model.getTypess().equalsIgnoreCase("ltl") || model.getTypess().equalsIgnoreCase("tl")){

            holder.loadType.setText("TL");
            holder.loadImage.setBackground(activity.getResources().getDrawable(R.drawable.ltl_trip_image));
        }
        else{

            holder.loadType.setText(model.getTypess());
            holder.loadImage.setBackground(activity.getResources().getDrawable(R.drawable.ftl_trip_image));


        }


        holder.origin.setText(CustomValidator.properCase(model.getStart_location()));
        holder.destination.setText(CustomValidator.properCase(model.getEnd_location()));
        holder.start_time.setText(model.getStart_time());
        holder.end_time.setText(model.getEnd_time());

//       Float valuet =  model.getDistance().floatValue();

        holder.distance.setText(math(model.getDistance().floatValue())+" Miles");
        holder.stops.setText(model.getStops().toString());
        holder.tripNo.setText("#"+model.getTripNo());


//
//        static let tmsActiveColor = UIColor(red:85/255,green:190/255,blue:237/255,alpha:1)
//
//        // Completed status
////        static let tmsCompletedColor = UIColor(red:40/255,green: 223/255,blue:153/255,alpha:1)
//
//        static let tmsCompletedColor = UIColor(red:153/255,green: 243/255,blue:189/255,alpha:1)
//          99F3BD


        Log.e("fragmen name "," : "+fragment_name);
        if (fragment_name.equalsIgnoreCase("completed_trips")){
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.selected_color));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.selected_color));
        }else {
            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.unselected_color));
            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.unselected_color));
        }


//        if (position%5 == 0){
//            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_one));
//            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_one));
//        } else if (position%5 == 1){
//            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_two));
//            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_two));
//        } else if (position%5 == 2){
//            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_three));
//            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_three));
//        } else if (position%5 == 3){
//            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_four));
//            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_four));
//        }
//        else if (position%5 == 4){
//            holder.first_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_main_five));
//            holder.second_block.setBackgroundColor(activity.getResources().getColor(R.color.trip_blend_five));
//        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity,TripDetail.class);
                i.putExtra("trip_id",model.getTrip_id());
                i.putExtra("fragment_name",fragment_name);
                activity.startActivity(i);
            }
        });

        return convertView;
    }




    public static class ViewHolder {
        TextView loadType,tripNo,origin,distance,destination,stops,start_time,end_time;
        ImageView loadImage,proceed;
        LinearLayout first_block,second_block;

    }

}
