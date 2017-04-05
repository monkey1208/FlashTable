package com.example.yang.flashtable;


public class CustomerAppointInfo {
    public int id;
    public String name;
    public int honor;
    public int number;
    public int im_id;
    public int expireTime;
    public CustomerAppointInfo(int id,String name,int honor,int number){
        this.id = id;
        this.name = name;
        this.honor = honor;
        this.number = number;
        this.expireTime = 60;
        this.im_id = R.drawable.ic_temp_user1;
    }
    public CustomerAppointInfo(String name,int honor,int number,int im_id){
        this.name=name;
        this.honor=honor;
        this.number=number;
        this.im_id=im_id;
        this.expireTime = 60;
    }
}
