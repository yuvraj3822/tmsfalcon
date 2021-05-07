package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailDataModel;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class EscrowTransactionListAdapter extends BaseAdapter {

    private ArrayList<EscrowDetailDataModel.EscrowTransaction> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    String periodic_amount;

    public EscrowTransactionListAdapter(Activity activity, ArrayList<EscrowDetailDataModel.EscrowTransaction> data, String periodic_amount) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.periodic_amount = periodic_amount;
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
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EscrowTransactionListAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        EscrowDetailDataModel.EscrowTransaction model = mData.get(position);
        if (convertView == null) {
            holder = new EscrowTransactionListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_escrow_transaction, null);

            holder.transaction_id_textview = convertView.findViewById(R.id.transaction_id);
            holder.date_textview = convertView.findViewById(R.id.transaction_date);
            holder.type_textview = convertView.findViewById(R.id.type);
            holder.desription_textview = convertView.findViewById(R.id.description);
            holder.amount_textview = convertView.findViewById(R.id.amount);
            holder.balance_textview = convertView.findViewById(R.id.balance);

        convertView.setTag(holder);
        } else {

            holder = (EscrowTransactionListAdapter.ViewHolder)convertView.getTag();

        }
        //convertView.setId(Integer.parseInt(model.getSchedule_id()));
        holder.transaction_id_textview.setText("#"+model.getTransaction_id());
        holder.date_textview.setText(model.getCreated_at());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.desription_textview.setText(Html.fromHtml(model.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.desription_textview.setText(Html.fromHtml(model.getDescription()));
        }
        holder.type_textview.setText(model.getType());
        holder.balance_textview.setText("$"+model.getBalance());
        holder.amount_textview.setText("$"+model.getAmount());

        return convertView;
    }
    public static class ViewHolder {
        TextView transaction_id_textview,date_textview,desription_textview,amount_textview,balance_textview,type_textview;

    }

}
