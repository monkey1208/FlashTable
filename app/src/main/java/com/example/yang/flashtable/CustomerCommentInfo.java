package com.example.yang.flashtable;

/**
 * Created by CS on 2017/3/25.
 */

public class CustomerCommentInfo {
    String user;
    String shop;
    String content;
    float rating;

    public CustomerCommentInfo(String _user, String _shop, String _content, float _rating) {
        user = _user;
        shop = _shop;
        content = _content;
        rating = _rating;
    }
}
