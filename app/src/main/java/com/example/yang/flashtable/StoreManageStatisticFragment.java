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

import java.util.ArrayList;
import java.util.List;

public class StoreManageStatisticFragment extends ListFragment {

    public StoreManageStatisticFragment() {
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
        View v = inflater.inflate(R.layout.store_manage_statistic_fragment, container, false);
        List<StoreDiscountInfo> list = new ArrayList<>();
        getValues(list);
        StoreManageStatisticAdapter adapter = new StoreManageStatisticAdapter(getActivity(), list);
        setListAdapter(adapter);

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_statistic_tb_toolbar);
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

    private void getValues(List<StoreDiscountInfo> list){
        //Set discount detail
        //list should be sorted by count!!!!!!
        StoreDiscountInfo tmp = new StoreDiscountInfo("9折", "", 30, false);
        list.add(tmp);
        tmp = new StoreDiscountInfo("85折", "無", 15, false);
        list.add(tmp);
        tmp = new StoreDiscountInfo("77折", "無", 7, false);
        list.add(tmp);
        tmp = new StoreDiscountInfo("(暫無優惠)", "", 6, true);
        list.add(tmp);
    }
}
