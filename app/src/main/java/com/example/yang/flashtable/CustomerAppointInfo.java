package com.example.yang.flashtable;


public class CustomerAppointInfo {
    public String name;
    public int honor;
    public int number;
    public int im_id;
    public int expireTime;
    public CustomerAppointInfo(String name,int honor,int number,int im_id){
        this.name=name;
        this.honor=honor;
        this.number=number;
        this.im_id=im_id;
        this.expireTime = 60;
    }
}
