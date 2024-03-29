package com.example.yang.flashtable;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.yang.flashtable.customer.infos.CustomerAppointInfo;

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

public class APIHandler {
    private List<String> deleteList = new ArrayList<>();
    private boolean isActive = false;

    private static String domain_name;

    public APIHandler(String domain){
        this.domain_name = domain;
    };

    public void postPromotionInactive(){
        new APIpromotion_inactive().execute(Integer.toString(StoreMainActivity.storeInfo.discountCurrentId));
        Log.d("Promotion","Inactivate "+Integer.toString(StoreMainActivity.storeInfo.discountCurrentId));
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
                HttpPost post = new HttpPost(domain_name+"api/cancel_request");
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
                HttpPost post = new HttpPost(domain_name+"api/accept_request");
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
            StoreMainActivity.fragmentController.storeAppointFragment.refresh();
        }
    }
    private class APIsession_cancel extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(domain_name+"api/cancel_session");
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
                HttpPost post = new HttpPost(domain_name+"api/inactivate_promotion");
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
        @Override
        protected void onPostExecute(Void _params){
            StoreMainActivity.storeInfo.stopDiscount();
        }

    }


    public static class Post_promotion extends AsyncTask<String,Void,Void> {
        int new_promotion_id;
        String name = "11";
        String description;
        boolean exception = false;
        Context context;
        public Post_promotion(Context context){
            super();
            this.context = context;
        }
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(params[0]+"api/new_promotion");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                description = params[1];
                param.add(new BasicNameValuePair("name", "11"));
                param.add(new BasicNameValuePair("description",params[1]));
                param.add(new BasicNameValuePair("shop_id",params[2]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(post)));
                new_promotion_id = recordInfo.getInt("promotion_id");
            } catch (Exception e) {
                e.printStackTrace();
                exception = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            if(exception){
                new AlertDialogController(domain_name).warningConfirmDialog(context,"提醒", "網路連線失敗，請檢查您的網路");
            }else {
                StoreDiscountInfo info = new StoreDiscountInfo(new_promotion_id, description, false, 0, false);
                StoreMainActivity.storeInfo.discountList.add(info);
                StoreMainActivity.storeInfo.not_delete_discountList.add(info);
                StoreManageDiscountFragment.addPromotionList(info);
            }
        }
    }

}

