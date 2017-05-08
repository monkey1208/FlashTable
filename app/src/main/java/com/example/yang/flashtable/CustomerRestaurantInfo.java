package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerRestaurantInfo implements Parcelable{
    int id;
    String name;
    float rating;
    int discount;
    String offer;
    LatLng latLng;
    int consumption;
    String image_url;
    String promotion_id;
    String category;
    byte[] image = null;
    String address = "";
    String intro = "";

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(rating);
        dest.writeInt(discount);
        dest.writeString(offer);
        dest.writeParcelable(latLng, flags);
        dest.writeInt(consumption);
        dest.writeString(image_url);
        dest.writeString(promotion_id);
        dest.writeString(category);
        dest.writeByteArray(image);
        dest.writeString(address);
        dest.writeString(intro);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CustomerRestaurantInfo> CREATOR = new Creator<CustomerRestaurantInfo>() {
        @Override
        public CustomerRestaurantInfo createFromParcel(Parcel in) {
            return new CustomerRestaurantInfo(in);
        }

        @Override
        public CustomerRestaurantInfo[] newArray(int size) {
            return new CustomerRestaurantInfo[size];
        }
    };

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
