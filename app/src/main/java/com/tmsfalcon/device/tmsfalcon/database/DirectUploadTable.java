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
 * Created by Android on 11/18/2017.
 */

public class DirectUploadTable extends SQLiteOpenHelper {

    Context context;
    public static final int DATABASE_VERSION = Utils.DATABASE_VERSION;
    public static final String DATABASE_NAME = "tmsfalcon";
    public static final String TABLE_DOCUMENTS = "direct_upload";

    public static final String KEY_ID = "id";
    public static final String KEY_FILE_URL = "file_url";
    public static final String KEY_DOCUMENT_ID = "document_id";
    public static final String KEY_DOCUMENT_BELONGS_TO = "document_belongs_to";
    public static final String KEY_DOCUMENT_TYPE = "document_type";

    public DirectUploadTable(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
       /* String CREATE_TABLE_DOCUMENTS = "CREATE TABLE IF NOT EXISTS "+TABLE_DOCUMENTS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "
                                                        +KEY_FILE_URL+" TEXT ,"+KEY_DOCUMENT_ID+" TEXT ,"+
                                                        KEY_DOCUMENT_BELONGS_TO+" TEXT ,"+KEY_DOCUMENT_TYPE+" TEXT );";*/
        String CREATE_TABLE_DOCUMENTS = "CREATE TABLE IF NOT EXISTS "+TABLE_DOCUMENTS+" ( "+KEY_ID+" INTEGER PRIMARY KEY, "
                +KEY_FILE_URL+" TEXT );";
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
        values.put(KEY_FILE_URL,model.getFile_url());
        // values.put(KEY_DOCUMENT_ID,model.getDocument_id());
        // values.put(KEY_DOCUMENT_BELONGS_TO,model.getDocument_belongs_to());
        // values.put(KEY_DOCUMENT_TYPE,model.getDocument_type());
        long result = db.insert(TABLE_DOCUMENTS, null, values);
        db.close(); // Closing database connection
        return result;
    }

    public int getCount(){
        String selectQuery = "SELECT  * FROM " + TABLE_DOCUMENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor.getCount();
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

                model.setFile_url(cursor.getString(1));

               // model.setDocument_id(cursor.getString(2));

               // model.setDocument_belongs_to(cursor.getString(3));

               //  model.setDocument_type(cursor.getString(4));

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

        int result = db.delete(TABLE_DOCUMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        Log.e("delete result ",""+result);
        db.close();
    }

    public int delRecordById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(TABLE_DOCUMENTS, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
        return result;
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
}
