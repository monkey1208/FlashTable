package com.example.yang.flashtable;

import android.content.Context;

import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailItem {
    String shop;
    String location;
    String time;
    int success_type;
    int persons;

    public CustomerDetailItem(String _shop, String _location, String _time, int _success_type, int _persons) {
        shop = _shop;
        location = _location;
        time = _time;
        success_type = _success_type;
        persons = _persons;
    }


}
