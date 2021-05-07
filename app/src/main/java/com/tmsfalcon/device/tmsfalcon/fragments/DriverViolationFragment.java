package com.tmsfalcon.device.tmsfalcon.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.common.ANRequest;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.FmcsaHosLogAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.FmcsaHosLogModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DriverViolationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverViolationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverViolationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SessionManager session;
    NetworkValidator networkValidator;
    Context context;
    ListView fmcsa_log_book_list_view,fmsca_unsafe_driving_listview,fmcsa_vehicle_maintenance_listview;
    FmcsaHosLogAdapter fmcsaHosLogAdapter;
    ArrayList<FmcsaHosLogModel> fmcsaHosLogModelArrayList = new ArrayList<>();
    ProgressBar progressBar;
    TextView no_data_textview_1,no_data_textview_2,no_data_textview_3;
    ArrayList<FmcsaHosLogModel> unsafe_driving_arraylist = new ArrayList<>();
    ArrayList<FmcsaHosLogModel> vehicle_maintenance_arraylist = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public DriverViolationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DriverViolationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DriverViolationFragment newInstance(String param1, String param2) {
        DriverViolationFragment fragment = new DriverViolationFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_driver_violation, container, false);
        initIds(view);
        if (getUserVisibleHint()) {
            if(networkValidator.isNetworkConnected()){
                //fmcsaHosLogModelArrayList.clear();
                getData();

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
        if (isVisibleToUser) { // fragment is visible and have created
            if(networkValidator.isNetworkConnected()){
               // fmcsaHosLogModelArrayList.clear();
                getData();

            }
            else {
                Toast.makeText(getActivity(),getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }

        }
    }
    public static class ListUtils {
        public static void setDynamicHeight(ListView mListView) {
            ListAdapter mListAdapter = mListView.getAdapter();
            if (mListAdapter == null) {
                // when adapter is null
                return;
            }
            int height = 0;
           // mListView.setDividerHeight(5);
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(mListView.getWidth(), View.MeasureSpec.UNSPECIFIED);
            for (int i = 0; i < mListAdapter.getCount(); i++) {
                View listItem = mListAdapter.getView(i, null, mListView);
                listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                height += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = mListView.getLayoutParams();
            Log.e("height"," is "+height);
            //params.height = height;
           // params.height = height + (mListView.getDividerHeight() * (mListAdapter.getCount() - 1));
            params.height = height - (60 * mListAdapter.getCount());
            Log.e("params.height"," is "+params.height);
            mListView.setLayoutParams(params);
           // mListView.setDividerHeight(5);
            mListView.requestLayout();
        }
    }

    public void getData(){
        progressBar.setVisibility(View.VISIBLE);
        ANRequest.GetRequestBuilder getRequestBuilder = new ANRequest.GetRequestBuilder<>(UrlController.VIOLATIONS);
        getRequestBuilder.addHeaders("Token", session.get_token());
        ANRequest anRequest = getRequestBuilder.build();

        anRequest.getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // accident_progress_layout.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Log.e("new response", " is "+String.valueOf(response));
                        try {
                            boolean status = response.getBoolean("status");
                            if(status){
                                JSONObject data_obj = response.getJSONObject("data");
                                JSONArray fmcsa_hos_log_json_array = data_obj.getJSONArray("FMCSA_HOS_LOG");
                                JSONArray fmcsa_unsafe_driver_json_array = data_obj.getJSONArray("FMCSA_Unsafe_Driving");
                                JSONArray fmcsa_vehicle_maintenance_json_array = data_obj.getJSONArray("FMCSA_Vehicle_Maintenance");
                                if(fmcsaHosLogModelArrayList != null){
                                    fmcsaHosLogModelArrayList.clear();
                                }
                                if(fmcsa_hos_log_json_array !=null && fmcsa_hos_log_json_array.length()>0){

                                    for(int i = 0; i<fmcsa_hos_log_json_array.length(); i++){

                                        JSONObject jEmpObj = fmcsa_hos_log_json_array.getJSONObject(i);

                                        int hos_violation_id = jEmpObj.optInt("hos_violation_id");
                                        int violation_id = jEmpObj.optInt("violation_id");
                                        int driver_id = jEmpObj.optInt("driver_id");
                                        int company_id = jEmpObj.optInt("company_id");
                                        int is_deleted = jEmpObj.optInt("is_deleted");
                                        int oos = jEmpObj.optInt("oos");
                                        String report = jEmpObj.optString("report");
                                        String state = jEmpObj.optString("state");
                                        String location = jEmpObj.optString("location");
                                        String section = jEmpObj.optString("section");
                                        String description = jEmpObj.optString("description");
                                        String date = jEmpObj.optString("date");
                                        String created_at = jEmpObj.optString("created_at");
                                        String updated_at = jEmpObj.optString("updated_at");
                                        FmcsaHosLogModel model = new FmcsaHosLogModel(0,hos_violation_id,
                                                violation_id,driver_id,company_id,is_deleted,oos,report,state,location,section,
                                                description,date,created_at,updated_at);

                                        fmcsaHosLogModelArrayList.add(model);

                                    }

                                    fmcsaHosLogAdapter = new FmcsaHosLogAdapter(getActivity(),fmcsaHosLogModelArrayList);
                                    fmcsa_log_book_list_view.setAdapter(fmcsaHosLogAdapter);

                                }
                                else{
                                    no_data_textview_1.setVisibility(View.VISIBLE);
                                }
                                if(unsafe_driving_arraylist != null){
                                    unsafe_driving_arraylist.clear();
                                }

                                if(fmcsa_unsafe_driver_json_array != null && fmcsa_unsafe_driver_json_array.length()>0){
                                    for(int i = 0; i<fmcsa_unsafe_driver_json_array.length(); i++){
                                        JSONObject jEmpObj = fmcsa_unsafe_driver_json_array.getJSONObject(i);
                                        int driver_violation_id = jEmpObj.optInt("driver_violation_id");
                                        int violation_id = jEmpObj.optInt("violation_id");
                                        int driver_id = jEmpObj.optInt("driver_id");
                                        int company_id = jEmpObj.optInt("company_id");
                                        int is_deleted = jEmpObj.optInt("is_deleted");
                                        int oos = jEmpObj.optInt("oos");
                                        String report = jEmpObj.optString("report");
                                        String state = jEmpObj.optString("state");
                                        String location = jEmpObj.optString("location");
                                        String section = jEmpObj.optString("section");
                                        String description = jEmpObj.optString("description");
                                        String date = jEmpObj.optString("date");
                                        String created_at = jEmpObj.optString("created_at");
                                        String updated_at = jEmpObj.optString("updated_at");
                                        FmcsaHosLogModel model = new FmcsaHosLogModel(0,driver_violation_id,
                                                violation_id,driver_id,company_id,is_deleted,oos,report,state,location,section,
                                                description,date,created_at,updated_at);

                                        unsafe_driving_arraylist.add(model);

                                    }
                                    Log.e("driving_arraylist"," is "+unsafe_driving_arraylist.size());

                                    fmcsaHosLogAdapter = new FmcsaHosLogAdapter(getActivity(),unsafe_driving_arraylist);
                                    fmsca_unsafe_driving_listview.setAdapter(fmcsaHosLogAdapter);


                                }
                                else{
                                    no_data_textview_2.setVisibility(View.VISIBLE);
                                }
                                if(vehicle_maintenance_arraylist != null){
                                    vehicle_maintenance_arraylist.clear();
                                }

                                if(fmcsa_vehicle_maintenance_json_array != null && fmcsa_vehicle_maintenance_json_array.length()>0){
                                    for(int i = 0; i<fmcsa_vehicle_maintenance_json_array.length(); i++){
                                        JSONObject jEmpObj = fmcsa_vehicle_maintenance_json_array.getJSONObject(i);
                                        int driver_violation_id = jEmpObj.optInt("driver_violation_id");
                                        int violation_id = jEmpObj.optInt("violation_id");
                                        int driver_id = jEmpObj.optInt("driver_id");
                                        int company_id = jEmpObj.optInt("company_id");
                                        int is_deleted = jEmpObj.optInt("is_deleted");
                                        int oos = jEmpObj.optInt("oos");
                                        String report = jEmpObj.optString("report");
                                        String state = jEmpObj.optString("state");
                                        String location = jEmpObj.optString("location");
                                        String section = jEmpObj.optString("section");
                                        String description = jEmpObj.optString("description");
                                        String date = jEmpObj.optString("date");
                                        String created_at = jEmpObj.optString("created_at");
                                        String updated_at = jEmpObj.optString("updated_at");
                                        String resource_type = jEmpObj.optString("resource_type");
                                        String unit_number = jEmpObj.optString("unit_number");
                                        FmcsaHosLogModel model = new FmcsaHosLogModel(1,driver_violation_id,
                                                violation_id,driver_id,company_id,is_deleted,oos,report,state,location,section,
                                                description,date,created_at,updated_at,resource_type,unit_number);

                                        vehicle_maintenance_arraylist.add(model);

                                    }

                                    fmcsaHosLogAdapter = new FmcsaHosLogAdapter(getActivity(),vehicle_maintenance_arraylist);
                                    fmcsa_vehicle_maintenance_listview.setAdapter(fmcsaHosLogAdapter);

                                }
                                else{
                                    no_data_textview_3.setVisibility(View.VISIBLE);
                                }

                                ListUtils.setDynamicHeight(fmcsa_log_book_list_view);
                                ListUtils.setDynamicHeight(fmsca_unsafe_driving_listview);
                                ListUtils.setDynamicHeight(fmcsa_vehicle_maintenance_listview);
                            }
                            else{

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.GONE);
                        String error_body = anError.getErrorBody();
                        String msg = "";
                        if(error_body != null){
                            msg = error_body;
                        }
                        else{
                            msg = "Some Error Occured.Please Try Again Later!";
                        }
                        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();
                        Log.e("new error"," is "+anError.getErrorBody());
                    }
                });

    }

    private void initIds(View view){
        networkValidator = new NetworkValidator(context);
        session = new SessionManager(context);
        fmcsa_log_book_list_view = view.findViewById(R.id.fmcsa_hos_log_listview);
        progressBar = view.findViewById(R.id.progress_bar);
        no_data_textview_1 = view.findViewById(R.id.no_data_textview_1);
        no_data_textview_2 = view.findViewById(R.id.no_data_textview_2);
        no_data_textview_3 = view.findViewById(R.id.no_data_textview_3);
        fmsca_unsafe_driving_listview = view.findViewById(R.id.fmsca_unsafe_driving_listview);
        fmcsa_vehicle_maintenance_listview = view.findViewById(R.id.fmcsa_vehicle_maintenance_listview);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
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
