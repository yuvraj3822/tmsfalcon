package com.tmsfalcon.device.tmsfalcon.adapters;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.PdfWebViewActivity;
import com.tmsfalcon.device.tmsfalcon.activities.UploadedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.ViewPdf;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ManagePdf;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Android on 8/10/2017.
 */

public class DocumentAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface{

    private ArrayList<DocumentModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    ProgressDialog pd;
    private String name,document_name;
    private progressCallback callback;
    private String other_type;
    private String root_directory_name = "tmsfalcon";
    private String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    private File myDir = new File(root+"/"+"uploaded_docs");
    String downloadedDirPath = root+"/"+"downloaded_docs";
    DocumentModel current_model;
    String TAG = DocumentAdapter.this.getClass().getSimpleName();

    public void registerProgressCallback(progressCallback Mcallback){
        callback = Mcallback;
    }

    public DocumentAdapter(Activity activity, ArrayList<DocumentModel> data) {
        this.activity = activity;
        this.mData = data;
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
        DocumentAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final DocumentModel model = mData.get(position);
        if (convertView == null) {
            holder = new DocumentAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.document_list_item, null);
            holder.document_name = convertView.findViewById(R.id.document_name);
            holder.document_type = convertView.findViewById(R.id.document_type);
            //holder.document_uid = convertView.findViewById(R.id.uid);
            holder.tracking = convertView.findViewById(R.id.tracking);
            holder.document_thumb =  convertView.findViewById(R.id.document_thumb);
            holder.viewDocBtn = convertView.findViewById(R.id.viewDocBtn);
            holder.imageProgress = convertView.findViewById(R.id.image_progress);
            holder.expiry_date = convertView.findViewById(R.id.expiry_date);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            /*int colorCodeDark = Color.parseColor("#071528");
            holder.imageProgress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));*/
            convertView.setTag(holder);
        } else {
            holder = (DocumentAdapter.ViewHolder)convertView.getTag();
        }
        //Log.e("data ","Expiry Date => "+model.getExpiry_date()+" tracking status => "+model.getTracking_status()+" tracking type => "+model.getTracking_type());
        holder.document_name.setText(model.getFile_name());
        holder.document_type.setText(model.getDocument_type());
        CustomValidator.setCustomText(holder.expiry_date,model.getExpiry_date(), (View) holder.expiry_date.getParent());

        /*if(model.getTracking().length() > 0 && model.getTracking().trim().equals("No Tracking Method Selected")){
            ((LinearLayout)(holder.tracking.getParent())).setVisibility(View.GONE);
        }
        else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.tracking.setText(Html.fromHtml(model.getTracking(),Html.FROM_HTML_MODE_LEGACY));
        } else {
            holder.tracking.setText(Html.fromHtml(model.getTracking()));
        }*/
        if(!CustomValidator.checkNullState(model.getTracking_status()) && model.getTracking_status().trim().equalsIgnoreCase("Expired")){
           // Log.e("in","true");
            holder.tracking.setText(model.getTracking_status());
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
            final ViewHolder finalHolder1 = holder;
            holder.tracking.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                 /*   ViewTooltip
                            .on(activity, finalHolder1.tracking)
                            .corner(10)
                            .color(activity.getResources().getColor(R.color.progress_background_layout))
                            .textColor(activity.getResources().getColor(R.color.white_greyish))
                            .clickToHide(true)
                            .withShadow(false)
                            .position(ViewTooltip.Position.TOP)
                            .text(model.getTracking_type())
                            .show();*/
                }
            });


        }
        else{
            ((LinearLayout)(holder.tracking.getParent())).setVisibility(View.GONE);
            //Log.e("in","false");
        }

        String url = UrlController.HOST+model.getUrl();
       // Log.e(TAG,"getFile_type:"+model.getFile_type());
        if(model.getFile_type() != null && model.getFile_type() != "false"){
            final String[] file_type = model.getFile_type().split("/");
            other_type = model.getFile_type();

            final ViewHolder finalHolder = holder;
            Glide.with(activity)
                    .load(model.getThumb())
                    .error(R.drawable.no_image)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                            finalHolder.imageProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                            finalHolder.imageProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.document_thumb);
       /* Picasso.with(activity)
                .load(model.getThumb())
                .error(R.drawable.no_image)
                .resize(100,100)
                //.placeholder( R.drawable.ic_progress_circles )
                .into(holder.document_thumb, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //rotate.cancel();
                        finalHolder.imageProgress.setVisibility(View.GONE);
                        //do smth when picture is loaded successfully
                    }

                    @Override
                    public void onError() {
                        finalHolder.imageProgress.setVisibility(View.GONE);
                        //do smth when there is picture loading error
                    }
                });*/
            if(file_type[0].equals("image")){
                final RotateAnimation rotate = new RotateAnimation(0, 360,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);

                rotate.setDuration(4000);
                rotate.setRepeatCount(Animation.INFINITE);
                //holder.document_thumb.setAnimation(rotate);
            /*final ViewHolder finalHolder = holder;
            Picasso.with(activity)
                    .load(url)
                    .error(R.drawable.no_image)
                    .resize(100,100)
                    //.placeholder( R.drawable.ic_progress_circles )
                    .into(holder.document_thumb, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //rotate.cancel();
                            finalHolder.imageProgress.setVisibility(View.GONE);
                            //do smth when picture is loaded successfully
                        }

                        @Override
                        public void onError() {
                            //do smth when there is picture loading error
                        }
                    });*/
                holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = activity.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.setCancelable(false);
                    /*TextView title = (TextView) dialogView.findViewById(R.id.doc_title);
                    title.setText(model.getFile_name());*/
                        final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                        String url = UrlController.HOST+model.getUrl();
                        Log.e("url: ","-> "+url);
                        final ProgressBar dialog_image_progress = dialogView.findViewById(R.id.progress_bar);
                        dialog_image_progress.setVisibility(View.VISIBLE);
                   /* final RotateAnimation rotate = new RotateAnimation(0, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);

                    rotate.setDuration(4000);
                    rotate.setRepeatCount(Animation.INFINITE);
                    doc_image.setAnimation(rotate);*/
                        Glide.with(activity)
                                .load(url)
                                .error(R.drawable.no_image)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Drawable> target, boolean b) {
                                        dialog_image_progress.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable drawable, Object o, Target<Drawable> target, DataSource dataSource, boolean b) {
                                        dialog_image_progress.setVisibility(View.GONE);
                                        return false;
                                    }
                                })
                                .into(doc_image);
                  /*  Picasso.with(activity)
                            .load(url)
                            // .placeholder( R.drawable.ic_progress_circles_big )
                            .error(R.drawable.no_image)
                            .into(doc_image, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    dialog_image_progress.setVisibility(View.GONE);
                                    //rotate.cancel();

                                    //do smth when picture is loaded successfully
                                }

                                @Override
                                public void onError() {
                                    dialog_image_progress.setVisibility(View.GONE);
                                    //do smth when there is picture loading error
                                }
                            });*/
                        Button close_btn = dialogView.findViewById(R.id.close_btn);
                        final AlertDialog b = dialogBuilder.create();
                        b.show();
                        close_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                b.dismiss();
                            }
                        });
                    }
                });
            }
            else {
                holder.imageProgress.setVisibility(View.GONE);
                /*if(file_type[1].trim().equals("pdf")){
                    holder.document_thumb.setImageResource(R.drawable.ic_picture_as_pdf_white_24dp);
                }else{
                    holder.document_thumb.setImageResource(R.drawable.ic_file_white);
                }*/

                holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    /* callback.showProgressCallback();*/
                        int permissionCheck = ContextCompat.checkSelfPermission(activity,
                                Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(permissionCheck == PackageManager.PERMISSION_DENIED){
                            current_model = model;
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    UploadedDocumentsActivity.PERMISSIONS_REQUEST_CODE);
                        }
                        else{
                            downloadingProcess(model);
                        }

                    }
                });
            }
        }
        else{
            Toast.makeText(activity,activity.getResources().getString(R.string.no_url_exist),Toast.LENGTH_LONG).show();
        }

        return convertView;
    }

    public void downloadingProcess(DocumentModel model){
        name = model.getId()+"-"+model.getU_id()+"-"+model.getFile_name();
        document_name = model.getFile_name();
        Log.e("doc url ",""+model.getUrl());
        if(model.getUrl()!= "false") {
            Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
            openWEbView.putExtra("pdf_url", model.getUrl());
            openWEbView.putExtra("file_name", "truck-document-" + model.getId());
            activity.startActivity(openWEbView);
        }
        else{
            Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
        }
                    /*ManagePdf managePdf = new ManagePdf();
                    managePdf.registerDownloadInterface(DocumentAdapter.this);
                    managePdf.downloadPdf(activity,model.getUrl(),name);*/
        /*int downloadId = PRDownloader.download(model.getUrl(), downloadedDirPath, name)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        callback.showProgressCallback();
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        int perc = (int)(100 * progress.currentBytes / progress.totalBytes);
                        //Log.e("progress : ",""+progress.toString());
                        callback.progressUpdate(perc);
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        callback.hideProgressCallback();
                        String document_extension_arr[] = name.split("\\.");
                        Log.e("document extension arr",Arrays.toString(document_extension_arr));
                        String document_extension = document_extension_arr[document_extension_arr.length-1];
                        Log.e("document ext",document_extension);
                        if(document_extension != null && document_extension.trim().equals("pdf")){
                            Intent next = new Intent(activity,ViewPdf.class);
                            next.putExtra("name",name);
                            next.putExtra("document_name",document_name);
                            activity.startActivity(next);
                        }
                        else{
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setAction(Intent.ACTION_VIEW);
                            File file = new File(downloadedDirPath,name);
                            intent.setDataAndType(Uri.fromFile(file), other_type);
                            activity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Error error) {
                        callback.hideProgressCallback();
                        Log.e("isConnectionError ",""+error.isConnectionError());
                        Log.e("isServerError ",""+error.isServerError());
                        Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
                    }
                });*/
    }

    @Override
    public void downloadPercentageCallback(int percentage) {
        callback.progressUpdate(percentage);
    }

    @Override
    public void downloadCompleteCallback() {
        callback.hideProgressCallback();
        String document_extension_arr[] = name.split("\\.");
       // Log.e("document extension arr",Arrays.toString(document_extension_arr));
        String document_extension = document_extension_arr[document_extension_arr.length-1];
       // Log.e("document ext",document_extension);
        if(document_extension != null && document_extension.trim().equals("pdf")){
            Intent next = new Intent(activity,ViewPdf.class);
            next.putExtra("name",name);
            next.putExtra("document_name",document_name);
            activity.startActivity(next);
        }
        else{
            Intent intent = new Intent();   
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            File file = new File(myDir,name);
            intent.setDataAndType(Uri.fromFile(file), other_type);
            activity.startActivity(intent);
        }
    }

    @Override
    public void downloadFailureCallback() {
        callback.hideProgressCallback();
        Toast.makeText(activity,"Download was not successfull for document "+document_name,Toast.LENGTH_LONG).show();
    }

    @Override
    public void urlNotFoundCallback() {
        callback.hideProgressCallback();
        Toast.makeText(activity,"Url not Found for document "+document_name,Toast.LENGTH_LONG).show();
    }

    public static class ViewHolder {
        TextView document_name,document_type,expiry_date,tracking;
        ImageView document_thumb;
        Button viewDocBtn;
        ProgressBar imageProgress;
        View status_circle;

    }
    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }

}
