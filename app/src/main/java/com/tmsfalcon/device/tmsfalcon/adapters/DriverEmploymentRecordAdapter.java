package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.entities.DriverEmploymentModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/13/2017.
 */

public class DriverEmploymentRecordAdapter extends BaseAdapter {

    private ArrayList<DriverEmploymentModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    public DriverEmploymentRecordAdapter(Activity activity,ArrayList<DriverEmploymentModel> data) {
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

        DriverEmploymentModel model = mData.get(position);
//        parent.setId(Integer.parseInt(model.getEmployer_id()));
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.list_item_employment_record, null);
            holder.employerNameTextView = convertView.findViewById(R.id.employer_name);
            holder.positionTextView = convertView.findViewById(R.id.position);
            holder.durationTextView = convertView.findViewById(R.id.duration);
            holder.addressTextView = convertView.findViewById(R.id.address);
            holder.faxTextView = convertView.findViewById(R.id.fax);
            holder.emailTextView = convertView.findViewById(R.id.email);
            holder.salaryTextView = convertView.findViewById(R.id.salary);
            holder.leavingReasonTextView = convertView.findViewById(R.id.leaving_reason);
            holder.mcTextView = convertView.findViewById(R.id.mc);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        String duration = model.getEmployment_from()+" to "+model.getEmployment_to();

        CustomValidator.setCustomText(holder.employerNameTextView,model.getEmployer_name(),(View)holder.employerNameTextView.getParent());
        CustomValidator.setCustomText(holder.positionTextView,model.getEmployment_position(), (View) holder.positionTextView.getParent());
        CustomValidator.setCustomText(holder.durationTextView,duration, (View) holder.durationTextView.getParent());
        CustomValidator.setCustomText(holder.addressTextView,model.getEmployment_address(), (View) holder.addressTextView.getParent());
        CustomValidator.setCustomText(holder.faxTextView,model.getEmployment_fax(), (View) holder.faxTextView.getParent());
        CustomValidator.setCustomText(holder.emailTextView,model.getEmployment_email(), (View) holder.emailTextView.getParent());
        CustomValidator.setCustomSalaryText(holder.salaryTextView,model.getEmployment_salary(), (View) holder.salaryTextView.getParent());
        CustomValidator.setCustomText(holder.leavingReasonTextView,model.getEmployment_leaving_reason(), (View) holder.leavingReasonTextView.getParent());
        CustomValidator.setCustomText(holder.mcTextView,model.getEmployment_mc(), (View) holder.mcTextView.getParent());


        /*holder.employerNameTextView.setText(model.getEmployer_name());
        holder.positionTextView.setText(model.getEmployment_position());
        holder.durationTextView.setText(duration);

        holder.addressTextView.setText(model.getEmployment_address());
        holder.faxTextView.setText(model.getEmployment_fax());
        holder.emailTextView.setText(model.getEmployment_email());
        holder.salaryTextView.setText(model.getEmployment_salary());
        holder.leavingReasonTextView.setText(model.getEmployment_leaving_reason());
        holder.mcTextView.setText(model.getEmployment_mc());*/
        return convertView;
    }
    public static class ViewHolder {
        TextView employerNameTextView,positionTextView,durationTextView,addressTextView,faxTextView,emailTextView,
                salaryTextView,leavingReasonTextView,mcTextView;

    }

}

