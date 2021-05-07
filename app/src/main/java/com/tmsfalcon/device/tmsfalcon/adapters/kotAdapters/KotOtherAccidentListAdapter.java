package com.tmsfalcon.device.tmsfalcon.adapters.kotAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.dbKotlin.MultipleOtherVehicleDetails;
import com.tmsfalcon.device.tmsfalcon.interfacess.OtherAccidetntClickDetails;
import com.tmsfalcon.device.tmsfalcon.interfacess.clickEventForUpdate;

import java.util.ArrayList;

/**
 * Created by Android on 8/23/2017.
 */
public class KotOtherAccidentListAdapter extends BaseAdapter{
    
//    private ArrayList<AccidentVehicleDetailsModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    SessionManager sessionManager;
    ViewHolder holder = null;
    ArrayList<MultipleOtherVehicleDetails> list;
    OtherAccidetntClickDetails clickEvent;
    clickEventForUpdate clickEventForUpdate;
   /* public AccidentVehicleListAdapter(Activity activity, ArrayList<AccidentVehicleDetailsModel> data) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
    }*/
    public KotOtherAccidentListAdapter(Activity activity, ArrayList<MultipleOtherVehicleDetails> list, OtherAccidetntClickDetails clickEvent, clickEventForUpdate clickEventForUpdate) {
        this.activity = activity;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sessionManager = new SessionManager(activity);
        this.list = list;
        this.clickEvent = clickEvent;
        this.clickEventForUpdate = clickEventForUpdate;
    }

    @Override
    public int getCount() {
        return list.size();
        //return mData.size();
    }

    @Override
    public Object getItem(int position) {

        return list.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        int type = getItemViewType(position);

        //final AccidentVehicleDetailsModel model = mData.get(position);
        if (convertView == null) {
            holder = new KotOtherAccidentListAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_other_accident_vehicle, null);
            holder.cancel_icon = convertView.findViewById(R.id.cancel_icon);
            holder.trailerNo = convertView.findViewById(R.id.trailer_no);
            holder.dotNo = convertView.findViewById(R.id.dotno);
            holder.licnesePlate = convertView.findViewById(R.id.license_no);
            holder.licenseState = convertView.findViewById(R.id.license_state);
            holder.companyName = convertView.findViewById(R.id.company_names);
            holder.policyNo = convertView.findViewById(R.id.policy_no);
            holder.policyHolderName = convertView.findViewById(R.id.policy_holder_mobno);
            holder.ownerName = convertView.findViewById(R.id.owner_name);
            holder.ownerPhoneNo = convertView.findViewById(R.id.phone_no);
            holder.ownerAddress = convertView.findViewById(R.id.address);
            holder.towingCompanyName = convertView.findViewById(R.id.towing_company_name);
            holder.towingPhoneNo = convertView.findViewById(R.id.towing_phone_no);
            holder.detail_layoutl = convertView.findViewById(R.id.detail_layout);

            convertView.setTag(holder);
        } else {
            holder = (KotOtherAccidentListAdapter.ViewHolder)convertView.getTag();
        }

        setTextOnEditTextView(list.get(position).getUnitNo(),holder.trailerNo);
//        setTextOnEditTextView(list.get(position).getDotNo(),holder.dotNo);
        setTextOnEditTextView(list.get(position).getLicenseNos(),holder.licnesePlate);
        setTextOnEditTextView(list.get(position).getLicenseState(),holder.licenseState);
        setTextOnEditTextView(list.get(position).getInsuranceCompany(),holder.companyName);
        setTextOnEditTextView(list.get(position).getInsurancePolicyNo(),holder.policyNo);
        setTextOnEditTextView(list.get(position).getPolicyHolderFirstName(),holder.policyHolderName);
        setTextOnEditTextView(list.get(position).getOwnerName(),holder.ownerName);
        setTextOnEditTextView(list.get(position).getOwnerAddress(),holder.ownerPhoneNo);
        setTextOnEditTextView(list.get(position).getOwnerCity()+" "+list.get(position).getOwnerState(),holder.ownerAddress);
        setTextOnEditTextView(list.get(position).getTowedCompanyName(),holder.towingCompanyName);
        setTextOnEditTextView(list.get(position).getTowedCompanyPhnNo(),holder.towingPhoneNo);

        holder.cancel_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEvent.otherAccidentEvent(list.get(position));
            }
        });

        holder.detail_layoutl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEventForUpdate.updateEventClickInterface(list.get(position));
            }
        });

        return convertView;
    }



    private void setTextOnEditTextView(String text, TextView textView){

        if (text != null){
            if (text.length() != 0) {
                textView.setText(text);
            } else {
                textView.setText("");
            }
        }

    }

    public static class ViewHolder {
        TextView trailerNo,dotNo,licnesePlate,licenseState,companyName,policyNo,policyHolderName,ownerName,ownerPhoneNo,ownerAddress,towingCompanyName,towingPhoneNo;
        ImageView cancel_icon;
        LinearLayout detail_layoutl;
    }

}
