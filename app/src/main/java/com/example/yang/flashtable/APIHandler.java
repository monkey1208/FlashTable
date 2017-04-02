package com.example.yang.flashtable;

import java.util.ArrayList;
import java.util.List;

public class APIHandler {
    private class UserCache{
        private String name;
        private int id;
        private int honor;
        private long current_time;
        public UserCache(int id,String name,int honor){
            this.id =id;
            this.name = name;
            this.honor = honor;
            this.current_time =System.currentTimeMillis();
        }
    }
    private List<UserCache> userCache = new ArrayList<>();
    public void updateCache(){
        //TODO get requests from server
        for(int i=0;i<userCache.size();i++){

        }
    }
}
