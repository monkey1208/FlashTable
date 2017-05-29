package com.example.yang.flashtable;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo {
    public String id;
    public String name;
    public String address;
    public String url;
    public int discountDefault;
    public int discountCurrent;
    public int totalAppointment;
    public int successAppointment;
    public List<StoreDiscountInfo> discountList;

    private List<ReservationInfo> recordList;
    private int success_record_num;
    private int contract_fee;

    public StoreInfo(){};
    public StoreInfo(String n,String a,String url){
        name = n;
        address = a;
        this.url = url;
        discountList = new ArrayList<>();
        discountDefault = 0;
        discountCurrent = discountDefault;
        recordList = new ArrayList<>();
        success_record_num = 0;
    }
    public void addAppointment(CustomerAppointInfo info){
        //TODO: notify server new appointment
        new APIHandler().postSession(info);
        totalAppointment = totalAppointment +1;
        return;
    }
    public void addSuccessAppointment(){
        //TODO: notify server new appointment
        successAppointment = successAppointment+1;
    }

    public void setRecordList(List<ReservationInfo> list){
        this.recordList = new ArrayList<>(list);
    }

    public List<ReservationInfo> getRecordList(){
        return  recordList;
    }

    public int getSuccess_record_num(){
        return success_record_num;
    }

    public void  setSuccess_record_num(int num){
        this.success_record_num = num;
    }

    void setContract_fee(int fee){
        this.contract_fee = fee;
    }
    int getContract_fee(){
        return contract_fee;
    }

}
