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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/7/5.
 */

public class StoreBGService extends Service{

    private StoreNotificationManager notificationManager = new StoreNotificationManager(this);
    private Timer timer;
    private String id;
    private int test;
    private int notifyID;
    private int upperID;

    private void update(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new APIrequest().execute();
                test++;
                Log.d("Notify",Integer.toString(test));
            }
        },0,3000);
    }
    private void killtimer(){
        timer.cancel();
    }

    @Override
    public void onCreate(){
        timer = new Timer();
        test = 0;
        notifyID = 0;
        upperID = 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null)
            id = intent.getExtras().getString("name");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        //new StoreNotificationManager(getApplicationContext()).remindPromotionOpen(true);
        remindPromotionOpen(true);
        update();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //new StoreNotificationManager(getApplicationContext()).remindPromotionOpen(false);
        remindPromotionOpen(false);
        killtimer();
    }

    private class APIrequest extends AsyncTask<Void,Void,Void>{
        public APIrequest(){}
        @Override
        protected Void doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet(getString(R.string.server_domain) +"api/shop_requests?shop_id="+id+"&verbose=1");
                JSONArray responseRequest = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(get)));
                for(int i=1;i<responseRequest.length();i++){
                    JSONObject object = responseRequest.getJSONObject(i);
                    int id = object.getInt("request_id");
                    String name = object.getString("user_id");
                    if(id>upperID) {
                        notification(name);
                        upperID = id;
                    }
                }
                Log.d("Notify",id+":"+Integer.toString(responseRequest.length()));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public void notification(String name){
        int requestCode = notifyID;
        Intent intent = new Intent(this,StoreMainActivity.class);
        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);*/
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, 0);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("您有新的預約").setContentText("來自"+name+"的預約").setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL).setContentIntent(pendingIntent).setPriority(Notification.PRIORITY_HIGH).build();
        notificationManager.notify(notifyID, notification);
        notifyID++;
    }
    public void remindPromotionOpen(Boolean isActive){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(isActive){
            int requestCode = -1;
            Intent intent = new Intent(this,StoreMainActivity.class);
            /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);*/
            intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, intent, 0);
            Notification notification = new Notification.Builder(this).setWhen(System.currentTimeMillis()).setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("優惠開啟中").setOngoing(true)
                    .setContentIntent(pendingIntent).build();
            notificationManager.notify(-1, notification);
        }
        else
            notificationManager.cancel(-1);
    }
}
