package com.example.yang.flashtable.customer.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by Yang on 2017/5/18.
 */

public class HistorySqlHandler extends SQLiteOpenHelper {
    public static final String DB_NAME = "History";
    public static final String TABLE_NAME = "Customer";
    public static final int DB_VER = 1;
    public static final String HISTORY_COLUMN = "history";
    public SQLiteDatabase db;
    public HistorySqlHandler(Context context){
        super(context, DB_NAME, null, DB_VER);
        this.db = getWritableDatabase();
    }

    public HistorySqlHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("HistorySqlHandler", "Create Table");
        String createDB = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME +
                "(" +
                HISTORY_COLUMN + " text no null" +
                ");";
        sqLiteDatabase.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private Cursor getAll(){
        Cursor cursor = db.query(true,
                TABLE_NAME,
                new String[] {HISTORY_COLUMN},
                null,
                null,
                null,
                null,
                null,
                null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    public ArrayList<String> getHistoryList(){
        ArrayList<String> history_list = new ArrayList<>();
        Cursor cursor = getAll();
        int index = cursor.getColumnIndex(HISTORY_COLUMN);
        for(int i = 0; i < cursor.getCount(); i++){
            String history = cursor.getString(index);
            history_list.add(history);
            cursor.moveToNext();
        }
        cursor.close();
        return history_list;
    }

    public void insert(String history){
        ContentValues cv = new ContentValues();
        cv.put(HISTORY_COLUMN, history);
        db.insert(TABLE_NAME, null, cv);
        cv.clear();
        cv = null;
    }

    public static void deleteDB(Context c){
        c.deleteDatabase(DB_NAME);
    }
}
