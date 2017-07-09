package com.example.yang.flashtable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.yang.flashtable.customer.CustomerMainActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Yang on 2017/7/9.
 */

public class CustomerSessionServic extends Service {

        private StoreNotificationManager notificationManager = new StoreNotificationManager(this);
        private Timer timer;
        private String user_id;
        private String session_id;
        boolean session_flag = true;
        private int test;
        private int notifyID;


        private void update(){
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if(session_flag)
                        new ApiSessionSuccess().execute();
                    Log.d("Notify",Integer.toString(test));
                }
            },0,2000);
        }
        private void killtimer(){
            if(timer != null)
                timer.cancel();
        }

        @Override
        public void onCreate(){
            timer = new Timer();
            notifyID = 0;
        }

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            System.out.println("service create");
            if(intent != null) {
                user_id = intent.getExtras().getString("user_id");
                session_id = intent.getExtras().getString("session_id");
            }
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public void onStart(Intent intent, int startId){
            super.onStart(intent, startId);
            new StoreNotificationManager(getApplicationContext()).remindPromotionOpen(true);
            update();
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            new StoreNotificationManager(getApplicationContext()).remindPromotionOpen(false);
            killtimer();
        }

        public void finish(){
            killtimer();
        }

        class ApiSessionSuccess extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected void onPreExecute() {
            session_flag = false;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", user_id);
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_sessions?"+param.toString());
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String session_response = handler.handleResponse(response);
                JSONArray session_array = new JSONArray(session_response);
                JSONObject session_object = session_array.getJSONObject(0);
                System.out.println("session = "+session_response);
                if(session_object.get("status_code").equals("0")){
                    int size = Integer.valueOf(session_object.get("size").toString());
                    if(size == 0){
                        //no sessions => rejected
                        //Do something when rejected.
                        return "finish";
                    }else{
                        //accept
                        for(int i = 1; i <= size; i++){
                            if(session_id.equals(session_array.getJSONObject(i).get("session_id").toString())){
                                return "waiting";
                            }
                        }
                        return "finish";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "waiting";
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "finish":
                    if(timer != null)
                        killtimer();
                    session_flag = true;
                    new ApiRecord().execute();
                    break;
                default:
                    break;
            }
            session_flag = true;
            super.onPostExecute(s);
        }
    }

        class ApiRecord extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", user_id);
            HttpGet httpGet = new HttpGet(getString(R.string.server_domain) + "/api/user_records?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String json = handler.handleResponse(httpResponse);
                System.out.println("record:"+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.get("status_code").equals("0")){
                    int last = Integer.valueOf(jsonObject.get("size").toString());
                    String record_id = jsonArray.getJSONObject(last).getString("record_id");
                    param = new BasicNameValuePair("record_id", record_id);
                    httpGet = new HttpGet(getString(R.string.server_domain) + "/api/record_info?" + param.toString());
                    httpResponse = httpClient.execute(httpGet);
                    json = handler.handleResponse(httpResponse);
                    System.out.println("record:"+json);
                    jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status_code").equals("0")){
                        return jsonObject.getString("is_succ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("true")){
                finish();
            }else{
                notification();
                finish();
            }
            super.onPostExecute(s);
        }
    }

        public void notification(){
            int requestCode = notifyID;
            Intent intent = new Intent(this, CustomerMainActivity.class);
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, flags);
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(this).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("您的預約被取消了").setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL).setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_HIGH).build();
            notificationManager.notify(notifyID, notification);
            notifyID++;
        }


}
