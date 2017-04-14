package com.example.yang.flashtable;

/**
 * Created by 奕先 on 2017/3/25.
 */

public class StoreDiscountInfo {
    int id;
    int discount;
    String description;
    int count;
    boolean isDefault;
    public StoreDiscountInfo(int id,int discount, String description, int count){
        this.id = id;
        this.discount = discount;
        this.description = description;
        this.count = count;
        this.isDefault = false;
    }
    public StoreDiscountInfo(int discount, String description, int count, boolean isDefault){
        this.discount = discount;
        this.description = description;
        this.count = count;
        this.isDefault = isDefault;
    }

    public int getCount(){
        return count;
    }
}
