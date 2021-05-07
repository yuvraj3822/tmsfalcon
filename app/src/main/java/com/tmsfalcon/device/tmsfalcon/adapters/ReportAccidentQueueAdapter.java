package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.database.AccidentCaptureImageTable;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Android on 10/12/2017.
 */

public class ReportAccidentQueueAdapter extends BaseAdapter {

    ArrayList<String> imagesList = new ArrayList<>();
    Context context;
    Activity activity;
    private LayoutInflater mInflater;
    AccidentCaptureImageTable db;
    ArrayList<AccidentCaptureModel> arrayList = new ArrayList<>();

    public ReportAccidentQueueAdapter(Activity activity, Context context, ArrayList<AccidentCaptureModel> list,ArrayList<String> image_list){
        this.context = context;
        this.activity = activity;
        this.arrayList = list;
        this.imagesList = image_list;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new AccidentCaptureImageTable(activity);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Log.e("position",""+position);
        ReportAccidentQueueAdapter.ViewHolder holder = null;
        if (convertView == null) {
            holder = new ReportAccidentQueueAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item_accident_queue, null);
            holder.imageView = convertView.findViewById(R.id.single_image);
            holder.deleteImage = convertView.findViewById(R.id.delete_img);
            holder.doc_type = convertView.findViewById(R.id.doc_type);
            convertView.setTag(holder);
        } else {
            holder = (ReportAccidentQueueAdapter.ViewHolder)convertView.getTag();
        }
        final AccidentCaptureModel model = arrayList.get(position);

        File fileLocation = null;
        try{
            fileLocation = new File(model.getImage_url());
        }
        catch (Exception e){

        }
        holder.doc_type.setText(model.getDoc_type());
        Glide.with(context)
                .load(fileLocation)
                .into(holder.imageView);
       // Picasso.with(context).load(fileLocation).into(holder.imageView);
        final int final_position = position;
        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                int result = db.deleteImagesById(String.valueOf(model.getId()));
                                Log.e("result "," is "+result+" id is "+model.getId());
                                if(result != 0){
                                    arrayList.remove(final_position);
                                    Toast.makeText(activity,"Image deleted successfully.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(activity,"There was some issue deleting the Image.Please try again later.",Toast.LENGTH_SHORT).show();
                                }
                                notifyDataSetChanged();
                               // db.showRecords(db);
                                dialog.dismiss();
                            }})
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = activity.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_images_swipe_viewpager, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                File fileLocation = new File(imagesList.get(final_position));
               /* Glide.with(context)
                        .load(fileLocation)
                        .into(doc_image);
                Button close_btn = dialogView.findViewById(R.id.close_btn);*/
                final AlertDialog b = dialogBuilder.create();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(b.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                b.getWindow().setAttributes(lp);

                final ViewPager viewPager = dialogView.findViewById(R.id.pager);
                Log.e("imageslist"," is "+imagesList.size());
                DialogImageSwipeAdapter adapter = new DialogImageSwipeAdapter(activity,b,imagesList);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(position);

                b.show();

              /*  close_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        b.dismiss();
                    }
                });*/
            }
        });
        return convertView;
    }
    public static class ViewHolder {
        ImageView imageView,deleteImage;
        TextView doc_type;

    }
}
