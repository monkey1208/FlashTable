package com.example.yang.flashtable.customer.infos;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Yang on 2017/5/25.
 */

public class CustomerAppInfo {
    private static CustomerAppInfo appInfo = new CustomerAppInfo();
    private CustomerAppInfo(){}

    public static CustomerAppInfo getInstance(){
        return appInfo;
    }

    private ArrayList<CustomerRestaurantInfo> restaurant_list = new ArrayList<>();
    private Location location;

    public void setRestaurantList(ArrayList<CustomerRestaurantInfo> list){
        restaurant_list.clear();
        for(CustomerRestaurantInfo item:list)
            restaurant_list.add(item);
    }

    public ArrayList<CustomerRestaurantInfo> getRestaurantList(){
        return restaurant_list;
    }

    public void setLocation(LatLng latLng){
        this.location = new Location("");
        this.location.setLatitude(latLng.latitude);
        this.location.setLongitude(latLng.longitude);
    }

    public void setLocation(double lat, double lng){
        this.location = new Location("");
        this.location.setLatitude(lat);
        this.location.setLongitude(lng);
    }

    public void setLocation(Location location){
        this.location = location;
    }

    public Location getLocation(){
        return this.location;
    }
}
