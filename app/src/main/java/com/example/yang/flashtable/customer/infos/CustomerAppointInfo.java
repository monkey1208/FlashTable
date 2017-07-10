package com.example.yang.flashtable.customer.infos;


import android.graphics.Bitmap;

import com.example.yang.flashtable.R;

public class CustomerAppointInfo {
    public int id;
    public String name;
    public int honor;
    public int number;
    public int im_id;
    public long expireTime;
    public long due_time;
    public Bitmap picture;
    public String url;
    public boolean isDelete = false;
    public CustomerAppointInfo(int id,String name,int honor,int number,String url,long due_time){
        this.id = id;
        this.name = name;
        this.honor = honor;
        this.number = number;
        this.due_time = due_time;
        this.expireTime = 120;
        this.im_id = R.drawable.ic_temp_user1;
        this.url =url;
        picture = null;
    }
    public CustomerAppointInfo(String name,int honor,int number,int im_id){
        this.name=name;
        this.honor=honor;
        this.number=number;
        this.im_id=im_id;
        this.expireTime = 120;
        picture = null;
    }
}
