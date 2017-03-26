package com.example.yang.flashtable;


public class CustomerAppointInfo {
    public String name;
    public int honor;
    public int number;
    public int im_id;
    public int stat;
    public final static int READY = 0;
    public final static int CONFIRM = 1;
    public final static int CANCEL = 2;
    public final static int DONE = 3;
    public CustomerAppointInfo(String name,int honor,int number,int im_id){
        this.name=name;
        this.honor=honor;
        this.number=number;
        this.im_id=im_id;
        this.stat = READY;
    }
}
