package com.example.yang.flashtable;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class StoreMainActivity extends AppCompatActivity {

    private ImageButton[] button;

    private static final int FRAG_COUNT = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;

    FragmentController fragmentController;
    private FragmentManager fragmentManager;

    private int current_stat=HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button = new ImageButton[FRAG_COUNT];
        setContentView(R.layout.store_main_activity);
        fragmentManager = getSupportFragmentManager();
        fragmentController = new FragmentController(fragmentManager);
        fragmentController.act(current_stat);
        init_bt_button();
    }

    private void init_bt_button(){
        button[HOME] = (ImageButton)findViewById(R.id.bt_home);
        button[RECENT] = (ImageButton)findViewById(R.id.bt_recent);
        button[APPOINT] = (ImageButton)findViewById(R.id.bt_appoint);
        button[MANAGE] = (ImageButton)findViewById(R.id.bt_manage);
        button[HOME].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = HOME;
                fragmentController.act(HOME);
                pressFeedBack();
            }
        });
        button[RECENT].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = RECENT;
                fragmentController.act(RECENT);
                pressFeedBack();
            }
        });
        button[APPOINT].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = APPOINT;
                pressFeedBack();
            }
        });
        button[MANAGE].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = MANAGE;
                pressFeedBack();
            }
        });
    }
    public void onStart(){
        super.onStart();
    }
    public void pressFeedBack(){
        for(int i=0;i<FRAG_COUNT;i++)
            button[i].setBackgroundColor(getResources().getColor(R.color.btBottomColor));
        button[current_stat].setBackgroundColor(getResources().getColor(R.color.btBottomPressColor));
    }
}
