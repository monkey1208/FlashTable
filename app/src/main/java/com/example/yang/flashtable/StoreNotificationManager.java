package com.example.yang.flashtable;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Administrator on 2017/7/5.
 */

public class StoreNotificationManager {
    private Context context;
    private int notifyID = 0;
    public StoreNotificationManager(Context context){
        this.context = context;
    }
    public void sentNotification(String name){
        if(context!= null){
            int requestCode = notifyID;
            Intent intent = new Intent();
            int flags = PendingIntent.FLAG_CANCEL_CURRENT;
            PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, flags);
            NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new Notification.Builder(context.getApplicationContext()).setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle("您有新的預約").setContentText("來自"+name+"的預約").setTicker("您有新的預約").setContentIntent(pendingIntent).build();
            notificationManager.notify(notifyID, notification);
        }
        else
            Log.d("Notify","Context = NULL");
        notifyID++;
    }
    public void remindPromotionOpen(Boolean active){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(active) {
            if (context != null) {
                Notification notification = new Notification.Builder(context.getApplicationContext()).setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("優惠開啟中").setOngoing(true).build();
                notificationManager.notify(-1, notification);
            } else
                Log.d("Notify", "Context = NULL");
        }else
            notificationManager.cancel(-1);

    }
}
