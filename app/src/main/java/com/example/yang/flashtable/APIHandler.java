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
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.yang.flashtable.StoreManageSuccessFragment.tv_fail;
import static com.example.yang.flashtable.StoreManageSuccessFragment.tv_rate;
import static com.example.yang.flashtable.StoreManageSuccessFragment.tv_success;
import static com.example.yang.flashtable.StoreManageSuccessFragment.tv_total;

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

    public static class APIRecordDetail extends AsyncTask<Object, Void, Void> {
        List<ReservationInfo> list = new ArrayList<>();
        @Override
        protected Void doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ String.valueOf(1));
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                for (int i = 1; i < recordsInfo.length(); i++) {
                    JSONObject jsonItem = recordsInfo.getJSONObject(i);
                    int record_id = jsonItem.getInt("record_id");
                    HttpGet getRecordInfo = new HttpGet("https://flash-table.herokuapp.com/api/record_info?record_id="+record_id);
                    JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordInfo)));
                    int num = recordInfo.getInt("number");
                    String is_success = recordInfo.getString("is_succ");

                    String time = recordInfo.getString("created_at");
                    DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss yyyy", Locale.getDefault());
                    Date date =  df.parse(time);
                    df = new SimpleDateFormat("yyyy/MM/dd  ahh:mm", Locale.getDefault());
                    time = df.format(date);

                    int user_id = recordInfo.getInt("user_id");
                    HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+user_id);
                    JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
                    String account = userInfo.getString("account");
                    int point = userInfo.getInt("point");
                    final ReservationInfo info = new ReservationInfo(account, num, point, time, is_success);
                    list.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            StoreManageRecordFragment.list.addAll(list);
            StoreManageRecordFragment.adapter.notifyDataSetChanged();
        }
    }

    public static class Post_promotion extends AsyncTask<String,Void,Void> {
        int new_promotion_id;
        String name;
        String description;
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/new_promotion");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                name = params[0];
                description = params[1];
                param.add(new BasicNameValuePair("name",params[0]));
                param.add(new BasicNameValuePair("description",params[1]));
                param.add(new BasicNameValuePair("shop_id",params[2]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null) {
                    JSONObject jsonResponse = new JSONObject(resEntity.toString());
                    if(jsonResponse.getInt("status_code") == 0) {
                        new_promotion_id = jsonResponse.getInt("promotion_id");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            StoreDiscountInfo info = new StoreDiscountInfo(new_promotion_id, Integer.valueOf(name), description);
            StoreManageDiscountFragment.discountList.add(info);
            StoreManageDiscountFragment.adapter.notifyDataSetChanged();
        }
    }

    public static class ReservationSuccessDetail extends AsyncTask<Object, Void, Void> {
        int sum = 0, total;
        @Override
        protected Void doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getReservationInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ String.valueOf(1));
                JSONArray reservationInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getReservationInfo)));
                total = reservationInfo.length();
                for (int i = 1; i < reservationInfo.length(); i++) {
                    JSONObject jsonItem = reservationInfo.getJSONObject(i);
                    int record_id = jsonItem.getInt("record_id");
                    HttpGet getRecordInfo = new HttpGet("https://flash-table.herokuapp.com/api/record_info?record_id="+record_id);
                    JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordInfo)));
                    String is_success = recordInfo.getString("is_succ");
                    if(is_success.equals("true")){
                        sum += 1;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            DecimalFormat df2 = new DecimalFormat(".##");
            tv_rate.setText(df2.format((sum+0.0)/total*100));
            tv_total.setText(Integer.toString(total));
            tv_fail.setText(Integer.toString(total-sum));
            tv_success.setText(Integer.toString(sum));
        }
    }
}

