package com.example.yang.flashtable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/3/25.
 */

public class SqlHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Shops";
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "shop";
    public static final String ID_COLUMN = "_id";
    public static final String NAME_COLUMN = "name";
    public static final String LATITUDE_COLUMN = "latitude";
    public static final String LONGITUDE_COLUMN = "longitude";
    public static final String PHONE_COLUMN = "phone";
    public static final String EMAIL_COLUMN = "email";
    public static final String TIME_COLUMN = "time";
    public static final String CATEGORY_COLUMN = "catergory";
    public static final String URL_COLUMN = "url";
    public static final String ADDRESS_COLUMN = "address";
    public static final String INTRO_COLUMN = "intro";
    public static final String IMG_COLUMN = "img";
    public SQLiteDatabase db;
    private Context context;
    public SqlHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("SQLite", "Create Table");
        String createDB = "CREATE TABLE IF NOT EXISTS " +
                DATABASE_TABLE +
                "(" +
                ID_COLUMN + " integer primary key autoincrement, " +
                NAME_COLUMN + " text no null, " +
                LATITUDE_COLUMN + " real no null, " +
                LONGITUDE_COLUMN + " real no null, " +
                PHONE_COLUMN + " text, " +
                EMAIL_COLUMN + " text, " +
                TIME_COLUMN + " text, " +
                CATEGORY_COLUMN + " text, " +
                URL_COLUMN + " text, " +
                ADDRESS_COLUMN + " text, " +
                INTRO_COLUMN + " text, " +
                IMG_COLUMN + " blob" +
                ");";
        db.execSQL(createDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
    public Cursor getAll() {
        return db.query(DATABASE_TABLE,
                new String[] {NAME_COLUMN, ID_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN, IMG_COLUMN},	//column
                null, // WHERE
                null, // WHERE parameter
                null, // GROUP BY
                null, // HAVING
                null  // ORDOR BY
        );
    }
    public Cursor get(int rowId){
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[] {NAME_COLUMN, ID_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN, IMG_COLUMN,
                PHONE_COLUMN, EMAIL_COLUMN, TIME_COLUMN, CATEGORY_COLUMN, URL_COLUMN, ADDRESS_COLUMN, INTRO_COLUMN},	//column
                ID_COLUMN+"="+ rowId,				//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        if (cursor != null) {
            cursor.moveToFirst();	//cursor to the first data
        }
        return cursor;
    }
    public List<CustomerRestaurantInfo> getList(){
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        Cursor cursor = getAll();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CustomerRestaurantInfo info = new CustomerRestaurantInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.NAME_COLUMN)),
                    Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.ID_COLUMN))),
                    new LatLng(cursor.getFloat(cursor.getColumnIndex(SqlHandler.LATITUDE_COLUMN)), cursor.getFloat(cursor.getColumnIndex(SqlHandler.LONGITUDE_COLUMN)))
            );

            info.image = cursor.getBlob(cursor.getColumnIndex(SqlHandler.IMG_COLUMN));
            list.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }
    public CustomerRestaurantInfo getDetail(int id){
        Cursor cursor = get(id);
        CustomerRestaurantInfo info = new CustomerRestaurantInfo(
                cursor.getString(cursor.getColumnIndex(SqlHandler.NAME_COLUMN)),
                Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.ID_COLUMN))),
                new LatLng(cursor.getFloat(cursor.getColumnIndex(SqlHandler.LATITUDE_COLUMN)), cursor.getFloat(cursor.getColumnIndex(SqlHandler.LONGITUDE_COLUMN)))
        );
        info.detailInfo.setInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.ADDRESS_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.PHONE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.TIME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.CATEGORY_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.URL_COLUMN))
            );
        return info;
    }

    public void insertList(List<CustomerRestaurantInfo> list){
        for(int i = 0;i<list.size(); i++){
            insert(list.get(i));
        }
    }
    public void insert(CustomerRestaurantInfo info){
        Log.d("SQLite", "insert data");
        //byte[] array = getBitmapAsByteArray(bitmap);
        ContentValues cv = new ContentValues();
        cv.put(ID_COLUMN, info.id);
        cv.put(NAME_COLUMN, info.name);
        cv.put(LATITUDE_COLUMN, info.latLng.latitude);
        cv.put(LONGITUDE_COLUMN, info.latLng.longitude);
        cv.put(PHONE_COLUMN, info.detailInfo.phone);
        cv.put(EMAIL_COLUMN, info.detailInfo.email);
        cv.put(TIME_COLUMN, info.detailInfo.time);
        cv.put(CATEGORY_COLUMN, info.detailInfo.category);
        cv.put(URL_COLUMN, info.detailInfo.url);
        cv.put(ADDRESS_COLUMN, info.detailInfo.address);
        cv.put(INTRO_COLUMN, info.detailInfo.intro);
        cv.put(IMG_COLUMN, info.image);
        long id = db.insert(DATABASE_TABLE, null, cv);
        cv = null;
        Log.d("SQLite", id+"");
    }
    public void deleteTable(){
        db.execSQL("DROP DATABASE " + DATABASE_TABLE);
    }
    public static void deleteDB(Context c){
        c.deleteDatabase(DATABASE_NAME);
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
