package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class StoreMainActivity extends AppCompatActivity{

    private ImageButton button_home;
    private ImageButton button_recent;
    private ImageButton button_appoint;
    private ImageButton button_manage;

    private static final int FRAG_COUNT = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;

    public static FragmentController fragmentController;
    private Fragment[] fragment;
    private FragmentManager fragmentManager;

    private int current_stat=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_main_activity);
        fragmentManager = getSupportFragmentManager();
        fragmentController = new FragmentController(fragmentManager);
        init_bot_button();
    }
    private void init_bot_button(){
        button_home = (ImageButton)findViewById(R.id.bt_home);
        button_recent = (ImageButton)findViewById(R.id.bt_recent);
        button_appoint = (ImageButton)findViewById(R.id.bt_appoint);
        button_manage = (ImageButton)findViewById(R.id.bt_manage);
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
                fragmentController.act(APPOINT);
            }
        });
        button_manage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = MANAGE;
                fragmentController.act(MANAGE);
            }
        });
    }
    public void onStart(){
        super.onStart();

    }
}
