package com.example.yang.flashtable;

import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class StoreMain extends AppCompatActivity {

    private ImageButton button_home;
    private ImageButton button_recent;
    private ImageButton button_appoint;
    private ImageButton button_manage;

    private static final int FRAG_COUNT = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;

    FragmentController fragmentController;
    private Fragment[] fragment;
    private FragmentManager fragmentManager;

    private int current_stat=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_main_layout);
        fragmentManager = getSupportFragmentManager();
        fragmentController = new FragmentController(fragmentManager);
        init_bot_button();
    }
    private void init_bot_button(){
        button_home = (ImageButton)findViewById(R.id.but_home);
        button_recent = (ImageButton)findViewById(R.id.but_recent);
        button_appoint = (ImageButton)findViewById(R.id.but_appoint);
        button_manage = (ImageButton)findViewById(R.id.but_manage);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = HOME;
                fragmentController.act(HOME);
            }
        });
        button_recent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = RECENT;
                fragmentController.act(RECENT);
            }
        });
        button_appoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = APPOINT;
            }
        });
        button_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = MANAGE;
            }
        });
    }
    public void onStart(){
        super.onStart();

    }
}
