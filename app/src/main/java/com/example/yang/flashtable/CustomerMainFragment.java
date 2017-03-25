package com.example.yang.flashtable;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment {
    private View view;
    private Spinner spinner_dis, spinner_food, spinner_default;
    private SearchView searchView;
    private ListView listView;
    private List<RestaurantInfo> restaurantList;
    private CustomerMainAdapter adapter;
    private SqlHandler sqlHandler = null;
    private Bitmap image;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initId();

        openDB();
        List<RestaurantInfo> list = generateTestList();
        //insertDB(list);
        //deletDB();
        setList();
        System.gc();
        return view;
    }
    private void initId(){
        spinner_dis = (Spinner)view.findViewById(R.id.customer_main_sp_distance);
        spinner_food = (Spinner)view.findViewById(R.id.customer_main_sp_food);
        spinner_default = (Spinner)view.findViewById(R.id.customer_main_sp_default);
        listView = (ListView)view.findViewById(R.id.customer_main_lv);
    }
    private void openDB(){
        sqlHandler = new SqlHandler(view.getContext());

    }
    private void insertDB(List<RestaurantInfo> infoList){
        image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_gift);
        //System.out.println(infoList.size());
        for(int i = 0; i < infoList.size(); i++){
            System.out.println(infoList.get(i).id);
            sqlHandler.insert(infoList.get(i), image);
            //image = null;
        }
        image = null;
    }
    private List<RestaurantInfo> getListFromDB(){
        List<RestaurantInfo> list = new ArrayList<>();
        Cursor cursor = sqlHandler.getAll();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            RestaurantInfo info = new RestaurantInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.NAME_COLUMN)),
                    Integer.valueOf(cursor.getString(cursor.getColumnIndex(SqlHandler.ID_COLUMN))),
                    new LatLng(cursor.getFloat(cursor.getColumnIndex(SqlHandler.LATITUDE_COLUMN)), cursor.getFloat(cursor.getColumnIndex(SqlHandler.LONGITUDE_COLUMN)))
            );
            /*info.detailInfo.setInfo(
                    cursor.getString(cursor.getColumnIndex(SqlHandler.ADDRESS_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.PHONE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.TIME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.CATEGORY_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SqlHandler.URL_COLUMN))
            );*/
            info.image = cursor.getBlob(cursor.getColumnIndex(SqlHandler.IMG_COLUMN));
            list.add(info);

            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }
    private void deletDB(){
        view.getContext().deleteDatabase(SqlHandler.DATABASE_NAME);
    }
    private void closeDB(){
        sqlHandler.close();
    }
    private void setList(){
        //restaurantList = generateTestList();
        restaurantList = getListFromDB();
        adapter = new CustomerMainAdapter(view.getContext(), restaurantList);
        listView.setAdapter(adapter);
    }
    private List<RestaurantInfo> generateTestList(){
        List<RestaurantInfo> list = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            LatLng latLng = new LatLng(121.5, 25.01);
            RestaurantInfo info = new RestaurantInfo("Restaurant "+Integer.toString(i), "discount", "offer", i,latLng);
            info.detailInfo.setInfo("address", "0933278802", "10:00~20:00", "garbage store", "http://fluffs-press.herokuapp.com/");
            list.add(info);
            latLng = null;
            info = null;
        }
        return list;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDB();
    }
}
