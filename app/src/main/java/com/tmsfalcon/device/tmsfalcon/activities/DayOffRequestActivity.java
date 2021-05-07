package com.tmsfalcon.device.tmsfalcon.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.IdRes;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tmsfalcon.device.tmsfalcon.NavigationBaseActivity;
import com.tmsfalcon.device.tmsfalcon.R;
import com.tmsfalcon.device.tmsfalcon.customtools.AnimationUtils;
import com.tmsfalcon.device.tmsfalcon.customtools.AppController;
import com.tmsfalcon.device.tmsfalcon.customtools.CustomValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.ErrorHandler;
import com.tmsfalcon.device.tmsfalcon.customtools.NetworkValidator;
import com.tmsfalcon.device.tmsfalcon.customtools.SessionManager;
import com.tmsfalcon.device.tmsfalcon.customtools.UrlController;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DayOffRequestActivity extends NavigationBaseActivity implements View.OnClickListener{

    EditText date_off,start_time_editText,end_time_editText,off_reason;
    RadioGroup dayOffRadioGroup;
    RadioButton fullDayRadioButton,halfDayRadioButton;
    String selectedDate,selectedEndDate,selectedStartTime,selectedEndTime = "";
    LinearLayout time_layout;
    TimePickerDialog.OnTimeSetListener startTimeListener;
    TimePickerDialog.OnTimeSetListener endTimeListener;
    DatePickerDialog.OnDateSetListener startDateListener;
    DatePickerDialog.OnDateSetListener endDateListener;
    NetworkValidator networkValidator;
    CustomValidator customValidator;
    SessionManager session;
    boolean isFormValid ;
    String selected_radio = "h";
    Button submit;
    int is_full_off = 0;
    LinearLayout submit_layout;
    TextInputLayout date_input_layout,reason_input_layout,start_time_input_layout,end_time_input_layout;
    int is_multiple = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_day_off_request);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_day_off_request, null, false);
        drawer.addView(contentView, 0);
        ButterKnife.bind(this);
       /* lights_off.setVisibility(View.GONE);
        lights_on.setVisibility(View.GONE);*/
        initIds();
        int colorCodeDark = Color.parseColor("#071528");
        singleDayOffLayout.setVisibility(View.GONE);
        //progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        date_off.setOnClickListener(DayOffRequestActivity.this);
        endDate.setOnClickListener(DayOffRequestActivity.this);
        start_time_editText.setOnClickListener(DayOffRequestActivity.this);
        end_time_editText.setOnClickListener(DayOffRequestActivity.this);
        submit.setOnClickListener(DayOffRequestActivity.this);
        dayOffRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.full_off){
                    selected_radio = "f";
                    is_full_off = 1;
                    AnimationUtils.slideDown(time_layout);
                    /*time_layout.animate().alpha(0).setDuration(400);
                    submit_layout.animate().setStartDelay(400).setDuration(400).translationY(-320).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            start_time_editText.setOnClickListener(null);
                            end_time_editText.setOnClickListener(null);
                        }
                    });*/
                }
                else if(checkedId == R.id.half_off){
                    selected_radio = "h";
                    is_full_off = 0;
                    AnimationUtils.slideUp(time_layout);
                   /* time_layout.animate().setStartDelay(400).alpha(1).setDuration(600);
                    submit_layout.animate().setDuration(400).translationY(0).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            start_time_editText.setOnClickListener(DayOffRequestActivity.this);
                            end_time_editText.setOnClickListener(DayOffRequestActivity.this);
                        }
                    });*/

                }
            }
        });
        setListenerForDayOffType();
        setTimeListeners();
        setDateListeners();
    }

    private void setListenerForDayOffType(){
        dayOffTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.single_day_off){
                    is_multiple = 0;
                    AnimationUtils.slideUp(singleDayOffLayout);
                    endDateInputLayout.setVisibility(View.GONE);
                }
                else if(checkedId == R.id.multiple_day_off){
                    is_multiple = 1;
                    AnimationUtils.slideDown(singleDayOffLayout);
                    endDateInputLayout.setVisibility(View.VISIBLE);

                }
            }
        });
    }

  /* @Override
    protected void onResume() {

        super.onResume();
        //Set up Badge Count
        Log.e("count ",""+SessionManager.getInstance().getNotificationCount());
        Utils.setNotificationCount(cartBadgeTextView,SessionManager.getInstance().getNotificationCount());

    } */

    private void setTimeListeners() {
        startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                String startTime = String.format("%02d:%02d", hourOfDay, minute);
                //String startTime = hourOfDay+":"+minute;
                start_time_editText.setText(startTime);
                selectedStartTime = startTime;
            }
        };
        endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
                String endTime = String.format("%02d:%02d", hourOfDay, minute);
                //String endTime = hourOfDay+":"+minute;
                end_time_editText.setText(endTime);
                selectedEndTime = endTime;
            }
        };
    }

    private void setDateListeners(){
        startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                date_off.setText(date);
                selectedDate = date;
            }
        };

        endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
                String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                endDate.setText(date);
                selectedEndDate = date;
            }
        };
    }

    public void initIds(){

        date_off = findViewById(R.id.date);
        off_reason = findViewById(R.id.reason);
        start_time_editText = findViewById(R.id.start_time);
        end_time_editText = findViewById(R.id.end_time);
        dayOffRadioGroup = findViewById(R.id.is_full_off);
        fullDayRadioButton = findViewById(R.id.full_off);
        halfDayRadioButton = findViewById(R.id.half_off);
        time_layout = findViewById(R.id.time_layout);
        networkValidator = new NetworkValidator(DayOffRequestActivity.this);
        customValidator = new CustomValidator(DayOffRequestActivity.this);
        session = new SessionManager(DayOffRequestActivity.this);
        submit = findViewById(R.id.submit);
        submit_layout = findViewById(R.id.submit_layout);

        date_input_layout = findViewById(R.id.date_input_layout);
        reason_input_layout = findViewById(R.id.reason_input_layout);
        start_time_input_layout = findViewById(R.id.start_time_input_layout);
        end_time_input_layout = findViewById(R.id.end_time_input_layout);

    }


    public static Calendar stringToCalendar(String stringDate, String datePattern) {
        if (stringDate == null) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        try {
            Timestamp newDate = Timestamp.valueOf(stringDate);
            calendar.setTime(newDate);
        }
        catch (Exception e) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
            try {
                calendar.setTime(simpleDateFormat.parse(stringDate));
            }
            catch (ParseException pe) {
                calendar = null;
            }
        }
        return calendar;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        startDateListener,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setMinDate(now);
                dpd.show(DayOffRequestActivity.this.getFragmentManager(), "DatePickerDialog");
                break;
            case R.id.end_date:
                Calendar end_date_calendar = Calendar.getInstance();
                DatePickerDialog end_date_dpd = DatePickerDialog.newInstance(
                        endDateListener,
                        end_date_calendar.get(Calendar.YEAR),
                        end_date_calendar.get(Calendar.MONTH),
                        end_date_calendar.get(Calendar.DAY_OF_MONTH)
                );
                Calendar minCal = stringToCalendar(selectedDate,"dd/MM/yyyy");
                minCal.add(Calendar.DATE,1);
                end_date_dpd.setMinDate(minCal);
                end_date_dpd.show(DayOffRequestActivity.this.getFragmentManager(), "DatePickerDialogEndDate");
                break;
            case R.id.start_time:
                Calendar time = Calendar.getInstance();
                TimePickerDialog tpd = TimePickerDialog.newInstance(startTimeListener,time.HOUR_OF_DAY,time.MINUTE,true);
                tpd.show(DayOffRequestActivity.this.getFragmentManager(),"TimePickerDialog");
                break;

            case R.id.end_time:
                Calendar endtime_calendar = Calendar.getInstance();
                TimePickerDialog tpd_end = TimePickerDialog.newInstance(endTimeListener,endtime_calendar.HOUR_OF_DAY,endtime_calendar.MINUTE,true);
                tpd_end.show(DayOffRequestActivity.this.getFragmentManager(),"TimePickerDialog");
                break;
            case R.id.submit:
                validateForm();
                if(networkValidator.isNetworkConnected()){
                    if(isFormValid){
                        dayOffRequest();
                    }
                }
                else {
                    Toast.makeText(DayOffRequestActivity.this,getResources().getString(R.string.network_error),Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void dayOffRequest() {
        // Tag used to cancel the request
        String tag_json_obj = "day_off_request_tag";

        String url = UrlController.DAY_OFF;

        showProgessBar();

        Map<String, String> params = new HashMap<>();

        if(is_multiple  == 0){
            params.put("date", date_off.getText().toString());
            params.put("reason", off_reason.getText().toString());
            params.put("is_full_off", String.valueOf(is_full_off));
            params.put("start",start_time_editText.getText().toString());
            params.put("end",end_time_editText.getText().toString());

        }
        else if(is_multiple  == 1){
            params.put("date", date_off.getText().toString());
            params.put("reason", off_reason.getText().toString());
            params.put("is_full_off", String.valueOf(is_full_off));
            params.put("start",date_off.getText().toString());
            params.put("end",endDate.getText().toString());

        }

        params.put("is_multiple",""+is_multiple);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request
                .Method.POST,
                url, new JSONObject(params),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Boolean status = null;
                        JSONArray json_messages ;
                        String messages = "";

                        try {
                            status = response.getBoolean("status");
                            json_messages = response.getJSONArray("messages");

                            for(int i = 0 ; i < json_messages.length(); i++){
                                messages += json_messages.get(i)+"\n";
                            }

                            Toast.makeText(DayOffRequestActivity.this,messages,Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            Log.e("exception ", String.valueOf(e));
                        }
                        if(status){

                            date_off.setText("");
                            off_reason.setText("");
                            start_time_editText.setText("");
                            end_time_editText.setText("");
                            endDate.setText("");
                        }

                        Log.e("Response", response.toString());
                        hideProgressBar();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                ErrorHandler.setVolleyMessage(DayOffRequestActivity.this,error);
                hideProgressBar();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                //headers.put("Content-Type", "application/json");
                headers.put("Token",session.get_token());
                return headers;
            }

        };
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    public boolean validateForm() {
        if(dayOffTypeRadioGroup.getCheckedRadioButtonId() == R.id.single_day_off){
            if(selected_radio == "h"){
                if(!customValidator.setRequired(start_time_editText.getText().toString())){
                    start_time_input_layout.setError(getResources().getString(R.string.start_time_error));
                    isFormValid = false;

                }
                else{
                    start_time_input_layout.setError(null);
                    isFormValid = true;
                }
                if(!customValidator.setRequired(end_time_editText.getText().toString())){
                    end_time_input_layout.setError(getResources().getString(R.string.end_time_error));
                    isFormValid = false;
                }
                else{
                    end_time_input_layout.setError(null);
                    isFormValid = true;
                }
            }
        }
        else if(dayOffTypeRadioGroup.getCheckedRadioButtonId() == R.id.multiple_day_off){
            if(!customValidator.setRequired(endDate.getText().toString())){
                endDateInputLayout.setError(getResources().getString(R.string.end_date_error));
                isFormValid = false;
            }
            else{
                endDateInputLayout.setError(null);
                isFormValid = true;
            }
        }
        if(!customValidator.setRequired(date_off.getText().toString())){
            date_input_layout.setError(getResources().getString(R.string.date_error));
            isFormValid = false;
        }
        else{
            date_input_layout.setError(null);
            isFormValid = true;
        }
        if(!customValidator.setRequired(off_reason.getText().toString())){
            reason_input_layout.setError(getResources().getString(R.string.reason_error));
            isFormValid = false;
        }
        else{
            reason_input_layout.setError(null);
            isFormValid = true;
        }



        return isFormValid ;
    }

   /* @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
        date_off.setText(date);
        selectedDate = date;
    }*/

    public void showProgessBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    /*@Bind(R.id.light_off)
    ImageView lights_off;
    @Bind(R.id.light_on)
    ImageView lights_on;*/
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    /* @Bind(R.id.cart_badge)
    TextView cartBadgeTextView;*/
    @Bind(R.id.days_off_type)
    RadioGroup dayOffTypeRadioGroup;
    @Bind(R.id.single_day_off)
    RadioButton singleDayOffRadioButton;
    @Bind(R.id.multiple_day_off)
    RadioButton multipleDayOffRadioButton;
    @Bind(R.id.single_day_off_lyout)
    LinearLayout singleDayOffLayout;
    @Bind(R.id.bottom_layout)
    LinearLayout bottomLayout;
    @Bind(R.id.end_date_input_layout)
    TextInputLayout endDateInputLayout;
    @Bind(R.id.end_date)
    EditText endDate;

    /*@OnClick(R.id.bell_layout)
    void bell_functionality(){
        Intent i = new Intent(DayOffRequestActivity.this, NotificationActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.back_btn)
    void PreviousScreen() {
        super.onBackPressed();
    }*/

    @OnClick(R.id.view)
    void showDaysOff(){
        Intent startdActivity = new Intent(DayOffRequestActivity.this,DayOffActivity.class);
        startActivity(startdActivity);
    }

    /*@OnClick(R.id.toolbar_logo)
    void dashboard_func(){
        Utils.goToDashboard(DayOffRequestActivity.this);
    }*/


}
