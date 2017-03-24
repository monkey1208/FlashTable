package com.example.yang.flashtable;

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
        bar.setNavigationIcon(R.drawable.ic_back);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });
        setValues(v);
        return v;
    }

    void setValues(View v){
        TextView tv1 = (TextView)v.findViewById(R.id.store_manage_success_tv_rate);
        TextView tv2 = (TextView)v.findViewById(R.id.store_manage_success_tv_amount);
        TextView tv3 = (TextView)v.findViewById(R.id.store_manage_success_tv_fail);
        TextView tv4 = (TextView)v.findViewById(R.id.store_manage_success_tv_success);
        //Get values from server
        tv1.setText("75");
        tv2.setText("200");
        tv3.setText("50");
        tv4.setText("150");
    }

}
