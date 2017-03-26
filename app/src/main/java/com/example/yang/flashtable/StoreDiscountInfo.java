package com.example.yang.flashtable;

/**
 * Created by 奕先 on 2017/3/25.
 */

public class StoreDiscountInfo {
    String name;
    String description;
    int count;
    boolean isDefault;
    public StoreDiscountInfo(String name, String description, int count, boolean isDefault){
        this.name = name;
        this.description = description;
        this.count = count;
        this.isDefault = isDefault;
    }
}
