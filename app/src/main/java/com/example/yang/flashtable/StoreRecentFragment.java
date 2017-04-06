package com.example.yang.flashtable;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StoreRecentFragment extends Fragment {

    private View v;
    public static List<CustomerAppointInfo> recentList;
    private ListView lv_recent;
    private View item_view;
    private int selected;
    private StoreRecentAdapter recentAdapter;
    private List<CustomerAppointInfo> waitingList = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private int size;
    private boolean active=true;
    public int test = 10;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            for(int i=0;i<recentList.size();i++) {
                if (recentList.get(i).expireTime > 0)
                    recentList.get(i).expireTime--;
                else {
                    StoreMainActivity.apiHandler.postRequestDeny(recentList.get(i).id,recentList.get(i).name);
                    recentList.remove(i);
                    i--;
                }
            }
            for(int i=0;i<waitingList.size();i++)
                recentList.add(waitingList.get(i));
            waitingList.clear();

            recentAdapter.notifyDataSetChanged();
        }
    };
    private void countDown(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        },0,1000);
    }
    public StoreRecentFragment() {}

    public void addRequest2List(final List<CustomerAppointInfo> list){
        waitingList.clear();
        for(int i=0;i<list.size();i++)
            waitingList.add(list.get(i));
        return;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //test-------------
        recentList = new ArrayList<>();
        //func_test();
        //init-------------
        v =  inflater.inflate(R.layout.store_recent_fregment, container, false);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setPadding(0, getStatusBarHeight(), 0, 0);
        handler = new Handler();
        waitingList = new ArrayList<>();
        //listview---------
        lv_recent = (ListView) v.findViewById(R.id.lv_recent);
        recentAdapter = new StoreRecentAdapter(getActivity(),recentList);
        lv_recent.setAdapter(recentAdapter);
        countDown();
        //------------------
        return v;
    }

    public CustomerAppointInfo getSelected(){
        return recentList.get(selected);
    }

    private void func_test(){
        CustomerAppointInfo test1 = new CustomerAppointInfo("張庭維",100,10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test2 = new CustomerAppointInfo("李承軒",0,1,R.drawable.ic_temp_user2);
        CustomerAppointInfo test3 = new CustomerAppointInfo("陳奕先",10,90,R.drawable.ic_temp_user1);
        test3.expireTime = test3.expireTime-6;
        test2.expireTime = test2.expireTime-2;
        test1.expireTime = test1.expireTime-6;
        recentList.add(test1);
        recentList.add(test2);
        recentList.add(test3);
    }
    public void removeItem(int position){
        selected = position;
        recentList.remove(position);
        recentAdapter.notifyDataSetChanged();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
