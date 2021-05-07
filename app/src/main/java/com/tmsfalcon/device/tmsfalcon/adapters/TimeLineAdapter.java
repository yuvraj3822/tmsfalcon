package com.tmsfalcon.device.tmsfalcon.adapters;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.TripItineraryModel;
import com.tmsfalcon.device.tmsfalcon.interfacess.clickLoadDetail;
import com.tmsfalcon.device.tmsfalcon.widgets.TimeLineViewHolder;
import com.tmsfalcon.device.tmsfalcon.widgets.VectorDrawableUtils;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Dell on 3/27/2018.
 */

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineViewHolder> {

    private List<TripItineraryModel> mFeedList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    int trip_id;
    private clickLoadDetail clickLoadDetail;

    AlertDialog dialogAlerOut;
    public TimeLineAdapter(List<TripItineraryModel> feedList,int trip_id,clickLoadDetail clickLoadDetail) {
        mFeedList = feedList;
        this.trip_id =trip_id;
        this.clickLoadDetail = clickLoadDetail;
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position,getItemCount());
    }

    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        View view;
        view = mLayoutInflater.inflate( R.layout.item_timeline, parent, false);
        return new TimeLineViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(final TimeLineViewHolder holder, final int position) {
        if(position == 0 || position == mFeedList.size()-1){
            holder.main_layout.setVisibility(View.GONE);
            //holder.mTimelineView.setMarkerSize(23);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            //params.setMargins(13, 0, 0, 0);
            holder.mTimelineView.setLayoutParams(params);
            holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_marker_active, R.color.timeline_line));
        }
        else{
            final TripItineraryModel timeLineModel = mFeedList.get(position);
            String date_time = "";
            if(timeLineModel.getType() != null && (timeLineModel.getType() != "") && (timeLineModel.getType().trim().equals("P"))) {
                //holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_trip_marker, R.color.trip_green));
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_trip_marker, R.color.trip_green));
                if(position == 1){
                    holder.text_one.setText("ORIGIN/PICKUP");
                }
                else{
                    holder.text_one.setText("PICKUP");
                }
                holder.text_one.setTextColor(mContext.getResources().getColor(R.color.trip_green));
                holder.icon_location.setBackground(mContext.getDrawable(R.drawable.ic_trip_origin));
            }
            else if(timeLineModel.getType() != null && (timeLineModel.getType() != "") && (timeLineModel.getType().trim().equals("S"))) {
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_trip_marker, R.color.trip_red));
                holder.text_one.setText("STOP");
                holder.text_one.setTextColor(mContext.getResources().getColor(R.color.trip_red));
                holder.icon_location.setBackground(mContext.getDrawable(R.drawable.ic_trip_stop));
            }
            else if(timeLineModel.getType() != null && (timeLineModel.getType() != "") && (timeLineModel.getType().trim().equals("D"))) {
                holder.mTimelineView.setMarker(VectorDrawableUtils.getDrawable(mContext, R.drawable.ic_trip_marker, R.color.trip_yellow));
                holder.text_one.setText("DROP OFF");
                holder.text_one.setTextColor(mContext.getResources().getColor(R.color.trip_yellow));
                holder.icon_location.setBackground(mContext.getDrawable(R.drawable.ic_trip_dropoff));
            }
            final String location = timeLineModel.getCity()+","+timeLineModel.getState()+" "+timeLineModel.getZipcode();
            holder.text_location.setText(location);


            Log.e("getTime: ",": "+timeLineModel.getTime());
            Log.e("getEnd_time: ",": "+timeLineModel.getEnd_time());



//            if(timeLineModel.getEnd_time().isEmpty()){
//                date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim();
//            }
//            else{
//                date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim()+" - "+timeLineModel.getEnd_time().trim();
//            }


            date_time = setTimeSpan(timeLineModel.getEnd_time(),timeLineModel);


            holder.text_date_time.setText(date_time);
            final String finalDate_time = date_time;
            holder.click_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickLoadDetail.loadDetailShow(timeLineModel,position,holder.click_view);
                }
            });
        }
    }


    private Boolean allDetailIsEmpty(String city,String state,String zipcode){
        if (city.isEmpty()&& state.isEmpty() && zipcode.isEmpty()){
            return true;
        }else {
            return false;
        }
    }


    private String setTimeSpan(String endtime, TripItineraryModel timeLineModel){
        String date_time = "";
        if(endtime.isEmpty()){
            date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim();
        }
        else{
            date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim()+" - "+timeLineModel.getEnd_time().trim();
        }
        return date_time;
    }


private void manageHeaderSection(String contactPer, String address, Boolean isEmpty,
                                 String dateTime, String phnNo, TextView textContactPerson, TextView textAddress,
                                 TextView textDetailAddress, TextView textDateTime, TextView textPhnNo,TripItineraryModel timeLineModel){

        if (contactPer.isEmpty()){
            textContactPerson.setVisibility(View.GONE);
            textContactPerson.setText("");
        }
        else {
            textContactPerson.setVisibility(View.VISIBLE);
            textContactPerson.setText(contactPer);

        }

    if (address.isEmpty()){
        textAddress.setVisibility(View.GONE);
        textAddress.setText("");

    }
    else {
        textAddress.setVisibility(View.VISIBLE);
        textAddress.setText(Html.fromHtml("<u>" +address+ "</u>"));

    }
    if (isEmpty){
        textDetailAddress.setVisibility(View.GONE);
        textDetailAddress.setText("");

    }
    else {
        textDetailAddress.setVisibility(View.VISIBLE);
        textDetailAddress.setText(addressFormation(timeLineModel.getCity(),timeLineModel.getState(),timeLineModel.getZipcode()));

    }if (dateTime.isEmpty()){
        textDateTime.setVisibility(View.GONE);
        textDateTime.setText("");

    }
    else {
        textDateTime.setVisibility(View.VISIBLE);
        textDateTime.setText(dateTime);

    }
    if (phnNo.isEmpty()){
        textPhnNo.setVisibility(View.GONE);
        textPhnNo.setText("");

    }
    else {
        textPhnNo.setVisibility(View.VISIBLE);
        textPhnNo.setText(Html.fromHtml("<u>" +phnNo+ "</u>"));


    }

}

    private void manageViewsAsPerData(String pickNo, String ref, String ins,
                                      LinearLayout hashView, LinearLayout clipView,
                                      LinearLayout insView, TextView textPicNo,
                                      TextView textRefNo, TextView textInsNo){
        if (pickNo.isEmpty()){

            hashView.setVisibility(View.GONE);
            textPicNo.setText("");

        } else {
            hashView.setVisibility(View.VISIBLE);
            textPicNo.setText(pickNo);


        }
        if (ref.isEmpty()){
            clipView.setVisibility(View.GONE);
            textRefNo.setText("");

        } else {
            clipView.setVisibility(View.VISIBLE);
            textRefNo.setText(ref);
        }

        if (ins.isEmpty()){
            insView.setVisibility(View.GONE);
            textInsNo.setText("");

        } else {
            insView.setVisibility(View.VISIBLE);
            textInsNo.setText(ins);
        }
    }



    private Spanned addressFormation(String city, String state, String zipcode){
       String value =  city+","+" "+state+" "+zipcode;
       Spanned result = Html.fromHtml("<u>" +value+ "</u>");
        Log.e("result",":  "+result);
        return result;
    }

    @Override
    public int getItemCount() {
        return (mFeedList!=null? mFeedList.size():0);
    }

}
