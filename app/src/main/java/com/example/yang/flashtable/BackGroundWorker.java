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
        @Override
        protected Void doInBackground(String... params) {
            final HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_requests?shop_id="+params[0]+"&verbose=1");
                final JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                for(int i=1;i<responseRequest.length();i++) {
                    JSONObject object = responseRequest.getJSONObject(i);
                    int id  = object.getInt("request_id");
                    int promotion_id = object.getInt("promotion_id");
                    int user_id = object.getInt("user_id");
                    String user_account = object.getString("user_account");
                    int number = object.getInt("number");
                    int point = object.getInt("user_point");
                    CustomerAppointInfo newInfo = new CustomerAppointInfo(id,user_account,point,number);
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
            Collections.sort(request_id);
            if(request_id.size()>0)
                StoreMainActivity.fragmentController.storeRecentFragment.setRequestIDupper(request_id.get(request_id.size()-1));
            StoreMainActivity.fragmentController.storeRecentFragment.addItem(newInfoList);
            newInfoList.clear();
            request_id.clear();
        }
    }

}
