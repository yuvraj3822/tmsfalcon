package com.tmsfalcon.device.tmsfalcon.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.Responses.VoicemailDataResponse;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.activities.QueueActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TestAudio;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.fragments.VoiceMailTabFragment;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 7/18/2017.
 */

public class VoicemailAdapter extends BaseAdapter {

    private List<VoicemailDataResponse.Data.Records> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    VoiceMailTabFragment fragment;
    String message_id;
    String access_token;

    public VoicemailAdapter(Activity activity, List<VoicemailDataResponse.Data.Records> data, String access_token,VoiceMailTabFragment fragment) {
        this.activity = activity;
        this.mData = data;
        if(activity != null){
            mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        this.fragment = fragment;
        this.access_token = access_token;
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
        VoicemailAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final VoicemailDataResponse.Data.Records model = mData.get(position);

        if (convertView == null) {

            holder = new VoicemailAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_voicemail, null);
            holder.recording_from_textview = convertView.findViewById(R.id.recording_from);
            holder.recording_date_textview = convertView.findViewById(R.id.recording_date);
            holder.recording_duration_textview = convertView.findViewById(R.id.recording_duration);
            holder.swipeLayout = convertView.findViewById(R.id.swipe_layout);
            holder.btnDelete = convertView.findViewById(R.id.delete);
            holder.play_layout = convertView.findViewById(R.id.play_layout);
            holder.read_image_view = convertView.findViewById(R.id.read_image_view);
            holder.phone_image_view = convertView.findViewById(R.id.phone_image_view);

            holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            convertView.setTag(holder);

        } else {

            holder = (VoicemailAdapter.ViewHolder)convertView.getTag();

        }
        message_id = (model.getRecord_id());
        holder.btnDelete.setOnClickListener(onDeleteListener(position, holder));
        /*if(!Objects.equals(model.getMessageFrom().getMessage_from_name(),null)){
            holder.recording_from_textview.setText(model.getMessageFrom().getMessage_from_name());
        }
        else{
            holder.recording_from_textview.setText(model.getMessageFrom().getMessage_from_phone());
        }*/
        holder.recording_from_textview.setText(model.getMessageFrom().getMessage_from_phone());
        if(Objects.equals(model.getRecord_readStatus(),"Read")){
            holder.read_image_view.setBackgroundResource(R.drawable.tick_blue);
        }
        else if(Objects.equals(model.getRecord_readStatus(),"Unread")){
            holder.read_image_view.setBackgroundResource(R.drawable.untick_blue);
        }
        holder.play_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testAudio = new Intent(activity,TestAudio.class);
                testAudio.putExtra("uri",model.getAttachmentList().get(0).getAttachment_uri());
                testAudio.putExtra("access_token",access_token);
                testAudio.putExtra("message_id",model.getRecord_id());
                testAudio.putExtra("read_status",model.getRecord_readStatus());
                activity.startActivity(testAudio);
            }
        });
        holder.phone_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String intent_phone = model.getMessageFrom().getMessage_from_phone();
                Bundle bundle = new Bundle();
                Intent phoneActivityScreen = new Intent(activity, PhoneActivity.class);
                bundle.putString("intent_phone",intent_phone);
                bundle.putString("intent_ext","");
                bundle.putString("intent_name","");
                bundle.putInt("open_fragment",0);
                phoneActivityScreen.putExtras(bundle);
                activity.startActivity(phoneActivityScreen);
            }
        });
        String date = DateFormat.getDateTimeInstance().format(Utils.getUTCToLocalDate(model.getRecord_creationTime()));
        holder.recording_date_textview.setText(date);
        int seconds = Integer.parseInt(model.getAttachmentList().get(0).getAttachment_vmDuration());

        String duration ;
        if(seconds <= 60){
            duration = "0."+seconds;
        }
        else{
            duration = String.valueOf(seconds/60);
        }
        //Log.e("duration","seconds=> "+seconds+" duration_in_minutes => "+duration_in_minutes);

        holder.recording_duration_textview.setText(""+duration);

        return convertView;
    }

    public static class ViewHolder {
        TextView recording_duration_textview,recording_from_textview,recording_date_textview;
        SwipeLayout swipeLayout;
        View btnDelete;
        LinearLayout play_layout;
        ImageView read_image_view,phone_image_view;

    }

    private View.OnClickListener onDeleteListener(final int position, final ViewHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteVoicemail(message_id,position,holder.swipeLayout);
            }
        };
    }

    private void deleteVoicemail(String message_id,final int position, final SwipeLayout swipeLayout) {
        fragment.showProgessBar();

        RestClient.get().deleteUserVoicemail(SessionManager.getInstance().get_token(),message_id).enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                if(response.body() != null  || response.isSuccessful()){
                    Log.e("in",""+new Gson().toJson(response.body()));
                    List messagesList = response.body().getMessages();
                    String messages = "";
                    for(int i = 0; i<messagesList.size();i++){
                        messages += messagesList.get(i);
                    }
                    if(response.body().getStatus()){

                        mData.remove(position);
                        swipeLayout.close();
                        fragment.updateAdapter();
                    }
                    else {
                        Log.e("in","status false body "+response.body());
                    }
                    Toast.makeText(activity, ""+messages, Toast.LENGTH_SHORT).show();

                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(activity,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                fragment.hideProgressBar();
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                fragment.hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });
    }
}
