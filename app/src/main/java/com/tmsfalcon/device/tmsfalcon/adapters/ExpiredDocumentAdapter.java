package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

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
import com.tmsfalcon.device.tmsfalcon.activities.ViewPdf;
import com.tmsfalcon.device.tmsfalcon.activities.kotlinWork.testCam.TestCameraScreen;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ManagePdf;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.ExpiredDocumentModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Dell on 11/26/2018.
 */

public class ExpiredDocumentAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface{

    private ArrayList<ExpiredDocumentModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    ProgressDialog pd;
    private String name,document_name;
    private DocumentAdapter.progressCallback callback;
    private String other_type;
    private String root_directory_name = "tmsfalcon";
    private String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    private File myDir = new File(root+"/"+"uploaded_docs");
    String downloadedDocs = root+"/"+"downloaded_docs";

    public void registerProgressCallback(DocumentAdapter.progressCallback Mcallback){
        callback = Mcallback;
    }

    public ExpiredDocumentAdapter(Activity activity, ArrayList<ExpiredDocumentModel> data) {
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
        ViewHolder holder = null;
        int type = getItemViewType(position);

        final ExpiredDocumentModel model = mData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.expired_document_list_item, null);
            holder.document_name = convertView.findViewById(R.id.document_name);
            holder.document_type = convertView.findViewById(R.id.document_type);
            holder.status = convertView.findViewById(R.id.status);
            holder.document_thumb = convertView.findViewById(R.id.document_thumb);
            holder.viewDocBtn =  convertView.findViewById(R.id.viewDocBtn);
            holder.imageProgress = convertView.findViewById(R.id.image_progress);
            holder.expiry_date = convertView.findViewById(R.id.expiry_date);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            holder.renewDocBtn = convertView.findViewById(R.id.renewDocBtn);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.document_name.setText(model.getFile_name());
        holder.document_type.setText(model.getDocument_type());
        CustomValidator.setCustomText(holder.expiry_date,model.getExpiry_date(), (View) holder.expiry_date.getParent());

        if(!CustomValidator.checkNullState(model.getStatus())){
            //if status is not null
            holder.status.setText(""+CustomValidator.capitalizeFirstLetter(model.getStatus()));

            if(model.getStatus().equals("uploaded")){

                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
                holder.renewDocBtn.setVisibility(View.GONE);
            }
            else if(model.getStatus().equals("rejected")){

                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
            }
            else if(model.getStatus().equals("approved")){
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
            }
            else if(model.getStatus().equals("pending")){
                holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
            }
            else{

            }
        }
        else{
            ((LinearLayout)(holder.status.getParent())).setVisibility(View.GONE);
        }


        String url = UrlController.HOST+model.getUrl();
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
        /*Picasso.with(activity)
                .load(model.getThumb())
                .error(R.drawable.no_image)
                .resize(100,100)
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

                holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                        LayoutInflater inflater = activity.getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
                        dialogBuilder.setView(dialogView);
                        dialogBuilder.setCancelable(false);

                        final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                        String url = UrlController.HOST+model.getUrl();
                        final ProgressBar dialog_image_progress = dialogView.findViewById(R.id.progress_bar);
                        dialog_image_progress.setVisibility(View.VISIBLE);
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
                   /* Picasso.with(activity)
                            .load(url)
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

                holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                   /* callback.showProgressCallback();*/
                        name = model.getId()+"-"+model.getU_id()+"-"+model.getFile_name();
                        document_name = model.getFile_name();

                        if(model.getUrl()!= "false") {
                            Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
                            openWEbView.putExtra("pdf_url", model.getUrl());
                            openWEbView.putExtra("file_name", "expired-document-" + model.getId());
                            activity.startActivity(openWEbView);
                        }
                        else{
                            Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
                        }
                   /* ManagePdf managePdf = new ManagePdf();
                    managePdf.registerDownloadInterface(ExpiredDocumentAdapter.this);
                    managePdf.downloadPdf(activity,model.getUrl(),name);*/

                       /* int downloadId = PRDownloader.download(model.getUrl(), downloadedDocs, name)
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
                                        Log.e("progress : ",""+progress.toString());
                                        callback.progressUpdate(perc);
                                    }
                                })
                                .start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        callback.hideProgressCallback();
                                        String document_extension_arr[] = name.split("\\.");
                                        Log.e("document extension arr", Arrays.toString(document_extension_arr));
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
                                            File file = new File(downloadedDocs,name);
                                            intent.setDataAndType(Uri.fromFile(file), other_type);
                                            activity.startActivity(intent);
                                        }
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        callback.hideProgressCallback();
                                        Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
                                    }
                                });*/
                    }
                });
            }
        }
        else{
            Toast.makeText(activity,activity.getResources().getString(R.string.no_url_exist),Toast.LENGTH_LONG).show();
        }


        holder.renewDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent goToCapture = new Intent(activity, CaptureDocument.class);
                Intent goToCapture = new Intent(activity, TestCameraScreen.class);

                goToCapture.putExtra("document_request_id",model.getId());
                goToCapture.putExtra("document_type",model.getDocument_type());
                goToCapture.putExtra("document_name",model.getFile_name());
                goToCapture.putExtra("load_number","");
                goToCapture.putExtra("due_date","");
                goToCapture.putExtra("comment","");
                goToCapture.putExtra("status","");
                goToCapture.putExtra("key","");
                goToCapture.putExtra("is_expired","1");
                goToCapture.putExtra("document_belongs_to",model.getDoc_belongs_to());
                activity.startActivity(goToCapture);
            }
        });

        return convertView;
    }

    @Override
    public void downloadPercentageCallback(int percentage) {
        callback.progressUpdate(percentage);
    }

    @Override
    public void downloadCompleteCallback() {
        callback.hideProgressCallback();
        String document_extension_arr[] = name.split("\\.");
        // Log.e("document extension arr", Arrays.toString(document_extension_arr));
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
        TextView document_name,document_type,expiry_date,status;
        ImageView document_thumb;
        Button viewDocBtn,renewDocBtn;
        ProgressBar imageProgress;
        View status_circle;

    }
    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }

}
