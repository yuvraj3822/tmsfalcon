package com.tmsfalcon.device.tmsfalcon.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.EscrowDetailDataModel;
import com.tmsfalcon.device.tmsfalcon.adapters.EscrowTransactionListAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class EscrowTransactionFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;

    ListView transaction_listview;
    ArrayList<EscrowDetailDataModel.EscrowTransaction> escrowTransactionArrayList;
    EscrowTransactionListAdapter adapter;
    String periodic_amount;
    TextView no_data_textview;

    public EscrowTransactionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_escrow_transaction, container, false);
        initIds(view);
        bundle = this.getArguments();
        escrowTransactionArrayList = bundle.getParcelableArrayList("transactions");
        periodic_amount = bundle.getString("periodic_amount");
        if (getUserVisibleHint()) {
            bundle = this.getArguments();
            escrowTransactionArrayList = bundle.getParcelableArrayList("transactions");
            periodic_amount = bundle.getString("periodic_amount");
            if(escrowTransactionArrayList != null && escrowTransactionArrayList.size() >0){
                adapter = new EscrowTransactionListAdapter(getActivity(),escrowTransactionArrayList,periodic_amount);
                transaction_listview.setAdapter(adapter);
            }
            else{
                Toast.makeText(getActivity(),"Data Not Available",Toast.LENGTH_LONG).show();
            }


        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() ) { // fragment is visible and have created
            if(escrowTransactionArrayList != null && escrowTransactionArrayList.size() >0){
                adapter = new EscrowTransactionListAdapter(getActivity(),escrowTransactionArrayList,periodic_amount);
                transaction_listview.setAdapter(adapter);
            }
            else{
                no_data_textview.setVisibility(View.VISIBLE);
            }
        }
    }

    public void initIds(View view){
        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        transaction_listview = view.findViewById(R.id.list);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        //progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

}
