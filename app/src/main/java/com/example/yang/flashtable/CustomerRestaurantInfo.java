package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerRestaurantInfo {
    int id;
    String name;
    float rating;
    int discount;
    String offer;
    LatLng latLng;
    int consumption;
    DetailInfo detailInfo;
    String image_url;
    String promotion_id;
    String category;
    byte[] image = null;
    public class DetailInfo{
        String address;
        String intro;
        public DetailInfo(){
            this(null, null);
        }
        public DetailInfo(String address, String intro){
            this.address = address;
            this.intro = intro;
        }
        public void setInfo(String address, String intro){
            this.address = address;
            this.intro = intro;
        }
    }

    public CustomerRestaurantInfo(String name, int discount, String offer, int id, int consumption, String tag,LatLng latLng){
        this.name = name;
        this.discount = discount;
        this.offer = offer;
        this.latLng = latLng;
        this.id = id;
        this.category = tag;
        this.consumption = consumption;
        detailInfo = new DetailInfo();
    }
    public CustomerRestaurantInfo(String name, int id, int consumption, String tag, LatLng latLng){
        this(name, 101, "暫無優惠", id, consumption, tag, latLng);
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
