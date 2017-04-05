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
    public void addAppointment(CustomerAppointInfo info){
        //TODO: notify server new appointment
        StoreMainActivity.apiHandler.postSession(info);

        totalAppointment = totalAppointment +1;
        return;
    }
    public void addSuccessAppointment(){
        //TODO: notify server new appointment
        successAppointment = successAppointment+1;
    }
    public int id;
    public String name;
    public String address;
    public int discountDefault;
    public int discountCurrent;
    public int totalAppointment;
    public int successAppointment;
}
