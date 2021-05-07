package com.tmsfalcon.device.tmsfalcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentCaptureModel;
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;
import com.tmsfalcon.device.tmsfalcon.entities.GpsLocationParams;

import java.util.ArrayList;
import java.util.List;

public class AccidentCaptureImageTable extends SQLiteOpenHelper {

    Context context;
    private static final int DATABASE_VERSION = Utils.DATABASE_VERSION;

    // Database Name
    private static final String DATABASE_NAME = "tmsfalcon";

    private static final String TABLE_ACCIDENT_IMAGES = "accident_images";

    private static final String KEY_ID = "id";

    private static final String KEY_DRIVER_ID = "driver_id";

    private static final String KEY_ACCIDENT_REPORT_ID = "accident_report_id";

    private static final String KEY_DOC_TYPE = "doc_type";

    private static final String KEY_IMAGE_URL = "image_url";

    public AccidentCaptureImageTable(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ACCIDENT_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_ACCIDENT_REPORT_ID + " INTEGER," + KEY_DOC_TYPE + " TEXT,"
                + KEY_IMAGE_URL + " TEXT )";


        db.execSQL(CREATE_ACCIDENT_IMAGE_TABLE);

    }

    public void createaccidentImageTable(SQLiteDatabase db){

        String CREATE_ACCIDENT_IMAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_IMAGES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_ACCIDENT_REPORT_ID + " INTEGER," + KEY_DOC_TYPE + " TEXT,"
                + KEY_IMAGE_URL + " TEXT )";


        db.execSQL(CREATE_ACCIDENT_IMAGE_TABLE);
    }

    public boolean checkIfTableExists(){
        SQLiteDatabase db = null;
        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            db = this.getWritableDatabase();
            c = db.query(TABLE_ACCIDENT_IMAGES, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
            /* fail */
            Log.d("log", "accident witness table "+" doesn't exist :(((");
        }

        return tableExists;
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCIDENT_IMAGES);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void deleteAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_IMAGES, null, null);
    }

    public int delRecordById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ACCIDENT_IMAGES, KEY_ACCIDENT_REPORT_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public long saveAccidentImage(AccidentCaptureModel model){
        long result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createaccidentImageTable(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_REPORT_ID, model.getAccident_report_id());
            values.put(KEY_DOC_TYPE, model.getDoc_type());
            values.put(KEY_IMAGE_URL, model.getImage_url());

            // Inserting Row

            result = db.insert(TABLE_ACCIDENT_IMAGES, null, values);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;
    }

    public void deleteAllImages(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_IMAGES, null, null);
    }

    public int deleteImagesById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result =  db.delete(TABLE_ACCIDENT_IMAGES, KEY_ID + " = ?",
                        new String[] {String.valueOf(id)});
        db.close();
        return result;
    }

    public int deleteImagesByAccidentId(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result =  db.delete(TABLE_ACCIDENT_IMAGES, KEY_ACCIDENT_REPORT_ID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();
        return result;
    }

    public ArrayList<AccidentCaptureModel> getAllAccidentImagesById( int accident_id) {
        ArrayList<AccidentCaptureModel> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ACCIDENT_IMAGES+" WHERE "+KEY_ACCIDENT_REPORT_ID+" = "+accident_id;

        SQLiteDatabase db = this.getWritableDatabase();

        createaccidentImageTable(db);

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AccidentCaptureModel model = new AccidentCaptureModel();
                model.setId(cursor.getInt(0));
                model.setDriver_id(cursor.getInt(1));
                model.setAccident_report_id(cursor.getInt(2));
                model.setDoc_type(cursor.getString(3));
                model.setImage_url(cursor.getString(4));

                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public boolean checkAccidentDocumentEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ACCIDENT_IMAGES, null);
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

}
