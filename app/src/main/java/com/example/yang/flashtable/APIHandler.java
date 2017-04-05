package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class APIHandler {
    private Handler handler = new Handler();
    private Context context;

    private List<String> requestCache = new ArrayList<>();
    private List<String> deleteList = new ArrayList<>();
    private List<Integer> request_id = new ArrayList<>();
    private List<CustomerAppointInfo> newInfoList = new ArrayList<>();

    private Timer timer;
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
    private void checkListBeforeShow(List<CustomerAppointInfo> list){
        List<CustomerAppointInfo> temp = new ArrayList<>();
        boolean stat = true;
        for(int j=0;j<list.size();j++) {
            String account = list.get(j).name;
            for (int i = 0; i < StoreMainActivity.fragmentController.storeRecentFragment.recentList.size(); i++) {
                if (StoreMainActivity.fragmentController.storeRecentFragment.recentList.get(i).name.equals(account)) {
                    stat = false;
                    break;
                }
            }
            if (stat) {
                for (int i = 0; i < deleteList.size(); i++) {
                    if (deleteList.get(i).equals(account)) {
                        stat = false;
                        break;
                    }
                }
            }
            sum++;
            if (stat)
                temp.add(newInfoList.get(j));
        }
        deleteList.clear();
        newInfoList = temp;
        return;
    }
    public void updateCache(){
        //TODO get requests from server
        timer = new Timer();
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
                        //handlerPost("getting "+Integer.toString(request_id.size())+" new info del: "+Integer.toString(deleteList.size()));
                        Log.d("Update","getting "+Integer.toString(request_id.size())+" new info del: "+Integer.toString(deleteList.size()));
                        while(sum!=request_id.size())continue;
                        checkListBeforeShow(newInfoList);
                        //handlerPost("get "+Integer.toString(newInfoList.size())+" new info del: "+Integer.toString(deleteList.size()));
                        //for(int i=0;i<deleteList.size();i++)
                        Log.d("Update","get "+Integer.toString(newInfoList.size())+" new info del: "+Integer.toString(deleteList.size()));
                        StoreMainActivity.fragmentController.storeRecentFragment.addRequest2List(newInfoList);
                    }
                }).start();
            }
        },0,5000);
    }
    public void killTimer(){
        timer.cancel();
    }
    public void postPromotionInactive(){
        new APIpromotion_inactive().execute(Integer.toString(StoreMainActivity.storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).id));
        Log.d("Promotion","Inactivate "+Integer.toString(StoreMainActivity.storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).id));
        killTimer();
        return;
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
    public void postRequestDeny(int id,String name){
        handlerPost(Integer.toString(id));
        new APIrequest_deny().execute(Integer.toString(id));
        deleteList.add(name);
        return;
    }
    public void postSession(CustomerAppointInfo cinfo){
        ReservationInfo info = new ReservationInfo(cinfo.name,cinfo.number,System.currentTimeMillis());
        new APIrequest_accept().execute(Integer.toString(cinfo.id));
        StoreMainActivity.fragmentController.storeAppointFragment.appointList.add(info);
        StoreMainActivity.fragmentController.storeAppointFragment.adapter.notifyDataSetChanged();
    }
    public void postSessionDeny(int id){
        new APIsession_cancel().execute(Integer.toString(id));
        Log.d("Accept",Integer.toString(id));
    }
    private class APIrequest_deny extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/cancel_request");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("request_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null)
                    result = resEntity.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class APIrequest_accept extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        int id = -1;
        @Override
        protected Void doInBackground(String... params) {

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/accept_request");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("request_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                JSONObject result =new JSONObject (new BasicResponseHandler().handleResponse(httpClient.execute(post)));
                /*HttpEntity resEntity = response.getEntity();
                if(resEntity != null)
                    result = resEntity.toString();*/
                //JSONObject object = new JSONObject(result);
                id = result.getInt("session_id");
            } catch (UnsupportedEncodingException e) {
                id = -2;
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                id = -3;
                e.printStackTrace();
            } catch (IOException e) {
                id = -4;
                e.printStackTrace();
            } catch (JSONException e) {
                id = -5;
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void _params) {
            StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(StoreMainActivity.fragmentController.storeAppointFragment.appointList.size()-1).id = id;
            Log.d("Accept","Done getting "+Integer.toString(StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(StoreMainActivity.fragmentController.storeAppointFragment.appointList.size()-1).id));
        }
    }
    private class APIsession_cancel extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/cancel_session");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("session_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null)
                    result = resEntity.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private class APIpromotion_inactive extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/inactivate_promotion");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("promotion_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null)
                    result = resEntity.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

