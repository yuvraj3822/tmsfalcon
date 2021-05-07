package com.tmsfalcon.device.tmsfalcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;
import com.tmsfalcon.device.tmsfalcon.entities.RecentCallsModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 4/4/2019.
 */

public class RecentCallsTable extends SQLiteOpenHelper {

    Context context;
    public static final int DATABASE_VERSION = Utils.DATABASE_VERSION;
    public static final String DATABASE_NAME = "tmsfalcon";
    public static final String TABLE_RECENT_CALLS = "recent_calls";
    public static final String KEY_ID = "id";
    public static final String KEY_DIALER_NAME = "dialer_name";
    public static final String KEY_DIALER_PHONE = "dialer_phone";
    public static final String KEY_DIALER_EXTENSION = "dialer_extension";
    public static final String KEY_DIALER_TIME = "dialer_time";
    public static final String KEY_DIALER_CALL_TYPE = "dialer_call_type";

    public RecentCallsTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_RECENT_CALLS = "CREATE TABLE IF NOT EXISTS "+TABLE_RECENT_CALLS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+
                KEY_DIALER_NAME+" TEXT, "+KEY_DIALER_PHONE+" TEXT, "+KEY_DIALER_EXTENSION+" TEXT, "+
                KEY_DIALER_TIME+" TEXT, "+KEY_DIALER_CALL_TYPE+" TEXT );";
        db.execSQL(CREATE_TABLE_RECENT_CALLS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_RECENT_CALLS);
        onCreate(db);
    }

    public void createCallTable(SQLiteDatabase db){
        String CREATE_TABLE_RECENT_CALLS = "CREATE TABLE IF NOT EXISTS "+TABLE_RECENT_CALLS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+
                KEY_DIALER_NAME+" TEXT, "+KEY_DIALER_PHONE+" TEXT, "+KEY_DIALER_EXTENSION+" TEXT, "+
                KEY_DIALER_TIME+" TEXT, "+KEY_DIALER_CALL_TYPE+" TEXT );";
        db.execSQL(CREATE_TABLE_RECENT_CALLS);
    }

    public long insertCallRecord(RecentCallsModel model){

        SQLiteDatabase db = this.getWritableDatabase();
        createCallTable(db);
        ContentValues values = new ContentValues();
        values.put(KEY_DIALER_NAME,model.getDialer_name());
        values.put(KEY_DIALER_PHONE,model.getDialer_phone());
        values.put(KEY_DIALER_EXTENSION,model.getDialer_ext());
        values.put(KEY_DIALER_TIME,model.getDialer_time());
        values.put(KEY_DIALER_CALL_TYPE,model.getDialer_call_type());

        long result = db.insert(TABLE_RECENT_CALLS, null, values);
        db.close(); // Closing database connection
        return result;
    }

    public List<RecentCallsModel> getAllCalls() {
        List<RecentCallsModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();
        createCallTable(db);
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECENT_CALLS;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                RecentCallsModel model = new RecentCallsModel();
                model.setId(cursor.getString(0));
                model.setDialer_name(cursor.getString(1));
                model.setDialer_phone(cursor.getString(2));
                model.setDialer_ext(cursor.getString(3));
                model.setDialer_time(cursor.getString(4));
                model.setDialer_call_type(cursor.getString(5));
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public void deleteAllCalls(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECENT_CALLS, null, null);
    }
    public boolean checkEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_RECENT_CALLS, null);
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

    public void showRecords(RecentCallsTable table){
        List<RecentCallsModel> records = table.getAllCalls();

        for (RecentCallsModel cn : records) {
            String log = "Id: " + cn.getId() + " ,Name: " + cn.getDialer_name() + " ,Ext: " + cn.getDialer_ext() +
                    " Phone: " + cn.getDialer_phone() + " Time: " + cn.getDialer_time() + " Call type: " + cn.getDialer_call_type();
            // Writing Contacts to log
            Log.e("RecentCalls Data: ", log);
        }
    }
}
