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
    }
    public String name;
    public String address;
    public int discountDefault;
    public int discountCurrent;
}
