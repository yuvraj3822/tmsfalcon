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
import android.widget.Toast;

import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.VoicemailDataResponse;
import com.tmsfalcon.device.tmsfalcon.adapters.VoicemailAdapter;
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
public class VoiceMailTabFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    VoicemailAdapter adapter;

    public VoiceMailTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voice_mail_tab, container, false);
        ButterKnife.bind(VoiceMailTabFragment.this,view);
        initIds();
        if (getUserVisibleHint()) {
            setData();
        }
        return view;
    }

    public void setData(){
        if(networkValidator.isNetworkConnected()){
            getVoicemailData();
        }
        else {
            Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgressBar();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            setData();

        }
    }
    public void updateAdapter() {
        adapter.notifyDataSetChanged(); //update adapter
    }

    private void getVoicemailData() {
        showProgessBar();

        RestClient.get().fetchUserVoicemails(SessionManager.getInstance().get_token()).enqueue(new Callback<VoicemailDataResponse>() {
            @Override
            public void onResponse(Call<VoicemailDataResponse> call, Response<VoicemailDataResponse> response) {
                if(response.body() != null  || response.isSuccessful()){
                    Log.e("in",""+new Gson().toJson(response.body()));
                    if(response.body().getStatus()){

                        List<VoicemailDataResponse.Data.Records> records = response.body().getData().getRecordsList();
                        if(records != null && records.size()> 0) {

                            adapter = new VoicemailAdapter(getActivity(),records,response.body().getAccess_token(),VoiceMailTabFragment.this);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                        else{

                            no_data_textview.setVisibility(View.VISIBLE);

                        }
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
            public void onFailure(Call<VoicemailDataResponse> call, Throwable t) {
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
