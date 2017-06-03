package com.example.yang.flashtable;


public class StoreDiscountInfo {
    int id;
    String description;
    int count;
    boolean isDefault;
    public StoreDiscountInfo(int id, String description, int count){
        this.id = id;
        this.description = description;
        this.count = count;
        this.isDefault = false;
    }
    public StoreDiscountInfo(int id, String description, int count, boolean isDefault){
        this.id = id;
        this.description = description;
        this.count = count;
        this.isDefault = isDefault;
    }

    public int getCount(){
        return count;
    }

    public int getId(){
        return this.id;
    }
}
