package com.example.yang.flashtable.customer;

import java.util.Observable;

/**
 * Created by Yang on 2017/5/25.
 */

public class CustomerObservable extends Observable{
    private static CustomerObservable observable = new CustomerObservable();
    public String distance, food, mode;

    public static CustomerObservable getInstance(){
        return observable;
    }

    private CustomerObservable(){
        food = "all";
        distance = "-1";
        mode = "default";
    }

    public void init(){
        food = "all";
        distance = "-1";
        mode = "default";
    }

    public void setData(String distance, String food, String mode){
        if(this.distance.equals(distance) && this.food.equals(food) && this.mode.equals(mode)){
            return;
        }else{
            this.distance = distance;
            this.food = food;
            this.mode = mode;
            setChanged();
        }
        notifyObservers(new String[]{distance, food, mode});
    }

}
