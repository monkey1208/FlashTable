package com.example.yang.flashtable.customer.infos;

import android.graphics.Bitmap;

/**
 * Created by Yang on 2017/7/5.
 */

public class CustomerCommentContentInfo{
    public String time;
    public String shop_name;
    public String shopId;
    public String address;
    public String recordId;
    public Bitmap img;
    public CustomerCommentContentInfo(String shop_name, String shopId, String address, String time, String recordId){
        this.shop_name = shop_name;
        this.shopId = shopId;
        this.address = address;
        this.time = time;
        this.recordId = recordId;
    }

    public void setImg(Bitmap img){
        this.img = img;
    }
}