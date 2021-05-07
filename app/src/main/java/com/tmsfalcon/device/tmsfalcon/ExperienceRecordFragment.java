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

import com.tmsfalcon.device.tmsfalcon.adapters.DriverExperienceRecordAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.DriverExperienceModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExperienceRecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExperienceRecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExperienceRecordFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    NetworkValidator networkValidator;
    String TAG = this.getClass().getSimpleName();
    SessionManager session;
    Context context;
    JSONArray experienceJsonArray;
    ListView listViewExperienceRecord;
    DriverExperienceRecordAdapter mAdapter;
    ArrayList<DriverExperienceModel> arrayExperienceRecord = new ArrayList<>();
    TextView no_data_textview;
    ProgressBar progressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ExperienceRecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExperienceRecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExperienceRecordFragment newInstance(String param1, String param2) {
        ExperienceRecordFragment fragment = new ExperienceRecordFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_experience_record, container, false);
        initIds(view);
        getProfileAction();
        return view;
    }

    private void initIds(View view) {
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        listViewExperienceRecord = view.findViewById(R.id.listViewExperienceRecord);
        no_data_textview = view.findViewById(R.id.no_data_textview);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void getProfileAction() {

        // Tag used to cancel the request
        String tag_json_obj = "employment_info_tag";

        /*final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.show();*/
        arrayExperienceRecord.clear();
        JSONObject jobj = null;
        try {
            jobj = new JSONObject(session.getProfileJsonArray("data"));
            experienceJsonArray = jobj.getJSONArray("experience");
            if(experienceJsonArray !=null && experienceJsonArray.length()>0){
                for (int i = 0; i < experienceJsonArray.length(); i++) {
                    JSONObject jEmpObj = experienceJsonArray.getJSONObject(i);
                    DriverExperienceModel driverEmploymentModel = new DriverExperienceModel(
                            jEmpObj.getString("equipment_class"),
                            jEmpObj.getString("equipment_type"),
                            jEmpObj.getString("from_date_parsed"),
                            jEmpObj.getString("to_date_parsed"),
                            jEmpObj.getString("total_miles")
                    );

                    arrayExperienceRecord.add(driverEmploymentModel);
                    mAdapter = new DriverExperienceRecordAdapter(getActivity(), arrayExperienceRecord);
                    listViewExperienceRecord.setAdapter(mAdapter);
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

    // TODO: Rename method, update argument and hook method into UI event
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
