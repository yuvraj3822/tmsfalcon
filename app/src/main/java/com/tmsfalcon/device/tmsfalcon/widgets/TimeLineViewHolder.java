package com.tmsfalcon.device.tmsfalcon.widgets;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.vipulasri.timelineview.TimelineView;
import com.tmsfalcon.device.tmsfalcon.R;

/**
 * Created by Dell on 3/27/2018.
 */


public class TimeLineViewHolder extends RecyclerView.ViewHolder {

    public TextView text_one,text_location,text_date_time;
    public ImageView icon_location,trip_view;
    public TimelineView mTimelineView;
    public LinearLayout main_layout,click_view;

    public TimeLineViewHolder(View itemView, int viewType) {
        super(itemView);

        text_one = (TextView) itemView.findViewById(R.id.text_one);
        text_location = (TextView) itemView.findViewById(R.id.text_location);
        text_date_time = (TextView) itemView.findViewById(R.id.text_date_time);
        icon_location = (ImageView) itemView.findViewById(R.id.icon_location);
        trip_view = (ImageView) itemView.findViewById(R.id.trip_location_detail);
        mTimelineView = (TimelineView) itemView.findViewById(R.id.time_marker);
        main_layout = (LinearLayout) itemView.findViewById(R.id.main_layout);
        click_view = (LinearLayout) itemView.findViewById(R.id.click_view);


        mTimelineView.initLine(viewType);



    }
}
