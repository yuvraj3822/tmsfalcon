package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsActivity extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_contacts, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        //setContentView(R.layout.activity_contacts);
        initIds();

        if(networkValidator.isNetworkConnected()){
            getContactsData();
        }
        else {
            List<ContactsDataResponse.Datum> list = SessionManager.getInstance().getContactsDataResponse("contactsDataResponse");
            Log.e("contactsData ",list.toString());
            setAdapterForActivity(list);
            //Toast.makeText(ContactsActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    public void setAdapterForActivity(List<ContactsDataResponse.Datum> data){
        if(data != null && data.size()>0) {

            /*ContactsDataAdapter adapter = new ContactsDataAdapter(ContactsActivity.this,data);
            listView.setAdapter(adapter);*/
        }
        else{

            no_data_textview.setVisibility(View.VISIBLE);

        }
    }

    private void getContactsData() {
        showProgessBar();

        RestClient.get().fetchContactInformation(SessionManager.getInstance().get_token()).enqueue(new Callback<ContactsDataResponse>() {
            @Override
            public void onResponse(Call<ContactsDataResponse> call, Response<ContactsDataResponse> response) {
                if(response.body() != null  || response.isSuccessful()){

                    if(response.body().getStatus()){

                        List<ContactsDataResponse.Datum> data = response.body().getData();
                        SessionManager.getInstance().saveContactsDataResponse(data,"contactsDataResponse");
                        setAdapterForActivity(data);


                    }
                    else {
                        Log.e("in","status false body "+response.body());
                    }

                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(ContactsActivity.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<ContactsDataResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });
    }

    public void initIds(){

        networkValidator = new NetworkValidator(ContactsActivity.this);
        session = new SessionManager(ContactsActivity.this);

    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    @Bind(R.id.list_view)
    ListView listView;
}
