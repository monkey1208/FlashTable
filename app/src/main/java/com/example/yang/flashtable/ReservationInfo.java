package com.example.yang.flashtable;

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
    private String image_url;
    public String promotion_name;
    public  String promotion_des;

    public ReservationInfo(int id, String name,int number,long due_time,int promotion_id){
        this.id = id;
        this.name = name;
        this.number = number;
        this.due_time = due_time;
        this.isActive = true;
        this.promotion_id = promotion_id;
    }

    public ReservationInfo(String name, int number, int point, String record_time, String is_succ, String image_url, String promotion_name, String promotion_des){
        this.name = name;
        this.number = number;
        this.point = point;
        this.record_time = record_time;
        this.is_succ = is_succ;
        this.image_url = image_url;
        this.promotion_name = promotion_name;
        this.promotion_des = promotion_des;
    }

    String get_Image_Url(){
        return this.image_url;
    }
}
