package com.example.yang.flashtable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Yang on 2017/3/23.
 */

public class RestaurantInfo {
    String name;
    String discount;
    String offer;
    LatLng latLng;
    DetailInfo detailInfo;
    public class DetailInfo{
        String address;
        String phone;
        String time;
        String category;
        String url;
        public DetailInfo(){
            this("", "", "", "", "");
        }
        public DetailInfo(String address, String phone, String time, String category, String url){
            this.address = address;
            this.phone = phone;
            this.time = time;
            this.category = category;
            this.url = url;
        }
        public void setInfo(String address, String phone, String time, String category, String url){
            this.address = address;
            this.phone = phone;
            this.time = time;
            this.category = category;
            this.url = url;
        }
    }
    public RestaurantInfo(String name, String discount, String offer, LatLng latLng){
        this.name = name;
        this.discount = discount;
        this.offer = offer;
        this.latLng = latLng;
        detailInfo = new DetailInfo();
    }
}
