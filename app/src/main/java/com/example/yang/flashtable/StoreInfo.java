package com.example.yang.flashtable;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo {
    public String id;
    public String name;
    public String address;
    public String url;
    public int discountDefault; //Position in not_deleted_discount_list
    public int discountCurrent; //Position in not_deleted_discount_list
    public int discountCurrentId; // Promotion Id for current active discount in not_deleted_discount_list
    public int totalAppointment;
    public int successAppointment;
    public List<StoreDiscountInfo> discountList;
    public List<StoreDiscountInfo> not_delete_discountList;

    private List<RecordInfo> recordList;
    private int success_record_num;
    public int contract_fee;

    public StoreInfo(){};
    public StoreInfo(String n,String a,String url, int contract_fee){
        name = n;
        address = a;
        this.url = url;
        this.contract_fee = contract_fee;
        discountList = new ArrayList<>();
        not_delete_discountList = new ArrayList<>();
        discountDefault = -1;
        discountCurrent = discountDefault;
        recordList = new ArrayList<>();
        success_record_num = 0;
        discountCurrentId = -1;
    }
    public void addAppointment(CustomerAppointInfo info,String domain){
        //TODO: notify server new appointment
        new APIHandler(domain).postSession(info);
        totalAppointment = totalAppointment +1;
        return;
    }
    public void addSuccessAppointment(){
        //TODO: notify server new appointment
        successAppointment = successAppointment+1;
    }

    public void setRecordList(List<RecordInfo> list){
        this.recordList = new ArrayList<>(list);
    }

    public List<RecordInfo> getRecordList(){
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

    void changeDiscountCurrentId(int id){
        this.discountCurrentId = id;
    }

    void stopDiscount(){
        this.discountCurrentId = -1;
    }


}
