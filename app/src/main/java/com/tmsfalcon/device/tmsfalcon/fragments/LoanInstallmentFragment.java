package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.AppCompatSpinner;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.LoanInstallmentFragmenResponse;
import com.tmsfalcon.device.tmsfalcon.adapters.LoanInstallmentEmiAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.LoanDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanEmiModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoanInstallmentModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoanInstallmentFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;
    ArrayList<LoanInstallmentModel> installmentModel = new ArrayList<>();
    ArrayList<LoanEmiModel> emiModelArrayList = new ArrayList<>();
    LoanDetailDataModel loan_detail;

    //StickyListHeadersListView stickyList;
    ListView listView;
    //LoanInstallmentStickyHeaderAdapter adapter;
    LoanInstallmentEmiAdapter adapter;

    int is_first_data_call = 1;

    static final String[] Months = new String[] { "January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December" };
    int current_year,current_month;
    String selected_duration,loan_id;
    int selected_month,selected_year;
    ArrayList<String> month_list = new ArrayList<>();
    ArrayList<String> year_list = new ArrayList<>();
    LinearLayout footer_layout;
    TextView footer_text;
    AppCompatSpinner month_spinner,year_spinner;
    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    private boolean call_manual = false;
    private boolean manual_call_done = false;
    private int sent_request_offset = 0;

    TextView no_data_textview,text_year;
    int total_count = 0;


    public LoanInstallmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loan_installment, container, false);
        Calendar c = Calendar.getInstance();
        current_year = c.get(Calendar.YEAR);
        current_month = c.get(Calendar.MONTH);
        for(int i = 0 ; i < Months.length ; i++){
            month_list.add(Months[i]);
        }
        initIds(view);
        selected_month = current_month;
        selected_year = current_year;
        text_year.setText(""+selected_year);
        bundle = this.getArguments();
        loan_id = bundle.getString("loan_id");
        if(getUserVisibleHint()){ // fragment is visible
            if(networkValidator.isNetworkConnected()){
                getData(10);
                setScrollEvent();

            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }
        }
        return view;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            if(networkValidator.isNetworkConnected()){
                getData(10);
                setScrollEvent();

            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }

    public static RequestBody requestBody(String name) {
        return RequestBody.create(MediaType.parse("t" +
                "" +
                "" +
                "" +
                "ext/plain"), name);
    }

    public void setScrollEvent(){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.e("in", "firstVisibleItem " + firstVisibleItem + " visibleItemCount " + visibleItemCount + " totalItemCount " + totalItemCount);
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                        currentPage++;
                    }
                }
                if (!loading && (lastInScreen == totalItemCount)) {
                    if(is_first_data_call != 1){
                        if (sent_request_offset != totalItemCount){
                            getData(10);
                            sent_request_offset = totalItemCount;
                        }
                        loading = true;
                    }
                }

            }
        });
    }

    public void getData(int limit) {

        int data_month = selected_month+1;
        String data_month_str = String.valueOf(data_month);
        Log.e("data","loan_month "+data_month_str+" loan_year "+selected_year);
        Map<String, RequestBody> postFields = new HashMap<>();
        postFields.put("loan_id", requestBody(loan_id));
        postFields.put("loan_month",requestBody(data_month_str));
        postFields.put("loan_year",requestBody(String.valueOf(selected_year)));
        postFields.put("limit",requestBody(String.valueOf(limit)));
        postFields.put("start",requestBody(String.valueOf(emiModelArrayList.size())));
        showProgessBar();
        // Tag used to cancel the request
        RestClient.get().getLoanInstallments(SessionManager.getInstance().get_token(), postFields).enqueue(new Callback<LoanInstallmentFragmenResponse>() {
            @Override
            public void onResponse(Call<LoanInstallmentFragmenResponse> call, retrofit2.Response<LoanInstallmentFragmenResponse> response) {

                if(response.body() != null || response.isSuccessful()){
                    Log.e("response",""+new Gson().toJson(response.body()));
                    if (response.body().getStatus()) {
                        sent_request_offset = 0;
                        total_count = response.body().getTotal();


                       // installmentModel = response.body().getData();

                        if(response.body().getData().size() > 0){
                            no_data_textview.setVisibility(View.GONE);
                            if(total_count > emiModelArrayList.size()){
                                emiModelArrayList.addAll(response.body().getData().get(0).getEmi());
                            }

                            text_year.setText(response.body().getData().get(0).getYear());

                            footer_layout.setVisibility(View.VISIBLE);
                            footer_text.setText(setFooterText(emiModelArrayList.size(),total_count));

                            if(is_first_data_call == 1){
                                adapter = new LoanInstallmentEmiAdapter(getActivity(),emiModelArrayList);
                                listView.setAdapter(adapter);
                                /*adapter = new LoanInstallmentStickyHeaderAdapter(getActivity(),installmentModel);
                                stickyList.setAdapter(adapter);*/
                                is_first_data_call = 0;

                            }
                            else{
                                listView.invalidateViews();
                                //stickyList.invalidateViews();
                                adapter.notifyDataSetChanged();
                            }

                        }
                        else{
                            if(!(emiModelArrayList.size() > 0)){
                                no_data_textview.setVisibility(View.VISIBLE);
                                footer_layout.setVisibility(View.GONE);
                            }
                        }

                    } else {
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
            public void onFailure(Call<LoanInstallmentFragmenResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });

    }

    public void initIds(View view){
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        //stickyList = view.findViewById(R.id.list);
        listView = view.findViewById(R.id.list);
        progressBar = view.findViewById(R.id.progress_bar);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        footer_layout = view.findViewById(R.id.footer_layout);
        footer_text = view.findViewById(R.id.footer_text);
        month_spinner = view.findViewById(R.id.month_spinner);
        year_spinner = view.findViewById(R.id.year_spinner);
        text_year = view.findViewById(R.id.text_year);

        int start_year = current_year-10;
        for(int i = start_year ; i <= current_year ; i++){
            year_list.add(String.valueOf(i));
        }

        ArrayAdapter year_adapter = new ArrayAdapter(getActivity(),R.layout.custom_textview_to_spinner,year_list);
        year_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        year_spinner.setAdapter(year_adapter);

        year_spinner.setSelection(((ArrayAdapter<String>)year_spinner.getAdapter()).getPosition(String.valueOf(current_year)));

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //selected_duration = year_spinner.getSelectedItem().toString();
                selected_year = Integer.parseInt(year_spinner.getSelectedItem().toString());
                if(emiModelArrayList.size() > 0){
                    emiModelArrayList.clear();
                }

                loading = false;
                previousTotal = 0;
                currentPage = 0;
                getData(10);
                setScrollEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        ArrayAdapter month_adapter = new ArrayAdapter(getActivity(),R.layout.custom_textview_to_spinner,month_list);
        month_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        month_spinner.setAdapter(month_adapter);

        month_spinner.setSelection(((ArrayAdapter<String>)month_spinner.getAdapter()).getPosition(Months[current_month]));

        month_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_duration = month_spinner.getSelectedItem().toString();
                selected_month = i;
                if(emiModelArrayList.size() > 0){
                    emiModelArrayList.clear();
                }

                loading = false;
                previousTotal = 0;
                currentPage = 0;
                getData(10);
                setScrollEvent();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }
    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


}
