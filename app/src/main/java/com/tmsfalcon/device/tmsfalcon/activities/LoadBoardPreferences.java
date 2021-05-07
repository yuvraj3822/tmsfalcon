package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.thomashaertel.widget.MultiSpinner;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.adapters.LoadPreferenceSpinnerAdapter;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DestinationLocationModel;
import com.tmsfalcon.device.tmsfalcon.entities.LoadPrefereneSpinnerModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoadBoardPreferences extends NavigationBaseActivity {

    ArrayList<LoadPrefereneSpinnerModel> trailer_type_arrayList;
    DatePickerDialog.OnDateSetListener startDateListener;
    DatePickerDialog.OnDateSetListener endDateListener;
    ArrayList<String> load_type_arrayList;
    ArrayList<String> post_age_arrayList = new ArrayList<>();
    LoadPreferenceSpinnerAdapter loadPreferenceSpinnerAdapter;
    SessionManager session;
    NetworkValidator networkValidator;
    int first_time_login;
    ArrayList<String> list_city_states = new ArrayList<>();
    ArrayAdapter<String> city_state_adapter;

    ArrayList<String> destination_list_city_states = new ArrayList<>();
    ArrayAdapter<String> destination_city_state_adapter;
    ArrayAdapter trailer_type_adapter;
    String trailer_type_string,load_type_string;

    String[] max_weight_array = {"45000","44000","48000","48000","45000"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_load_board_preferences, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        initIds();
        first_time_login = AppController.first_time_login;

        /*trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Select Trailer Type",""));
        trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Van","V"));
        trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Reefer","R"));
        trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Flatbed","Fb"));
        trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Step Deck","SD"));
        trailer_type_arrayList.add(new LoadPrefereneSpinnerModel("Van or Reefer","VR"));

        loadPreferenceSpinnerAdapter = new LoadPreferenceSpinnerAdapter(LoadBoardPreferences.this,trailer_type_arrayList);
        trailer_type_spinner.setAdapter(loadPreferenceSpinnerAdapter);*/

        trailer_type_spinner.setHint("Select Trailer Type");
        trailer_type_spinner.setHintTextColor(getResources().getColor(R.color.white_greyish));
        trailer_type_spinner.setTextColor(getResources().getColor(R.color.white_greyish));
        trailer_type_spinner.setTextSize(15);


        trailer_type_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        trailer_type_adapter.add("Van");
        trailer_type_adapter.add("Reefer");
        trailer_type_adapter.add("Flatbed");
        trailer_type_adapter.add("Step Deck");
        trailer_type_adapter.add("Van or Reefer");

        // get spindf klgjhsdl iner and set adapter
        trailer_type_spinner.setAdapter(trailer_type_adapter, false, onSelectedListener);

        setDateListeners();
        post_age_arrayList.add("Post age(Hours)");
        post_age_arrayList.add("2");
        post_age_arrayList.add("6");
        post_age_arrayList.add("12");
        post_age_arrayList.add("24");
        post_age_arrayList.add("48");
        post_age_arrayList.add("72");
        post_age_arrayList.add("96");

        ArrayAdapter post_age_adapter = new ArrayAdapter(LoadBoardPreferences.this,android.R.layout.simple_spinner_item,post_age_arrayList);
        post_age_adapter.setDropDownViewResource(R.layout.custom_textview_to_spinner);
        post_age_spinner.setAdapter(post_age_adapter);

        load_type_arrayList.add("Both");
        load_type_arrayList.add("Full");
        load_type_arrayList.add("Partial");

        ArrayAdapter load_type_adapter = new ArrayAdapter(LoadBoardPreferences.this,android.R.layout.simple_spinner_item,load_type_arrayList);
        load_type_adapter.setDropDownViewResource(R.layout.custom_textview_to_spinner);
        load_type_spinner.setAdapter(load_type_adapter);

        load_type_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapterView.getCount() > 0){

                    TextView textView = (TextView) adapterView.getChildAt(0);
                    if(textView != null){
                        textView.setTextColor(getResources().getColor(R.color.white_greyish));
                        textView.setTextSize(15);
                    }

                }


                String selected_item = adapterView.getItemAtPosition(i).toString();
                if(i == 0){
                    load_type_string = "";
                }
                else if(i == 1){
                    load_type_string = "F";
                }
                else if(i == 2){
                    load_type_string = "P";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        post_age_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView = (TextView) adapterView.getChildAt(0);
                if(textView != null){
                    textView.setTextColor(getResources().getColor(R.color.white_greyish));
                    textView.setTextSize(15);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(AppController.first_time_login == 1){
            AppController.first_time_login = 0;
            if(networkValidator.isNetworkConnected()){
                getData();
            }
            else {
                Toast.makeText(LoadBoardPreferences.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
            }
        }
        else{
            body_layout.setVisibility(View.VISIBLE);
            setSessionValues();
        }
        city_state_adapter = new ArrayAdapter<String>
                (LoadBoardPreferences.this,android.R.layout.simple_list_item_1,list_city_states);
        city_state_adapter.setNotifyOnChange(true);
        origin_state_edittext.setAdapter(city_state_adapter);

        destination_city_state_adapter = new ArrayAdapter<String>
                (LoadBoardPreferences.this,android.R.layout.simple_list_item_1,destination_list_city_states);
        destination_city_state_adapter.setNotifyOnChange(true);
        destination_state_edittext.setAdapter(destination_city_state_adapter);

        setCustomEditTextListener();

    }

    public void setSessionValues(){
        trailer_type_spinner.setText(session.get_trailer_type());
        if(session.get_origin_zipcode() != ""){
            origin_state_edittext.setText(session.get_origin_state()+","+session.get_origin_zipcode());
        }
        else{
            origin_state_edittext.setText(session.get_origin_state());
        }

        //destination_state_edittext.setText(session.getdes);
        pick_up_date_edittext.setText(session.get_origin_pickup_date());
        pick_up_end_date_edittext.setText(session.get_origin_pickup_enddate());
        int i = 0;
        if(session.get_load_type() == ""){
            i = 0;
        }
        else if(session.get_load_type() == "F"){
            i = 1;
        }
        else if(session.get_load_type() == "P"){
            i = 2;
        }
        load_type_spinner.setSelection(i);
        length_trailer_edittext.setText(session.get_trailer_length());
        weight_trailer_edittext.setText(session.get_trailer_weight());
        post_age_spinner.setSelection(((ArrayAdapter<String>)post_age_spinner.getAdapter()).getPosition(session.get_post_age()));
    }


    private MultiSpinner.MultiSpinnerListener onSelectedListener = new MultiSpinner.MultiSpinnerListener() {
        public void onItemsSelected(boolean[] selected) {
            // Do something here with the selected items
            Log.e("selected ", Arrays.toString(selected));
            StringBuilder builder = new StringBuilder();
            ArrayList<Integer> boolean_true_selected = new ArrayList<>();

            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    builder.append(trailer_type_adapter.getItem(i)).append(",");
                    boolean_true_selected.add(i);
                }

            }
            if(boolean_true_selected.size() == 1){
                weight_trailer_edittext.setText(max_weight_array[boolean_true_selected.get(0)]);
            }
            else{
                weight_trailer_edittext.setText("48000");
            }
            trailer_type_spinner.setText(builder.toString());
            trailer_type_string = builder.toString();

        }
    };

    public void getData() {
        // Tag used to cancel the request
        String tag_json_obj = "load_board_settings_tag";
        String url = UrlController.LOAD_BOARD_SETTINGS_DATA;
        //arrayList_loan.clear();
        showProgessBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONObject data_json;
                        hideProgressBar();
                        try {
                            status = response.getBoolean("status");
                            body_layout.setVisibility(View.VISIBLE);
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){

                                    JSONArray trailers_array = data_json.getJSONArray("trailers");
                                    JSONArray trailer_types_array = data_json.getJSONArray("trailer_types");
                                    JSONObject last_delivered_load = data_json.getJSONObject("last_delivered_load");
                                    JSONArray dispatchers = data_json.getJSONArray("dispatchers");

                                    if(last_delivered_load != null && last_delivered_load.length() > 0){
                                        String start_location = last_delivered_load.getString("start_location");
                                        String server_pick_up_date = last_delivered_load.getString("start_date");
                                        String server_pick_up_end_date = last_delivered_load.getString("end_date");

                                        if(server_pick_up_date != null){
                                            pick_up_date_edittext.setText(server_pick_up_date);
                                        }
                                        else{
                                            String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                                            pick_up_date_edittext.setText(date);
                                            pick_up_end_date_edittext.setText(date);
                                        }

                                        if(server_pick_up_end_date != null){
                                            pick_up_end_date_edittext.setText(server_pick_up_end_date);
                                        }
                                        else{
                                            String date = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
                                            pick_up_end_date_edittext.setText(date);
                                        }
                                        if(start_location != null){

                                            String[] separated = start_location.split(",");
                                            origin_state_edittext.setText(start_location);
                                            String server_origin_city = separated[0];
                                            String server_origin_state = separated[1];


                                        }
                                    }

                                    if(trailers_array != null && trailers_array.length() > 0){
                                        //trailer assigned
                                        JSONObject trailer = (JSONObject) trailers_array.get(0);
                                        String server_trailer_make = trailer.getString("make");
                                        String server_trailer_weight = trailer.getString("max_weight");
                                        String server_trailer_length = trailer.getString("length");
                                        String server_trailer_width = trailer.getString("width");
                                        String server_trailer_type = trailer.getString("model");

                                    }


                                }
                                else{

                                }

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorHandler.setVolleyMessage(LoadBoardPreferences.this,error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public void fetchCityStates(final String edit_text_type, String edit_text_value, final String call_type) {
        // Tag used to cancel the request
        String tag_json_obj = "tag_city_states";
        String url = UrlController.FETCH_CITY_STATES;

        Map<String, String> params = new HashMap<>();
        params.put("limit", "50");
        params.put("start", "0");
        params.put("query",edit_text_value);
        showProgessBar();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response "," is "+response);
                        Boolean status = null;
                        JSONObject data_json;
                        hideProgressBar();
                        try {
                            status = response.getBoolean("status");
                            //collapseAllViews();
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){

                                    JSONArray city_states = data_json.getJSONArray("city_states");
                                    list_city_states.clear();

                                    if(city_states != null && city_states.length() > 0){
                                        for(int i= 0; i < city_states.length();i++){
                                            JSONObject city_state_single = (JSONObject) city_states.get(i);
                                            String city = city_state_single.getString("City");
                                            String state = city_state_single.getString("State");
                                            String zipcode = city_state_single.getString("ZipCode");
                                            String list_string = city+","+state+","+zipcode;
                                            if(edit_text_type != null && edit_text_type.equals("Origin")){
                                                list_city_states.add(list_string);
                                            }
                                            else if(edit_text_type != null && edit_text_type.equals("Destination")){
                                                destination_list_city_states.add(list_string);
                                            }



                                        }
                                    }

                                    if(edit_text_type != null && edit_text_type.equals("Origin")){
                                        city_state_adapter.notifyDataSetChanged();
                                        if(list_city_states != null && list_city_states.size() > 0){
                                            for(int i = 0 ; i < list_city_states.size() ; i ++){
                                                city_state_adapter.insert(list_city_states.get(i),i);
                                            }
                                        }
                                        city_state_adapter.notifyDataSetChanged();
                                    }
                                    else if(edit_text_type != null && edit_text_type.equals("Destination")){
                                        destination_city_state_adapter.notifyDataSetChanged();
                                        if(destination_list_city_states != null && destination_list_city_states.size() > 0){
                                            for(int i = 0 ; i < destination_list_city_states.size() ; i ++){
                                                destination_city_state_adapter.insert(destination_list_city_states.get(i),i);
                                            }
                                        }
                                        destination_city_state_adapter.notifyDataSetChanged();
                                    }


                                }

                            }


                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                ErrorHandler.setVolleyMessage(LoadBoardPreferences.this,error);
                hideProgressBar();

            }

        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                // headers.put("Content-Type", "application/json");
                headers.put("Token", session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    public void setCustomEditTextListener(){
        origin_state_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //int count = origin_state_edittext.getText().length();
                if(s != null && s.length() > 1){
                    fetchCityStates("Origin",origin_state_edittext.getText().toString(),"city_states");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        destination_state_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //int count = origin_state_edittext.getText().length();
                if(s != null && s.length() > 1){
                    fetchCityStates("Destination",destination_state_edittext.getText().toString(),"city_states");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        origin_state_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                InputMethodManager img = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                img.hideSoftInputFromWindow(origin_state_edittext.getWindowToken(), 0);

                String selected_text = origin_state_edittext.getText().toString();
                String[] selected_text_array = selected_text.split(",");

            }
        });

        destination_state_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                InputMethodManager img = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                img.hideSoftInputFromWindow(destination_state_edittext.getWindowToken(), 0);

                String selected_text = destination_state_edittext.getText().toString();
                String[] selected_text_array = selected_text.split(",");

            }
        });


    }

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void setDateListeners(){
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                pick_up_date_edittext.setText(date);
                pick_up_end_date_edittext.setText(date);
                //selectedDate = date;
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                pick_up_end_date_edittext.setText(date);
                //selectedEndDate = date;
            }
        };

    }


    public void initIds(){
        trailer_type_arrayList = new ArrayList<>();
        load_type_arrayList = new ArrayList<>();
        networkValidator = new NetworkValidator(LoadBoardPreferences.this);
        session = new SessionManager(LoadBoardPreferences.this);
    }

    @Bind(R.id.trailer_type_spinner)
    MultiSpinner trailer_type_spinner;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.load_type_spinner)
    AppCompatSpinner load_type_spinner;

    @Bind(R.id.pick_up_date_edittext)
    EditText pick_up_date_edittext;

    @Bind(R.id.pick_up_end_date_edittext)
    EditText pick_up_end_date_edittext;

    @Bind(R.id.origin_state_edittext)
    AutoCompleteTextView origin_state_edittext;

    @Bind(R.id.destination_state_edittext)
    AutoCompleteTextView destination_state_edittext;

    @Bind(R.id.origin_radius_edittext)
    EditText origin_radius_edittext;

    @Bind(R.id.destination_radius_edittext)
    EditText destination_radius_edittext;

    @Bind(R.id.length_trailer_edittext)
    EditText length_trailer_edittext;

    @Bind(R.id.weight_trailer_edittext)
    EditText weight_trailer_edittext;

    @Bind(R.id.post_age_spinner)
    AppCompatSpinner post_age_spinner;

    @Bind(R.id.pickup_date_textinput)
    TextInputLayout pickup_date_textinput;

    @Bind(R.id.pickup_end_date_textinput)
    TextInputLayout pickup_end_date_textinput;

    @Bind(R.id.body_layout)
    LinearLayout body_layout;

    @OnClick(R.id.pick_up_date_edittext)
    void setPickUpDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                startDateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(LoadBoardPreferences.this.getFragmentManager(), "DatePickerDialog");


    }

    @OnClick(R.id.pick_up_end_date_edittext)
    void setPickUpEndDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                endDateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(LoadBoardPreferences.this.getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.advance_filter_btn)
    void goToAdvanceFilters(){
        Intent i = new Intent(LoadBoardPreferences.this,LoadBoardSettings.class);
        startActivity(i);
    }

    @OnClick(R.id.apply_filters_btn)
    void applyFilters(){
        saveFilters();
    }

    private void saveFilters(){
        String origin = origin_state_edittext.getText().toString();
        String origin_radius = origin_radius_edittext.getText().toString();
        String destination = destination_state_edittext.getText().toString();
        String destination_radius = destination_radius_edittext.getText().toString();
        String pick_up_date = pick_up_date_edittext.getText().toString();
        String pick_up_end_date = pick_up_end_date_edittext.getText().toString();
        String length_trailer = length_trailer_edittext.getText().toString();
        String weight_trailer = weight_trailer_edittext.getText().toString();
        String post_age = post_age_spinner.getSelectedItem().toString();
        showProgessBar();
        String origin_zipcode = "";
        String origin_state = "";
        String destination_zipcode = "";
        String json_destination = "";
        String[] origin_array,destination_array;
        if(origin != "" && origin.length() > 0){
            origin_array = origin.split(",");
            Log.e("array length ",""+origin_array.length);
            if(1 < origin_array.length){
                origin_state = origin_array[0]+","+origin_array[1];
            }
            else{
                origin_state = origin_array[0];
            }

            Log.e("condition ","is "+(2 <= origin_array.length));
            if(2 < origin_array.length){
                //index exists
                origin_zipcode = origin_array[2];
            }
        }
        if(destination != "" && destination.length() > 0){
            destination_array = destination.split(",");
            String destination_state = destination_array[0]+","+destination_array[1];
            if(2 < destination_array.length){
                //index exists
                destination_zipcode = destination_array[2];
            }
            ArrayList<DestinationLocationModel> destination_arraylist = new ArrayList<>();
            destination_arraylist.add(new DestinationLocationModel("",destination_array[1],destination_array[0]));

            Gson gson = new Gson();
            json_destination = gson.toJson(destination_arraylist);
        }
        Log.e("trailer_type_string",""+trailer_type_spinner.getText().toString());

        if(post_age != "" && post_age.equals("Post age(Hours)")){
            post_age = "";
        }
        trailer_type_string = trailer_type_spinner.getText().toString();
        session.storeLoadBoardPreference(trailer_type_string,weight_trailer,length_trailer,origin_state,origin_zipcode,origin_radius,
                pick_up_date,pick_up_end_date,json_destination,destination_radius,destination_zipcode,load_type_string,post_age);
        hideProgressBar();
        Intent i  = new Intent(LoadBoardPreferences.this,LoadBaordActivity.class);
        startActivity(i);
    }

}
