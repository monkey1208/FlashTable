package com.example.yang.flashtable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StoreRecentConfirmFragment extends Fragment {

    private TextView tv_test;
    private Button bt_click;
    public StoreRecentConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_recent_confirm_fragment, container, false);
        tv_test = (TextView)v.findViewById(R.id.tv_test);
        tv_test.setText(FragmentController.storeRecentFragment.getSelected().name);
        bt_click = (Button)v.findViewById(R.id.bt_click);
        bt_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.RECENT);
            }
        });
        return v;
    }
}
