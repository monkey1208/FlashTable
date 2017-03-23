package com.example.yang.flashtable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeFragment extends Fragment {

    private TextView text_home;
    private ImageButton but_active;
    private String screen_msg;
    private View v;
    private int count=0;
    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        screen_msg = "Price Off";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_home_layout, container, false);
        text_home = (TextView)v.findViewById(R.id.frag_home);
        text_home.setText(screen_msg);
        but_active = (ImageButton)v.findViewById(R.id.but_active);
        but_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screen_msg = "No Price Off!!!"+count;
                count++;
                text_home.setText(screen_msg);
            }
        });
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }
}
