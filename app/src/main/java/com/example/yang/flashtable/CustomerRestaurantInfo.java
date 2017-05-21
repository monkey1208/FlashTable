package com.example.yang.flashtable;

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
    public String image_url;
    public String promotion_id;
    public String category;
    public byte[] image = null;
    public String address = "";
    public String intro = "";
    public String date = "";

    protected CustomerRestaurantInfo(Parcel in) {
        id = in.readInt();
        name = in.readString();
        rating = in.readFloat();
        discount = in.readInt();
        offer = in.readString();
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        consumption = in.readInt();
        image_url = in.readString();
        promotion_id = in.readString();
        category = in.readString();
        image = in.createByteArray();
    }




    public CustomerRestaurantInfo(String name, int discount, String offer, int id, int consumption, String tag,LatLng latLng){
        this.name = name;
        this.discount = discount;
        this.offer = offer;
        this.latLng = latLng;
        this.id = id;
        this.category = tag;
        this.consumption = consumption;
    }
    public CustomerRestaurantInfo(String name, int id, int consumption, String tag, LatLng latLng){
        this(name, 101, "暫無優惠", id, consumption, tag, latLng);
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
