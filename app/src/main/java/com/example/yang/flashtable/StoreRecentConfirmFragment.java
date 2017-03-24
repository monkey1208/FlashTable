package com.example.yang.flashtable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class StoreRecentConfirmFragment extends Fragment {

    private TextView tv_test;
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
        return v;
    }
}
