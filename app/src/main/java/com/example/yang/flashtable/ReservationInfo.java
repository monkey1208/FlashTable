package com.example.yang.flashtable;

/**
 * Created by 奕先 on 2017/3/23.
 */

public class ReservationInfo {
    public String name;
    public int number;
    public long due_time;
    public ReservationInfo(String name,int number){
        this.name = name;
        this.number = number;
        this.due_time = 60000;
    }
}
