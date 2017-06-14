package com.example.yang.flashtable.customer.infos;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailInfo {
    // TODO: Store information can be read from phone

    public String shop;
    public String location;
    public float rating;
    public String time;
    public int discount;
    public String gift;
    public boolean success;
    public int persons;
    public String shop_info;
    public String category;
    public int shop_id;
    public int record_id;
    public Bitmap image = null;

    public CustomerDetailInfo(String _shop, String _location, float _rating, String _time, int _discount,
                              String _gift, boolean _success, int _persons, String _shop_info, String _category, int _shop_id, int _record_id, Bitmap _image) {
        shop = _shop;
        location = _location;
        rating = _rating;
        discount = _discount;
        gift = _gift;
        time = _time;
        success = _success;
        persons = _persons;
        shop_info = _shop_info;
        category = _category;
        shop_id = _shop_id;
        record_id = _record_id;
        image = _image;
    }


}
