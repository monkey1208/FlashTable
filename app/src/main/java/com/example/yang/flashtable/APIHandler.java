package com.example.yang.flashtable;

import android.os.AsyncTask;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class APIHandler {
    private class UserRequestCache{
        private String name;
        private int id;
        private int honor;
        private long current_time;
        public UserRequestCache(int id,String name,int honor){
            this.id =id;
            this.name = name;
            this.honor = honor;
            this.current_time =System.currentTimeMillis();
        }
    }
    private List<UserRequestCache> requestCache = new ArrayList<>();
    private boolean isActive = false;
    private HttpClient httpClient;

    public void updateCache(){
        //TODO get requests from server
        for(int i=0;i<requestCache.size();i++){

        }
    }

    public void changePromotions(){
        if(!isActive)
            updateCache();
    }
    public void postRequestDeny(){

    }
    public void postSession(CustomerAppointInfo cinfo){
        ReservationInfo info = new ReservationInfo(cinfo.name,cinfo.number,System.currentTimeMillis());
        StoreMainActivity.fragmentController.storeAppointFragment.appointList.add(info);
        StoreMainActivity.fragmentController.storeAppointFragment.adapter.notifyDataSetChanged();
    }
    public void postSessionDeny(){

    }
}
