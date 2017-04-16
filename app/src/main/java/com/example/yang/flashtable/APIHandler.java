package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

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

    public APIHandler(){};

    public void postPromotionInactive(){
        new APIpromotion_inactive().execute(Integer.toString(StoreMainActivity.storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).id));
        Log.d("Promotion","Inactivate "+Integer.toString(StoreMainActivity.storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).id));
        isActive = false;
        return;
    }

    public void postRequestDeny(int id,String name){
        new APIrequest_deny().execute(Integer.toString(id));
        deleteList.add(name);
        return;
    }
    public void postSession(CustomerAppointInfo cinfo){
        new APIrequest_accept().execute(Integer.toString(cinfo.id),cinfo.name,Integer.toString(cinfo.number));

    }
    public void postSessionDeny(int id){
        new APIsession_cancel().execute(Integer.toString(id));
        Log.d("Accept",Integer.toString(id));
    }
    private class APIrequest_deny extends AsyncTask<String,Void,Void> {
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
        int id = -1;
        String name;
        String number;
        int res = -1;
        @Override
        protected Void doInBackground(String... params) {
            name = params[1];
            number = params[2];
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/accept_request");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("request_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                JSONObject result =new JSONObject (new BasicResponseHandler().handleResponse(httpClient.execute(post)));
                id = result.getInt("session_id");
                res = result.getInt("status_code");
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
            ReservationInfo info = new ReservationInfo(name,Integer.valueOf(number),System.currentTimeMillis());
            info.id = id;
            if(res == 0) {
                StoreMainActivity.fragmentController.storeAppointFragment.addItem(info);
                Log.e("Accept","Success");
            }
            else
                Log.e("Accept","Fail");
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
                //HttpResponse response = httpClient.execute(post);
                JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(post)));
                /*HttpEntity resEntity = response.getEntity();
                if(resEntity != null) {
                    if(recordInfo.getInt("status_code") == 0) {*/
                new_promotion_id = recordInfo.getInt("promotion_id");
                Log.d("ChangePromotion",Integer.toString(new_promotion_id));
                //}
                } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                Log.d("ChangePromotion","Exception");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            StoreDiscountInfo info = new StoreDiscountInfo(new_promotion_id, Integer.valueOf(name), description, 0);
            StoreMainActivity.storeInfo.discountList.add(info);
            StoreManageDiscountFragment.adapter.notifyDataSetChanged();
        }
    }


}

