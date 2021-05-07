package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.PdfWebViewActivity;
import com.tmsfalcon.device.tmsfalcon.activities.ViewPdf;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ManagePdf;
import com.tmsfalcon.device.tmsfalcon.entities.SettlementBatchModel;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Dell on 5/8/2018.
 */

public class SettlementBatchListAdapter extends BaseAdapter implements ManagePdf.downloadPdfInterface{

    private ArrayList<SettlementBatchModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    ProgressDialog pd;
    String name,document_name;
    progressCallback callback;
    String other_type;
    String root_directory_name = "tmsfalcon";
    String root = Environment.getExternalStorageDirectory().toString()+"/"+root_directory_name;
    File myDir = new File(root+"/"+"uploaded_docs");

    public void registerProgressCallback(progressCallback Mcallback){
        callback = Mcallback;
    }

    public SettlementBatchListAdapter(Activity activity, ArrayList<SettlementBatchModel> data) {
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

        final SettlementBatchModel model = mData.get(position);
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.driver_settlements_batch_list_item, null);
            holder.settled_at = convertView.findViewById(R.id.settled_at);
            holder.settlement_amount = convertView.findViewById(R.id.total_settlement_amount);
            holder.viewDocBtn = convertView.findViewById(R.id.viewDocBtn);
            holder.settlement_batch_id = convertView.findViewById(R.id.settlement_batch_id);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.settled_at.setText(model.getDate_settled());
        holder.settlement_amount.setText("$"+model.getSettlement_total());
        holder.settlement_batch_id.setText("BATCH-"+model.getSettlement_batch_id());

        final ViewHolder finalHolder = holder;

        holder.viewDocBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  callback.showProgressCallback();*/
                name = "BATCH-"+model.getSettlement_batch_id();
                document_name = name;
                Log.e("url "," is "+model.getU_id());
                if(!CustomValidator.checkNullState(model.getU_id())){
                    Intent openWEbView = new Intent(activity, PdfWebViewActivity.class);
                    openWEbView.putExtra("pdf_url",model.getU_id());
                    openWEbView.putExtra("file_name",name);
                    activity.startActivity(openWEbView);

                }
                else{
                    Toast.makeText(activity,"File Not Available",Toast.LENGTH_LONG).show();
                }

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
        TextView settled_at,settlement_amount,settlement_batch_id;
        Button viewDocBtn;

    }
    public interface progressCallback {
        void showProgressCallback();
        void hideProgressCallback();
        void progressUpdate(int percentage);
    }

}
