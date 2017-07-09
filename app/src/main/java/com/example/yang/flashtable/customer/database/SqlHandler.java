package com.example.yang.flashtable.customer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;
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
    public static final String CONSUMPTION_COLUMN = "consumption";
    public static final String CATEGORY_COLUMN = "catergory";
    public static final String ADDRESS_COLUMN = "address";
    public static final String PHONE_COLUMN = "phone";
    public static final String MINCONSUMPTION_COLUMN = "minconsumption";
    public static final String WEB_COLUMN = "web";
    public static final String BUSINESS_COLUMN = "business";
    public static final String INTRO_COLUMN = "intro";
    public static final String IMG_COLUMN = "img";
    public static final String IMG_COLUMN_1 = "img_1";
    public static final String IMG_COLUMN_2 = "img_2";
    public static final String IMG_COLUMN_3 = "img_3";
    public static final String IMG_COLUMN_4 = "img_4";
    public static final String IMG_COLUMN_5 = "img_5";
    public SQLiteDatabase db;
    public SqlHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
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
                CONSUMPTION_COLUMN + " integer, " +
                MINCONSUMPTION_COLUMN + " integer, " +
                CATEGORY_COLUMN + " text, " +
                ADDRESS_COLUMN + " text, " +
                PHONE_COLUMN + " text, " +
                WEB_COLUMN + " text, " +
                BUSINESS_COLUMN + " text, " +
                INTRO_COLUMN + " text, " +
                IMG_COLUMN + " blob no null, " +
                IMG_COLUMN_1 + " blob, " +
                IMG_COLUMN_2 + " blob, " +
                IMG_COLUMN_3 + " blob, " +
                IMG_COLUMN_4 + " blob, " +
                IMG_COLUMN_5 + " blob" +
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
                new String[] {NAME_COLUMN, ID_COLUMN, LATITUDE_COLUMN, LONGITUDE_COLUMN, IMG_COLUMN,
                        CATEGORY_COLUMN, ADDRESS_COLUMN, INTRO_COLUMN, CONSUMPTION_COLUMN,
                        PHONE_COLUMN, MINCONSUMPTION_COLUMN, WEB_COLUMN, BUSINESS_COLUMN},	//column
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
                        CATEGORY_COLUMN, ADDRESS_COLUMN, INTRO_COLUMN, CONSUMPTION_COLUMN,
                        PHONE_COLUMN, MINCONSUMPTION_COLUMN, WEB_COLUMN, BUSINESS_COLUMN},	//column
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
                    Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.CONSUMPTION_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.CATEGORY_COLUMN)),
                    new LatLng(cursor.getFloat(cursor.getColumnIndex(SqlHandler.LATITUDE_COLUMN)),
                            cursor.getFloat(cursor.getColumnIndex(SqlHandler.LONGITUDE_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.WEB_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.PHONE_COLUMN)),
                    Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.MINCONSUMPTION_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.BUSINESS_COLUMN))
            );
            info.setInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.ADDRESS_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.INTRO_COLUMN))
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
                Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.CONSUMPTION_COLUMN))),
                cursor.getString(cursor.getColumnIndex(SqlHandler.CATEGORY_COLUMN)),
                new LatLng(cursor.getFloat(cursor.getColumnIndex(SqlHandler.LATITUDE_COLUMN)),
                        cursor.getFloat(cursor.getColumnIndex(SqlHandler.LONGITUDE_COLUMN))),
                cursor.getString(cursor.getColumnIndex(SqlHandler.WEB_COLUMN)),
                cursor.getString(cursor.getColumnIndex(SqlHandler.PHONE_COLUMN)),
                Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.MINCONSUMPTION_COLUMN))),
                cursor.getString(cursor.getColumnIndex(SqlHandler.BUSINESS_COLUMN))
        );
        info.setInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.ADDRESS_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.INTRO_COLUMN))
            );
        info.image = cursor.getBlob(cursor.getColumnIndex(SqlHandler.IMG_COLUMN));
        return info;
    }

    public void insertList(List<CustomerRestaurantInfo> list){
        insertList(list, null, null, null, null, null);
    }

    public void insertList(List<CustomerRestaurantInfo> list, byte[] img_1, byte[] img_2, byte[] img_3, byte[] img_4, byte[] img_5){
        for(int i = 0;i<list.size(); i++){
            insert(list.get(i), img_1, img_2, img_3, img_4, img_5);
        }
    }

    public void insert(CustomerRestaurantInfo info, byte[] img_1, byte[] img_2, byte[] img_3, byte[] img_4, byte[] img_5){

        Log.d("SQLite", "Saving Data");
        ContentValues cv = new ContentValues();
        cv.put(ID_COLUMN, info.id);
        cv.put(NAME_COLUMN, info.name);
        cv.put(LATITUDE_COLUMN, info.latLng.latitude);
        cv.put(LONGITUDE_COLUMN, info.latLng.longitude);
        cv.put(CONSUMPTION_COLUMN, info.consumption);
        cv.put(CATEGORY_COLUMN, info.category);
        cv.put(ADDRESS_COLUMN, info.address);
        cv.put(INTRO_COLUMN, info.intro);
        cv.put(IMG_COLUMN, info.image);
        cv.put(PHONE_COLUMN, info.phone);
        cv.put(WEB_COLUMN, info.web);
        cv.put(MINCONSUMPTION_COLUMN, info.minconsumption);
        cv.put(BUSINESS_COLUMN, info.business);
        cv.put(IMG_COLUMN_1, img_1);
        cv.put(IMG_COLUMN_2, img_2);
        cv.put(IMG_COLUMN_3, img_3);
        cv.put(IMG_COLUMN_4, img_4);
        cv.put(IMG_COLUMN_5, img_5);
        if(checkDataInDB(DATABASE_TABLE, ID_COLUMN, info.id)){
            db.update(DATABASE_TABLE, cv, ID_COLUMN + "=" + info.id, null);
        }else {
            db.insert(DATABASE_TABLE, null, cv);
        }
        cv = null;
    }

    public ArrayList<Bitmap> getBitmapList(int shop_id){
        Cursor cursor = db.query(true,
                DATABASE_TABLE,
                new String[] {IMG_COLUMN_1, IMG_COLUMN_2, IMG_COLUMN_3, IMG_COLUMN_4, IMG_COLUMN_5},	//column
                ID_COLUMN+"="+ shop_id,				//WHERE
                null, // WHERE 的參數
                null, // GROUP BY
                null, // HAVING
                null, // ORDOR BY
                null  // 限制回傳的rows數量
        );
        if (cursor != null) {
            cursor.moveToFirst();	//cursor to the first data
        }
        ArrayList<Bitmap> list = new ArrayList<>();
        byte[] array = cursor.getBlob(cursor.getColumnIndex(IMG_COLUMN_1));
        if(array != null)
            list.add(BitmapFactory.decodeByteArray(array, 0, array.length));
        array = cursor.getBlob(cursor.getColumnIndex(IMG_COLUMN_2));
        if(array != null)
            list.add(BitmapFactory.decodeByteArray(array, 0, array.length));
        array = cursor.getBlob(cursor.getColumnIndex(IMG_COLUMN_3));
        if(array != null)
            list.add(BitmapFactory.decodeByteArray(array, 0, array.length));
        array = cursor.getBlob(cursor.getColumnIndex(IMG_COLUMN_4));
        if(array != null)
            list.add(BitmapFactory.decodeByteArray(array, 0, array.length));
        array = cursor.getBlob(cursor.getColumnIndex(IMG_COLUMN_5));
        if(array != null)
            list.add(BitmapFactory.decodeByteArray(array, 0, array.length));
        cursor.close();
        return list;
    }

    public boolean checkDataInDB(String TableName, String dbfield, int id) {
        String Query = "Select * from " + TableName + " where " + dbfield + " = " + id;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
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
