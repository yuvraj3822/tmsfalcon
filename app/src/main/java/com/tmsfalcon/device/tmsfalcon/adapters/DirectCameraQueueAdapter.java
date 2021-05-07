package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.database.DirectUploadTable;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Android on 11/21/2017.
 */

public class DirectCameraQueueAdapter extends BaseAdapter {

    ArrayList<String> imagesList = new ArrayList<>();
    ArrayList<String> dbIdList = new ArrayList<>();
    Context context;
    Activity activity;
    private LayoutInflater mInflater;
    DirectUploadTable db;

    public DirectCameraQueueAdapter(Activity activity,Context context, ArrayList<String> list,ArrayList<String> idList){
        this.context = context;
        this.imagesList = list;
        this.dbIdList = idList;
        this.activity = activity;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        db = new DirectUploadTable(activity);
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return imagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item_queue, null);
            holder.imageView = convertView.findViewById(R.id.single_image);
            holder.deleteImage = convertView.findViewById(R.id.delete_img);
            //holder.send_to_dispatcher_layout = convertView.findViewById(R.id.send_to_dispatcher_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        final File fileLocation = new File(imagesList.get(position));
        Glide.with(context)
                .load(fileLocation)
                .into(holder.imageView);
        //Picasso.with(context).load(fileLocation).into(holder.imageView);
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
                                int result = db.delRecordById(dbIdList.get(final_position));
                                if(result != 0){
                                    imagesList.remove(final_position);
                                    Toast.makeText(activity,"Document deleted successfully.",Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(activity,"There was some issue deleting the document.Please try again later.",Toast.LENGTH_SHORT).show();
                                }
                                notifyDataSetChanged();
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

                final AlertDialog b = dialogBuilder.create();

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(b.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                b.getWindow().setAttributes(lp);


                final ViewPager viewPager = dialogView.findViewById(R.id.pager);

                DialogImageSwipeAdapter adapter = new DialogImageSwipeAdapter(activity,b,imagesList);
                viewPager.setAdapter(adapter);
                viewPager.setCurrentItem(position);

               /* final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                File fileLocation = new File(imagesList.get(final_position));
                Glide.with(context)
                        .load(fileLocation)
                        .into(doc_image);
                Button close_btn = dialogView.findViewById(R.id.close_btn);*/

                b.show();
                /*close_btn.setOnClickListener(new View.OnClickListener() {
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
        LinearLayout send_to_dispatcher_layout;
    }
}

