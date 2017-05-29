package com.example.yang.flashtable;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by CS on 2017/5/22.
 */

public class FlashCouponInfo {
    // Named according to API
    public String name, description;
    public int flash_point;
    public String picture_url_small, picture_url_large;
    public String tutorial;
    public int coupon_id;

    public Bitmap picture_small = null;
}
