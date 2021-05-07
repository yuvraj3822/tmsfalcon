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
import com.tmsfalcon.device.tmsfalcon.adapters.RecentCallsDataAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.database.RecentCallsTable;
import com.tmsfalcon.device.tmsfalcon.entities.RecentCallsModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class RecentCallsTabFragment extends Fragment {

    SessionManager session;
    NetworkValidator networkValidator;
    RecentCallsDataAdapter adapter;
    RecentCallsTable table;
    List<RecentCallsModel> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recent_calls_tab, container, false);
        ButterKnife.bind(RecentCallsTabFragment.this,view);
        table = new RecentCallsTable(getActivity());
        //table.deleteAllCalls();
        //setContentView(R.layout.activity_contacts);
        initIds();
        if (getUserVisibleHint()) {
            setData();
        }
        return view;
    }

    public void setData(){
        if(networkValidator.isNetworkConnected()){
            // getContactsData();
            list = table.getAllCalls();
            adapter = new RecentCallsDataAdapter(getActivity(),list);
            listView.setAdapter(adapter);
        }
        else {
           Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created
            setData();

        }
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
