package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.OfflineReportedAccidentListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.AccidentBasicDetails;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.ReportedAccidentListModel;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OfflineReportedAccident extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    ListView accidentListView;

    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = true;
    int is_first_data_call = 1;
    int total_count = 0;
    LinearLayout footer_layout;
    TextView footer_text;

    AccidentBasicDetails db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_offline_reported_accident);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_offline_reported_accident, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        getAccidentData(true);
    }

    private void getAccidentData(boolean showProgressBar){
        Log.e("in","getAccidentData ");
        if(showProgressBar){
            progressBar.setVisibility(View.VISIBLE);
        }

        db = new AccidentBasicDetails(OfflineReportedAccident.this);

        if(db.checkIfAccidentTableExists() && !db.checkAccidentBasicDetailEmptyTable()) {
            ArrayList<AccidentBasicDetailsModel> arrayList = db.getAllAccidentBasicDetai();

            ArrayList<ReportedAccidentListModel> arrayList_accidents = new ArrayList<>();

            for(AccidentBasicDetailsModel accidentBasicDetailsModel : arrayList) {
                Log.e("accident id"," is "+accidentBasicDetailsModel.getId());
                String accident_id = String.valueOf(accidentBasicDetailsModel.getId());
                Log.e("accident id s"," is "+accident_id);
                ReportedAccidentListModel model = new ReportedAccidentListModel(accident_id,accidentBasicDetailsModel.getAccident_date(),
                        accidentBasicDetailsModel.getAccident_time(),accidentBasicDetailsModel.getAccident_location());
                arrayList_accidents.add(model);
            }
            Log.e("size",""+arrayList_accidents.size());
            if(arrayList_accidents.size() > 0){
                OfflineReportedAccidentListAdapter mAdapter = new OfflineReportedAccidentListAdapter(OfflineReportedAccident.this,arrayList_accidents);
                accidentListView.setAdapter(mAdapter);
            }
            else{
                no_data_textview.setVisibility(View.VISIBLE);
            }


        }
        else{
            no_data_textview.setVisibility(View.VISIBLE);
        }
        if(progressBar != null){
            progressBar.setVisibility(View.GONE);
        }

    }

    public String setFooterText(int arrayListSize,int totalRecords){
        return "Showing "+arrayListSize+" of "+totalRecords+" Records.";
    }

    public void initIds(){

        networkValidator = new NetworkValidator(OfflineReportedAccident.this);
        session = new SessionManager(OfflineReportedAccident.this);
        accidentListView = findViewById(R.id.listViewAccident);

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

}
