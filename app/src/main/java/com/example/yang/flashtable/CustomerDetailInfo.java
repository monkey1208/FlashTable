package com.example.yang.flashtable;

import android.content.Context;

import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailInfo {
    // TODO: Store information can be read from phone

    String shop;
    String location;
    float rating;
    String time;
    int discount;
    String gift;
    boolean success;
    int persons;

    public CustomerDetailInfo(String _shop, String _location, float _rating, String _time, int _discount,
                              String _gift, boolean _success, int _persons) {
        shop = _shop;
        location = _location;
        rating = _rating;
        discount = _discount;
        gift = _gift;
        time = _time;
        success = _success;
        persons = _persons;
    }


}
