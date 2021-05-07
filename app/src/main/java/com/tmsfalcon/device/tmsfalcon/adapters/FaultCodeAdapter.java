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
import com.tmsfalcon.device.tmsfalcon.entities.FaultCodeModel;

import java.util.ArrayList;

/**
 * Created by Android on 7/18/2017.
 */

public class FaultCodeAdapter extends BaseAdapter {

    private ArrayList<FaultCodeModel> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;

    public FaultCodeAdapter(Activity activity, ArrayList<FaultCodeModel> data) {
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

    @SuppressWarnings("unused")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);

        FaultCodeModel model = mData.get(position);

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_fault_code, null);
            holder.codeTextView = convertView.findViewById(R.id.code);
            holder.descriptionTextView = convertView.findViewById(R.id.description);
            holder.sourceTextView = convertView.findViewById(R.id.source);
            holder.typeTextView = convertView.findViewById(R.id.type);
            holder.firstOnTextView = convertView.findViewById(R.id.first_on);
            holder.lastOnTextView = convertView.findViewById(R.id.last_on);
            holder.statusTextView = convertView.findViewById(R.id.status);
            convertView.setTag(holder);

        } else {

            holder = (FaultCodeAdapter.ViewHolder)convertView.getTag();

        }

        CustomValidator.setCustomText(holder.codeTextView,model.getCode(), (View) holder.codeTextView.getParent());
        CustomValidator.setCustomText(holder.sourceTextView,model.getSource(), (View) holder.sourceTextView.getParent());
        CustomValidator.setCustomText(holder.descriptionTextView,model.getDescription(), (View) holder.descriptionTextView.getParent());

        CustomValidator.setCustomText(holder.typeTextView,model.getType(), (View) holder.typeTextView.getParent());
        CustomValidator.setCustomText(holder.firstOnTextView,model.getFirst_on(), (View) holder.firstOnTextView.getParent());
        CustomValidator.setCustomText(holder.lastOnTextView,model.getLast_on(), (View) holder.lastOnTextView.getParent());
        CustomValidator.setCustomText(holder.statusTextView,model.getStatus(), (View) holder.statusTextView.getParent());


        return convertView;
    }
    public static class ViewHolder {
        TextView codeTextView,sourceTextView,descriptionTextView,typeTextView,firstOnTextView,lastOnTextView,statusTextView;
    }
}
