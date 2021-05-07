package com.tmsfalcon.device.tmsfalcon.customtools;

/**
 * Created by Android on 7/6/2017.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tmsfalcon.device.tmsfalcon.Login;
import com.tmsfalcon.device.tmsfalcon.Responses.ContactsDataResponse;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class SessionManager {

    private static final String TAG_TOKEN = "tagtoken";
    private static SessionManager instance;
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "TmsFalcon";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    private static final String KEY_DRIVER_ID = "driver_id";
    private static final String KEY_DRIVER_FULL_NAME = "full_name";
    private static final String KEY_SHOULD_UPDATE_PASSWORD = "should_update_password";
    private static final String KEY_DRIVER_NICK_NAME = "nick_name";
    private static final String KEY_DRIVER_THUMB = "thumb";
    private static final String KEY_DRIVER_UID = "uid";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TYPE = "type";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_GENDER = "gender";

    private static final String NOTIFICATION_COUNT = "notification_count";
    private static final String EXPIRED_DOC_COUNT = "expired_documents_count";

    //GPS Variables
    private static final String KEY_GPS_ON_DUTY_INTERVAL = "on_duty_interval";
    private static final String KEY_GPS_OFF_DUTY_INTERVAL = "off_duty_interval";
    private static final String KEY_GPS_ON_DUTY_METRES = "on_duty_metres";
    private static final String KEY_GPS_OFF_DUTY_METRES = "on_duty_metres";
    //private static final String KEY_GPS_INTERVAL = "interval";
    private static final String KEY_GPS_CURRENT_STATUS = "current_status";
    private static final String KEY_GPS_LOCATION_STATUS = "location_status";

    //Direct Upload Document Info Variables
    private static final String KEY_DOCUMENT_BELONGS_TO = "document_belongs_to";
    private static final String KEY_DOCUMENT_TYPE = "document_type";
    private static final String KEY_DOCUMENT_ID = "document_id";

    //Widget Variables
    private static final String KEY_WIDGET_LOAD_PICKUP_LOCATION = "widget_pickup_location";
    private static final String KEY_WIDGET_LOAD_DELIVERY_LOCATION = "widget_delivery_location";
    private static final String KEY_WIDGET_SETTLEMENT_AMOUNT = "widget_settlement_amount";

    private static final String KEY_COMPANY_ID = "company_id";

    private static final String KEY_CURRENT_LATITUDE = "current_latitude";
    private static final String KEY_CURRENT_LONGITUDE = "current_longitude";

    private static final String KEY_CURRENT_STATE = "current_state";
    private static final String KEY_CURRENT_CITY = "current_city";

    /**
     * database accident info save status
     */
    private static final String ACCIDENT_BASIC_SAVE = "ACCIDENT_BASIC_SAVE";
    private static final String OTHER_ACCIDENT_BASIC_SAVE = "OTHER_ACCIDENT_BASIC_SAVE";
    private static final String INJURED_ACCIDENT_BASIC_SAVE = "INJURED_ACCIDENT_BASIC_SAVE";
    private static final String WITNESS_ACCIDENT_SAVE = "WITNESS_ACCIDENT_SAVE";
    private static final String PHOTOGRAPHIC_ACCIDENT_SAVE = "PHOTOGRAPHIC_ACCIDENT_SAVE";
    private static final String SIGNATURE_ACCIDENT_SAVE = "SIGNATURE_ACCIDENT_SAVE";
    private static final String ACCIDENT_DATA_UPLOADED = "ACCIDENT_DATA_UPLOADED";

    private static final String ACCIDENT_TEXT_SAVE = "ACCIDENT_TEXT_SAVE";
    private static final String OTHER_ACCIDENT_TEXT_SAVE = "OTHER_ACCIDENT_TEXT_SAVE";
    private static final String INJURED_ACCIDENT_TEXT_SAVE = "INJURED_ACCIDENT_TEXT_SAVE";
    private static final String WITNESS_ACCIDENT_TEXT_SAVE = "WITNESS_ACCIDENT_TEXT_SAVE";
    private static final String PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE = "PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE";
    private static final String SIGNATURE_ACCIDENT_TEXT_SAVE = "SIGNATURE_ACCIDENT_TEXT_SAVE";
    private static final String ACCIDENT_DATA_TEXT_UPLOADED = "ACCIDENT_DATA_TEXT_UPLOADED";


    //load board settings variables
    private static final String KEY_LOAD_TYPE = "load_type";
    private static final String KEY_POST_AGE = "post_age";
    private static final String KEY_MARK_AVAILABLE = "mark_available";
    private static final String KEY_TRAILER_NAME = "trailer_name";
    private static final String KEY_TRAILER_TYPE = "trailer_type";
    private static final String KEY_TRAILER_WEIGHT = "trailer_weight";
    private static final String KEY_TRAILER_LENGTH = "trailer_length";
    private static final String KEY_TRAILER_WIDTH = "trailer_width";
    private static final String KEY_ORIGIN_STATE = "origin_state";
    private static final String KEY_ORIGIN_ZIPCODE = "origin_zipcode";
    private static final String KEY_ORIGIN_RADIUS = "origin_radius";
    private static final String KEY_ORIGIN_PICK_UP_DATE = "origin_pick_up_date";
    private static final String KEY_ORIGIN_PICK_UP_END_DATE = "origin_pick_up_end_date";
    private static final String KEY_DESTINATION_RADIUS = "destination_radius";
    private static final String KEY_DESTINATION_ZIPCODE = "destination_zipcode";
    private static final String KEY_DELIVERY_DATE = "delivery_date";
    private static final String KEY_DELIVERY_END_DATE = "delivery_end_date";
    private static final String KEY_ADVANCE_FILTERS = "advance_filters";
    private static final String KEY_MIN_PER_MILE = "min_per_mile";
    private static final String KEY_MAX_MILES = "max_miles";
    private static final String KEY_DISPLAY_LOADS_NOT_HAVING_RATE = "display_loads_not_having_rate";
    private static final String KEY_AGENT = "agent";
    private static final String KEY_AGENT_ID = "agent_id";
    private static final String KEY_DESTINATION_LOCATION_ARRAYLIST = "destination_location_arraylist";

    private static final String KEY_CURRENT_STREET_ADDRESS = "current_street_address";


    private static String KEY_EMAIL_ID = "email_id";

    public void storeLoadBoardSettings(String mark_available,String trailer_name, String trailer_type, String trailer_weight,
                                       String trailer_length,
                                       String trailer_width, String origin_state, String origin_zipcode,
                                       String origin_radius,
                                       String origin_pick_up_date, String origin_pick_up_end_date,
                                       String destination_location_arraylist, String destination_radius,
                                       String delivery_date, String delivery_end_date, String advance_filters,
                                       String min_per_mile,
                                       String max_miles, String display_loads_not_having_rate, String agent,String agent_id){
        editor.putString(KEY_MARK_AVAILABLE,mark_available);
        editor.putString(KEY_TRAILER_NAME,trailer_name);
        editor.putString(KEY_TRAILER_TYPE,trailer_type);
        editor.putString(KEY_TRAILER_WEIGHT,trailer_weight);
        editor.putString(KEY_TRAILER_LENGTH,trailer_length);
        editor.putString(KEY_TRAILER_WIDTH,trailer_width);
        editor.putString(KEY_ORIGIN_STATE,origin_state);
        editor.putString(KEY_ORIGIN_ZIPCODE,origin_zipcode);
        editor.putString(KEY_ORIGIN_RADIUS,origin_radius);
        editor.putString(KEY_ORIGIN_PICK_UP_DATE,origin_pick_up_date);
        editor.putString(KEY_ORIGIN_PICK_UP_END_DATE,origin_pick_up_end_date);
        editor.putString(KEY_DESTINATION_LOCATION_ARRAYLIST,destination_location_arraylist);
        editor.putString(KEY_DESTINATION_RADIUS,destination_radius);
        editor.putString(KEY_DELIVERY_DATE,delivery_date);
        editor.putString(KEY_DELIVERY_END_DATE,delivery_end_date);
        editor.putString(KEY_ADVANCE_FILTERS,advance_filters);
        editor.putString(KEY_MIN_PER_MILE,min_per_mile);
        editor.putString(KEY_MAX_MILES,max_miles);
        editor.putString(KEY_DISPLAY_LOADS_NOT_HAVING_RATE,display_loads_not_having_rate);
        editor.putString(KEY_AGENT,agent);
        editor.putString(KEY_AGENT_ID,agent_id);
        editor.commit();
    }

    public void storeLoadBoardPreference(String trailer_type, String trailer_weight,
                                       String trailer_length, String origin_state, String origin_zipcode,
                                       String origin_radius,
                                       String origin_pick_up_date, String origin_pick_up_end_date,
                                       String destination_location_arraylist, String destination_radius,
                                       String destination_zipcode,
                                       String load_type,String post_age){
        editor.putString(KEY_TRAILER_TYPE,trailer_type);
        editor.putString(KEY_TRAILER_WEIGHT,trailer_weight);
        editor.putString(KEY_TRAILER_LENGTH,trailer_length);
        editor.putString(KEY_ORIGIN_STATE,origin_state);
        editor.putString(KEY_ORIGIN_ZIPCODE,origin_zipcode);
        editor.putString(KEY_ORIGIN_RADIUS,origin_radius);
        editor.putString(KEY_ORIGIN_PICK_UP_DATE,origin_pick_up_date);
        editor.putString(KEY_ORIGIN_PICK_UP_END_DATE,origin_pick_up_end_date);
        editor.putString(KEY_DESTINATION_LOCATION_ARRAYLIST,destination_location_arraylist);
        editor.putString(KEY_DESTINATION_RADIUS,destination_radius);
        editor.putString(KEY_DESTINATION_ZIPCODE,destination_zipcode);
        editor.putString(KEY_LOAD_TYPE,load_type);
        editor.putString(KEY_POST_AGE,post_age);

        editor.putString(KEY_MARK_AVAILABLE,String.valueOf(true));

        editor.commit();
    }

    public void storeEmailID(String emailID){
        editor.putString(KEY_EMAIL_ID,emailID);
        editor.commit();
    }

    public String getEmailId(){
        return pref.getString(KEY_EMAIL_ID,"");
    }
    public void deleteLoadBoardSettings(){
        editor.remove(KEY_MARK_AVAILABLE);
        editor.remove(KEY_TRAILER_NAME);
        editor.remove(KEY_TRAILER_TYPE);
        editor.remove(KEY_TRAILER_WEIGHT);
        editor.remove(KEY_TRAILER_LENGTH);
        editor.remove(KEY_TRAILER_WIDTH);
        editor.remove(KEY_ORIGIN_STATE);
        editor.remove(KEY_ORIGIN_ZIPCODE);
        editor.remove(KEY_ORIGIN_RADIUS);
        editor.remove(KEY_ORIGIN_PICK_UP_DATE);
        editor.remove(KEY_ORIGIN_PICK_UP_END_DATE);
        editor.remove(KEY_DESTINATION_LOCATION_ARRAYLIST);
        editor.remove(KEY_DESTINATION_RADIUS);
        editor.remove(KEY_DELIVERY_DATE);
        editor.remove(KEY_DELIVERY_END_DATE);
        editor.remove(KEY_ADVANCE_FILTERS);
        editor.remove(KEY_MIN_PER_MILE);
        editor.remove(KEY_MAX_MILES);
        editor.remove(KEY_DISPLAY_LOADS_NOT_HAVING_RATE);
        editor.remove(KEY_AGENT);
        editor.remove(KEY_POST_AGE);
        editor.remove(KEY_LOAD_TYPE);

        editor.commit();
    }

    public String get_mark_available(){
        return pref.getString(KEY_MARK_AVAILABLE,"");
    }

    public String get_agent(){
        return pref.getString(KEY_AGENT,"");
    }

    public String get_agent_id(){
        return pref.getString(KEY_AGENT_ID,"");
    }

    public String get_display_load_not_having_rate(){
        return pref.getString(KEY_DISPLAY_LOADS_NOT_HAVING_RATE,"");
    }

    public String get_max_miles(){
        return pref.getString(KEY_MAX_MILES,"");
    }

    public String get_min_per_mile(){
        return pref.getString(KEY_MIN_PER_MILE,"");
    }

    public String get_advance_filters(){
        return pref.getString(KEY_ADVANCE_FILTERS,"");
    }

    public String get_delivery_enddate(){
        return pref.getString(KEY_DELIVERY_END_DATE,"");
    }

    public String get_delivery_date(){
        return pref.getString(KEY_DELIVERY_DATE,"");
    }

    public String get_destination_radius(){
        return pref.getString(KEY_DESTINATION_RADIUS,"");
    }

    public String get_destination_zipcode(){
        return pref.getString(KEY_DESTINATION_ZIPCODE,"");
    }


    public String get_destination_location_arraylist(){
        return pref.getString(KEY_DESTINATION_LOCATION_ARRAYLIST,"");
    }

    public String get_origin_pickup_enddate(){
        return pref.getString(KEY_ORIGIN_PICK_UP_END_DATE,"");
    }

    public String get_origin_pickup_date(){
        return pref.getString(KEY_ORIGIN_PICK_UP_DATE,"");
    }

    public String get_origin_radius(){
        return pref.getString(KEY_ORIGIN_RADIUS,"");
    }

    public String get_origin_zipcode(){
        return pref.getString(KEY_ORIGIN_ZIPCODE,"");
    }

    public String get_origin_state(){
        return pref.getString(KEY_ORIGIN_STATE,"");
    }

    public String get_trailer_width(){
        return pref.getString(KEY_TRAILER_WIDTH,"");
    }

    public String get_trailer_length(){
        return pref.getString(KEY_TRAILER_LENGTH,"");
    }

    public String get_trailer_name(){
        return pref.getString(KEY_TRAILER_NAME,"");
    }

    public String get_trailer_type(){
        return pref.getString(KEY_TRAILER_TYPE,"");
    }

    public String get_trailer_weight(){
        return pref.getString(KEY_TRAILER_WEIGHT,"");
    }

    public String get_load_type(){
        return pref.getString(KEY_LOAD_TYPE,"");
    }

    public String get_post_age(){
        return pref.getString(KEY_POST_AGE,"");
    }

    public int get_driver_id(){
        return pref.getInt(KEY_DRIVER_ID,0);
    }

    public String getProfileJsonArray(String JsonArrayName){
        return pref.getString(JsonArrayName,"");
    }

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public static SessionManager getInstance() {
        if (instance == null)
            instance = new SessionManager();

        return instance;
    }
    public SessionManager()
    {
        pref = AppController.getInstance().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createProfileJsonArrays(String JsonArrayName, String JsonArrayValue){
        editor.putString(JsonArrayName,JsonArrayValue);
        editor.commit();
    }


    public void deleteProfileJsonArray(String JsonArrayName){
        pref.edit().remove(JsonArrayName).commit();
    }

    public void deleteDestinationLocation(){
        pref.edit().remove(KEY_DESTINATION_LOCATION_ARRAYLIST).commit();
    }


    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     * */
    @SuppressWarnings("unused")
    public void checkLogin(){
        // Check login status
        if(!this.isLoggedIn()){
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, Login.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

    }

    /**
     * Get stored session data
     * */
    @SuppressWarnings("unused")
    public HashMap<String, String> getDriverDetails(){

        HashMap<String, String> user = new HashMap<>();
        user.put(KEY_DRIVER_ID, pref.getString(KEY_DRIVER_ID,null));
        user.put(KEY_DRIVER_FULL_NAME, pref.getString(KEY_DRIVER_FULL_NAME, null));
        user.put(KEY_DRIVER_NICK_NAME, pref.getString(KEY_DRIVER_NICK_NAME, null));
        user.put(KEY_DRIVER_THUMB, pref.getString(KEY_DRIVER_THUMB, null));
        user.put(KEY_DRIVER_UID, pref.getString(KEY_DRIVER_UID, null));
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        // return user
        return user;
    }

    public int get_company_id(){
        return pref.getInt(KEY_COMPANY_ID,0);
    }

    public String get_driver_fullname(){
        return pref.getString(KEY_DRIVER_FULL_NAME, null);
    }

    @SuppressWarnings("unused")
    public String get_driver_nickname(){
        return pref.getString(KEY_DRIVER_NICK_NAME, null);
    }
    public String get_driver_thumb(){
        return pref.getString(KEY_DRIVER_THUMB, null);
    }
    public String get_driver_uid(){
        return pref.getString(KEY_DRIVER_UID, null);
    }
    public String get_token(){
        return pref.getString(KEY_TOKEN, null);
    }
    public String get_company_name(){
        return pref.getString(KEY_COMPANY_NAME, null);
    }
    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences
        //editor.clear();
        editor.remove(IS_LOGIN);
        editor.remove(KEY_DRIVER_ID);
        editor.remove(KEY_DRIVER_FULL_NAME);
        editor.remove(KEY_DRIVER_NICK_NAME);
        editor.remove(KEY_DRIVER_THUMB);
        editor.remove(KEY_DRIVER_UID);
        editor.remove(KEY_TYPE);
        editor.remove(KEY_COMPANY_NAME);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_COMPANY_ID);

        editor.remove(KEY_CURRENT_LONGITUDE);
        editor.remove(KEY_CURRENT_LATITUDE);
//        new
        editor.remove(KEY_EMAIL_ID);
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, Login.class);
//        // Closing all the Activities
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        // Add new Flag to start new Activity
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
//        (Activity(_context.fi))
        ActivityCompat.finishAffinity((Activity) _context);

    }

    public void logoutUserForNotifications(){
        // Clearing all data from Shared Preferences
        //editor.clear();
        editor.remove(IS_LOGIN);
        editor.remove(KEY_DRIVER_ID);
        editor.remove(KEY_DRIVER_FULL_NAME);
        editor.remove(KEY_DRIVER_NICK_NAME);
        editor.remove(KEY_DRIVER_THUMB);
        editor.remove(KEY_DRIVER_UID);
        editor.remove(KEY_TYPE);
        editor.remove(KEY_COMPANY_NAME);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_CURRENT_LONGITUDE);
        editor.remove(KEY_CURRENT_LATITUDE);
        editor.commit();

    }
    /**
     * Create login session
     * */
    public void createLoginSession(int driver_id,String driver_name, String nick_name,String uid,String thumb,String token,String type,String company_name,String gender,int company_id){
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putInt(KEY_DRIVER_ID, driver_id);
        editor.putString(KEY_DRIVER_FULL_NAME, driver_name);
        editor.putString(KEY_DRIVER_NICK_NAME, nick_name);
        editor.putString(KEY_DRIVER_THUMB, thumb);
        editor.putString(KEY_DRIVER_UID, uid);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_TYPE, type);
        editor.putString(KEY_COMPANY_NAME,company_name);
        editor.putString(KEY_GENDER,gender);
        editor.putInt(KEY_COMPANY_ID,company_id);
        // commit changes
        editor.commit();
    }

    public void storeGpsParameters(int off_duty,int on_duty, int off_duty_metres,int on_duty_metres,String current,Boolean location_status){
        // Storing login value as TRUE
        editor.putBoolean(KEY_GPS_LOCATION_STATUS, location_status);
        editor.putInt(KEY_GPS_OFF_DUTY_INTERVAL, off_duty);
        editor.putInt(KEY_GPS_ON_DUTY_INTERVAL, on_duty);
        editor.putInt(KEY_GPS_ON_DUTY_METRES, off_duty_metres);
        editor.putInt(KEY_GPS_OFF_DUTY_METRES, on_duty_metres);
        editor.putString(KEY_GPS_CURRENT_STATUS, current);
        // commit changes
        editor.commit();
    }

    public void storeCurrentLocation(String latitude,String longitude){
        editor.putString(KEY_CURRENT_LATITUDE,latitude);
        editor.putString(KEY_CURRENT_LONGITUDE,longitude);
        editor.commit();
    }

    public void storeCurrentAddress(String state,String city){
        editor.putString(KEY_CURRENT_STATE,state);
        editor.putString(KEY_CURRENT_CITY,city);
        editor.commit();
    }


    public void clearSessionForAccidentDb(){
        editor.remove(ACCIDENT_BASIC_SAVE);
        editor.remove(OTHER_ACCIDENT_BASIC_SAVE);
        editor.remove(INJURED_ACCIDENT_BASIC_SAVE);
        editor.remove(WITNESS_ACCIDENT_SAVE);
        editor.remove(PHOTOGRAPHIC_ACCIDENT_SAVE);
        editor.remove(SIGNATURE_ACCIDENT_SAVE);
        editor.remove(ACCIDENT_DATA_UPLOADED);


        editor.remove(ACCIDENT_TEXT_SAVE);
        editor.remove(OTHER_ACCIDENT_TEXT_SAVE);
        editor.remove(INJURED_ACCIDENT_TEXT_SAVE);
        editor.remove(WITNESS_ACCIDENT_TEXT_SAVE);
        editor.remove(PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE);

        editor.commit();
    }


    public void saveStatusBasicPref(){
        editor.putBoolean(ACCIDENT_BASIC_SAVE,true);
        editor.commit();
    }

    public Boolean getStatusBasicPref(){
        return pref.getBoolean(ACCIDENT_BASIC_SAVE,false);
    }


    public void saveStatusOtherBasicPref(){
        editor.putBoolean(OTHER_ACCIDENT_BASIC_SAVE,true);
        editor.commit();
    }

    public Boolean getStatusOtherBasicPref(){
        return pref.getBoolean(OTHER_ACCIDENT_BASIC_SAVE,false);
    }

    public void saveStatusInjuredBasicPref(){
        editor.putBoolean(INJURED_ACCIDENT_BASIC_SAVE,true);
        editor.commit();
    }

    public Boolean getStatusInjuredBasicPref(){
        return pref.getBoolean(INJURED_ACCIDENT_BASIC_SAVE,false);
    }

    public void saveStatusWitenessPref(){
        editor.putBoolean(WITNESS_ACCIDENT_SAVE,true);
        editor.commit();
    }

    public Boolean getStatusWitnessPref(){
        return pref.getBoolean(WITNESS_ACCIDENT_SAVE,false);
    }

    public void saveStatusPhotographicPref(){
        editor.putBoolean(PHOTOGRAPHIC_ACCIDENT_SAVE,true);
        editor.commit();
    }



    public Boolean getStatusPhotographicPref(){
        return pref.getBoolean(PHOTOGRAPHIC_ACCIDENT_SAVE,false);
    }


    /**
     * save text to show
     */


    public void saveStatusTextBasicPref(){
        editor.putBoolean(ACCIDENT_TEXT_SAVE,true);
        editor.commit();
    }

    public void clearTextBasicPref(){
        editor.remove(ACCIDENT_TEXT_SAVE);
        editor.commit();
    }

    public Boolean getStatusTextBasicPref(){
        return pref.getBoolean(ACCIDENT_TEXT_SAVE,false);
    }


    public void saveStatusOtherTextBasicPref(){
        editor.putBoolean(OTHER_ACCIDENT_TEXT_SAVE,true);
        editor.commit();
    }

    public void clearTextOtherBasicPref(){
        editor.remove(OTHER_ACCIDENT_TEXT_SAVE);
        editor.commit();
    }

    public Boolean getStatusOtherTextBasicPref(){
        return pref.getBoolean(OTHER_ACCIDENT_TEXT_SAVE,false);
    }

    public void saveStatusInjuredTextBasicPref(){
        editor.putBoolean(INJURED_ACCIDENT_TEXT_SAVE,true);
        editor.commit();
    }

    public void clearTextInjuredPref(){
        editor.remove(INJURED_ACCIDENT_TEXT_SAVE);
        editor.commit();
    }

    public Boolean getStatusInjuredTextBasicPref(){
        return pref.getBoolean(INJURED_ACCIDENT_TEXT_SAVE,false);
    }

    public void saveStatusTextWitenessPref(){
        editor.putBoolean(WITNESS_ACCIDENT_TEXT_SAVE,true);
        editor.commit();
    }

    public void clearTextWitnessPref(){
        editor.remove(WITNESS_ACCIDENT_TEXT_SAVE);
        editor.commit();
    }


    public Boolean getStatusTextWitnessPref(){
        return pref.getBoolean(WITNESS_ACCIDENT_TEXT_SAVE,false);
    }

    public void saveStatusPhotographicTextPref(){
        editor.putBoolean(PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE,true);
        editor.commit();
    }

    public void clearTextPhotographicPref(){
        editor.remove(PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE);
        editor.commit();
    }

    public Boolean getStatusPhotographicTextPref(){
        return pref.getBoolean(PHOTOGRAPHIC_ACCIDENT_TEXT_SAVE,false);
    }









    public void saveStatusSignaturePref(){
        editor.putBoolean(SIGNATURE_ACCIDENT_SAVE,true);
        editor.commit();
    }

    public Boolean getStatusSignaturePref(){
        return pref.getBoolean(SIGNATURE_ACCIDENT_SAVE,false);
    }

    public void uploadTheSavedData(){
        editor.putBoolean(ACCIDENT_DATA_UPLOADED,true);
        editor.commit();
    }

    public Boolean getStatusOfUploadedData(){
        return pref.getBoolean(ACCIDENT_DATA_UPLOADED,false);
    }



    public String getKeyCurrentStreetAddress(){
        return pref.getString(KEY_CURRENT_STREET_ADDRESS,null);
    }

    public String getKeyCurrentState(){
        return pref.getString(KEY_CURRENT_STATE,null);
    }

    public String getKeyCurrentCity(){
        return pref.getString(KEY_CURRENT_CITY,null);
    }

    public String getKeyCurrentLatitude(){
        return pref.getString(KEY_CURRENT_LATITUDE,null);
    }

    public String getKeyCurrentLongitude(){
        return pref.getString(KEY_CURRENT_LONGITUDE,null);
    }

    public String getKeyDocumentBelongsTo() {
        return pref.getString(KEY_DOCUMENT_BELONGS_TO, null);
    }

    public String getKeyDocumentType() {
        return pref.getString(KEY_DOCUMENT_TYPE, null);
    }

    public String getKeyDocumentId() {
        return pref.getString(KEY_DOCUMENT_ID, null);
    }

    public void storeDocumentInfoForDirectUpload(String id, String document_belongs_to, String document_type){
        editor.putString(KEY_DOCUMENT_ID, id);
        editor.putString(KEY_DOCUMENT_BELONGS_TO, document_belongs_to);
        editor.putString(KEY_DOCUMENT_TYPE, document_type);
        editor.commit();
    }

    public void clearSessionForDirectUpload(){
        editor.remove(KEY_DOCUMENT_ID);
        editor.remove(KEY_DOCUMENT_BELONGS_TO);
        editor.remove(KEY_DOCUMENT_TYPE);
        editor.commit();
    }


    public void clearSession(){
        //editor.clear();
        editor.remove(IS_LOGIN);
        editor.remove(KEY_DRIVER_ID);
        editor.remove(KEY_DRIVER_FULL_NAME);
        editor.remove(KEY_DRIVER_NICK_NAME);
        editor.remove(KEY_DRIVER_THUMB);
        editor.remove(KEY_DRIVER_UID);
        editor.remove(KEY_TYPE);
        editor.remove(KEY_COMPANY_NAME);
        editor.remove(KEY_GENDER);
        editor.remove(KEY_COMPANY_ID);
        editor.remove(KEY_CURRENT_LONGITUDE);
        editor.remove(KEY_CURRENT_LATITUDE);

        editor.commit();
    }

    public void clearGpsParameters(){
        //editor.clear();
        editor.remove(KEY_GPS_LOCATION_STATUS);
        editor.remove(KEY_GPS_OFF_DUTY_METRES);
        editor.remove(KEY_GPS_ON_DUTY_METRES);
        editor.remove(KEY_GPS_OFF_DUTY_INTERVAL);
        editor.remove(KEY_GPS_ON_DUTY_INTERVAL);
        editor.remove(KEY_GPS_CURRENT_STATUS);
        editor.commit();
    }

    /**
     * Quick check for login
     * **/
    // Get Login State
    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

    public boolean saveDeviceToken(String token){
        //SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TAG_TOKEN, token);
        editor.apply();
        return true;
    }

    //this method will fetch the device token from shared preferences
    public String getDeviceToken(){
        //SharedPreferences sharedPreferences = _context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return  pref.getString(TAG_TOKEN, null);
    }

    public void storeNotificationCount(String count){
        editor.putString(NOTIFICATION_COUNT, count);
        editor.commit();
    }

    public void storeCurrentStreetAddress(String address){
        editor.putString(KEY_CURRENT_STREET_ADDRESS, address);
        editor.commit();
    }

    public void storeDriverGender(String gender){
        editor.putString(KEY_GENDER, gender);
        editor.commit();
    }

    public void storeDriverThumb(String thumb){
        editor.putString(KEY_DRIVER_THUMB, thumb);
        editor.commit();

    }

    public int getKeyGpsOnDutyMetres() {
        return pref.getInt(KEY_GPS_ON_DUTY_METRES, 0);
    }

    public int getKeyGpsOffDutyMetres() {
        return pref.getInt(KEY_GPS_OFF_DUTY_METRES, 0);
    }

    public int getKeyGpsOnDutyInterval() {
        return pref.getInt(KEY_GPS_ON_DUTY_INTERVAL, 0);
    }

    public int getKeyGpsOffDutyInterval() {
        return pref.getInt(KEY_GPS_OFF_DUTY_INTERVAL, 0);
    }

    /*public int getKeyGpsInterval() {
        return pref.getInt(KEY_GPS_INTERVAL, 0);
    }*/

    public String getKeyGpsCurrentStatus() {
        return pref.getString(KEY_GPS_CURRENT_STATUS, null);
    }

    public Boolean getKeyGpsLocationStatus() {
        return pref.getBoolean(KEY_GPS_LOCATION_STATUS, true);
    }

    public void storeExpiredDocumentsCount(int count){

        editor.putInt(EXPIRED_DOC_COUNT, count);
        editor.commit();
    }

    public void storeWidgetVariables(String pickup_location,String delivery_location,String settlement_amount){

        editor.putString(KEY_WIDGET_LOAD_PICKUP_LOCATION, pickup_location);
        editor.putString(KEY_WIDGET_LOAD_DELIVERY_LOCATION, delivery_location);
        editor.putString(KEY_WIDGET_SETTLEMENT_AMOUNT, settlement_amount);
        editor.commit();
    }
    public String getKeyWidgetLoadPickupLocation() {
        return pref.getString(KEY_WIDGET_LOAD_PICKUP_LOCATION, null);
    }

    public String getKeyWidgetLoadDeliveryLocation() {
        return pref.getString(KEY_WIDGET_LOAD_DELIVERY_LOCATION, null);
    }

    public String getKeyWidgetSettlementAmount() {
        return pref.getString(KEY_WIDGET_SETTLEMENT_AMOUNT, null);
    }

    public String getNotificationCount(){
        return pref.getString(NOTIFICATION_COUNT, null);
    }

    public String getDriverGender(){
        return pref.getString(KEY_GENDER, null);
    }

    public int getExpiredDocCount(){
        return pref.getInt(EXPIRED_DOC_COUNT, 0);
    }

    public void saveContactsDataResponse(List<ContactsDataResponse.Datum> list, String key){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public List<ContactsDataResponse.Datum> getContactsDataResponse(String key){
        Gson gson = new Gson();
        String json = pref.getString(key, null);
        Type type = new TypeToken<List<ContactsDataResponse.Datum>>() {}.getType();
        return gson.fromJson(json, type);
    }
}