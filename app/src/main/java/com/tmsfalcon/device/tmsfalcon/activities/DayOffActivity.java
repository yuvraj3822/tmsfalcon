package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.DayOffResponse;
import com.tmsfalcon.device.tmsfalcon.adapters.DayOffAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.DayOffModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DayOffActivity extends NavigationBaseActivity {

    NetworkValidator networkValidator;
    DayOffAdapter adapter;
    ArrayList<DayOffModel> arrayList = new ArrayList<>();
    int is_first_data_call = 1;

    private int visibleThreshold = 3;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int total_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_day_off);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_day_off, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
       /* lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        networkValidator = new NetworkValidator(DayOffActivity.this);
        if(networkValidator.isNetworkConnected()){
            fetchDayOffRecords(10);
            setScrollEvent();
        }
        else{
            Toast.makeText(DayOffActivity.this, ""+getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
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
                        fetchDayOffRecords(10);
                        loading = true;
                    }
                }
            }
        });
    }

   /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    }*/

    private void fetchDayOffRecords(int limit) {
        currentPage += 1;
        showProgessBar();
        RestClient.get().fetchDayOff(SessionManager.getInstance().get_token(),arrayList.size()).enqueue(new Callback<DayOffResponse>() {
            @Override
            public void onResponse(Call<DayOffResponse> call, Response<DayOffResponse> response) {

                if(response.body() != null  || response.isSuccessful()){

                    if(response.body().getStatus()){

                        List<DayOffResponse.Datum> data = response.body().getData();
                        total_count = response.body().getTotal();
                        /*if(data.size() == 0){
                            loadMoreLayout.setVisibility(View.GONE);
                            Toast.makeText(DayOffActivity.this,"No Records Available.",Toast.LENGTH_LONG).show();
                        }
                        else if(data.size() >0 && data.size() <= 9){
                            loadMoreLayout.setVisibility(View.GONE);
                        }
                        else{
                            loadMoreLayout.setVisibility(View.VISIBLE);
                        }*/
                        if(data != null && data.size()>0) {
                            for (int i = 0; i < data.size(); i++) {
                                DayOffModel model = new DayOffModel(data.get(i).getIsFullOff(), data.get(i).getReason(), data.get(i).getDate(),
                                        data.get(i).getStart(), data.get(i).getEnd(), data.get(i).getStatus(),data.get(i).getIs_multiple());
                                arrayList.add(model);
                            }

                            footer_layout.setVisibility(View.VISIBLE);
                            footer_text.setText(setFooterText(arrayList.size(),total_count));
                            if(is_first_data_call == 1){
                                adapter = new DayOffAdapter(DayOffActivity.this,arrayList);
                                listView.setAdapter(adapter);
                                is_first_data_call = 0;
                            }
                            else{
                                adapter.notifyDataSetChanged();
                            }
                        }
                        else{
                            if(arrayList.size() == 0){
                                no_data_textview.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                    else {
                        Log.e("in","status false body "+response.body());
                    }

                }
                else{
                    try {
                        String error_string = response.errorBody().string();
                        ErrorHandler.setRestClientMessage(DayOffActivity.this,error_string);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                hideProgressBar();
            }

            @Override
            public void onFailure(Call<DayOffResponse> call, Throwable t) {
                hideProgressBar();
                Log.e("server call error",t.getMessage());
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

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.list_view)
    ListView listView;

    @Bind(R.id.footer_layout)
    LinearLayout footer_layout;

    @Bind(R.id.footer_text)
    TextView footer_text;

    @Bind(R.id.no_data_textview)
    TextView no_data_textview;

    /*@Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/

   /* @OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(DayOffActivity.this, NotificationActivity.class);
        startActivity(i);
    }


    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }*/


   /* @OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(DayOffActivity.this);
    }*/


}
