package com.tmsfalcon.device.tmsfalcon.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.PdfWebViewActivity;
import com.tmsfalcon.device.tmsfalcon.activities.UploadedDocumentsActivity;
import com.tmsfalcon.device.tmsfalcon.activities.ViewPdf;
import com.tmsfalcon.device.tmsfalcon.customtools.ManagePdf;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/10/2017.
 */

public class UploadedDocumentsAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface {

    private ArrayList<DocumentRequestModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    progressCallback callback;
    String name,document_name;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    String uploadedDirPath = root+"/"+"downloaded_docs";
    DocumentRequestModel current_model;

    public void registerProgressCallback(progressCallback Mcallback){
        callback = Mcallback;
    }

    public UploadedDocumentsAdapter(Activity activity, ArrayList<DocumentRequestModel> data) {
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
        UploadedDocumentsAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);
        final DocumentRequestModel model = mData.get(position);
        if (convertView == null) {
            holder = new UploadedDocumentsAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.uploaded_document_list_item, null);
            holder.document_name = convertView.findViewById(R.id.document_name);
            holder.due_date = convertView.findViewById(R.id.document_due_date);
            holder.document_type = convertView.findViewById(R.id.document_type);
            holder.status = convertView.findViewById(R.id.status);
            holder.view = convertView.findViewById(R.id.view);
            holder.load_number = convertView.findViewById(R.id.load_number);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            convertView.setTag(holder);
        } else {
            holder = (UploadedDocumentsAdapter.ViewHolder)convertView.getTag();
        }
        holder.document_name.setText(model.getDocument_belongs_to()+" #"+model.getId());
        holder.due_date.setText(model.getDocument_due_date());
        holder.document_type.setText(model.getDocument_description());
        holder.status.setText(model.getStatus().substring(0,1).toUpperCase()+model.getStatus().substring(1).toLowerCase());
        if(model.getStatus().equals("uploaded")){
            /*holder.status.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
            holder.status.setTextColor(activity.getResources().getColor(R.color.status_green));*/
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        }
        else if(model.getStatus().equals("rejected")){
            /*holder.status.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
            holder.status.setTextColor(activity.getResources().getColor(R.color.status_red));*/
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equals("approved")){
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        return convertView;
    }

    public void downloadingProcess(DocumentRequestModel model){
        if(model.getFile_url() != null && model.getFile_url() != "false"){
            String[] file_type = model.getFile_type().split("/");
            if(file_type[0] != "" && file_type[0].equals("image")){
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                    /*TextView title = (TextView) dialogView.findViewById(R.id.doc_title);
                    title.setText(model.getFile_name());*/
                final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                String url = UrlController.HOST+model.getFile_url();
                   /* final RotateAnimation rotate = new RotateAnimation(0, 360,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);

                    rotate.setDuration(4000);
                    rotate.setRepeatCount(Animation.INFINITE);
                    doc_image.setAnimation(rotate);*/
                Glide.with(activity)
                        .load(url)
                        .error(R.drawable.no_image)
                        .into(doc_image);
           /* Picasso.with(activity)
                    .load(url)
                    // .placeholder( R.drawable.ic_progress_circles_big )
                    .error(R.drawable.no_image)
                    .into(doc_image, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //rotate.cancel();

                            //do smth when picture is loaded successfully
                        }

                        @Override
                        public void onError() {

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
            else if(file_type[1] != "" && file_type[1].equals("pdf")){

                //callback.showProgressCallback();
                name = model.getDocument_request_id()+"-"+model.getDocument_code()+"-"+model.getDocuments()+".pdf";
                document_name = model.getDocuments();
                    /* ManagePdf managePdf = new ManagePdf();
                    managePdf.registerDownloadInterface(UploadedDocumentsAdapter.this);
                    managePdf.downloadPdf(activity,model.getFile_url(),name);*/

                if(model.getFile_url()!= "false") {
                    Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
                    openWEbView.putExtra("pdf_url", model.getFile_url());
                    openWEbView.putExtra("file_name", "captured-document-" + model.getDocument_request_id());
                    activity.startActivity(openWEbView);
                }
                else{
                    Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
                }

                /*int downloadId = PRDownloader.download(model.getFile_url(), uploadedDirPath, name)
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
                                Intent next = new Intent(activity,ViewPdf.class);
                                next.putExtra("name",name);
                                next.putExtra("document_name",document_name);
                                activity.startActivity(next);
                            }

                            @Override
                            public void onError(Error error) {
                                callback.hideProgressCallback();
                                Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
                            }
                        });*/
            }
        }
        else{
            Toast.makeText(activity,activity.getResources().getString(R.string.no_url_exist),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void downloadPercentageCallback(int percentage) {
        Log.e("downloaded % ",""+percentage);
        callback.progressUpdate(percentage);
    }

    @Override
    public void downloadCompleteCallback() {
        callback.hideProgressCallback();
        Intent next = new Intent(activity,ViewPdf.class);
        next.putExtra("name",name);
        next.putExtra("document_name",document_name);
        activity.startActivity(next);
    }

    @Override
    public void downloadFailureCallback() {
        callback.hideProgressCallback();
        Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
    }

    @Override
    public void urlNotFoundCallback() {
        callback.hideProgressCallback();
        Toast.makeText(activity,"Url not Found.",Toast.LENGTH_LONG).show();
    }

    public static class ViewHolder {
        @SuppressWarnings("unused")
        TextView document_name,due_date,document_type,status,load_number;
        Button view;
        View status_circle;
    }
    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }
}
