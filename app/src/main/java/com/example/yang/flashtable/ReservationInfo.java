package com.example.yang.flashtable;

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

    public ReservationInfo(int id, String name,int number,long due_time){
        this.id = id;
        this.name = name;
        this.number = number;
        this.due_time = due_time;
        this.isActive = true;
    }

    public ReservationInfo(String name, int number, int point, String record_time, String is_succ){
        this.id = -1;
        this.name = name;
        this.number = number;
        this.point = point;
        this.record_time = record_time;
        this.is_succ = is_succ;
    }
}
