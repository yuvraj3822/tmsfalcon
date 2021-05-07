package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.CashHistoryModel;
import com.tmsfalcon.device.tmsfalcon.entities.TripModel;

import java.util.ArrayList;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Android on 7/18/2017.
 */

public class CashHistoryAdapter extends BaseAdapter {

    private ArrayList<CashHistoryModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    private ClipboardManager myClipboard;
    private ClipData myClip;

    public CashHistoryAdapter(Activity activity, ArrayList<CashHistoryModel> data) {
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
        CashHistoryAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final CashHistoryModel model = mData.get(position);

        myClipboard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        if (convertView == null) {
            holder = new CashHistoryAdapter.ViewHolder();

            convertView = mInflater.inflate(R.layout.list_cash_history, null);
            holder.load_textview = convertView.findViewById(R.id.load);
            holder.cash_for_textview = convertView.findViewById(R.id.cash_for);
            holder.issued_date_textview = convertView.findViewById(R.id.issued_date);
            holder.amount_textview = convertView.findViewById(R.id.amount);
            holder.code_textview = convertView.findViewById(R.id.cash_code);
            holder.view_btn = convertView.findViewById(R.id.view_btn);
            holder.cash_code_layout = convertView.findViewById(R.id.cash_code_layout);
            holder.status_circle = convertView.findViewById(R.id.status_circle);
            holder.status_textview = convertView.findViewById(R.id.status);

            convertView.setTag(holder);
        } else {
            holder = (CashHistoryAdapter.ViewHolder)convertView.getTag();

        }

        holder.load_textview.setText("#"+model.getTrip_id());
        holder.cash_for_textview.setText(model.getCash_for());
        holder.issued_date_textview.setText(model.getIssued_date());
        holder.amount_textview.setText("$"+model.getAmount());
        holder.code_textview.setText(model.getCode());

        if(model.getStatus().equalsIgnoreCase("cancel")){

            holder.status_textview.setText("Cancelled");
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_red));
        }
        else if(model.getStatus().equalsIgnoreCase("active")){
            holder.status_textview.setText("Active");
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_green));
        }
        else{
            holder.status_textview.setText("Not available");
            holder.status_circle.setBackground(activity.getResources().getDrawable(R.drawable.circle_yellow));
        }

        final ViewHolder finalHolder = holder;
        holder.view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalHolder.cash_code_layout.setVisibility(View.VISIBLE);
            }
        });

        holder.code_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textview_text = finalHolder.code_textview.getText().toString();
                myClip = ClipData.newPlainText("text", textview_text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(activity, "Text Copied to Clipboard.",
                        Toast.LENGTH_SHORT).show();
            }
        });




        return convertView;
    }
    public static class ViewHolder {

        TextView load_textview,cash_for_textview,amount_textview,issued_date_textview,code_textview,status_textview;
        Button view_btn;
        LinearLayout cash_code_layout;
        View status_circle;

    }

}
