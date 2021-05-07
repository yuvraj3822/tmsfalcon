package com.tmsfalcon.device.tmsfalcon.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.Responses.TripDetailResponse;
import com.tmsfalcon.device.tmsfalcon.activities.CallLogDetail;
import com.tmsfalcon.device.tmsfalcon.activities.PhoneActivity;
import com.tmsfalcon.device.tmsfalcon.activities.TripDetail;
import com.tmsfalcon.device.tmsfalcon.adapters.TimeLineAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.RestClient;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.entities.TripItineraryModel;
import com.tmsfalcon.device.tmsfalcon.interfacess.clickLoadDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.tmsfalcon.device.tmsfalcon.customtools.AppController.math;

/**
 * A simple {@link Fragment} subclass.
 */
public class TripDetailItineraryFragment extends Fragment implements clickLoadDetail {

    SessionManager session;
    NetworkValidator networkValidator;
    ProgressBar progressBar;
    Bundle bundle;
    int trip_id = 0;
    ArrayList<TripItineraryModel> locations;
    RecyclerView recyclerView;
    TimeLineAdapter adapter;
    String distance;
    TextView distance_textview;
    private AlertDialog dialogAlerOut;

    public TripDetailItineraryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trip_detail_itinerary, container, false);
        initIds(view);
        if (getUserVisibleHint()) {

            bundle = this.getArguments();
            locations = bundle.getParcelableArrayList("itinerary");
            trip_id = bundle.getInt("trip_id");
            distance = bundle.getString("distance");
            putData();

        }
        return view;
    }

    public void putData(){
        Float fDistance = Float.valueOf(distance).floatValue();
        distance_textview.setText("Distance: "+math(fDistance)+" Miles");
        adapter = new TimeLineAdapter(locations,trip_id,TripDetailItineraryFragment.this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) { // fragment is visible and have created


        }
    }
    private void initIds(View view) {

        networkValidator = new NetworkValidator(getActivity());
        session = new SessionManager(getActivity());
        distance_textview = view.findViewById(R.id.distance);
        recyclerView = view.findViewById(R.id.recyclerView);

        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

    }


    @Override
    public void loadDetailShow(TripItineraryModel timeLineModel,int position,LinearLayout click_view) {
        click_view.setClickable(false);
        fetchDetailsForDialogBox(position,click_view);
    }


    private void openDialogForItenaryDetails(final TripItineraryModel timeLineModel){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());

        final View dialogView = mLayoutInflater.inflate(R.layout.custom_dialog_trip_view, null);
        dialogBuilder.setView(dialogView);


        final TextView text_consigmentName = dialogView.findViewById(R.id.trip_no);
        final TextView text_streetAddress = dialogView.findViewById(R.id.location);
        final TextView text_address = dialogView.findViewById(R.id.status);


        final TextView text_dateTime = dialogView.findViewById(R.id.date_time);
        final TextView text_phoneNo = dialogView.findViewById(R.id.phone_no);


        text_phoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialogAlerOut.cancel();
//                Intent intent = new Intent(getActivity(), PhoneActivity.class);
//                intent.putExtra("intent_phone",timeLineModel.getPhone());
//                startActivity(intent);
            }
        });


        LinearLayout hashView =  dialogView.findViewById(R.id.hashtag_view);
        LinearLayout insView = dialogView.findViewById(R.id.instructions_view);
        LinearLayout clipView = dialogView.findViewById(R.id.clipboard_view);









        final TextView textView_pickNo = dialogView.findViewById(R.id.contact_person);
        final TextView textView_ref = dialogView.findViewById(R.id.phone);

        final TextView textView_fax = dialogView.findViewById(R.id.fax);
        final TextView textView_instructions = dialogView.findViewById(R.id.instructions);
        final LinearLayout textview_address_view = dialogView.findViewById(R.id.address_view);

//        textview_address_view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialogAlerOut.cancel();
//
//                Intent i = new Intent(getActivity(), CallLogDetail.class);
//                Log.e("plublic_link","https://traveloko.tmsfalcon.com//calllog/view/49258?view=embed&cid=81");
//                i.putExtra("public_link","https://traveloko.tmsfalcon.com//calllog/view/49258?view=embed&cid=81");
//                startActivity(i);
//            }
//        });

//
//
////                    timeLineModel.getContact_person()
//                    text_consigmentName.setText("Contact Person");
//
////
//                    text_streetAddress.setText(Html.fromHtml("<u>" +location+ "</u>"));
//                    text_address.setText(addressFormation(timeLineModel.getCity(),timeLineModel.getState(),timeLineModel.getZipcode()));
////                    finalDate_time
//                    text_dateTime.setText("final date time");
////                    timeLineModel.getPhone()
//                    text_phoneNo.setText("Phone No");
//



        manageHeaderSection(timeLineModel.getName(),timeLineModel.getLocation_address(),allDetailIsEmpty(timeLineModel.getCity(),timeLineModel.getState(),timeLineModel.getZipcode()),/*timeLineModel.getDate_parsed()+", "+*/setTimeSpan(timeLineModel.getEnd_time(),timeLineModel)
                ,timeLineModel.getPhone(),text_consigmentName,text_streetAddress,text_address,text_dateTime,text_phoneNo,timeLineModel);

        String pickUpNo = "alslkjdfj";
        String reference = "dskflsklj";
        String instructions = "flsjdk djkslgjsdljk jsdljksldk dfjdlgjlkd f kfdjljkgfljk kfdljgljk jkfdljgslk jfdkljgljk dfjkljkgklfj kjljdkflkjg jkfdljkglklk kkjdfljgk kfldjklgjkk jkdfljkgljkd jlfdkjglk ";



        manageViewsAsPerData(timeLineModel.getNumber(),timeLineModel.getReference(),timeLineModel.getInstructions(),hashView,clipView,insView,textView_pickNo,
                textView_ref,textView_instructions);
//
//                    CustomValidator.setStringData(textView_pickNo,timeLineModel.getContact_person());
//                    CustomValidator.setStringData(textView_ref,timeLineModel.getPhone());
////                    CustomValidator.setStringData(textView_fax,timeLineModel.getFax());
//                    CustomValidator.setStringData(textView_instructions,timeLineModel.getInstructions());
        //final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogAlerOut = dialogBuilder.create();
        dialogAlerOut.show();
    }



    private void manageHeaderSection(String contactPer, String address, Boolean isEmpty,
                                     String dateTime, String phnNo, TextView textContactPerson, TextView textAddress,
                                     TextView textDetailAddress, TextView textDateTime, TextView textPhnNo,TripItineraryModel timeLineModel){

        if (contactPer.isEmpty()){
            textContactPerson.setVisibility(View.GONE);
            textContactPerson.setText("");
        }
        else {
            textContactPerson.setVisibility(View.VISIBLE);
            textContactPerson.setText(contactPer);

        }

        if (address.isEmpty()){
            textAddress.setVisibility(View.GONE);
            textAddress.setText("");

        }
        else {
            textAddress.setVisibility(View.VISIBLE);
            textAddress.setText(Html.fromHtml("<u>" +address+ "</u>"));

        }
        if (isEmpty){
            textDetailAddress.setVisibility(View.GONE);
            textDetailAddress.setText("");

        }
        else {
            textDetailAddress.setVisibility(View.VISIBLE);
            textDetailAddress.setText(addressFormation(timeLineModel.getCity(),timeLineModel.getState(),timeLineModel.getZipcode()));

        }if (dateTime.isEmpty()){
            textDateTime.setVisibility(View.GONE);
            textDateTime.setText("");

        }
        else {
            textDateTime.setVisibility(View.VISIBLE);
            textDateTime.setText(dateTime);

        }
        if (phnNo.isEmpty()){
            textPhnNo.setVisibility(View.GONE);
            textPhnNo.setText("");

        }
        else {
            textPhnNo.setVisibility(View.VISIBLE);
            textPhnNo.setText(Html.fromHtml("<u>" +phnNo+ "</u>"));


        }

    }

    private void manageViewsAsPerData(String pickNo, String ref, String ins,
                                      LinearLayout hashView, LinearLayout clipView,
                                      LinearLayout insView, TextView textPicNo,
                                      TextView textRefNo, TextView textInsNo){


        if (pickNo.isEmpty()){

            hashView.setVisibility(View.GONE);
            textPicNo.setText("");

        } else {
            hashView.setVisibility(View.VISIBLE);
            textPicNo.setText(pickNo);


        }
        if (ref.isEmpty()){
            clipView.setVisibility(View.GONE);
            textRefNo.setText("");

        } else {
            clipView.setVisibility(View.VISIBLE);
            textRefNo.setText(ref);
        }

        if (ins.isEmpty()){
            insView.setVisibility(View.GONE);
            textInsNo.setText("");

        } else {
            insView.setVisibility(View.VISIBLE);
            textInsNo.setText(ins);
        }
    }

    private Spanned addressFormation(String city, String state, String zipcode){
        String value =  city+","+" "+state+" "+zipcode;
        Spanned result = Html.fromHtml("<u>" +value+ "</u>");
        Log.e("result",":  "+result);
        return result;
    }


    private Boolean allDetailIsEmpty(String city,String state,String zipcode){
        if (city.isEmpty()&& state.isEmpty() && zipcode.isEmpty()){
            return true;
        }else {
            return false;
        }
    }


    private String setTimeSpan(String endtime, TripItineraryModel timeLineModel){
        String date_time = "";
        if(endtime.isEmpty()){
            date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim();
        }
        else{
            date_time = timeLineModel.getDate_parsed()+", "+timeLineModel.getTime().trim()+" - "+timeLineModel.getEnd_time().trim();
        }
        return date_time;
    }


    private void fetchDetailsForDialogBox(final int position, final LinearLayout click_view){
        click_view.setClickable(false);
        showProgessBar();
        RestClient.get().fetchTripDetail(SessionManager.getInstance().get_token(),trip_id).enqueue(new Callback<TripDetailResponse>() {
            @Override
            public void onResponse(Call<TripDetailResponse> call, Response<TripDetailResponse> response) {


                Log.e("response:-"," "+response.toString());
                click_view.setClickable(true);
                if(response.body() != null || response.isSuccessful()){
                    if (response.body().getStatus()) {


                        ArrayList<TripItineraryModel> locations = response.body().getData().get(0).getLocations();

                            locations.add(0,new TripItineraryModel("","","","","","","","","",
                                                                        0,0,"","","","","","","","","","",""));

                            locations.add(locations.size(),new TripItineraryModel("","","","","","","","","",
                                    0,0,"","","","","","","","","","",""));


                        TripItineraryModel timeLineModel = locations.get(position);
                        openDialogForItenaryDetails(timeLineModel);
                        //Toast.makeText(DayOffActivity.this, ""+messages, Toast.LENGTH_SHORT).show();
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
            public void onFailure(Call<TripDetailResponse> call, Throwable t) {
                click_view.setClickable(true);
                hideProgressBar();
                Log.e("server call error",t.getMessage());
            }
        });
    }


    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

}
