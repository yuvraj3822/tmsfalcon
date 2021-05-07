package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.adapters.EscrowScheduleListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EscrowScheduleFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;

    ListView schedule_listview;
    ArrayList<EscrowDetailDataModel.EscrowStatement> escrowStatementArrayList;
    EscrowScheduleListAdapter adapter;
    String periodic_amount;
    TextView no_data_textview;

    public EscrowScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_escrow_schedule, container, false);
        initIds(view);

        if (getUserVisibleHint()) {
            bundle = this.getArguments();
            escrowStatementArrayList = bundle.getParcelableArrayList("escrow_statements");
            periodic_amount = bundle.getString("periodic_amount");
            if(escrowStatementArrayList != null && escrowStatementArrayList.size() > 0){
                adapter = new EscrowScheduleListAdapter(getActivity(),escrowStatementArrayList,periodic_amount);
                schedule_listview.setAdapter(adapter);
            }
            else{
                no_data_textview.setVisibility(View.VISIBLE);
            }


        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() ) { // fragment is visible and have created
            /*Log.e("escrowStatement"," is " +escrowStatementArrayList);
            adapter = new EscrowScheduleListAdapter(getActivity(),escrowStatementArrayList,periodic_amount);
            schedule_listview.setAdapter(adapter);*/
        }
    }

    public void initIds(View view){
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        schedule_listview = view.findViewById(R.id.list);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        //progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

}
