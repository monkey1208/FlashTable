package com.example.yang.flashtable;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo {

    public List<StoreDiscountInfo> discountList;
    public StoreInfo(String n,String a){
        name = n;
        address = a;
        discountList = new ArrayList<>();
        discountList.add(new StoreDiscountInfo(100,"暫無優惠",0,true));
        discountDefault = 0;
        discountCurrent = discountDefault;
        totalAppointment = 200;
        successAppointment = 99;
    }
    public void addAppointment(CustomerAppointInfo cinfo){
        //TODO: notify server new appointment
        ReservationInfo info = new ReservationInfo(cinfo.name,cinfo.number,System.currentTimeMillis());
        StoreMainActivity.fragmentController.storeAppointFragment.appointList.add(info);
        StoreMainActivity.fragmentController.storeAppointFragment.adapter.notifyDataSetChanged();
        totalAppointment = totalAppointment +1;
        return;
    }
    public void addSuccessAppointment(){
        //TODO: notify server new appointment
        successAppointment = successAppointment+1;
    }
    public String name;
    public String address;
    public int discountDefault;
    public int discountCurrent;
    public int totalAppointment;
    public int successAppointment;
}
