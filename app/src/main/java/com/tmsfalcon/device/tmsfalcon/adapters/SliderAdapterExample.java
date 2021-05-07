package com.tmsfalcon.device.tmsfalcon.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tmsfalcon.device.tmsfalcon.R;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;

    public SliderAdapterExample(Context context) {
        this.context = context;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {

        switch (position) {
            case 0:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_dashboard))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Dashboard");
                viewHolder.description_textview.setText("Easy home screen access to most used features of the app.");
                break;
            case 1:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_scanner))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Document Scanner");
                viewHolder.description_textview.setText("Picture and scan documents to send to dispatch or fax and email to anybody.");
                break;
            case 2:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_my_loads))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Load Board");
                viewHolder.description_textview.setText("Search available loads and book loads from this screen without making any calls.");
                break;

            case 3:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_upcoming_trips))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("My Loads");
                viewHolder.description_textview.setText("Preview all your current, upcoming and completed loads on this screen.");
                break;
            case 4:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_loads))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Load Detail");
                viewHolder.description_textview.setText("In this screen you can view detailed load itinerary and documents attached to load.\n");
                break;
            case 5:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_documents))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Documents");
                viewHolder.description_textview.setText("Preview all your documents and update any expiring documents with ease.");
                break;

            case 6:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_fuel_rebates))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Fuel Rebates");
                viewHolder.description_textview.setText("Preview your current fuel rebate credit or view any rebates in history.");
                break;
            case 7:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_settlements))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Settlements");
                viewHolder.description_textview.setText("Detailed view of settlements including all your loans, escrows, advances and deductions.");
                break;

            case 8:
                Glide.with(viewHolder.itemView)
                        .load(context.getResources().getDrawable(R.drawable.app_tour_fault_codes))
                        .into(viewHolder.imageViewBackground);
                viewHolder.title_textview.setText("Truck/Trailer");
                viewHolder.description_textview.setText("View all details and documents of your assigned truck or trailer. You can also view all current or past truck engine fault codes.");
                break;



        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return 9;
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView title_textview,description_textview;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slider_image);
            title_textview = itemView.findViewById(R.id.slider_title_textview);
            description_textview = itemView.findViewById(R.id.slider_description_textview);
            this.itemView = itemView;
        }
    }
}
