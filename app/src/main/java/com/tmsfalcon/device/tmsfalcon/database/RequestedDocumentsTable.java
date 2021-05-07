package com.tmsfalcon.device.tmsfalcon.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tmsfalcon.device.tmsfalcon.customtools.Utils;
import com.tmsfalcon.device.tmsfalcon.entities.DocumentRequestModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Android on 10/11/2017.
 */

public class RequestedDocumentsTable extends SQLiteOpenHelper {

    //This table is created to stored the (uploaded)documents which were requested by dispatcher.
    // This class creates the records of documents whih driver is uploading (storage is temporary basis)
    // and then after he clicks upload button in queue these records will be deleted


    @SuppressWarnings("unused")
    Context context;
    public static final int DATABASE_VERSION = Utils.DATABASE_VERSION;
    public static final String DATABASE_NAME = "tmsfalcon";
    public static final String TABLE_DOCUMENTS = "documents";
    public static final String KEY_ID = "id";
    public static final String KEY_DOCUMENT_ID = "document_id";
    public static final String KEY_DOCUMENT_TYPE = "document_type";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_DOCUMENT_NAME = "document_name";
    public static final String KEY_LOAD_NUMBER = "load_number";
    public static final String KEY_DUE_DATE = "due_date";
    public static final String KEY_COMMENT = "comment";
    public static final String KEY_STATUS = "status";
    public static final String KEY = "key";

    public RequestedDocumentsTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_DOCUMENTS = "CREATE TABLE IF NOT EXISTS "+TABLE_DOCUMENTS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "+KEY_DOCUMENT_ID+
                                        " TEXT, "+KEY_DOCUMENT_TYPE+" TEXT, "+KEY_FILE_URL+" TEXT, "+KEY_DOCUMENT_NAME+
                                        " TEXT, "+KEY_LOAD_NUMBER+" TEXT, "+KEY_DUE_DATE+" TEXT, "+KEY_COMMENT+" TEXT, "+
                                        KEY_STATUS+" TEXT , "+KEY+" TEXT );";
        db.execSQL(CREATE_TABLE_DOCUMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+TABLE_DOCUMENTS);
        onCreate(db);
    }

    public long insertDocument(DocumentRequestModel model){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_DOCUMENT_ID,model.getDocument_request_id());
        values.put(KEY_DOCUMENT_TYPE,model.getDocument_code());
        values.put(KEY_FILE_URL,model.getFile_url());
        values.put(KEY_DOCUMENT_NAME,model.getDocuments());
        values.put(KEY_LOAD_NUMBER,model.getTitle());
        values.put(KEY_DUE_DATE,model.getDocument_due_date());
        values.put(KEY_COMMENT,model.getComment());
        values.put(KEY_STATUS,model.getStatus());
        values.put(KEY,model.getKey());
        long result = db.insert(TABLE_DOCUMENTS, null, values);
        db.close(); // Closing database connection
        return result;
    }

    public List<DocumentRequestModel> getAllDocuments() {
        List<DocumentRequestModel> list = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_DOCUMENTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DocumentRequestModel model = new DocumentRequestModel();
                model.setDb_id(cursor.getString(0));
                model.setDocument_request_id(cursor.getString(1));
                model.setDocument_code(cursor.getString(2));
                model.setFile_url(cursor.getString(3));
                model.setDocuments(cursor.getString(4));
                model.setTitle(cursor.getString(5));
                model.setDocument_due_date(cursor.getString(6));
                model.setComment(cursor.getString(7));
                model.setStatus(cursor.getString(8));
                model.setKey(cursor.getString(9));
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public List<DocumentRequestModel> getAllDocumentsByKey(String key) {

        List<DocumentRequestModel> list = new ArrayList<>();

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_DOCUMENTS+" WHERE "+KEY+" = '"+key+"'",null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DocumentRequestModel model = new DocumentRequestModel();
                model.setDb_id(cursor.getString(0));
                model.setDocument_request_id(cursor.getString(1));
                model.setDocument_code(cursor.getString(2));
                model.setFile_url(cursor.getString(3));
                model.setDocuments(cursor.getString(4));
                model.setTitle(cursor.getString(5));
                model.setDocument_due_date(cursor.getString(6));
                model.setComment(cursor.getString(7));
                model.setStatus(cursor.getString(8));
                list.add(model);
            } while (cursor.moveToNext());
        }

        // return list
        return list;
    }

    public void deleteAllRecords(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, null, null);
    }

    public void deleteRecordById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }

    public int delRecordById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_DOCUMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
    }

    public void deleteRecordByKey(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DOCUMENTS, KEY_DOCUMENT_ID + " = ?",
                new String[] { String.valueOf(key) });
        db.close();
    }

    public boolean checkEmptyTable(){
        boolean empty = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_DOCUMENTS, null);
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
    public int getCount(){
        String selectQuery = "SELECT  * FROM " + TABLE_DOCUMENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
    }

    public void showRecords(RequestedDocumentsTable documentsTable){
        Log.e("in","show records");
        List<DocumentRequestModel> records = documentsTable.getAllDocuments();

        for (DocumentRequestModel cn : records) {
            String log = "Id: " + cn.getDb_id() + " ,Name: " + cn.getDocuments() + " ,Type: " + cn.getDocument_code() + " Saved Url : " + cn.getFile_url() + " LOAD: " + cn.getTitle() + " Due date: " + cn.getDocument_due_date() + " Comment: " + cn.getComment() + " Status: " + cn.getStatus() + " Document Request Id: " + cn.getDocument_request_id() + " Key: " + cn.getKey();
            // Writing Contacts to log
            Log.e("Data: ", log);
        }
    }

}
