package com.tmsfalcon.device.tmsfalcon.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
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
import android.widget.LinearLayout;
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
import com.tmsfalcon.device.tmsfalcon.interfacess.requestDocumentClick;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Android on 8/10/2017.
 */

public class DocumentRequestAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface{

    private ArrayList<DocumentRequestModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    progressCallback callback;
    String name,document_name;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    String uploadedDirPath = root+"/"+"downloaded_docs";
    requestDocumentClick reqClick;

    public DocumentRequestAdapter(Activity activity, ArrayList<DocumentRequestModel> data, requestDocumentClick reqClick) {
        this.activity = activity;
        this.mData = data;
        this.reqClick = reqClick;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void registerProgressCallback(progressCallback Mcallback){
        callback = Mcallback;
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
        DocumentRequestAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);
        final DocumentRequestModel model = mData.get(position);
        if (convertView == null) {
            holder = new DocumentRequestAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.document_request_list_item, null);
            holder.document_name = convertView.findViewById(R.id.document_name);
            holder.due_date = convertView.findViewById(R.id.document_due_date);
            holder.comment = convertView.findViewById(R.id.document_comment);
            holder.respond = convertView.findViewById(R.id.respond);
            holder.status = convertView.findViewById(R.id.status);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            holder.load_number = convertView.findViewById(R.id.load_number);
            holder.rejected_reason_layout = convertView.findViewById(R.id.rejected_reason_layout);
            holder.rejected_reason = convertView.findViewById(R.id.rejected_reason);
            holder.view_previous_doc = convertView.findViewById(R.id.previous_doc);
            convertView.setTag(holder);
        } else {
            holder = (DocumentRequestAdapter.ViewHolder)convertView.getTag();
        }
        holder.document_name.setText(model.getDocuments());
        holder.due_date.setText(model.getDocument_due_date());
        holder.comment.setText(model.getComment());
        holder.respond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent goToCapture = new Intent(activity, CaptureDocument.class);

               reqClick.reqClick(model);



            }
        });
        holder.status.setText(model.getStatus().substring(0,1).toUpperCase()+model.getStatus().substring(1).toLowerCase());
        if(model.getStatus().equals("accepted")){
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        }
        else if(model.getStatus().equals("rejected")){
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equals("pending")){
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
        }
        if(model.getRejected_reason() != null && model.getRejected_reason() != "null" && model.getRejected_reason().length() > 0){
            holder.rejected_reason_layout.setVisibility(View.VISIBLE);
            holder.rejected_reason.setText(model.getRejected_reason());
            holder.view_previous_doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int permissionCheck = ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.READ_EXTERNAL_STORAGE);
                    if(permissionCheck == PackageManager.PERMISSION_DENIED){

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
        return convertView;
    }

    public void downloadingProcess(DocumentRequestModel model){
        String[] file_type = model.getPrevious_doc_type().split("/");
        Log.e("file_type", Arrays.toString(file_type));
        if(file_type[0] != "false"){
            if(file_type[0] != "" && file_type[0].equals("image")){
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
                LayoutInflater inflater = activity.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.image_doc_alert, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);
                final ImageView doc_image = dialogView.findViewById(R.id.doc_image);
                String url = UrlController.HOST+model.getPrevious_doc_url();
                Glide.with(activity)
                        .load(url)
                        .error(R.drawable.no_image)
                        .into(doc_image);
                       /* Picasso.with(activity)
                                .load(url)
                                .error(R.drawable.no_image)
                                .into(doc_image, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {

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

                // callback.showProgressCallback();
                name = model.getDocument_request_id()+"-"+model.getDocument_code()+"-"+model.getDocuments()+".pdf";
                document_name = model.getDocuments();
                if(model.getPrevious_doc_url()!= "false") {
                    Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
                    openWEbView.putExtra("pdf_url", model.getPrevious_doc_url());
                    openWEbView.putExtra("file_name", "requested-document-" + model.getDocument_request_id());
                    activity.startActivity(openWEbView);
                }
                else{
                    Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
                }
                        /*ManagePdf managePdf = new ManagePdf();
                        managePdf.registerDownloadInterface(DocumentRequestAdapter.this);
                        managePdf.downloadPdf(activity,model.getPrevious_doc_url(),name);*/
               /* int downloadId = PRDownloader.download(model.getPrevious_doc_url(), uploadedDirPath, name)
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
                        });
*/
            }
        }
        else{
            Toast.makeText(activity,"File does not exist.",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void downloadPercentageCallback(int percentage) {
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
        showToastOnMainThread("Download was not successfull");
        //Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
    }

    public void showToastOnMainThread(final String text){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, ""+text, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void urlNotFoundCallback() {
        callback.hideProgressCallback();
        showToastOnMainThread("Url not Found.");
        //Toast.makeText(activity,"Url not Found.",Toast.LENGTH_LONG).show();
    }

    public static class ViewHolder {
        TextView document_name,title,status,due_date,comment,load_number;
        Button respond;
        View status_circle;
        LinearLayout rejected_reason_layout;
        TextView rejected_reason;
        Button view_previous_doc;
    }

    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }

}
