package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class APIHandler {
    private Handler handler = new Handler();
    private Context context;

    private List<String> requestCache = new ArrayList<>();
    private List<Integer> request_id = new ArrayList<>();
    private List<CustomerAppointInfo> newInfoList = new ArrayList<>();

    private boolean isActive = false;
    private boolean stat = false;
    private int sum = 0;

    public APIHandler(Context context){
        this.context = context;
    }

    public class APIRequestUpdate extends AsyncTask<String, Void, Void>{
        @Override
        protected Void doInBackground(String... params) {
            final HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_requests?shop_id="+params[0]);
                final JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                for(int i=1;i<responseRequest.length();i++)
                    request_id.add(responseRequest.getJSONObject(i).getInt("request_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            stat = true;
        }
    }
    public void getRequestDetail(int id){
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpGet getRequestInfo = new HttpGet("https://flash-table.herokuapp.com/api/request_info?request_id="+Integer.toString(id));
            JSONObject requestInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRequestInfo)));
            int number = requestInfo.getInt("number");
            String userId = requestInfo.getString("user_id");
            HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+userId);
            JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
            int honor = userInfo.getInt("point");
            String account = userInfo.getString("account");
            CustomerAppointInfo newInfo = new CustomerAppointInfo(id,account,honor,number);
            boolean stat = true;
            for(int i=0;i<StoreMainActivity.fragmentController.storeRecentFragment.recentList.size();i++){
                if(StoreMainActivity.fragmentController.storeRecentFragment.recentList.get(i).name.equals(account)) {
                    stat = false;
                    break;
                }
            }
            sum++;
            if(stat)
                newInfoList.add(newInfo);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (HttpResponseException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            return;
        }
    }
    private class ThreadGetDetail extends Thread{
        int request_id;
        public ThreadGetDetail(int id){
            this.request_id = id;
        }

        @Override
        public void run() {
            super.run();
            getRequestDetail(request_id);
        }
    }
    public void updateCache(){
        //TODO get requests from server
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                request_id.clear();
                sum = 0;
                stat = false;
                new APIRequestUpdate().execute("1");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(!stat) continue;
                        newInfoList.clear();
                        for(int i=0;i<request_id.size();i++){
                            ThreadGetDetail thread = new ThreadGetDetail(request_id.get(i));
                            thread.start();
                        }
                        handlerPost("getting "+Integer.toString(request_id.size())+"new info");
                        while(sum!=request_id.size())continue;
                        handlerPost("get "+Integer.toString(newInfoList.size())+"new info");
                        StoreMainActivity.fragmentController.storeRecentFragment.addRequest2List(newInfoList);
                    }
                }).start();
            }
        },0,5000);

    }
    public void handlerPost(final String str){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,str,Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void changePromotions(){
        if(!isActive)
            updateCache();
        isActive = true;
        return;
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

