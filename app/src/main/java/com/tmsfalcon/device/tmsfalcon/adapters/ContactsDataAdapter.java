package com.tmsfalcon.device.tmsfalcon.adapters;

import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.PostResponse;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.fragments.ContactsTabFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Android on 7/18/2017.
 */

public class ContactsDataAdapter extends BaseAdapter {

    private List<ContactsDataResponse.Datum> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Activity activity;
    AlertDialog emailAlertDialog;
    NetworkValidator networkValidator;
    ContactsTabFragment fragment;
    Call<PostResponse> call;

    public ContactsDataAdapter(Activity activity, List<ContactsDataResponse.Datum> data, ContactsTabFragment fragment) {
        this.activity = activity;
        this.mData = data;
        mInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        networkValidator = new NetworkValidator(activity);
        this.fragment = fragment;
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
        ContactsDataAdapter.ViewHolder holder = null;
        int type = getItemViewType(position);

        final ContactsDataResponse.Datum model = mData.get(position);

        if (convertView == null) {

            holder = new ContactsDataAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_contacts_data, null);
            holder.initialsTextView = convertView.findViewById(R.id.initials);
            holder.titleTextView = convertView.findViewById(R.id.title);
            holder.descriptionTextView = convertView.findViewById(R.id.description);
            holder.callLayout = convertView.findViewById(R.id.call_layout);
            holder.typeImageView = convertView.findViewById(R.id.type_image);
            convertView.setTag(holder);

        } else {

            holder = (ContactsDataAdapter.ViewHolder)convertView.getTag();

        }

        String initials = "";

        if(model.getDescription() != null){

            for (String s : model.getDescription().split(" ")) {
                initials+=Character.toUpperCase(s.charAt(0));
            }

        }
        holder.initialsTextView.setText(initials);
        holder.titleTextView.setText(CustomValidator.capitalizeFirstLetter(model.getDescription()));
        if(model.getType() != null && model.getType().equals("phone")){
            holder.descriptionTextView.setText(model.getPhone_number()+ " Ext : "+model.getExtension());
            holder.typeImageView.setImageResource(R.drawable.phone);
        }
        else if(model.getType() != null && model.getType().equals("mailing_address")){
            holder.descriptionTextView.setText(model.getAddress());
            holder.typeImageView.setImageResource(R.drawable.envelop);
        }
        else if(model.getType() != null && model.getType().equals("email")){
            holder.descriptionTextView.setText(model.getEmail());
            holder.typeImageView.setImageResource(R.drawable.envelope_email);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(model.getType() != null && model.getType().equals("phone")){
                    Bundle bundle = new Bundle();
                    Intent phoneActivityScreen = new Intent(activity, PhoneActivity.class);
                    bundle.putString("intent_phone",model.getPhone_number());
                    bundle.putString("intent_ext",model.getExtension());
                    bundle.putString("intent_name",model.getDescription());
                    Log.e("intent_name adapter","is"+model.getDescription());
                    bundle.putInt("open_fragment",0);
                    phoneActivityScreen.putExtras(bundle);
                    activity.startActivity(phoneActivityScreen);
                }
                else if(model.getType() != null && model.getType().equals("mailing_address")){

                }
                else if(model.getType() != null && model.getType().equals("email")){
                    String email = model.getEmail();
                    showEmailPopup(email);
                }
            }
        });


       // Log.e("data ",""+model.getType());


        return convertView;
    }
    void showEmailPopup(final String email){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_simple_email, null);
        dialogBuilder.setView(dialogView);

        final EditText email_edit_text = dialogView.findViewById(R.id.emailEditText);
        final EditText subject = dialogView.findViewById(R.id.subject);
        final EditText message = dialogView.findViewById(R.id.message);

        email_edit_text.setText(email);

        Button send_btn = dialogView.findViewById(R.id.send_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject.getText().toString().length() == 0) {
                    Toast.makeText(activity,
                            "Please enter Subject",
                            Toast.LENGTH_SHORT).show();
                }
                else if (message.getText().toString().length() == 0) {
                       Toast.makeText(activity,
                               "Please enter Message",
                               Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.e("send","clicked");
                    send_email_contact(email,subject.getText().toString(),message.getText().toString());
                }

            }
        });

        Button cancel_btn = dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailAlertDialog.dismiss();
            }
        });

        emailAlertDialog = dialogBuilder.create();
        emailAlertDialog.show();

    }
    public void send_email_contact(String email_str,String subject,String message) {
        if(networkValidator.isNetworkConnected()){

            Map<String, RequestBody> postFields = new HashMap<>();
            postFields.put("subject", requestBody(subject));
            postFields.put("message", requestBody(message));
            postFields.put("email", requestBody(email_str));

            //fragment.showProgessBar();
            call = RestClient.get().emailDocumentsForRc(SessionManager.getInstance().get_token(),postFields);
            call.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    //Log.e("documents response ",new Gson().toJson(response));
                    if(response.body() != null || response.isSuccessful()){
                        Log.e("Contacts Data Adapter", String.valueOf(response.body()));
                        List messagesList = response.body().getMessages();
                        String messages = "";
                        for(int i = 0; i<messagesList.size();i++){
                            messages += messagesList.get(i);
                        }
                        if (response.body().getStatus()) {


                        }
                        emailAlertDialog.dismiss();
                        Toast.makeText(activity, ""+messages, Toast.LENGTH_SHORT).show();

                    }
                    else {

                        try {
                            String error_string = response.errorBody().string();
                            ErrorHandler.setRestClientMessage(activity,error_string);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                   // fragment.hideProgressBar();
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    //fragment.hideProgressBar();
                    emailAlertDialog.dismiss();
                    Log.e("server call error",t.getMessage());
                    Toast.makeText(activity, "Response Call Failed" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }
        else{
            Toast.makeText(activity, ""+activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("text/plain"), name);
    }

    public static class ViewHolder {
        TextView initialsTextView,titleTextView,descriptionTextView;
        LinearLayout callLayout;
        ImageView typeImageView;
    }
}
