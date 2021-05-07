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
import com.tmsfalcon.device.tmsfalcon.adapters.FuelRebateAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.FuelFragmentModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuelScheduleFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;
    ArrayList<FuelFragmentModel> arraylist_fuel = new ArrayList<>();
    FuelFragmentModel model;
    ListView listView;
    FuelRebateAdapter mAdapter;
    TextView no_data_textview;

    public FuelScheduleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fuel_schedule, container, false);
        initIds(view);
        bundle = this.getArguments();
        arraylist_fuel = bundle.getParcelableArrayList("rebate_schedule");
//        if (getUserVisibleHint()) {
            if(arraylist_fuel != null && arraylist_fuel.size() > 0){
                mAdapter = new FuelRebateAdapter(getActivity(),arraylist_fuel);
                listView.setAdapter(mAdapter);
            }
            else{
                no_data_textview.setVisibility(View.VISIBLE);
            }


//        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() ) { // fragment is visible and have created


        }
    }

    public void initIds(View view){
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        listView = view.findViewById(R.id.listFuelSchedule);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        //progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

}
