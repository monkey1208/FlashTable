package com.example.yang.flashtable;

import android.graphics.Bitmap;


public class RecordInfo {
    public int id;
    public String name;
    public int number;
    public long due_time;
    public String record_time;
    public String session_time;
    public boolean isActive;
    public String is_succ;
    public int point;
    public int promotion_id;
    public Bitmap picture;
    private String image_url;
    public  String promotion_des;

    public RecordInfo(int id, String name, int point, int number, long due_time, int promotion_id, String url){
        this.id = id;
        this.name = name;
        this.point = point;
        this.number = number;
        this.due_time = due_time;
        this.isActive = true;
        this.promotion_id = promotion_id;
        this.image_url =url;
        this.picture = null;
    }

    public RecordInfo(String name, int number, int point, String record_time, String session_time, String is_succ, String image_url, String promotion_des){
        this.name = name;
        this.number = number;
        this.point = point;
        this.record_time = record_time;
        this.session_time = session_time;
        this.is_succ = is_succ;
        this.picture = null;
        this.image_url = image_url;
        this.promotion_des = promotion_des;
    }

    String get_Image_Url(){
        return this.image_url;
    }
}
