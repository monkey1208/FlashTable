package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoreManageSuccessFragment extends Fragment {
    public StoreManageSuccessFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.store_manage_success_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_success_tb_toolbar);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });
        setValues(v);
        return v;
    }

    void setValues(View v){
        TextView tv_rate = (TextView)v.findViewById(R.id.store_manage_success_tv_rate);
        TextView tv_total = (TextView)v.findViewById(R.id.store_manage_success_tv_amount);
        TextView tv_fail = (TextView)v.findViewById(R.id.store_manage_success_tv_fail);
        TextView tv_success = (TextView)v.findViewById(R.id.store_manage_success_tv_success);
        //Get values from server
        StoreInfo storeInfo = StoreMainActivity.storeInfo;
        double rate = ((double)storeInfo.successAppointment/(double)storeInfo.totalAppointment)*(double)100;
        tv_rate.setText(Double.toString(rate));
        tv_total.setText(Integer.toString(storeInfo.totalAppointment));
        tv_fail.setText(Integer.toString(storeInfo.totalAppointment-storeInfo.successAppointment));
        tv_success.setText(Integer.toString(storeInfo.successAppointment));
    }

}
