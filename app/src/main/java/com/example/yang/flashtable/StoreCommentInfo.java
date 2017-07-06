package com.example.yang.flashtable;

public class StoreCommentInfo {
    String user;
    String content;
    String user_pic_url;
    String record_time;
    float rating;

    public StoreCommentInfo(String _user, String _content, float _rating, String _user_pic_url, String _record_time) {
        user = _user;
        content = _content;
        rating = _rating;
        user_pic_url = _user_pic_url;
        record_time = _record_time;
    }
}
