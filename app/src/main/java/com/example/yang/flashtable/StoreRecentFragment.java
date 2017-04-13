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
    private List<CustomerAppointInfo> list;
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

            for(int i=0;i<list.size();i++) {
                if (list.get(i).expireTime > 0)
                    list.get(i).expireTime--;
                else {
                    StoreMainActivity.apiHandler.postRequestDeny(list.get(i).id,list.get(i).name);
                    list.remove(i);
                    i--;
                }
            }
            for(int i=0;i<waitingList.size();i++)
                list.add(waitingList.get(i));
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
        list = new ArrayList<>();
        //func_test();
        //init-------------
        v =  inflater.inflate(R.layout.store_recent_fregment, container, false);
        TextView title = (TextView)v.findViewById(R.id.title);
        title.setPadding(0, getStatusBarHeight(), 0, 0);
        handler = new Handler();
        waitingList = new ArrayList<>();
        //listview---------
        lv_recent = (ListView) v.findViewById(R.id.lv_recent);
        recentAdapter = new StoreRecentAdapter(getActivity(),list);
        lv_recent.setAdapter(recentAdapter);
        countDown();
        //------------------
        return v;
    }

    public CustomerAppointInfo getSelected(){
        return list.get(selected);
    }

    private void func_test(){
        CustomerAppointInfo test1 = new CustomerAppointInfo("張庭維",100,10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test2 = new CustomerAppointInfo("李承軒",0,1,R.drawable.ic_temp_user2);
        CustomerAppointInfo test3 = new CustomerAppointInfo("陳奕先",10,90,R.drawable.ic_temp_user1);
        test3.expireTime = test3.expireTime-6;
        test2.expireTime = test2.expireTime-2;
        test1.expireTime = test1.expireTime-6;
        list.add(test1);
        list.add(test2);
        list.add(test3);
    }
    public void removeItem(int position){
        selected = position;
        list.remove(position);
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
    public void addItem2List(CustomerAppointInfo info){
        list.add(info);
        return;
    }
    public CustomerAppointInfo getItem(int position){
        if(list.size()>position)
            return list.get(position);
        else
            return null;
    }
    public int getListSize(){
        return list.size();
    }
}
