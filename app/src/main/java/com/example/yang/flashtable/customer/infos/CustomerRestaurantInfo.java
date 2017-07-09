package com.example.yang.flashtable.customer.infos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerRestaurantInfo{
    public int id;
    public String name;
    public float rating;
    public int discount;
    public String offer;
    public LatLng latLng;
    public int consumption;
    public int minconsumption;
    public String image_url;
    public String promotion_id;
    public String category;
    public byte[] image = null;
    public String address = "";
    public String web;
    public String phone;
    public String intro = "";
    public String date = "";
    public String business = "";

    public CustomerRestaurantInfo(String name, int discount, String offer, int id, int consumption,
                                  String tag, LatLng latLng, String web, String phone, int minconsumption, String business){
        this.name = name;
        this.discount = discount;
        this.offer = offer;
        this.latLng = latLng;
        this.id = id;
        this.category = tag;
        this.consumption = consumption;
        this.web = web;
        this.phone = phone;
        this.minconsumption = minconsumption;
        this.business = business;
    }
    public CustomerRestaurantInfo(String name, int id, int consumption, String tag, LatLng latLng,
                                  String web, String phone, int minconsumption, String business){
        this(name, 101, "暫無優惠", id, consumption, tag, latLng, web, phone, minconsumption, business);
    }

    public void setInfo(String address, String intro){
        this.address = address;
        this.intro = intro;
    }

    public void turnBitmap2ByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        image =  outputStream.toByteArray();
    }
    public Bitmap getImage(){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
