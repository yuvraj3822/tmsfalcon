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
import com.tmsfalcon.device.tmsfalcon.entities.AccidentWitnessModel;

import java.util.ArrayList;

public class AccidentWitnessTable extends SQLiteOpenHelper {

    Context context;

    private static final int DATABASE_VERSION = Utils.DATABASE_VERSION;

    // Database Name
    private static final String DATABASE_NAME = "tmsfalcon";

    private static final String TABLE_ACCIDENT_WITNESS = "accident_witness";

    private static final String KEY_ID = "id";

    private static final String KEY_DRIVER_ID = "driver_id";

    private static final String KEY_ACCIDENT_REPORT_ID = "accident_report_id";

    private static final String KEY_WITNESS_NAME = "witness_name";

    private static final String KEY_WITNESS_PHONE = "witness_phone";

    private static final String KEY_WITNESS_STATEMENT = "witness_statement";

    private static final String KEY_WITNESS_AUDIO_URL = "witness_audio_url";

    public AccidentWitnessTable(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_ACCIDENT_WITNESS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_WITNESS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_ACCIDENT_REPORT_ID + " INTEGER," + KEY_WITNESS_NAME + " TEXT,"
                + KEY_WITNESS_PHONE + " TEXT," + KEY_WITNESS_STATEMENT + " TEXT ,"+KEY_WITNESS_AUDIO_URL +" TEXT )";


        db.execSQL(CREATE_ACCIDENT_WITNESS_TABLE);

    }

    public void createWitnessTable(SQLiteDatabase db){
        String CREATE_ACCIDENT_WITNESS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ACCIDENT_WITNESS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_DRIVER_ID + " INTEGER,"
                + KEY_ACCIDENT_REPORT_ID + " INTEGER," + KEY_WITNESS_NAME + " TEXT,"
                + KEY_WITNESS_PHONE + " TEXT," + KEY_WITNESS_STATEMENT + " TEXT,"+KEY_WITNESS_AUDIO_URL +" TEXT )";


        db.execSQL(CREATE_ACCIDENT_WITNESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCIDENT_WITNESS);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public boolean checkIfTableExists(){
        SQLiteDatabase db = null;
        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            db = this.getWritableDatabase();
            c = db.query(TABLE_ACCIDENT_WITNESS, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
            /* fail */
            Log.d("log", "accident witness table "+" doesn't exist :(((");
        }

        return tableExists;
    }

    public long saveWitness(AccidentWitnessModel model){
        long result = 0;
        SQLiteDatabase db = null;
        try{

            db = this.getWritableDatabase();

            createWitnessTable(db);

            ContentValues values = new ContentValues();
            values.put(KEY_DRIVER_ID, model.getDriver_id());
            values.put(KEY_ACCIDENT_REPORT_ID, model.getAccident_report_id());
            values.put(KEY_WITNESS_NAME, model.getWitness_name());
            values.put(KEY_WITNESS_PHONE, model.getWitness_phone());
            values.put(KEY_WITNESS_STATEMENT, model.getWitness_statement());
            values.put(KEY_WITNESS_AUDIO_URL,model.getWitness_audio_url());

            // Inserting Row

            result = db.insert(TABLE_ACCIDENT_WITNESS, null, values);
        }
        catch (SQLException e){
            Log.e("SQLException : ",""+e.getMessage());

        }
        finally {
            db.close(); // Closing database connection
        }

        return result;
    }

    public ArrayList<AccidentWitnessModel> getWitnessDetailById(int accident_report_id) {

        ArrayList<AccidentWitnessModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_ACCIDENT_WITNESS+" WHERE "+KEY_ACCIDENT_REPORT_ID+" = '"+accident_report_id+"'",null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AccidentWitnessModel model = new AccidentWitnessModel();
                model.setId(cursor.getInt(0));
                model.setDriver_id(cursor.getInt(1));
                model.setAccident_report_id(cursor.getInt(2));
                model.setWitness_name(cursor.getString(3));
                model.setWitness_phone(cursor.getString(4));
                model.setWitness_statement(cursor.getString(5));
                model.setWitness_audio_url(cursor.getString(6));
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public boolean checkWitnessEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_ACCIDENT_WITNESS, null);
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

    public void deleteAllWitness(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_WITNESS, null, null);
    }

    public void deleteWitnessById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_WITNESS, KEY_ACCIDENT_REPORT_ID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();
    }

    public void deleteWitnessByPrimaryId(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ACCIDENT_WITNESS, KEY_ID + " = ?",
                new String[] {String.valueOf(id)});
        db.close();
    }
}
