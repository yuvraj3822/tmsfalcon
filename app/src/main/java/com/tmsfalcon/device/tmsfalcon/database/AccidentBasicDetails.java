package com.tmsfalcon.device.tmsfalcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentBasicDetailsModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentVehicleDetailsModel;

import java.util.ArrayList;

public class AccidentBasicDetails extends SQLiteOpenHelper {

    Context context;

    private static final int DATABASE_VERSION = Utils.DATABASE_VERSION;

    // Database Name
    private static final String DATABASE_NAME = "tmsfalcon";

    private static final String TABLE_ACCIDENT_BASIC_DETAILS = "accident_basic_details";

    private static final String TABLE_ACCIDENT_VEHICLE_DETAILS = "accident_vehicle_details";

    private static final String KEY_ID = "id";

    private static final String KEY_ACCIDENT_REPORT_ID = "accident_report_id";

    private static final String KEY_DRIVER_ID = "driver_id";

    private static final String KEY_ACCIDENT_LAT = "accident_lat";

    private static final String KEY_ACCIDENT_LONG = "accident_long";

    private static final String KEY_ACCIDENT_LOCATION = "accident_location";

    private static final String KEY_ACCIDENT_DATE = "accident_date";

    private static final String KEY_ACCIDENT_TIME = "accident_time";

    private static final String KEY_DRIVER_EMPLOYER_NAME = "driver_employer_name";

    private static final String KEY_DRIVER_EMPLOYER_PHONE_NO = "driver_employer_phone_no";

    private static final String KEY_DRIVER_EMPLOYER_INSURANCE_PROVIDER = "driver_employer_insurance_provider";

    private static final String KEY_DRIVER_EMPLOYER_INSURANCE_POLICY_NUMBER = "driver_employer_insurance_policy_number";

    private static final String KEY_VEHICLE_INSURANCE_PROVIDER = "vehicle_insurance_provider";

    private static final String KEY_VEHICLE_INSURANCE_POLICY_NUMBER = "vehicle_insurance_policy_no";

    private static final String KEY_VEHICLE_DOT_NUMBER = "vehicle_dot_number";

    private static final String KEY_VEHICLE_LICENSE_NUMBER = "vehicle_license_number";

    private static final String KEY_VEHICLE_REGISTRATION_NUMBER = "vehicle_registration_number";

    private static final String KEY_VEHICLE_OWNER_NAME = "vehicle_owner_name";

    private static final String KEY_VEHICLE_OWNER_PHONE_NUMBER = "vehicle_owner_phone_number";

    private static final String KEY_VEHICLE_OWNER_INSURANCE_PROVIDER = "vehicle_owner_insurance_provider";

    private static final String KEY_VEHICLE_OWNER_INSURANCE_POLICY_NUMBER = "vehicle_owner_insurance_policy_number";

    String CREATE_ACCIDENT_BASIC_DETAILS_TABLE;

    String CREATE_VEHICLE_DETAILS_TABLE;


    public AccidentBasicDetails(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        createTables(sqLiteDatabase);

    }

    public void createTables(SQLiteDatabase sqLiteDatabase){
         CREATE_ACCIDENT_BASIC_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_BASIC_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_ACCIDENT_LAT + " TEXT," + KEY_ACCIDENT_LONG + " TEXT,"
                + KEY_ACCIDENT_LOCATION + " TEXT," + KEY_ACCIDENT_DATE + " TEXT ,"+KEY_ACCIDENT_TIME +" TEXT, "
                + KEY_DRIVER_EMPLOYER_NAME+" TEXT, "+KEY_DRIVER_EMPLOYER_PHONE_NO+" TEXT, "
                + KEY_DRIVER_EMPLOYER_INSURANCE_PROVIDER+" TEXT, "+KEY_DRIVER_EMPLOYER_INSURANCE_POLICY_NUMBER+" TEXT)";

         CREATE_VEHICLE_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_VEHICLE_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"+ KEY_ACCIDENT_REPORT_ID + " INTEGER,"
                + KEY_VEHICLE_INSURANCE_PROVIDER + " TEXT," + KEY_VEHICLE_INSURANCE_POLICY_NUMBER + " TEXT,"
                + KEY_VEHICLE_DOT_NUMBER + " TEXT," + KEY_VEHICLE_LICENSE_NUMBER + " TEXT ,"+KEY_VEHICLE_REGISTRATION_NUMBER +" TEXT, "
                + KEY_VEHICLE_OWNER_NAME+" TEXT, "+KEY_VEHICLE_OWNER_PHONE_NUMBER+" TEXT, "
                + KEY_VEHICLE_OWNER_INSURANCE_PROVIDER+" TEXT, "+KEY_VEHICLE_OWNER_INSURANCE_POLICY_NUMBER+" TEXT)";


        sqLiteDatabase.execSQL(CREATE_ACCIDENT_BASIC_DETAILS_TABLE);
        sqLiteDatabase.execSQL(CREATE_VEHICLE_DETAILS_TABLE);
    }

    public long saveAccidentBasicDetails(AccidentBasicDetailsModel model){

        long result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createTables(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_LAT, model.getAccident_lat());
            values.put(KEY_ACCIDENT_LONG, model.getAccident_long());
            values.put(KEY_ACCIDENT_LOCATION, model.getAccident_location());
            values.put(KEY_ACCIDENT_DATE,model.getAccident_date());
            values.put(KEY_ACCIDENT_TIME, model.getAccident_time());
            values.put(KEY_DRIVER_EMPLOYER_NAME, model.getEmployer_name());
            values.put(KEY_DRIVER_EMPLOYER_PHONE_NO, model.getEmployer_phone_number());
            values.put(KEY_DRIVER_EMPLOYER_INSURANCE_PROVIDER,model.getEmployer_insurance_provider());
            values.put(KEY_DRIVER_EMPLOYER_INSURANCE_POLICY_NUMBER,model.getEmployer_insurance_policy_number());

            // Inserting Row

            result = db.insert(TABLE_ACCIDENT_BASIC_DETAILS, null, values);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;

    }

    public int updateAccidentBasicDetails(AccidentBasicDetailsModel model){

        int result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createTables(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_LAT, model.getAccident_lat());
            values.put(KEY_ACCIDENT_LONG, model.getAccident_long());
            values.put(KEY_ACCIDENT_LOCATION, model.getAccident_location());
            values.put(KEY_ACCIDENT_DATE,model.getAccident_date());
            values.put(KEY_ACCIDENT_TIME, model.getAccident_time());
            values.put(KEY_DRIVER_EMPLOYER_NAME, model.getEmployer_name());
            values.put(KEY_DRIVER_EMPLOYER_PHONE_NO, model.getEmployer_phone_number());
            values.put(KEY_DRIVER_EMPLOYER_INSURANCE_PROVIDER,model.getEmployer_insurance_provider());
            values.put(KEY_DRIVER_EMPLOYER_INSURANCE_POLICY_NUMBER,model.getEmployer_phone_number());

            // Inserting Row

            result = db.update(TABLE_ACCIDENT_BASIC_DETAILS, values,KEY_ID + "=" + model.getId(),null);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;

    }

    public int updateVehicleDetails(AccidentVehicleDetailsModel model){
        int result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createTables(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_REPORT_ID, model.getAccident_report_id());
            values.put(KEY_VEHICLE_INSURANCE_PROVIDER, model.getVehicle_insurance_provider());
            values.put(KEY_VEHICLE_INSURANCE_POLICY_NUMBER, model.getVehicle_insurance_policy_number());
            values.put(KEY_VEHICLE_DOT_NUMBER,model.getVehicle_dot_number());
            values.put(KEY_VEHICLE_LICENSE_NUMBER, model.getVehicle_license_number());
            values.put(KEY_VEHICLE_REGISTRATION_NUMBER, model.getVehicle_registration_number());
            values.put(KEY_VEHICLE_OWNER_NAME, model.getVehicle_owner_name());
            values.put(KEY_VEHICLE_OWNER_PHONE_NUMBER,model.getVehicle_owner_phone_number());
            values.put(KEY_VEHICLE_OWNER_INSURANCE_PROVIDER,model.getVehicle_owner_insurance_provider());
            values.put(KEY_VEHICLE_OWNER_INSURANCE_POLICY_NUMBER,model.getVehicle_owner_insurance_policy_number());

            // Inserting Row

            result = db.update(TABLE_ACCIDENT_VEHICLE_DETAILS, values,KEY_ACCIDENT_REPORT_ID + "=" + model.getAccident_report_id(),null);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;
    }

    public AccidentBasicDetailsModel getAccidentBasicDetailById(int id) {

        AccidentBasicDetailsModel model = new AccidentBasicDetailsModel();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_ACCIDENT_BASIC_DETAILS+" WHERE "+KEY_ID+" = '"+id+"'",null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            model.setId(cursor.getInt(0));
            model.setDriver_id(cursor.getInt(1));
            model.setAccident_lat(cursor.getString(2));
            model.setAccident_long(cursor.getString(3));
            model.setAccident_location(cursor.getString(4));
            model.setAccident_date(cursor.getString(5));
            model.setAccident_time(cursor.getString(6));
            model.setEmployer_name(cursor.getString(7));
            model.setEmployer_phone_number(cursor.getString(8));
            model.setEmployer_insurance_provider(cursor.getString(9));
            model.setEmployer_insurance_policy_number(cursor.getString(10));
        }

        // return list
        return model;
    }

    public ArrayList<AccidentBasicDetailsModel> getAllAccidentBasicDetai() {

        ArrayList<AccidentBasicDetailsModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_ACCIDENT_BASIC_DETAILS,null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AccidentBasicDetailsModel model = new AccidentBasicDetailsModel();
                model.setId(cursor.getInt(0));
                model.setDriver_id(cursor.getInt(1));
                model.setAccident_lat(cursor.getString(2));
                model.setAccident_long(cursor.getString(3));
                model.setAccident_location(cursor.getString(4));
                model.setAccident_date(cursor.getString(5));
                model.setAccident_time(cursor.getString(6));
                model.setEmployer_name(cursor.getString(7));
                model.setEmployer_phone_number(cursor.getString(8));
                model.setEmployer_insurance_provider(cursor.getString(9));
                model.setEmployer_insurance_policy_number(cursor.getString(10));
                list.add(model);
            }while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public void deleteAccidentBasicAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_BASIC_DETAILS, null, null);
    }
    public void deleteVehicleBasicAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_VEHICLE_DETAILS, null, null);
    }

    public int delAccidentBasicById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ACCIDENT_BASIC_DETAILS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public int delVehicleBasicById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ACCIDENT_VEHICLE_DETAILS, KEY_ACCIDENT_REPORT_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public int delVehicleBasicByPrimaryId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ACCIDENT_VEHICLE_DETAILS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public boolean checkAccidentBasicDetailEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ACCIDENT_BASIC_DETAILS, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        else{
            empty = false;
        }
        if(cur != null){
            cur.close();
        }

        return empty;
    }
    public boolean checkIfAccidentTableExists(){
        SQLiteDatabase db = null;
        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            db = this.getWritableDatabase();
            c = db.query(TABLE_ACCIDENT_BASIC_DETAILS, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
            /* fail */
            Log.d("log", "TABLE_ACCIDENT_BASIC_DETAILS "+" doesn't exist :(((");
        }

        return tableExists;
    }

    public boolean checkIfVehicleTableExists(){
        SQLiteDatabase db = null;
        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            db = this.getWritableDatabase();
            c = db.query(TABLE_ACCIDENT_VEHICLE_DETAILS, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
            /* fail */
            Log.d("log", "TABLE_ACCIDENT_VEHICLE_DETAILS "+" doesn't exist :(((");
        }

        return tableExists;
    }

    public boolean checkVehicleBasicDetailEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ACCIDENT_VEHICLE_DETAILS, null);
        if (cur != null && cur.moveToFirst()) {
            empty = (cur.getInt (0) == 0);
        }
        else{
            empty = false;
        }
        if(cur != null){
            cur.close();
        }

        return empty;
    }


    public long saveVehicleDetails(AccidentVehicleDetailsModel model){
        long result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createTables(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_REPORT_ID, model.getAccident_report_id());
            values.put(KEY_VEHICLE_INSURANCE_PROVIDER, model.getVehicle_insurance_provider());
            values.put(KEY_VEHICLE_INSURANCE_POLICY_NUMBER, model.getVehicle_insurance_policy_number());
            values.put(KEY_VEHICLE_DOT_NUMBER,model.getVehicle_dot_number());
            values.put(KEY_VEHICLE_LICENSE_NUMBER, model.getVehicle_license_number());
            values.put(KEY_VEHICLE_REGISTRATION_NUMBER, model.getVehicle_registration_number());
            values.put(KEY_VEHICLE_OWNER_NAME, model.getVehicle_owner_name());
            values.put(KEY_VEHICLE_OWNER_PHONE_NUMBER,model.getVehicle_owner_phone_number());
            values.put(KEY_VEHICLE_OWNER_INSURANCE_PROVIDER,model.getVehicle_owner_insurance_provider());
            values.put(KEY_VEHICLE_OWNER_INSURANCE_POLICY_NUMBER,model.getVehicle_owner_insurance_policy_number());

            // Inserting Row

            result = db.insert(TABLE_ACCIDENT_VEHICLE_DETAILS, null, values);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;
    }

    public ArrayList<AccidentVehicleDetailsModel> getVehicleDetailById(int accident_report_id) {

        ArrayList<AccidentVehicleDetailsModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_ACCIDENT_VEHICLE_DETAILS+" WHERE "+KEY_ACCIDENT_REPORT_ID+" = '"+accident_report_id+"'",null);
        Log.e("accident id"," is "+accident_report_id);
        Log.e("cursor count"," is "+cursor.getCount());
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AccidentVehicleDetailsModel model = new AccidentVehicleDetailsModel();
                model.setId(cursor.getInt(0));
                model.setDriver_id(cursor.getInt(1));
                model.setAccident_report_id(cursor.getInt(2));
                model.setVehicle_insurance_provider(cursor.getString(3));
                model.setVehicle_insurance_policy_number(cursor.getString(4));
                model.setVehicle_dot_number(cursor.getString(5));
                model.setVehicle_license_number(cursor.getString(6));
                model.setVehicle_registration_number(cursor.getString(7));
                model.setVehicle_owner_name(cursor.getString(8));
                model.setVehicle_owner_phone_number(cursor.getString(9));
                model.setVehicle_owner_insurance_provider(cursor.getString(10));
                model.setVehicle_owner_insurance_policy_number(cursor.getString(11));
                list.add(model);
                Log.e("list size "," is "+list.size());
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_ACCIDENT_BASIC_DETAILS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CREATE_VEHICLE_DETAILS_TABLE);
        // Create tables again
        onCreate(sqLiteDatabase);
    }
}
