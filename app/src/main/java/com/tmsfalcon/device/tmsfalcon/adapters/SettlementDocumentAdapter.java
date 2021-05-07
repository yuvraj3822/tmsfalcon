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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.tmsfalcon.device.tmsfalcon.customtools.ManagePdf;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.SettlementModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Dell on 5/8/2018.
 */

public class SettlementDocumentAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface{

    private ArrayList<SettlementModel> mData = new ArrayList<SettlementModel>();
    private LayoutInflater mInflater;
    private Activity activity;
    ProgressDialog pd;
    String name,document_name;
    progressCallback callback;
    String other_type;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    File myDir = new File(root+"/"+"uploaded_docs");
    File downloadedDir = new File(root+"/"+"downloaded_docs");
    String downloadedDirPath = root+"/"+"downloaded_docs";

    public void registerProgressCallback(progressCallback Mcallback){
        callback = Mcallback;
    }

    public SettlementDocumentAdapter(Activity activity, ArrayList<SettlementModel> data) {
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

        final SettlementModel model = mData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.settlement_list_item, null);
            holder.settlement_date = convertView.findViewById(R.id.settlement_date);
            holder.settlement_amount = convertView.findViewById(R.id.total_settlement_amount);
            holder.document_thumb = convertView.findViewById(R.id.document_thumb);
            holder.viewDocBtn = convertView.findViewById(R.id.viewDocBtn);
            holder.imageProgress = convertView.findViewById(R.id.image_progress);
            /*int colorCodeDark = Color.parseColor("#071528");
            holder.imageProgress.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));*/

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.settlement_date.setText(model.getSettlement_date());
        holder.settlement_amount.setText("$"+model.getTotal_settled_amount());

        String url = UrlController.HOST+model.getDownload_url();

        holder.imageProgress.setVisibility(View.GONE);

        //holder.document_thumb.setImageResource(R.drawable.ic_picture_as_pdf_white_24dp);
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
                //.placeholder( R.drawable.ic_progress_circles )
                .into(holder.document_thumb, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //rotate.cancel();
                        //do smth when picture is loaded successfully
                        finalHolder.imageProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        //do smth when there is picture loading error
                    }
                });*/

        holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  callback.showProgressCallback();*/
                name = model.getTrip_id() + "-" + model.getStart_location() + "-" + model.getEnd_location();
                document_name = name;
                if(model.getDownload_url()!= "false") {
                    Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
                    openWEbView.putExtra("pdf_url", model.getDownload_url());
                    openWEbView.putExtra("file_name", "trip-settlement-document-" + model.getTrip_id());
                    activity.startActivity(openWEbView);
                }
                else{
                    Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
                }
               /* ManagePdf managePdf = new ManagePdf();
                managePdf.registerDownloadInterface(SettlementDocumentAdapter.this);
                managePdf.downloadPdf(activity, model.getDownload_url(), name);*/
                /*int downloadId = PRDownloader.download(model.getDownload_url(), downloadedDirPath, name)
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
                                Toast.makeText(activity,"Download was not successfull",Toast.LENGTH_LONG).show();
                            }
                        });*/
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
        String document_extension = document_extension_arr[document_extension_arr.length-1];
        if(document_extension == "pdf"){
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
        Toast.makeText(activity,"Download was not successful for document "+document_name,Toast.LENGTH_LONG).show();
    }

    @Override
    public void urlNotFoundCallback() {
        callback.hideProgressCallback();
        Toast.makeText(activity,"Url not Found for document "+document_name,Toast.LENGTH_LONG).show();
    }

    public static class ViewHolder {
        TextView settlement_date,settlement_amount;
        ImageView document_thumb;
        Button viewDocBtn;
        ProgressBar imageProgress;

    }
    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }

}
