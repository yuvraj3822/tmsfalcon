package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;
import com.tmsfalcon.device.tmsfalcon.adapters.ContactsDataAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsTabFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;

    public ContactsTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contacts_tab, container, false);
        ButterKnife.bind(ContactsTabFragment.this,view);
        //setContentView(R.layout.activity_contacts);
        initIds();
        if (getUserVisibleHint()) {
            setData();
        }

        return view;
    }

    public void setData(){
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            setData();

        }
    }

    public void setAdapterForActivity(List<ContactsDataResponse.Datum> data){
        if(data != null && data.size()>0) {

            if (getActivity()!=null){

                if (!getActivity().isDestroyed() && isAdded()) {
                    ContactsDataAdapter adapter = new ContactsDataAdapter(getActivity(), data, ContactsTabFragment.this);
                    listView.setAdapter(adapter);
                }
            }

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
                        ErrorHandler.setRestClientMessage(getActivity(),error_string);

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

        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());

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
