package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;


import com.example.yang.flashtable.customer.infos.CustomerAppointInfo;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackGroundWorker {

    private List<CustomerAppointInfo> newInfoList = new ArrayList<>();
    private Timer timer;
    private Context context;

    public BackGroundWorker(Context context){
        this.context = context;
    }
    public void updateRequestList(final String domain){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new APIRequestUpdate(domain).execute(StoreMainActivity.storeInfo.id);
            }
        },0,3000);
    }
    public void killTimer(){
        timer.cancel();
    }
    public class APIRequestUpdate extends AsyncTask<String, Void, Void> {
        private List<Integer> request_id = new ArrayList<>();
        private String domain;
        private int total;

        public APIRequestUpdate(String domain){
            this.domain =domain;
        }
        @Override
        protected Void doInBackground(String... params) {
            final HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet(domain +"api/shop_requests?shop_id="+params[0]+"&verbose=1");
                final JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                total = responseRequest.length()-1;
                for(int i=1;i<responseRequest.length();i++) {
                    JSONObject object = responseRequest.getJSONObject(i);
                    int id  = object.getInt("request_id");
                    int promotion_id = object.getInt("promotion_id");
                    int user_id = object.getInt("user_id");
                    String user_account = object.getString("user_account");
                    int number = object.getInt("number");
                    int point = object.getInt("user_point");
                    String url = object.getString("user_picture_url");
                    CustomerAppointInfo newInfo = new CustomerAppointInfo(id,user_account,point,number,url);
                    if(id>StoreMainActivity.fragmentController.storeRecentFragment.getRequestIDupper()) {
                        request_id.add(id);
                        newInfoList.add(newInfo);
                    }
                }
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
            final List<Thread> threadList = new ArrayList<>();
            Collections.sort(request_id);
            if(request_id.size()>0)
                StoreMainActivity.fragmentController.storeRecentFragment.setRequestIDupper(request_id.get(request_id.size()-1));
            for(int i=0;i<newInfoList.size();i++) {
                final CustomerAppointInfo info = newInfoList.get(i);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap mIcon = null;
                        try {
                            InputStream in = new java.net.URL(info.url).openStream();
                            mIcon = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Picture Error", e.getMessage());
                        }
                        info.picture = mIcon;
                        Log.e("picture", info.name + " get picture!");
                    }
                });
                threadList.add(t);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<threadList.size();i++)
                        threadList.get(i).start();
                    for(int i=0;i<threadList.size();i++) {
                        try {
                            threadList.get(i).join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    StoreMainActivity.fragmentController.storeRecentFragment.addItem(newInfoList);
                    newInfoList.clear();
                    request_id.clear();
                }
            }).start();
            if(total>9)
                total =10;
            StoreMainActivity.recentUpdateNumber(total);
        }
    }
    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        Bitmap image;
        boolean connect_error = false;

        private ImageDownloader(Bitmap image) {
            this.image = image;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                connect_error = true;
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }
        protected void onPostExecute(Bitmap result) {
            this.image = result;
        }
    }
}
