package com.example.yang.flashtable;

/**
 * Created by CS on 2017/5/22.
 */

public class FlashCouponInfo {
    // Named according to API
    public String name, description;
    public int flash_point;
    public String picture_url_small, picture_url_large;
    public String tutorial;

    public FlashCouponInfo(String _name, String _description, int _flash_point, String _tutorial) {
        name = _name;
        description = _description;
        flash_point = _flash_point;
        tutorial = _tutorial;
    }
}
