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

    public ReservationInfo(String name,int number,long current){
        this.id = -1;
        this.name = name;
        this.number = number;
        this.due_time = current + 12*1000;
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
