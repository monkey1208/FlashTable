package com.example.yang.flashtable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class StoreRecentFragment extends Fragment {

    private View v;
    private List<CustomerAppointInfo> recentList;
    private ListView lv_recent;
    public OnRecentFragmentListener listener;

    public StoreRecentFragment() {}

    public interface OnRecentFragmentListener {
        public void onRecentItemSelected(CustomerAppointInfo customerAppointInfo);
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
        func_test();
        //init-------------
        v =  inflater.inflate(R.layout.store_recent_fregment, container, false);
        lv_recent = (ListView) v.findViewById(R.id.lv_recent);
        RecentAdapter recentAdapter = new RecentAdapter(getActivity(),recentList);
        lv_recent.setAdapter(recentAdapter);
        lv_recent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listener.onRecentItemSelected(recentList.get(position));
            }
        });
        return v;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        listener = (OnRecentFragmentListener) activity;
    }
    private void func_test(){
        CustomerAppointInfo test1 = new CustomerAppointInfo("張庭維",100,10,R.drawable.ic_temp_user1);
        CustomerAppointInfo test2 = new CustomerAppointInfo("李承軒",0,1,R.drawable.ic_temp_user2);
        recentList.add(test1);
        recentList.add(test2);
    }
}
