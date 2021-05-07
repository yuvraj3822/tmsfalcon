package com.tmsfalcon.device.tmsfalcon.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanInstallmentModel;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created by Dell on 9/26/2018.
 */

public class LoanInstallmentStickyHeaderAdapter extends BaseAdapter implements StickyListHeadersAdapter {

    private String[] countries;
    private LayoutInflater inflater;
    ArrayList<LoanInstallmentModel> installmentModel;
    Context context;
    LoanInstallmentEmiAdapter emiAdapter;
    LoanDetailDataModel detail_model;

    public LoanInstallmentStickyHeaderAdapter(Context context, ArrayList<LoanInstallmentModel> model) {
        inflater = LayoutInflater.from(context);
        this.installmentModel = model;
        this.context = context;
    }

    @Override
    public int getCount() {
        return installmentModel.size();
    }

    @Override
    public Object getItem(int position) {
        return installmentModel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LoanInstallmentModel model = installmentModel.get(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_installment_sticky_emi_data, parent, false);
            holder.listViewEmi = convertView.findViewById(R.id.listViewEmi);
            holder.totalsLayout = convertView.findViewById(R.id.totals_layout);
            holder.totals = convertView.findViewById(R.id.totals);
            holder.total_interest = convertView.findViewById(R.id.total_interest);
            holder.total_payments = convertView.findViewById(R.id.payments);
            holder.total_principal = convertView.findViewById(R.id.total_principal);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        emiAdapter = new LoanInstallmentEmiAdapter(context,model.getEmi());
        holder.listViewEmi.setAdapter(emiAdapter);
        setListViewHeightBasedOnChildren(holder.listViewEmi);
        //holder.text.setText(countries[position]);

        //holder.totals.setText(""+model.getYear_text().substring(0,4));
        holder.totals.setText(model.getYear());
        holder.total_interest.setText(model.getYear_total_interest());
        holder.total_payments.setText(model.getYear_total_payment());
        holder.total_principal.setText(model.getYear_total_principal());

        return convertView;
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;
        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.fragment_installment_sticky_header, parent, false);
            holder.text_year = convertView.findViewById(R.id.text_year);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }
        //set header text as first char in name
        //String headerText = "" + countries[position].subSequence(0, 1).charAt(0);
        holder.text_year.setText(installmentModel.get(position).getYear());
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        //return the first character of the country as ID because this is what headers are based upon
        //return countries[position].subSequence(0, 1).charAt(0);
        return Long.parseLong(installmentModel.get(position).getYear());
    }

    class HeaderViewHolder {
        TextView text_year;
    }

    class ViewHolder {
        ListView listViewEmi;
        LinearLayout totalsLayout;
        TextView totals,total_principal,total_interest,total_payments;
    }

}
