package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerRestaurantInfo {
    int id;
    String name;
    String discount;
    String offer;
    LatLng latLng;
    DetailInfo detailInfo;
    String image_url;
    byte[] image;
    public class DetailInfo{
        String email;
        String address;
        String phone;
        String time;
        String category;
        String url;
        String intro;
        public DetailInfo(){
            this(null, null, null, null, null, null, null);
        }
        public DetailInfo(String address, String phone, String time, String category, String url, String intro, String email){
            this.address = address;
            this.phone = phone;
            this.time = time;
            this.category = category;
            this.url = url;
            this.intro = intro;
            this.email = email;
        }
        public void setInfo(String address, String phone, String time, String category, String url){
            this.address = address;
            this.phone = phone;
            this.time = time;
            this.category = category;
            this.url = url;
        }
    }

    public CustomerRestaurantInfo(String name, String discount, String offer, int id, LatLng latLng){
        this.name = name;
        this.discount = discount;
        this.offer = offer;
        this.latLng = latLng;
        detailInfo = new DetailInfo();
    }
    public CustomerRestaurantInfo(String name, int id, LatLng latLng){
        this(name, "暫無折扣", "暫無優惠", id, latLng);
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
