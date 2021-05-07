package com.tmsfalcon.device.tmsfalcon;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.adapters.DriverAccidentRecordAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.DriverAccidentRecordModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AccidentRecordFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @SuppressWarnings("unused")
    NetworkValidator networkValidator;
    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    Context context;
    JSONArray accidentJsonArray;
    ListView listViewAccidentRecord;
    DriverAccidentRecordAdapter mAdapter;
    ArrayList<DriverAccidentRecordModel> arrayAccidentRecord = new ArrayList<>();
    TextView no_data_textview;
    ProgressBar progressBar;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AccidentRecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AccidentRecordFragment.
     */
    @SuppressWarnings("unused")
    public static AccidentRecordFragment newInstance(String param1, String param2) {
        AccidentRecordFragment fragment = new AccidentRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    @Deprecated
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_accident_record, container, false);
        initIds(view);
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed() ) { // fragment is visible and have created
            getProfileAction();

        }
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        listViewAccidentRecord = view.findViewById(R.id.listViewAccidentRecord);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    private void getProfileAction() {
        // Tag used to cancel the request
        String tag_json_obj = "accident_info_tag";

        /*final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();*/
        arrayAccidentRecord.clear();
        JSONObject jobj = null;
        try {
           jobj = new JSONObject(session.getProfileJsonArray("data"));
           accidentJsonArray = jobj.getJSONArray("accident_record");
           if(accidentJsonArray !=null && accidentJsonArray.length()>0){
               for (int i = 0; i < accidentJsonArray.length(); i++) {
                   JSONObject jEmpObj = accidentJsonArray.getJSONObject(i);
                   DriverAccidentRecordModel driverEmploymentModel = new DriverAccidentRecordModel(
                           jEmpObj.getString("accident_nature"),
                           jEmpObj.getString("accident_date_parsed"),
                           jEmpObj.getString("fatalities"),
                           jEmpObj.getString("injuries")
                   );
                   arrayAccidentRecord.add(driverEmploymentModel);
                   mAdapter = new DriverAccidentRecordAdapter(getActivity(), arrayAccidentRecord);
                   listViewAccidentRecord.setAdapter(mAdapter);
               }
           }
           else{
               no_data_textview.setVisibility(View.VISIBLE);
           }
        } catch (JSONException e) {
            Log.e("exception ", String.valueOf(e));
        }

        // pDialog.hide();
    }


    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
      * >Communicating with Other Fragments</a> for more information.
     */
    @SuppressWarnings("unused")
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
