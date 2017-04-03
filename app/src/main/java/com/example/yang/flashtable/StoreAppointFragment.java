package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class StoreAppointFragment extends ListFragment {
    public static List<ReservationInfo> appointList = new ArrayList<>();
    public static StoreAppointAdapter adapter;
    public  StoreAppointFragment () {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_appoint_fragment, container, false);
        appointList = get_reservation_info();
        adapter = new StoreAppointAdapter(getContext(), appointList);
        setListAdapter(adapter);
        Toolbar bar = (Toolbar)v.findViewById(R.id.shop_toolbar);
        bar.setPadding(0,getStatusBarHeight(), 0,0);
        bar.inflateMenu(R.menu.shop_reservation_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_RECORD);
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        return v;
    }

    private  List<ReservationInfo> get_reservation_info(){
        List<ReservationInfo> list = new ArrayList<>();
        ReservationInfo tmp = new ReservationInfo("Chen",10,System.currentTimeMillis()-1000);
        list.add(tmp);
        tmp = new ReservationInfo("Yi",100,System.currentTimeMillis()-2000);
        list.add(tmp);
        tmp = new ReservationInfo("Shan",1,System.currentTimeMillis()-3000);
        list.add(tmp);
        return list;
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
