package com.tmsfalcon.device.tmsfalcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
import com.tmsfalcon.device.tmsfalcon.entities.DriverModel;
import com.tmsfalcon.device.tmsfalcon.entities.GpsLocationParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 7/5/2017.
 */

public class Driver extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    Context context;
    private static final int DATABASE_VERSION = Utils.DATABASE_VERSION;

    // Database Name
    private static final String DATABASE_NAME = "tmsfalcon";

    // Contacts table name
    private static final String TABLE_DRIVER = "driver_basic";
    public static final String TABLE_DOCUMENTS = "documents";
    public static final String TABLE_DIRECT_UPLOAD = "direct_upload";
    public static final String TABLE_GPS_OFFLINE_LOCATIONS = "gps_offline_locations";
    public static final String TABLE_RECENT_CALLS = "recent_calls";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_DRIVER_ID = "driver_id";
    private static final String KEY_DRIVER_FULL_NAME = "full_name";
    private static final String KEY_SHOULD_UPDATE_PASSWORD = "should_update_password";
    private static final String KEY_DRIVER_NICK_NAME = "nick_name";
    private static final String KEY_DRIVER_THUMB = "thumb";
    private static final String KEY_DRIVER_UID = "uid";
    private static final String KEY_COMPANY_NAME = "company_name";
    private static final String KEY_COMPANY_ADDRESS = "company_address";
    private static final String KEY_COMPANY_COUNTRY = "company_country";
    private static final String KEY_COMPANY_STATE = "company_state";
    private static final String KEY_COMPANY_CITY = "company_city";
    private static final String KEY_COMPANY_ZIP_CODE = "company_zip_code";
    private static final String KEY_COMPANY_EIN = "company_ein";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_MIDDLE_NAME = "middle_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_DOB = "dob";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TYPE = "type";
    private static final String KEY_SSN = "ssn";
    private static final String KEY_DL = "dl";
    private static final String KEY_DL_STATE = "dl_state";
    private static final String KEY_DL_EXPIRATION = "dl_expiration";
    private static final String KEY_DL_ADDITIONAL = "dl_additional";
    private static final String KEY_HOME_PHONE = "home_phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_STATE = "state";
    private static final String KEY_CITY = "city";
    private static final String KEY_ZIP_CODE = "zip_code";
    private static final String KEY_ADDRESS_DURATION_YEARS = "address_duration_years";
    private static final String KEY_ADDRESS_DURATION_MONTHS = "address_duration_months";
    private static final String KEY_EMERGENCY_CONTACT_PERSON = "emergency_contact_person";
    private static final String KEY_EMERGENCY_CONTACT_RELATIONSHIP = "emergency_contact_relationship";
    private static final String KEY_CONTACT_CELL = "emergency_contact_cell";
    private static final String KEY_TOKEN = "token";

    //Columns for Document Table
    public static final String KEY_DOCUMENT_ID = "document_id";
    public static final String KEY_DOCUMENT_TYPE = "document_type";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_DOCUMENT_NAME = "document_name";
    public static final String KEY_LOAD_NUMBER = "load_number";
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_STATUS = "status";
    public static final String KEY = "key";
    public static final String KEY_DOCUMENT_BELONGS_TO = "document_belongs_to";

    //Columns for Gps Offine Locations Table
    public static final String KEY_DT = "dt";
    public static final String KEY_LATITUDE = "lat";
    public static final String KEY_LONGITUDE = "lng";
    public static final String KEY_ANGLE = "angle";
    public static final String KEY_ALTITUDE = "altitude";
    public static final String KEY_PARAMS = "params";
    public static final String KEY_SPEED = "speed";
    public static final String KEY_LOC_VALID = "loc_valid";

    //for recent calls table
    public static final String KEY_DIALER_NAME = "dialer_name";
    public static final String KEY_DIALER_PHONE = "dialer_phone";
    public static final String KEY_DIALER_EXTENSION = "dialer_extension";
    public static final String KEY_DIALER_TIME = "dialer_time";
    public static final String KEY_DIALER_CALL_TYPE = "dialer_call_type";

    public Driver(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_DRIVER_BASIC_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_DRIVER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_DRIVER_FULL_NAME + " TEXT," + KEY_SHOULD_UPDATE_PASSWORD + " INTEGER,"
                + KEY_DRIVER_NICK_NAME + " TEXT," + KEY_DRIVER_THUMB + " TEXT,"
                + KEY_DRIVER_UID + " TEXT," + KEY_COMPANY_NAME + " TEXT,"
                + KEY_COMPANY_ADDRESS + " TEXT," + KEY_COMPANY_COUNTRY + " INTEGER,"
                + KEY_COMPANY_STATE + " TEXT," + KEY_COMPANY_CITY + " TEXT,"
                + KEY_COMPANY_ZIP_CODE + " TEXT," + KEY_COMPANY_EIN + " TEXT,"
                + KEY_USERNAME + " TEXT," + KEY_FIRST_NAME + " INTEGER,"
                + KEY_MIDDLE_NAME + " TEXT," + KEY_LAST_NAME + " TEXT,"
                + KEY_GENDER + " TEXT," + KEY_DOB + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_TYPE + " INTEGER,"
                + KEY_SSN + " TEXT," + KEY_DL + " TEXT,"
                + KEY_DL_STATE + " TEXT," + KEY_DL_EXPIRATION + " TEXT,"
                + KEY_DL_ADDITIONAL + " TEXT," + KEY_HOME_PHONE + " INTEGER,"
                + KEY_ADDRESS + " TEXT," + KEY_COUNTRY + " TEXT,"
                + KEY_STATE + " TEXT," + KEY_CITY + " TEXT,"
                + KEY_ZIP_CODE + " TEXT," + KEY_ADDRESS_DURATION_YEARS + " INTEGER,"
                + KEY_ADDRESS_DURATION_MONTHS + " TEXT," + KEY_EMERGENCY_CONTACT_PERSON + " TEXT,"
                + KEY_EMERGENCY_CONTACT_RELATIONSHIP + " TEXT," + KEY_CONTACT_CELL + " TEXT,"
                + KEY_TOKEN + " TEXT" + ")";

        String CREATE_TABLE_DOCUMENTS = "CREATE TABLE IF NOT EXISTS "+TABLE_DOCUMENTS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_DOCUMENT_ID+
                " TEXT, "+KEY_DOCUMENT_TYPE+" TEXT, "+KEY_FILE_URL+" TEXT, "+KEY_DOCUMENT_NAME+
                " TEXT, "+KEY_LOAD_NUMBER+" TEXT, "+KEY_DUE_DATE+" TEXT, "+KEY_COMMENT+" TEXT, "+
                KEY_STATUS+" TEXT , "+KEY+" TEXT );";

        String CREATE_TABLE_DIRECT_UPLOAD = "CREATE TABLE IF NOT EXISTS "+TABLE_DIRECT_UPLOAD+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "
                                                +KEY_FILE_URL+" TEXT );";


        String CREATE_TABLE_GPS_OFFLINE_LOCATIONS = "CREATE TABLE IF NOT EXISTS "+TABLE_GPS_OFFLINE_LOCATIONS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_LATITUDE+" TEXT, "+KEY_LONGITUDE+" TEXT, "+KEY_ANGLE+
                " TEXT, "+KEY_ALTITUDE+" TEXT, "+KEY_SPEED+" TEXT, "+KEY_LOC_VALID+" TEXT, "+
                KEY_PARAMS+" TEXT , "+KEY_DT+" TEXT );";

        String CREATE_TABLE_RECENT_CALLS = "CREATE TABLE IF NOT EXISTS "+TABLE_RECENT_CALLS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+
                                            KEY_DIALER_NAME+" TEXT, "+KEY_DIALER_PHONE+" TEXT, "+KEY_DIALER_EXTENSION+" TEXT, "+
                                            KEY_DIALER_TIME+" TEXT, "+KEY_DIALER_CALL_TYPE+" TEXT );";
        db.execSQL(CREATE_DRIVER_BASIC_TABLE);
        db.execSQL(CREATE_TABLE_DOCUMENTS);
        db.execSQL(CREATE_TABLE_DIRECT_UPLOAD);
        db.execSQL(CREATE_TABLE_GPS_OFFLINE_LOCATIONS);
        db.execSQL(CREATE_TABLE_RECENT_CALLS);
    }

    public long addLocation(GpsLocationParams model) {

        long result = 0;
        SQLiteDatabase db = null;
        try{
            
        db = this.getWritableDatabase();

        createLocationTable(db);

        ContentValues values = new ContentValues();
        values.put(KEY_LATITUDE, model.getLat());
        values.put(KEY_LONGITUDE, model.getLng());
        values.put(KEY_ANGLE, model.getAngle());
        values.put(KEY_ALTITUDE, model.getAltitude());
        values.put(KEY_SPEED, model.getSpeed());
        values.put(KEY_LOC_VALID, model.getLoc_valid());
        values.put(KEY_PARAMS, model.getParams());
        values.put(KEY_DT, model.getDt());


        // Inserting Row
        
             result = db.insert(TABLE_GPS_OFFLINE_LOCATIONS, null, values);

        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }
        
        return result;
    }

    public void createLocationTable(SQLiteDatabase db){
        String CREATE_TABLE_GPS_OFFLINE_LOCATIONS = "CREATE TABLE IF NOT EXISTS "+TABLE_GPS_OFFLINE_LOCATIONS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_LATITUDE+" TEXT, "+KEY_LONGITUDE+" TEXT, "+KEY_ANGLE+
                " TEXT, "+KEY_ALTITUDE+" TEXT, "+KEY_SPEED+" TEXT, "+KEY_LOC_VALID+" TEXT, "+
                KEY_PARAMS+" TEXT , "+KEY_DT+" TEXT );";
        db.execSQL(CREATE_TABLE_GPS_OFFLINE_LOCATIONS);
    }

    public List<GpsLocationParams> getAllLocations() {
        List<GpsLocationParams> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GPS_OFFLINE_LOCATIONS;

        SQLiteDatabase db = this.getWritableDatabase();

        createLocationTable(db);

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                GpsLocationParams model = new GpsLocationParams();
                model.setId(cursor.getString(0));
                model.setLat(cursor.getString(1));
                model.setLng(cursor.getString(2));
                model.setAngle(cursor.getString(3));
                model.setAltitude(cursor.getString(4));
                model.setSpeed(cursor.getString(5));
                model.setLoc_valid(cursor.getString(6));
                model.setParams(cursor.getString(7));
                model.setDt(cursor.getString(8));
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public void showLocations(Driver driver){
        List<GpsLocationParams> records = driver.getAllLocations();

        for (GpsLocationParams cn : records) {
            String log = "Id: " + cn.getId() + " ,Lat: " + cn.getLat() + " ,Long: " + cn.getLng() +
                    " Angle  : " + cn.getAngle() + " Altitude: " + cn.getAltitude() + " Speed: "
                    + cn.getSpeed() + " Loc Valid: " + cn.getLoc_valid() + " Params: " + cn.getParams() +
                    " DT: " + cn.getDt() ;
            // Writing Contacts to log
           // Log.e("Location Data: ", log);
        }
    }

    public void deleteAllLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GPS_OFFLINE_LOCATIONS, null, null);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GPS_OFFLINE_LOCATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIRECT_UPLOAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENT_CALLS);
        // Create tables again
        onCreate(db);
    }

    public long addDriverBasic(DriverModel driverModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DRIVER_ID, driverModel.get_driver_id());
        values.put(KEY_DRIVER_FULL_NAME, driverModel.get_full_name());
        values.put(KEY_SHOULD_UPDATE_PASSWORD, driverModel.get_should_update_password());
        values.put(KEY_DRIVER_THUMB, driverModel.get_thumb());
        values.put(KEY_DRIVER_NICK_NAME, driverModel.get_nick_name());
        values.put(KEY_COMPANY_ZIP_CODE, driverModel.get_company_zip_code());
        values.put(KEY_DRIVER_UID, driverModel.get_uid());
        values.put(KEY_COMPANY_NAME, driverModel.get_company_name());
        values.put(KEY_COMPANY_ADDRESS, driverModel.get_company_address());
        values.put(KEY_COMPANY_COUNTRY, driverModel.get_company_country());
        values.put(KEY_COMPANY_STATE, driverModel.get_company_state());
        values.put(KEY_COMPANY_CITY, driverModel.get_company_city());
        values.put(KEY_COMPANY_EIN, driverModel.get_company_ein());
        values.put(KEY_USERNAME, driverModel.get_username());
        values.put(KEY_FIRST_NAME, driverModel.get_first_name());
        values.put(KEY_MIDDLE_NAME, driverModel.get_middle_name());
        values.put(KEY_LAST_NAME, driverModel.get_last_name());
        values.put(KEY_GENDER, driverModel.get_gender());
        values.put(KEY_DOB, driverModel.get_dob());
        values.put(KEY_EMAIL, driverModel.get_email());
        values.put(KEY_TYPE, driverModel.get_type());
        values.put(KEY_SSN, driverModel.get_ssn());
        values.put(KEY_DL, driverModel.get_dl());
        values.put(KEY_DL_STATE, driverModel.get_dl_state());

        values.put(KEY_DL_EXPIRATION, driverModel.get_dl_expiration());
        values.put(KEY_DL_ADDITIONAL, driverModel.get_dl_additional());
        values.put(KEY_HOME_PHONE, driverModel.get_home_phone());
        values.put(KEY_ADDRESS, driverModel.get_address());
        values.put(KEY_COUNTRY, driverModel.get_country());
        values.put(KEY_STATE, driverModel.get_state());

        values.put(KEY_CITY, driverModel.get_city());
        values.put(KEY_ZIP_CODE, driverModel.get_zip_code());
        values.put(KEY_ADDRESS_DURATION_YEARS, driverModel.get_address_duration_years());
        values.put(KEY_ADDRESS_DURATION_MONTHS, driverModel.get_address_duration_months());
        values.put(KEY_EMERGENCY_CONTACT_PERSON, driverModel.get_emergency_contact_person());
        values.put(KEY_EMERGENCY_CONTACT_RELATIONSHIP, driverModel.get_emergency_contact_relationship());
        values.put(KEY_CONTACT_CELL, driverModel.get_emergency_contact_cell());
        values.put(KEY_TOKEN, driverModel.get_token());


        // Inserting Row
        long result = db.insert(TABLE_DRIVER, null, values);
        db.close(); // Closing database connection
        return result;
    }



    // Getting single contact
    /*public DriverModel getDriver(int driver_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DRIVER, new String[] { KEY_ID,
                        KEY_DRIVER_ID, KEY_DRIVER_FULL_NAME,KEY_SHOULD_UPDATE_PASSWORD,KEY_DRIVER_NICK_NAME,KEY_DRIVER_THUMB,
                KEY_DRIVER_UID,KEY_COMPANY_NAME,KEY_COMPANY_ADDRESS,KEY_COMPANY_COUNTRY,KEY_COMPANY_STATE,KEY_COMPANY_CITY,
                KEY_COMPANY_ZIP_CODE,KEY_COMPANY_EIN,KEY_USERNAME,KEY_FIRST_NAME,KEY_MIDDLE_NAME,KEY_LAST_NAME,KEY_GENDER,
                KEY_DOB,KEY_EMAIL,KEY_TYPE,KEY_SSN,KEY_DL,KEY_DL_STATE,KEY_DL_EXPIRATION,KEY_DL_ADDITIONAL,KEY_HOME_PHONE,KEY_ADDRESS,
                KEY_COUNTRY,KEY_STATE,KEY_CITY,KEY_ZIP_CODE,KEY_ADDRESS_DURATION_YEARS,KEY_ADDRESS_DURATION_MONTHS,
                KEY_EMERGENCY_CONTACT_PERSON,KEY_EMERGENCY_CONTACT_RELATIONSHIP,KEY_CONTACT_CELL,KEY_TOKEN}, KEY_DRIVER_ID + "=?",
                new String[] { String.valueOf(driver_id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DriverModel driverModel = new DriverModel(Integer.parseInt(cursor.getString(0)),
                Integer.parseInt(cursor.getString(1)), cursor.getString(2),Integer.parseInt(cursor.getString(3)),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),
                cursor.getString(9),cursor.getString(10),cursor.getString(11),cursor.getString(12),cursor.getString(13),
                cursor.getString(14),cursor.getString(15),cursor.getString(16),cursor.getString(17),cursor.getString(18),
                cursor.getString(19),cursor.getString(20),cursor.getString(21),cursor.getString(22),
                cursor.getString(23),cursor.getString(24),cursor.getString(25),cursor.getString(26),cursor.getString(27),
                cursor.getString(28),cursor.getString(29),cursor.getString(30),cursor.getString(31),cursor.getString(32),
                cursor.getString(33),cursor.getString(34),cursor.getString(35),cursor.getString(36),cursor.getString(37),
                cursor.getString(38)
                );
        return driverModel;
    }*/

    // Getting All Contacts
   /* public List<DriverModel> getAllContacts() {
        List<DriverModel> contactList = new ArrayList<DriverModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DRIVER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DriverModel driverModel = new DriverModel();
                driverModel.setID(Integer.parseInt(cursor.getString(0)));
                driverModel.setName(cursor.getString(1));
                driverModel.setPhoneNumber(cursor.getString(2));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }*/

    public int updateDriver(DriverModel driverModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DRIVER_ID, driverModel.get_driver_id());
        values.put(KEY_DRIVER_FULL_NAME, driverModel.get_full_name());
        values.put(KEY_SHOULD_UPDATE_PASSWORD, driverModel.get_should_update_password());
        values.put(KEY_DRIVER_THUMB, driverModel.get_thumb());
        values.put(KEY_DRIVER_NICK_NAME, driverModel.get_nick_name());
        values.put(KEY_COMPANY_ZIP_CODE, driverModel.get_company_zip_code());
        values.put(KEY_DRIVER_UID, driverModel.get_uid());
        values.put(KEY_COMPANY_NAME, driverModel.get_company_name());
        values.put(KEY_COMPANY_ADDRESS, driverModel.get_company_address());
        values.put(KEY_COMPANY_COUNTRY, driverModel.get_company_country());
        values.put(KEY_COMPANY_STATE, driverModel.get_company_state());
        values.put(KEY_COMPANY_CITY, driverModel.get_company_city());
        values.put(KEY_COMPANY_EIN, driverModel.get_company_ein());
        values.put(KEY_USERNAME, driverModel.get_username());
        values.put(KEY_FIRST_NAME, driverModel.get_first_name());
        values.put(KEY_MIDDLE_NAME, driverModel.get_middle_name());
        values.put(KEY_LAST_NAME, driverModel.get_last_name());
        values.put(KEY_GENDER, driverModel.get_gender());
        values.put(KEY_DOB, driverModel.get_dob());
        values.put(KEY_EMAIL, driverModel.get_email());
        values.put(KEY_TYPE, driverModel.get_type());
        values.put(KEY_SSN, driverModel.get_ssn());
        values.put(KEY_DL, driverModel.get_dl());
        values.put(KEY_DL_STATE, driverModel.get_dl_state());

        values.put(KEY_DL_EXPIRATION, driverModel.get_dl_expiration());
        values.put(KEY_DL_ADDITIONAL, driverModel.get_dl_additional());
        values.put(KEY_HOME_PHONE, driverModel.get_home_phone());
        values.put(KEY_ADDRESS, driverModel.get_address());
        values.put(KEY_COUNTRY, driverModel.get_country());
        values.put(KEY_STATE, driverModel.get_state());
        values.put(KEY_CITY, driverModel.get_city());
        values.put(KEY_ZIP_CODE, driverModel.get_zip_code());
        values.put(KEY_ADDRESS_DURATION_YEARS, driverModel.get_address_duration_years());
        values.put(KEY_ADDRESS_DURATION_MONTHS, driverModel.get_address_duration_months());
        values.put(KEY_EMERGENCY_CONTACT_PERSON, driverModel.get_emergency_contact_person());
        values.put(KEY_EMERGENCY_CONTACT_RELATIONSHIP, driverModel.get_emergency_contact_relationship());
        values.put(KEY_CONTACT_CELL, driverModel.get_emergency_contact_cell());
        values.put(KEY_TOKEN, driverModel.get_token());

        // updating row
        return db.update(TABLE_DRIVER, values, KEY_ID + " = ?",
                new String[] { String.valueOf(driverModel.get_id()) });
    }

    public void deleteDriver(DriverModel driverModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DRIVER, KEY_ID + " = ?",
                new String[] { String.valueOf(driverModel.get_id()) });
        db.close();
    }

    public int getDriversCount() {
        String countQuery = "SELECT  * FROM " + TABLE_DRIVER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
        // return count
        return cursor.getCount();
    }

    public void displayDriverBasicData(DriverModel driverModel){
        String data = "Driver Id "+driverModel.get_driver_id()+" Full Name : "+driverModel.get_full_name()+" Nick Name : "+driverModel.get_nick_name()+
                " should update password "+driverModel.get_should_update_password()+" uid "+driverModel.get_uid()+" thumb "+
                driverModel.get_thumb()+" Token "+driverModel.get_token();
        Toast.makeText(context,data,Toast.LENGTH_LONG).show();
    }

}