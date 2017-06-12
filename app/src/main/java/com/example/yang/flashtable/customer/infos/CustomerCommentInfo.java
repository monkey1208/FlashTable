package com.example.yang.flashtable.customer.infos;

import android.graphics.Bitmap;

/**
 * Created by CS on 2017/3/25.
 */

public class CustomerCommentInfo {
    public String user;
    public String shop;
    public String content;
    public float rating;
    public int userID;
    public int shopID;

    public CustomerCommentInfo(String _user, String _shop, String _content, float _rating, int _userID, int _shopID) {
        user = _user;
        shop = _shop;
        content = _content;
        rating = _rating;
        userID = _userID;
        shopID = _shopID;
    }

    public Bitmap avatar;
}
