package com.example.yang.flashtable;

/**
 * Created by 奕先 on 2017/3/23.
 */

public class ReservationInfo {
    public int id;
    public String name;
    public int number;
    public long due_time;
    public String record_time;
    public boolean isActive;
    public String is_succ;
    public int point;

    public ReservationInfo(String name,int number,long current){
        this.name = name;
        this.number = number;
        this.due_time = current + 12*1000;
        this.isActive = true;
    }

    public ReservationInfo(String name, int number, int point, String record_time, String is_succ){
        this.name = name;
        this.number = number;
        this.point = point;
        this.record_time = record_time;
        this.is_succ = is_succ;
    }
}
