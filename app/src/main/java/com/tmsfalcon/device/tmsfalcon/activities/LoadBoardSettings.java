package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AnimationUtils;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.MinMaxFilter;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.tmsfalcon.device.tmsfalcon.entities.DestinationLocationModel;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class LoadBoardSettings extends NavigationBaseActivity {

    SessionManager session;
    NetworkValidator networkValidator;
    String server_trailer_make,server_trailer_type,server_trailer_weight,server_trailer_length,server_trailer_width;
    ArrayList<String> trailer_types_arrayList = new ArrayList<>();
    ArrayList<String> dispatchers_arrayList = new ArrayList<>();
    DatePickerDialog.OnDateSetListener startDateListener;
    DatePickerDialog.OnDateSetListener endDateListener;
    DatePickerDialog.OnDateSetListener deliveryDateListener;
    DatePickerDialog.OnDateSetListener deliveryEndDateListener;
    String server_pick_up_date,server_pick_up_end_date;
    ArrayList<String> group_arrayList = new ArrayList<>();
    ArrayList<String> state_arrayList = new ArrayList<>();
    ArrayList<String> city_arrayList = new ArrayList<>();
    String selected_group,selected_state,server_origin_state,server_origin_city;
    AlertDialog destinationAlertDialog;
    AppCompatSpinner destination_group_spinner,destination_state_spinner,destination_city_spinner;
    ArrayList<DestinationLocationModel> destination_location_arraylist = new ArrayList<>();
    int first_time_login;
    ArrayList<String> list_city_states = new ArrayList<>();
    ArrayList<String> list_zipcode = new ArrayList<>();
    ArrayAdapter<String> city_state_adapter,zipcode_adapter;
    int first_city_state_call = 1;
    String[] trailer_prefix = {"Van","Reefer","Flatbed","Step Deck","Van or Reefer"};
    HashMap<String,String> dispatcher_hashmap = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_load_board_settings, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
        first_time_login = AppController.first_time_login;
        initIds();

        //hideAllFilterBodyViews();
        body_layout.setVisibility(View.GONE);

        if(networkValidator.isNetworkConnected()){
            getData();
        }
        else {
            Toast.makeText(LoadBoardSettings.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
        }
        setValidations();
        setDateListeners();
        if(AppController.first_time_login == 1){
            AppController.first_time_login = 0;
        }

        //hide cross image for first view
        /*LinearLayout cross_linear_layout = (LinearLayout) destination_location_container.getChildAt(0);
        LinearLayout cross_child_layout = (LinearLayout) cross_linear_layout.getChildAt(3);
        ImageView cross_image = (ImageView) cross_child_layout.getChildAt(0);
        cross_image.setVisibility(View.GONE);*/
        city_state_adapter = new ArrayAdapter<String>
                (LoadBoardSettings.this,android.R.layout.simple_list_item_1,list_city_states);
        city_state_adapter.setNotifyOnChange(true);
        origin_state_edittext.setAdapter(city_state_adapter);

        zipcode_adapter = new ArrayAdapter<>(LoadBoardSettings.this,android.R.layout.simple_list_item_1,list_zipcode);
        zipcode_adapter.setNotifyOnChange(true);
        origin_zipcode_edittext.setAdapter(zipcode_adapter);

        setCustomEditTextListener();

    }

    public void setCustomEditTextListener(){
        origin_state_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //int count = origin_state_edittext.getText().length();
                if(s != null && s.length() > 2){
                    fetchCityStates("Origin",origin_state_edittext.getText().toString(),"city_states");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        origin_zipcode_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s != null && s.length() > 2){
                    fetchCityStates("Origin",origin_zipcode_edittext.getText().toString(),"zipcode");
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
                //index exist (zipcode exists)
                if (2 <= selected_text_array.length) {
                    origin_zipcode_edittext.setText(selected_text_array[2]);
                }
            }
        });
        origin_zipcode_edittext.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                InputMethodManager img = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);
                img.hideSoftInputFromWindow(origin_zipcode_edittext.getWindowToken(), 0);

                String selected_text = list_city_states.get(i);
                origin_state_edittext.setText(selected_text);

            }
        });

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
                                    list_zipcode.clear();
                                    if(city_states != null && city_states.length() > 0){
                                        for(int i= 0; i < city_states.length();i++){
                                            JSONObject city_state_single = (JSONObject) city_states.get(i);
                                            String city = city_state_single.getString("City");
                                            String state = city_state_single.getString("State");
                                            String zipcode = city_state_single.getString("ZipCode");
                                            String list_string = city+","+state+","+zipcode;
                                            if(call_type != null && call_type.equalsIgnoreCase("city_states")){
                                                list_city_states.add(list_string);
                                            }
                                            else{
                                                list_city_states.add(list_string);
                                                list_zipcode.add(zipcode);
                                            }


                                        }
                                    }


                                    /*if(edit_text_type != null && edit_text_type.equalsIgnoreCase("origin")){

                                        Log.e("in","list size "+list_city_states.size());

                                        if(first_city_state_call == 1){
                                            origin_state_edittext.setAdapter(city_state_adapter);
                                            first_city_state_call = 0;
                                        }
                                        else{
                                            city_state_adapter.notifyDataSetChanged();
                                        }


                                    }
                                    else{
                                        Log.e("in","destination");
                                    }*/
                                    if(call_type != null && call_type.equalsIgnoreCase("city_states")){
                                        city_state_adapter.notifyDataSetChanged();
                                        if(list_city_states != null && list_city_states.size() > 0){
                                            for(int i = 0 ; i < list_city_states.size() ; i ++){
                                                city_state_adapter.insert(list_city_states.get(i),i);
                                            }
                                        }
                                        city_state_adapter.notifyDataSetChanged();
                                    }
                                    else{
                                        zipcode_adapter.notifyDataSetChanged();
                                        if(list_zipcode != null && list_zipcode.size() > 0){
                                            for(int i = 0 ; i < list_zipcode.size() ; i ++){
                                                zipcode_adapter.insert(list_zipcode.get(i),i);
                                            }
                                        }
                                        zipcode_adapter.notifyDataSetChanged();
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

                ErrorHandler.setVolleyMessage(LoadBoardSettings.this,error);
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

    public void showData(){
        if(first_time_login == 1){
           // setServerValues();
        }
        else{
            setSessionValues();
        }
    }

    public void setServerValues(){

        trailer_name_textview.setText(server_trailer_make);
        trailer_type_spinner.setSelection(((ArrayAdapter<String>)trailer_type_spinner.getAdapter()).getPosition(server_trailer_type));
        trailer_weight_editText.setText(server_trailer_weight);
        trailer_length_edittext.setText(server_trailer_length);
        trailer_width_editText.setText(server_trailer_width);

        pick_up_date_edittext.setText(server_pick_up_date);
        pick_up_end_date_edittext.setText(server_pick_up_end_date);
        origin_state_edittext.setText(server_origin_city+","+server_origin_state);
        origin_radius_edittext.setText("");

    }

    public void setSessionValues(){
        if(session.get_mark_available().equals("true")){
            collapsing_views.setVisibility(View.VISIBLE);
            trailer_name_textview.setText(session.get_trailer_name());
            trailer_type_spinner.setSelection(((ArrayAdapter<String>)trailer_type_spinner.getAdapter()).getPosition(session.get_trailer_type()));
            trailer_weight_editText.setText(session.get_trailer_weight());
            trailer_length_edittext.setText(session.get_trailer_length());
            trailer_width_editText.setText(session.get_trailer_width());

            origin_state_edittext.setText(session.get_origin_state());
            origin_zipcode_edittext.setText(session.get_origin_zipcode());
            // origin_city_edittext.setText(session.get_origin_city());
            origin_radius_edittext.setText(session.get_origin_radius());
            pick_up_date_edittext.setText(session.get_origin_pickup_date());
            pick_up_end_date_edittext.setText(session.get_origin_pickup_enddate());

            Type type = new TypeToken<List<DestinationLocationModel>>(){}.getType();
            Gson gson = new Gson();
            List<DestinationLocationModel> destination_list = gson.fromJson(session.get_destination_location_arraylist(), type);

            if(destination_list !=  null && destination_list.size() > 0){
                for(int i = 0 ; i < destination_list.size() ; i++){

                    DestinationLocationModel model = destination_list.get(i);

                    LayoutInflater inflater = (LayoutInflater)getBaseContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = inflater.inflate(R.layout.destination_filter_location_item, null);

                    destination_location_container.addView(view);

                    TextView group_textview,state_textview,city_textview;
                    ImageView cancel_icon;

                    group_textview = view.findViewById(R.id.destination_location_group_textview);
                    state_textview = view.findViewById(R.id.destination_location_state_textview);
                    city_textview = view.findViewById(R.id.destination_location_city_textview);
                    cancel_icon = view.findViewById(R.id.cancel_icon);

                    group_textview.setText(model.getDestination_group());
                    state_textview.setText(model.getDestination_state());
                    city_textview.setText(model.getDestination_city());
                    cancel_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            destination_location_container.removeView((View) view.getParent().getParent().getParent());
                        }
                    });
                }
            }

            destination_radius_edittext.setText(session.get_destination_radius());
            delivery_date_edittext.setText(session.get_delivery_date());
            delivery_end_date_edittext.setText(session.get_delivery_enddate());

            if(session.get_advance_filters().equals("true")){

                advance_filter_layout.setVisibility(View.VISIBLE);
                advance_filter_checkbox.setChecked(true);
                min_per_mile_edittext.setText(session.get_min_per_mile());
                max_miles_edittext.setText(session.get_max_miles());
                agent_spinner.setSelection(((ArrayAdapter<String>)agent_spinner.getAdapter()).getPosition(session.get_agent()));
                if(session.get_display_load_not_having_rate().equals("true")){
                    display_load_checkbox.setChecked(true);
                }
                else{
                    display_load_checkbox.setChecked(false);
                }
            }
            else{
                advance_filter_layout.setVisibility(View.GONE);
                advance_filter_checkbox.setChecked(false);

            }
        }
        else{
            available_switch.setChecked(false);
            collapsing_views.setVisibility(View.GONE);
        }
    }

    public static String getKeyFromValue(Map hm, Object value) {
        for (Object o : hm.keySet()) {
            if (hm.get(o).equals(value)) {
                return o.toString();
            }
        }
        return null;
    }

    public void saveLoadBoardSettingsData(){
        boolean is_available_checked = available_switch.isChecked();
        String trailer_name = trailer_name_textview.getText().toString();
        String trailer_type = trailer_type_spinner.getSelectedItem().toString();
        String max_weight = trailer_weight_editText.getText().toString();
        String trailer_length = trailer_length_edittext.getText().toString();
        String trailer_width = trailer_length_edittext.getText().toString();

        String origin_state = origin_state_edittext.getText().toString();
        String origin_zipcode = origin_zipcode_edittext.getText().toString();
        String origin_radius = origin_radius_edittext.getText().toString();
        String origin_pickup_date = pick_up_date_edittext.getText().toString();
        String origin_pickup_enddate = pick_up_end_date_edittext.getText().toString();

        String destination_radius = destination_radius_edittext.getText().toString();
        String destination_delivery_date = delivery_date_edittext.getText().toString();
        String destination_delivery_enddate = delivery_end_date_edittext.getText().toString();

        boolean advance_filters = advance_filter_checkbox.isChecked();
        String min_per_mile = min_per_mile_edittext.getText().toString();
        boolean display_loads_not_having_rate = display_load_checkbox.isChecked();
        String max_miles = max_miles_edittext.getText().toString();
        String agent = agent_spinner.getSelectedItem().toString();

        int childCount = destination_location_container.getChildCount();
        if(childCount > 0){
            for(int i = 0 ; i < childCount ; i++){

                LinearLayout linearLayout = (LinearLayout) destination_location_container.getChildAt(i);
                LinearLayout linearLayout1 = (LinearLayout) linearLayout.getChildAt(0);

                LinearLayout group_linear_layout = (LinearLayout) linearLayout1.getChildAt(0);
                TextView group_textview = (TextView) group_linear_layout.getChildAt(0);
                String group_name = group_textview.getText().toString();

                LinearLayout state_linear_layout = (LinearLayout) linearLayout1.getChildAt(1);
                TextView state_textview = (TextView) state_linear_layout.getChildAt(0);
                String state_name = state_textview.getText().toString();

                LinearLayout city_linear_layout = (LinearLayout) linearLayout1.getChildAt(2);
                TextView city_textview = (TextView) city_linear_layout.getChildAt(0);
                String city_name = city_textview.getText().toString();

                destination_location_arraylist.add(new DestinationLocationModel(group_name,state_name,city_name));
            }
        }

        Gson gson = new Gson();
        String json_destination = gson.toJson(destination_location_arraylist);

        showProgessBar();
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        session.deleteDestinationLocation();
        if(trailer_type.equalsIgnoreCase("Select Trailer Type")){
            trailer_type = "";
        }

        String dispatcher_id = getKeyFromValue(dispatcher_hashmap,agent);
       // Log.e("dispatcher_id",dispatcher_id);
        session.storeLoadBoardSettings(String.valueOf(is_available_checked),trailer_name,trailer_type,max_weight,trailer_length,
                trailer_width,origin_state,origin_zipcode,origin_radius,origin_pickup_date,origin_pickup_enddate,
                json_destination, destination_radius,destination_delivery_date,destination_delivery_enddate,
                String.valueOf(advance_filters),
                min_per_mile,max_miles,String.valueOf(display_loads_not_having_rate),agent,dispatcher_id);
        Toast.makeText(LoadBoardSettings.this,"Settings for Load Board Saved Successfully.",Toast.LENGTH_LONG).show();
        hideProgressBar();
        Intent i  = new Intent(LoadBoardSettings.this,LoadBaordActivity.class);
        startActivity(i);

    }

    public void loadGroups(){
        String state_full_name = "";
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray groups_array = obj.getJSONArray("groups");
            group_arrayList.clear();
            for(int i=0;i<groups_array.length();i++){
                JSONObject obj_key = groups_array.getJSONObject(i);
                Iterator<String> iter = obj_key.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    group_arrayList.add(key);
                    try {
                        if(i==0){
                            JSONArray states_array = obj_key.getJSONArray(key);
                            state_arrayList.clear();
                            for (int j=0; j<states_array.length(); j++) {

                                state_arrayList.add( states_array.get(j).toString());

                                if(j==0){
                                    state_full_name = states_array.get(j).toString();
                                }
                            }
                            ArrayAdapter state_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,state_arrayList);
                            state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            destination_state_spinner.setAdapter(state_adapter);

                        }

                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }
            }
            JSONArray cities_array = obj.getJSONArray("cities");
            for(int i=0;i<cities_array.length();i++){
                JSONObject obj_key = cities_array.getJSONObject(i);
                Iterator<String> iter = obj_key.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if(key.equals(state_full_name)){
                        try {
                            JSONArray city_array = obj_key.getJSONArray(key);
                            city_arrayList.clear();
                            for (int j=0; j<city_array.length(); j++) {
                                city_arrayList.add( city_array.getString(j) );
                            }

                            ArrayAdapter city_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,city_arrayList);
                            city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            destination_city_spinner.setAdapter(city_adapter);

                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }


                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadStatesFromGroup(String group_name){
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray groups_array = obj.getJSONArray("groups");
            state_arrayList.clear();
            for(int i=0;i<groups_array.length();i++){
                JSONObject obj_key = groups_array.getJSONObject(i);
                Iterator<String> iter = obj_key.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if(key.equals(group_name)){
                        try {
                            JSONArray states_array = obj_key.getJSONArray(key);

                            for (int j=0; j<states_array.length(); j++) {
                                state_arrayList.add( states_array.get(j).toString());
                            }

                            ArrayAdapter state_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,state_arrayList);
                            state_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            destination_state_spinner.setAdapter(state_adapter);

                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }


                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void loadCitiesFromState(String state_name){
        try {

            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray cities_array = obj.getJSONArray("cities");
            for(int i=0;i<cities_array.length();i++){
                JSONObject obj_key = cities_array.getJSONObject(i);
                Iterator<String> iter = obj_key.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    if(key.equals(state_name)){
                        try {
                            JSONArray city_array = obj_key.getJSONArray(key);
                            city_arrayList.clear();
                            for (int j=0; j<city_array.length(); j++) {
                                city_arrayList.add( city_array.getString(j) );
                            }

                            ArrayAdapter city_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,city_arrayList);
                            city_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            //Setting the ArrayAdapter data on the Spinner
                            destination_city_spinner.setAdapter(city_adapter);

                        } catch (JSONException e) {
                            // Something went wrong!
                        }
                    }


                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("states_cities.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void setValidations(){
        trailer_weight_editText.setFilters(new InputFilter[]{ new MinMaxFilter("1", "50000")});
        trailer_length_edittext.setFilters(new InputFilter[]{ new MinMaxFilter("1", "53")});
        trailer_width_editText.setFilters(new InputFilter[]{ new MinMaxFilter("1", "102")});
    }

    private void setDateListeners(){
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                pick_up_date_edittext.setText(date);
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

        deliveryDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                delivery_date_edittext.setText(date);
                //selectedEndDate = date;
            }
        };
        deliveryEndDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = (monthOfYear+1)+"/"+dayOfMonth+"/"+year;
                delivery_end_date_edittext.setText(date);
                //selectedEndDate = date;
            }
        };
    }

    public void initIds(){

        networkValidator = new NetworkValidator(LoadBoardSettings.this);
        session = new SessionManager(LoadBoardSettings.this);

    }

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
                            //collapseAllViews();
                            body_layout.setVisibility(View.VISIBLE);
                            collapseAllViews();
                            if(status){
                                data_json = response.getJSONObject("data");
                                if(data_json != null && data_json.length() > 0){

                                    JSONArray trailers_array = data_json.getJSONArray("trailers");
                                    JSONArray trailer_types_array = data_json.getJSONArray("trailer_types");
                                    JSONObject last_delivered_load = data_json.getJSONObject("last_delivered_load");
                                    JSONArray dispatchers = data_json.getJSONArray("dispatchers");

                                    if(last_delivered_load != null && last_delivered_load.length() > 0){
                                        String start_location = last_delivered_load.getString("start_location");
                                        server_pick_up_date = last_delivered_load.getString("start_date");
                                        server_pick_up_end_date = last_delivered_load.getString("end_date");

                                        if(start_location != null){

                                            String[] separated = start_location.split(",");
                                            server_origin_city = separated[0];
                                            server_origin_state = separated[1];


                                        }
                                    }

                                    if(trailers_array != null && trailers_array.length() > 0){
                                        //trailer assigned
                                        JSONObject trailer = (JSONObject) trailers_array.get(0);
                                        server_trailer_make = trailer.getString("make");
                                        server_trailer_weight = trailer.getString("max_weight");
                                        server_trailer_length = trailer.getString("length");
                                        server_trailer_width = trailer.getString("width");
                                        server_trailer_type = trailer.getString("model");

                                        /*CustomValidator.load_settings_widget(trailer_weight_editText,trailer_weight);
                                        CustomValidator.load_settings_widget(trailer_length_edittext,trailer_length);
                                        CustomValidator.load_settings_widget(trailer_width_editText,trailer_width);*/
                                    }

                                    int selected_spinner_position = 0;
                                    if(trailer_types_array != null && trailer_types_array.length() > 0){
                                        for(int i= 0; i < trailer_types_array.length();i++){
                                            JSONObject trailer_type = (JSONObject) trailer_types_array.get(i);
                                            String trailer_t = trailer_type.getString("cEquipmentDescription");
                                            /*for(int j = 0; j < trailer_prefix.length ; j++){
                                                if(trailer_t.startsWith(trailer_prefix[j])){

                                                    trailer_types_arrayList.add(trailer_t);
                                                }
                                                else{
                                                    Log.e("j ",trailer_t+" :: "+trailer_prefix[j]);
                                                }
                                            }
                                            {"Van","Reefer","Flatbed","Step Deck","Van or Reefer"};
*/

                                        }
                                        trailer_types_arrayList.add(0,"Select Trailer Type");
                                        trailer_types_arrayList.add("Van");
                                        trailer_types_arrayList.add("Reefer");
                                        trailer_types_arrayList.add("Flatbed");
                                        trailer_types_arrayList.add("Step Deck");
                                        trailer_types_arrayList.add("Van or Reefer");
                                    }

                                    if(dispatchers != null && dispatchers.length() > 0){
                                        for(int i= 0; i < dispatchers.length();i++){
                                            JSONObject dispatcher = (JSONObject) dispatchers.get(i);
                                            String dispatcher_name = dispatcher.getString("first_name")+" "+dispatcher.getString("last_name");
                                            String id = dispatcher.getString("dispatcher_id");
                                            dispatchers_arrayList.add(dispatcher_name);
                                            dispatcher_hashmap.put(id,dispatcher_name);

                                        }
                                    }

                                    dispatchers_arrayList.add(0,"All");
                                    ArrayAdapter dispatcher_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,dispatchers_arrayList);
                                    dispatcher_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Setting the ArrayAdapter data on the Spinner
                                    agent_spinner.setAdapter(dispatcher_adapter);


                                    ArrayAdapter aa = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,trailer_types_arrayList);
                                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    //Setting the ArrayAdapter data on the Spinner
                                    trailer_type_spinner.setAdapter(aa);
                                    /*for (int position = 0; position < aa.getCount(); position++) {
                                        if(aa.getItem(position).equals(server_trailer_type)) {
                                            trailer_type_spinner.setSelection(position);
                                            return;
                                        }
                                    }*/

                                    showData();
                                    //trailer_type_spinner.setSelection(selected_spinner_position);


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

                ErrorHandler.setVolleyMessage(LoadBoardSettings.this,error);
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

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void collapseAllViews(){
        AnimationUtils.collapse(trailer_filter_body);
        AnimationUtils.collapse(origin_filter_body);
        AnimationUtils.collapse(destination_filter_body);
        AnimationUtils.collapse(advance_filter_body);
    }

    public void hideAllFilterBodyViews(){
        trailer_filter_body.setVisibility(View.GONE);
        origin_filter_body.setVisibility(View.GONE);
        destination_filter_body.setVisibility(View.GONE);
        advance_filter_body.setVisibility(View.GONE);

    }

    private void resetData(){
        available_switch.setChecked(true);

        trailer_name_textview.setText("");
        trailer_type_spinner.setSelection(0);
        trailer_weight_editText.setText("");
        trailer_length_edittext.setText("");
        trailer_width_editText.setText("");

        origin_state_edittext.setText("");
        origin_zipcode_edittext.setText("");
        origin_radius_edittext.setText("");
        pick_up_date_edittext.setText("");
        pick_up_end_date_edittext.setText("");

        destination_location_container.removeAllViews();
        destination_radius_edittext.setText("");
        delivery_end_date_edittext.setText("");
        delivery_date_edittext.setText("");

        advance_filter_checkbox.setChecked(false);
        AnimationUtils.collapse(advance_filter_body);

        min_per_mile_edittext.setText("");
        display_load_checkbox.setChecked(false);
        max_miles_edittext.setText("");
        agent_spinner.setSelection(0);
        session.storeLoadBoardSettings(String.valueOf(true),"","","","",
                "","","","","","",
                "", "","","",
                String.valueOf(false),
                "","",String.valueOf(false),"","");
        Toast.makeText(LoadBoardSettings.this,"Settings for Load Board are Reset Successfully.",Toast.LENGTH_LONG).show();
        Intent i  = new Intent(LoadBoardSettings.this,LoadBaordActivity.class);
        startActivity(i);
    }

    void showDestinationLocationPopUp(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_destination_location, null);
        dialogBuilder.setView(dialogView);

        //final EditText subject = dialogView.findViewById(R.id.subject);
        destination_group_spinner = dialogView.findViewById(R.id.destination_group_spinner);
        destination_state_spinner = dialogView.findViewById(R.id.destination_state_spinner);
        destination_city_spinner = dialogView.findViewById(R.id.destination_city_spinner);

        loadGroups();
        ArrayAdapter group_adapter = new ArrayAdapter(LoadBoardSettings.this,android.R.layout.simple_spinner_item,group_arrayList);
        group_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        destination_group_spinner.setAdapter(group_adapter);

        destination_group_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_group = destination_group_spinner.getSelectedItem().toString();
                loadStatesFromGroup(selected_group);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        destination_state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected_state = destination_state_spinner.getSelectedItem().toString();
                loadCitiesFromState(selected_state);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button add_location_btn = dialogView.findViewById(R.id.add_destination_location);
        add_location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                destinationAlertDialog.dismiss();

                LayoutInflater inflater = (LayoutInflater)getBaseContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View view = inflater.inflate(R.layout.destination_filter_location_item, null);

                destination_location_container.addView(view);

                TextView group_textview,state_textview,city_textview;
                ImageView cancel_icon;

                group_textview = view.findViewById(R.id.destination_location_group_textview);
                state_textview = view.findViewById(R.id.destination_location_state_textview);
                city_textview = view.findViewById(R.id.destination_location_city_textview);
                cancel_icon = view.findViewById(R.id.cancel_icon);

                group_textview.setText(destination_group_spinner.getSelectedItem().toString());
                state_textview.setText(destination_state_spinner.getSelectedItem().toString());
                city_textview.setText(destination_city_spinner.getSelectedItem().toString());
                cancel_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        destination_location_container.removeView((View) view.getParent().getParent().getParent());
                    }
                });

            }
        });

        Button cancel_btn =  dialogView.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destinationAlertDialog.dismiss();

            }
        });

        destinationAlertDialog = dialogBuilder.create();
        //emailAlertDialog.setTitle("Send Documents By Email");
        destinationAlertDialog.show();

    }

    @Bind(R.id.min_per_mile_edittext)
    EditText min_per_mile_edittext;

    @Bind(R.id.display_load_checkbox)
    AppCompatCheckBox display_load_checkbox;

    @Bind(R.id.max_miles_edittext)
    EditText max_miles_edittext;

    @Bind(R.id.origin_radius_edittext)
    EditText origin_radius_edittext;

    @Bind(R.id.body_layout)
    LinearLayout body_layout;

    @Bind(R.id.progress_bar)
    ProgressBar progressBar;

    @Bind(R.id.collapsing_views)
    LinearLayout collapsing_views;

    @Bind(R.id.trailer_filter_header)
    LinearLayout trailer_filter_header;

    @Bind(R.id.trailer_filter_body)
    LinearLayout trailer_filter_body;

    @Bind(R.id.advance_filter_layout)
    LinearLayout advance_filter_layout;

    @Bind(R.id.advance_filter_body)
    LinearLayout advance_filter_body;

    @Bind(R.id.origin_filter_body)
    LinearLayout origin_filter_body;

    @Bind(R.id.destination_filter_body)
    LinearLayout destination_filter_body;

   /* @Bind(R.id.destination_location_layout)
    LinearLayout destination_location_layout;*/

    @Bind(R.id.destination_location_container)
    LinearLayout destination_location_container;

    @Bind(R.id.trailer_name_textview)
    TextView trailer_name_textview;

    @Bind(R.id.trailer_type_textview)
    TextView trailer_type_textview;

    @Bind(R.id.trailer_type_spinner)
    AppCompatSpinner trailer_type_spinner;

    @Bind(R.id.max_weight_edittext)
    EditText trailer_weight_editText;

    @Bind(R.id.trailer_length_edittext)
    EditText trailer_length_edittext;

    @Bind(R.id.trailer_width_edittext)
    EditText trailer_width_editText;

    @Bind(R.id.origin_state_edittext)
    AutoCompleteTextView origin_state_edittext;

    @Bind(R.id.origin_zipcode_edittext)
    AutoCompleteTextView origin_zipcode_edittext;

    @Bind(R.id.pick_up_date_edittext)
    EditText pick_up_date_edittext;

    @Bind(R.id.pick_up_end_date_edittext)
    EditText pick_up_end_date_edittext;

    @Bind(R.id.destination_radius_edittext)
    EditText destination_radius_edittext;

    @Bind(R.id.delivery_date_edittext)
    EditText delivery_date_edittext;

    @Bind(R.id.delivery_end_date_edittext)
    EditText delivery_end_date_edittext;

    @Bind(R.id.agent_spinner)
    AppCompatSpinner agent_spinner;

    @Bind(R.id.available_switch)
    SwitchCompat available_switch;

    @Bind(R.id.advance_filter_checkbox)
    AppCompatCheckBox advance_filter_checkbox;

    @Bind(R.id.scrollView)
    ScrollView scrollView;

    @OnClick(R.id.pickup_date_cancel_icon)
    void deletePickUpDate(){
        pick_up_date_edittext.setText("");
    }

    @OnClick(R.id.pickup_enddate_cancel_icon)
    void deletePickUpEndDate(){
        pick_up_end_date_edittext.setText("");
    }

    @OnClick(R.id.delivery_date_cancel_icon)
    void deleteDeliveryDate(){
        delivery_date_edittext.setText("");
    }

    @OnClick(R.id.delivery_enddate_cancel_icon)
    void deleteDeliveryEndDate(){
        delivery_end_date_edittext.setText("");
    }

    @OnClick(R.id.save_button)
    void saveSettings(){
        saveLoadBoardSettingsData();
    }

    @OnClick(R.id.add_destination_icon)
    void addDestinationLayout(){

        showDestinationLocationPopUp();
        /*LayoutInflater inflater = (LayoutInflater)getBaseContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.destination_filter_location_item, null);

        destination_location_container.addView(view);*/
    }

    @OnClick(R.id.trailer_filter_header)
    void toggleTrailerFilter(){
        if(trailer_filter_body.isShown()){
            AnimationUtils.collapse(trailer_filter_body);
        }
        else{
            AnimationUtils.expand(trailer_filter_body);
        }
    }

    @OnClick(R.id.advance_filter_header)
    void toggleAdvanceFilter(){
        if(advance_filter_body.isShown()){
            AnimationUtils.collapse(advance_filter_body);
        }
        else{
            AnimationUtils.expand(advance_filter_body);
        }
    }

    @OnClick(R.id.origin_filter_header)
    void toggleOriginFilter(){
        if(origin_filter_body.isShown()){
            AnimationUtils.collapse(origin_filter_body);
        }
        else{
            AnimationUtils.expand(origin_filter_body);
        }
    }

    @OnClick(R.id.destination_filter_header)
    void toggleDestinationFilter(){
        if(destination_filter_body.isShown()){
            AnimationUtils.collapse(destination_filter_body);
        }
        else{
            AnimationUtils.expand(destination_filter_body);
        }
    }

    @OnCheckedChanged(R.id.available_switch)
    public void onCheckedChanged(SwitchCompat switchCompat, boolean isChecked){

        if (isChecked) {
            collapsing_views.setVisibility(View.VISIBLE);
        }
        else {
            collapsing_views.setVisibility(View.GONE);
        }
    }

    @OnCheckedChanged(R.id.advance_filter_checkbox)
    void onAdvanceFilterSelection(CompoundButton button, boolean checked) {
        if(button.isChecked()){
            advance_filter_layout.requestFocus();
            advance_filter_layout.setVisibility(View.VISIBLE);
        }
        else{
            advance_filter_layout.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.current_location_icon)
    void setCurrentAddress(){
        String state = session.getKeyCurrentState();
        String city = session.getKeyCurrentCity();

        origin_state_edittext.setText(city+","+state);
        origin_zipcode_edittext.setText("");

    }

    @OnClick(R.id.pick_up_date_edittext)
    void setPickUpDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd;
        if(server_pick_up_date != null){
            String pick_up_date_array[] = server_pick_up_date.split("/");
            int year = Integer.parseInt(pick_up_date_array[2]);
            int month = Integer.parseInt(pick_up_date_array[1]);
            int day = Integer.parseInt(pick_up_date_array[0]);
             dpd = DatePickerDialog.newInstance(
                    startDateListener,
                     year,month-1,day

            );
        }
        else{
             dpd = DatePickerDialog.newInstance(
                    startDateListener,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }


        //dpd.setMinDate(now);
        dpd.show(LoadBoardSettings.this.getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.pick_up_end_date_edittext)
    void setPickUpEndDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd;
        if(server_pick_up_end_date != null){
            String pick_up_date_array[] = server_pick_up_end_date.split("/");
            int year = Integer.parseInt(pick_up_date_array[2]);
            int month = Integer.parseInt(pick_up_date_array[1]);
            int day = Integer.parseInt(pick_up_date_array[0]);
            dpd = DatePickerDialog.newInstance(
                    endDateListener,
                    year,month-1,day

            );
        }
        else{
            dpd = DatePickerDialog.newInstance(
                    endDateListener,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }


        //dpd.setMinDate(now);
        dpd.show(LoadBoardSettings.this.getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.delivery_date_edittext)
    void deliveryDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd;
        dpd = DatePickerDialog.newInstance(
                deliveryDateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        //dpd.setMinDate(now);
        dpd.show(LoadBoardSettings.this.getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.delivery_end_date_edittext)
    void deliveryEndDatePicker(){
        Calendar now = Calendar.getInstance();

        DatePickerDialog dpd;
        dpd = DatePickerDialog.newInstance(
                deliveryEndDateListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );


        //dpd.setMinDate(now);
        dpd.show(LoadBoardSettings.this.getFragmentManager(), "DatePickerDialog");
    }

    @OnClick(R.id.reset_settings_imageview)
    void resetSettings(){

        new AlertDialog.Builder(this)
                .setTitle("Cancel")
                .setMessage("Do you really want to reset the Settings?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        resetData();

                    }})

                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }


}
