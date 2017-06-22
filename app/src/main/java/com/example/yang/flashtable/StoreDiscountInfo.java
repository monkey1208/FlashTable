package com.example.yang.flashtable;


public class StoreDiscountInfo {
    int id;
    String description;
    int count;
    boolean isDefault;
    boolean isDeleted;
    boolean isActive;
    public StoreDiscountInfo(int id, String description, boolean isDeleted, int count, boolean isActive){
        this.id = id;
        this.description = description;
        this.count = count;
        this.isDefault = false;
        this.isDeleted = isDeleted;
        this.isActive = isActive;
    }

    public int getCount(){
        return count;
    }

    public int getId(){
        return this.id;
    }

    public void deleteDiscount(){
        this.isDeleted = true;
    }
}
