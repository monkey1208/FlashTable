package com.example.yang.flashtable;

import java.util.ArrayList;
import java.util.List;

public class StoreInfo {

    public List<DiscountInfo> discountList;
    public StoreInfo(String n,String a){
        name = n;
        address = a;
        discountList = new ArrayList<>();
        addDiscount(100,"暫無優惠");
    }
    public String name;
    public String address;
    public class DiscountInfo{
        public String gift;
        public int discount;
        public DiscountInfo(int d,String g){
            gift = g;
            discount = d;
        }
    }

    public void addDiscount(int d,String g){
        DiscountInfo newDiscount = new DiscountInfo(d,g);
        discountList.add(newDiscount);
    }
}
