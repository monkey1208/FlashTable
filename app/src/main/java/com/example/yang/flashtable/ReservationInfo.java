package com.example.yang.flashtable;

/**
 * Created by 奕先 on 2017/3/23.
 */

public class ReservationInfo {
    public int id;
    public String name;
    public int number;
    public long due_time;
    public boolean isActive;
    public ReservationInfo(String name,int number,long current){
        this.name = name;
        this.number = number;
        this.due_time = current + 12*1000;
        this.isActive = true;
    }
}
