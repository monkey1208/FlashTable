package com.example.yang.flashtable;

/**
 * Created by Yang on 2017/3/23.
 */

public class RestaurantInfo {
    String name;
    String address;
    String date;
    String time;
    String status;
    String fame;
    public RestaurantInfo(String name, String address, String date, String time, String status, String fame){
        this.name = name;
        this.address = address;
        this.date = date;
        this.time = time;
        this.status = status;
        this.fame = fame;
    }
}
