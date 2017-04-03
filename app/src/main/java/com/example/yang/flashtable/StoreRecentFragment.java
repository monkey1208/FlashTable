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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StoreRecentFragment extends Fragment {

    private View v;
    private Handler handler;
    private List<CustomerAppointInfo> recentList;
    private ListView lv_recent;
    private View item_view;
    private int selected;
    private StoreRecentAdapter recentAdapter;
    private List<Integer> waitingList;
    private int size;
    private boolean active=true;
    public int test = 10;

    public StoreRecentFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //test-------------
        recentList = new ArrayList<>();
        func_test();
        //init-------------
        v =  inflater.inflate(R.layout.store_recent_fregment, container, false);
        handler = new Handler();
        waitingList = new ArrayList<>();
        //listview---------
        lv_recent = (ListView) v.findViewById(R.id.lv_recent);
        recentAdapter = new StoreRecentAdapter(getActivity(),recentList);
        lv_recent.setAdapter(recentAdapter);
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
}
