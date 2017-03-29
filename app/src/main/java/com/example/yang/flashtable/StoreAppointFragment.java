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
        List<ReservationInfo> list = get_reservation_info();
        StoreAppointAdapter adapter = new StoreAppointAdapter(getContext(), list);
        setListAdapter(adapter);
        Toolbar bar = (Toolbar)v.findViewById(R.id.shop_toolbar);
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
        ReservationInfo tmp = new ReservationInfo("Chen",10);
        list.add(tmp);
        tmp = new ReservationInfo("Yi",100);
        list.add(tmp);
        tmp = new ReservationInfo("Shan",1);
        list.add(tmp);
        return list;
    }
}
