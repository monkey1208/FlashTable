package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class StoreMainActivity extends AppCompatActivity{

    private static ImageButton[] button;
    public static StoreInfo storeInfo;

    private static final int PRIM_FRAG = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;

    public static FragmentController fragmentController;
    //public static AlertDialogController alertDialogController = new AlertDialogController();
    private FragmentManager fragmentManager;

    private int current_stat=HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        //TODO: Get Store Info
        //TODO: start a thread getting updating request
        //arguments init temp
        storeInfo = new StoreInfo(bundle.getString("name"),bundle.getString("address"),bundle.getString("url"));
        getStoreInfo();
        button = new ImageButton[PRIM_FRAG];
        setContentView(R.layout.store_main_activity);
        fragmentManager = getSupportFragmentManager();
        fragmentController = new FragmentController(fragmentManager);
        fragmentController.act(RECENT);
        fragmentController.act(APPOINT);
        fragmentController.act(current_stat);
        init_bt_button();
        Log.d("MainActivity","done");

    }

    private void getStoreInfo() {
        SharedPreferences store = this.getSharedPreferences("USER", MODE_PRIVATE);
        String shop_id = store.getString("userID", "");
        storeInfo.name = store.getString("name", "");
        storeInfo.address = store.getString("address", "");
        storeInfo.url = store.getString("url","");
        storeInfo.id = shop_id;
    }

    private void init_bt_button(){
        button[HOME] = (ImageButton)findViewById(R.id.bt_home);
        button[RECENT] = (ImageButton)findViewById(R.id.bt_recent);
        button[APPOINT] = (ImageButton)findViewById(R.id.bt_appoint);
        button[MANAGE] = (ImageButton)findViewById(R.id.bt_manage);
        pressFeedBack();
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
                fragmentController.act(APPOINT);
                pressFeedBack();
            }
        });
        button[MANAGE].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_stat = MANAGE;
                fragmentController.act(MANAGE);
                pressFeedBack();
            }
        });
    }
    public void onStart(){
        super.onStart();
    }
    public void pressFeedBack(){
        for(int i=0;i<PRIM_FRAG;i++)
            button[i].setBackgroundColor(getResources().getColor(R.color.btBottomColor));
        button[current_stat].setBackgroundColor(getResources().getColor(R.color.btBottomPressColor));
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        //.super.onSaveInstanceState(savedInstanceState);
    }
    @Override
    protected void onDestroy(){
        Log.d("Destroy","Destroyed");
        super.onDestroy();
    }

    public static void recentUpdateNumber(int num){
        switch(num){
            case 0:
                button[RECENT].setImageResource(R.drawable.bt_recent);
                break;
            case 1:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_1);
                break;
            case 2:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_2);
                break;
            case 3:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_3);
                break;
            case 4:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_4);
                break;
            case 5:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_5);
                break;
            case 6:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_6);
                break;
            case 7:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_7);
                break;
            case 8:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_8);
                break;
            case 9:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_9);
                break;
            case 10:
                button[RECENT].setImageResource(R.drawable.recent_red_dot_10);
                break;
        }
        return;
    }
    public static void appointUpdateNumber(int num){
        switch(num){
            case 0:
                button[APPOINT].setImageResource(R.drawable.bt_appoint);
                break;
            case 1:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_1);
                break;
            case 2:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_2);
                break;
            case 3:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_3);
                break;
            case 4:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_4);
                break;
            case 5:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_5);
                break;
            case 6:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_6);
                break;
            case 7:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_7);
                break;
            case 8:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_8);
                break;
            case 9:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_9);
                break;
            case 10:
                button[APPOINT].setImageResource(R.drawable.appoint_red_dot_10);
                break;
        }
    }
}