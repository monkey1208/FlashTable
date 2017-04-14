package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;

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
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BackGroundWorker {

    private List<CustomerAppointInfo> newInfoList = new ArrayList<>();
    private Timer timer;
    private Context context;

    public BackGroundWorker(Context context){
        this.context = context;
    }
    public void updateRequestList(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new APIRequestUpdate().execute(StoreMainActivity.storeInfo.id);
            }
        },0,5000);
    }
    public void killTimer(){
        timer.cancel();
    }
    public class APIRequestUpdate extends AsyncTask<String, Void, Void> {
        private List<Integer> request_id = new ArrayList<>();
        private List<Thread_request_detail> threadList = new ArrayList<>();
        @Override
        protected Void doInBackground(String... params) {
            final HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_requests?shop_id="+params[0]);
                final JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                for(int i=1;i<responseRequest.length();i++)
                    request_id.add(responseRequest.getJSONObject(i).getInt("request_id"));
                Log.e("Update","Get "+Integer.toString(request_id.size())+" new info");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            ThreadManager manager = new ThreadManager(request_id);
            manager.start();
        }
    }
    private class ThreadManager extends Thread{
        private List<Integer> request_id = new ArrayList<>();
        private List<Thread_request_detail> threadList = new ArrayList<>();
        public ThreadManager(List<Integer> request_id){
            this.request_id = request_id;
        }
        @Override
        public void run() {
            super.run();
            Collections.sort(request_id);
            for(int i=0;i<request_id.size();i++)
                threadList.add(new Thread_request_detail(request_id.get(i)));
            for(int i=0;i<threadList.size();i++)
                threadList.get(i).start();
            Log.e("Update",Integer.toString(threadList.size())+" threads running");
            for(int i=0;i<threadList.size();i++) {
                try {
                    threadList.get(i).join();
                } catch (InterruptedException e) {
                    Log.e("Recent Update Thread", Integer.toString(threadList.get(i).request_id));
                    e.printStackTrace();
                }
            }
            Log.e("Update","Got "+Integer.toString(newInfoList.size())+" new info");
            if(request_id.size()>0)
                StoreMainActivity.fragmentController.storeRecentFragment.setRequestIDupper(request_id.get(request_id.size()-1));
            StoreMainActivity.fragmentController.storeRecentFragment.addItem(newInfoList);
            newInfoList.clear();
        }
    }
    private class Thread_request_detail extends Thread{
        private int request_id;
        public Thread_request_detail(int id){
            this.request_id = id;
        }
        @Override
        public void run() {
            super.run();
            Log.e("Update","Before getting Detail");
            getRequestDetail(request_id);
            Log.e("Update","After getting Detail");
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
            if(newInfo.id > StoreMainActivity.fragmentController.storeRecentFragment.getRequestIDupper()) {
                synchronized (newInfoList) {
                    newInfoList.add(newInfo);
                }
            }
            Log.e("Update","No Exception Getting Detail");
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
}
