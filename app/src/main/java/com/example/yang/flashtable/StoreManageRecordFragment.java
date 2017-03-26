package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class StoreManageRecordFragment extends ListFragment {
    public StoreManageRecordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.store_manage_record_fragment, container, false);

        List<ReservationInfo> list = new ArrayList<>();
        setValues(list);
        StoreManageRecordAdapter adapter = new StoreManageRecordAdapter(getActivity(), list);
        setListAdapter(adapter);

        ListView lv =(ListView) v.findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_record_tb_toolbar);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });

        return v;

    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        //To each record detail
        Toast.makeText(getContext(), "Jump to page ", Toast.LENGTH_SHORT).show();
    }

    private void setValues(List<ReservationInfo> list){
        ReservationInfo tmp = new ReservationInfo("Cindy Chen");
        list.add(tmp);
        tmp = new ReservationInfo("Bing Bing");
        list.add(tmp);
    }
}
