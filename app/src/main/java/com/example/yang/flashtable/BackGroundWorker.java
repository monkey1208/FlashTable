package com.example.yang.flashtable;

import android.os.AsyncTask;
import android.util.Log;

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

public class BackGroundWorker {

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
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {

            for(int i=1;i<request_id.size();i++)
                threadList.add(new Thread_request_detail(request_id.get(i)));
            for(int i=0;i<threadList.size();i++)
                threadList.get(i).start();
            for(int i=0;i<threadList.size();i++)
                try {
                    threadList.get(i).join();
                } catch (InterruptedException e) {
                    Log.e("Recent Update Thread",Integer.toString(threadList.get(i).request_id));
                    e.printStackTrace();
                }
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
            getRequestDetail(request_id);
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
            //newInfoList.add(newInfo);

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
