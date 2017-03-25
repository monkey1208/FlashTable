package com.example.yang.flashtable;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment {

    View view;
    ViewFlipper vf_flipper;
    Spinner spinner_dis, spinner_food, spinner_default;
    ListView lv_shops;
    List<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    EditText et_search;
    SqlHandler sqlHandler = null;
    Bitmap image;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView(){
        vf_flipper = (ViewFlipper) view.findViewById(R.id.customer_main_vf_flipper);
        spinner_dis = (Spinner) view.findViewById(R.id.customer_main_sp_distance);
        spinner_food = (Spinner) view.findViewById(R.id.customer_main_sp_food);
        spinner_default = (Spinner) view.findViewById(R.id.customer_main_sp_default);
        lv_shops = (ListView) view.findViewById(R.id.customer_main_lv);
        et_search = (EditText) view.findViewById(R.id.customer_main_et_search);
    }

    private void initData(){
        restaurant_list = generateTestList();
        adapter = new CustomerMainAdapter(view.getContext(), restaurant_list);
        lv_shops.setAdapter(adapter);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int key_code, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && key_code == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(getActivity(), "Search " + et_search.getText(), Toast.LENGTH_SHORT).show();
                    et_search.setText("");
                }
                return true;
            }
        });
        // initId();
        openDB();
        List<CustomerRestaurantInfo> list = generateTestList();
        // insertDB(list);
        // deletDB();
        setList();
    }
    
    private void openDB(){
        sqlHandler = new SqlHandler(view.getContext());
    }
    
    private void insertDB(List<CustomerRestaurantInfo> infoList){
        image = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_gift);
        //System.out.println(infoList.size());
        for(int i = 0; i < infoList.size(); i++){
            System.out.println(infoList.get(i).id);
            sqlHandler.insert(infoList.get(i), image);
            //image = null;
        }
        image = null;
    }
    private List<CustomerRestaurantInfo> getListFromDB(){
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        Cursor cursor = sqlHandler.getAll();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            CustomerRestaurantInfo info = new CustomerRestaurantInfo(
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
        //restaurant_list = generateTestList();
        restaurant_list = getListFromDB();
        adapter = new CustomerMainAdapter(view.getContext(), restaurant_list);
        lv_shops.setAdapter(adapter);
    }
    private List<CustomerRestaurantInfo> generateTestList(){
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            LatLng latLng = new LatLng(121.5, 25.01);
            CustomerRestaurantInfo info = new CustomerRestaurantInfo("Restaurant "+Integer.toString(i), "discount", "offer", i,latLng);
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
