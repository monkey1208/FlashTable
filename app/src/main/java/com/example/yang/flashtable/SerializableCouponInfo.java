package com.example.yang.flashtable;

import java.io.Serializable;

/**
 * Created by CS on 2017/5/28.
 */

public class SerializableCouponInfo implements Serializable{
    public String name, description;
    public int flash_point;
    public String picture_url_small, picture_url_large;
    public String tutorial;
    public int coupon_id;
}
