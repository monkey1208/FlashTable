package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
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

public class APIHandler {
    private Handler handler = new Handler();
    private Context context;
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
    private List<Integer> request_id = new ArrayList<>();
    private List<String> testlist = new ArrayList<>();
    private boolean isActive = false;
    private boolean stat = false;
    private HttpClient httpClient;

    public APIHandler(Context context){
        this.context = context;
    }

    public class APIRequestUpdate extends AsyncTask<String, Void, Void>{
        String test = "";
        int size = 0;
        @Override
        protected Void doInBackground(String... params) {
            final HttpClient httpClient = new DefaultHttpClient();
            try {
                List<CustomerAppointInfo> newInfoList = new ArrayList<>();
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_requests?shop_id="+params[0]);
                final JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                size+=responseRequest.length();
                for(int i=1;i<responseRequest.length();i++) {
                    request_id.add(responseRequest.getJSONObject(i).getInt("request_id"));
                    int id = responseRequest.getJSONObject(i).getInt("request_id");
                    HttpGet getRequestInfo = new HttpGet("https://flash-table.herokuapp.com/api/request_info?request_id="+Integer.toString(id));
                    JSONObject requestInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRequestInfo)));
                    int number = requestInfo.getInt("number");
                    String userId = requestInfo.getString("user_id");
                    HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+userId);
                    JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
                    int honor = userInfo.getInt("point");
                    String account = userInfo.getString("account");
                    CustomerAppointInfo newInfo = new CustomerAppointInfo(id,account,honor,number);
                    test += ("number: "+Integer.toString(number)+" userId: "+userId+" honor:"+Integer.toString(honor)+" account: "+account+"\n");
                    newInfoList.add(newInfo);
                    size++;
                }
                StoreMainActivity.fragmentController.storeRecentFragment.addRequest2List(newInfoList);
            } catch (JSONException e) {
                test += "JSON";
                e.printStackTrace();
            } catch (IOException e) {
                test += "IO";
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            stat = true;
            Toast.makeText(context,test,Toast.LENGTH_LONG).show();
        }
    }
    public void getRequestDetail(int request_id){
        String test = "";
        try {
            HttpGet getRequestInfo = new HttpGet("https://flash-table.herokuapp.com/api/request_info?request_id="+Integer.toString(request_id));
            JSONObject requestInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRequestInfo)));
            int number = requestInfo.getInt("number");
            String userId = requestInfo.getString("user_id");
            test = ("number: "+Integer.toString(number)+" userId: "+userId);
            /*HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+userId);
            JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
            int honor = userInfo.getInt("point");
            String account = userInfo.getString("account");
            CustomerAppointInfo newInfo = new CustomerAppointInfo(request_id,account,honor,number);
            StoreMainActivity.fragmentController.storeRecentFragment.recentList.add(newInfo);*/
        } catch (JSONException e) {
            test = "JSON";
            e.printStackTrace();
        } catch (HttpResponseException e) {
            test = "Http";
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            test = "Client";
            e.printStackTrace();
        } catch (IOException e) {
            test = "IO";
            e.printStackTrace();
        }finally {
            testlist.add(test);
            return;
        }
    }
    private int sum = 0;
    private class ThreadGetDetail extends Thread{
        int request_id;
        public ThreadGetDetail(int id){
            this.request_id = id;
        }

        @Override
        public void run() {
            super.run();
            getRequestDetail(request_id);
            sum++;
        }
    }
    public void updateCache(){
        //TODO get requests from server
        new APIRequestUpdate().execute("3");
        /*new Thread(new Runnable() {

            @Override
            public void run() {
                while(!stat)
                    continue;
                for(int i=0;i<request_id.size();i++){
                    ThreadGetDetail thread = new ThreadGetDetail(request_id.get(i));
                    thread.start();
                }
                while(sum!=request_id.size())continue;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,"get Done",Toast.LENGTH_SHORT).show();
                    }
                });
                for(int i=0;i<sum;i++) {
                    final int finalI = i;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context,Integer.toString(finalI)+testlist.get(finalI),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();*/
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

