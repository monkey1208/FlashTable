package com.example.yang.flashtable;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StoreRecentFragment extends Fragment {

    private View v;
    private List<CustomerAppointInfo> list;
    private int requestIDupper = -1;

    private ListView lv_recent;
    private View item_view;
    private int selected;
    private StoreRecentAdapter adapter;
    private List<CustomerAppointInfo> waitingList = new ArrayList<>();
    public int test = 10;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            synchronized (list) {
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).expireTime > 0)
                        list.get(i).expireTime--;
                    else {
                        new APIHandler().postRequestDeny(list.get(i).id, list.get(i).name);
                        list.remove(i);
                        i--;
                    }
                }
                for (int i = 0; i < waitingList.size(); i++)
                    list.add(waitingList.get(i));
                waitingList.clear();
            }
            adapter.notifyDataSetChanged();
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
        Toolbar bar = (Toolbar)v.findViewById(R.id.title);
        bar.setPadding(0,getStatusBarHeight(), 0,0);
        bar.inflateMenu(R.menu.store_recent_menu);
        handler = new Handler();
        waitingList = new ArrayList<>();
        //listview---------
        lv_recent = (ListView) v.findViewById(R.id.lv_recent);
        adapter = new StoreRecentAdapter(getActivity(),list);
        lv_recent.setAdapter(adapter);
        countDown();
        //------------------
        return v;
    }

    public synchronized void removeItem(int position){
        selected = position;
        list.remove(position);
        adapter.notifyDataSetChanged();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public synchronized void addItem(List<CustomerAppointInfo> infoList){
        waitingList.clear();
        for(int i=0;i<infoList.size();i++)
            waitingList.add(infoList.get(i));
        return;
    }
    public CustomerAppointInfo getItem(int position){
        if(list.size()>position)
            return list.get(position);
        else
            return null;
    }
    public int getSize(){
        return list.size();
    }
    public int getRequestIDupper(){
        return requestIDupper;
    }
    public void setRequestIDupper(int upper){
        requestIDupper = upper;
        return;
    }
    private void func_test(){
        CustomerAppointInfo test1 = new CustomerAppointInfo(1,"張庭維",10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test2 = new CustomerAppointInfo(2,"李承軒",1,R.drawable.ic_temp_user2);
        CustomerAppointInfo test3 = new CustomerAppointInfo(3,"陳奕先",90,R.drawable.ic_temp_user1);
        CustomerAppointInfo test4 = new CustomerAppointInfo(1,"張庭維",10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test5 = new CustomerAppointInfo(2,"李承軒",1,R.drawable.ic_temp_user2);
        CustomerAppointInfo test6 = new CustomerAppointInfo(3,"陳奕先",90,R.drawable.ic_temp_user1);
        CustomerAppointInfo test7 = new CustomerAppointInfo(1,"張庭維",10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test8 = new CustomerAppointInfo(2,"李承軒",1,R.drawable.ic_temp_user2);
        CustomerAppointInfo test9 = new CustomerAppointInfo(3,"陳奕先",90,R.drawable.ic_temp_user1);

        list.add(test1);
        list.add(test2);
        list.add(test3);
        list.add(test4);
        list.add(test5);
        list.add(test6);
        list.add(test7);
        list.add(test8);
        list.add(test9);
    }
}
