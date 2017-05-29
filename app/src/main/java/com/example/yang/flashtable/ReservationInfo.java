package com.example.yang.flashtable;

import android.graphics.Bitmap;

import java.util.Date;

public class ReservationInfo {
    public int id;
    public String name;
    public int number;
    public long due_time;
    public String record_time;
    public boolean isActive;
    public String is_succ;
    public int point;
    public int promotion_id;
    public String url;
    public Bitmap picture;

    public ReservationInfo(int id, String name,int number,long due_time,int promotion_id,String url){
        this.id = id;
        this.name = name;
        this.number = number;
        this.due_time = due_time;
        this.isActive = true;
        this.promotion_id = promotion_id;
        this.url =url;
        this.picture = null;
    }

    public ReservationInfo(String name, int number, int point, String record_time, String is_succ){
        this.id = -1;
        this.name = name;
        this.number = number;
        this.point = point;
        this.record_time = record_time;
        this.is_succ = is_succ;
        this.picture = null;
    }
}
