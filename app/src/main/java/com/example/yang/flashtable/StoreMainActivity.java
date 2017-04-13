package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class StoreMainActivity extends AppCompatActivity{

    private ImageButton[] button;
    public static StoreInfo storeInfo;

    private static final int PRIM_FRAG = 4;
    private static final int HOME = 0;
    private static final int RECENT = 1;
    private static final int APPOINT = 2;
    private static final int MANAGE = 3;

    public static FragmentController fragmentController;
    //public static AlertDialogController alertDialogController = new AlertDialogController();
    public static APIHandler apiHandler;
    private FragmentManager fragmentManager;

    private int current_stat=HOME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: Get Store Info
        //TODO: start a thread getting updating request
        //arguments init temp
        apiHandler = new APIHandler(getApplicationContext());
        storeInfo = new StoreInfo("西堤牛排 南港店","台北市南港區忠孝東路七段369號C1棟(CITYLINK南港店)");
        button = new ImageButton[PRIM_FRAG];
        setContentView(R.layout.store_main_activity);
        fragmentManager = getSupportFragmentManager();
        fragmentController = new FragmentController(fragmentManager);
        fragmentController.act(RECENT);
        fragmentController.act(APPOINT);
        fragmentController.act(current_stat);
        init_bt_button();
        getStoreInfo();
    }

    private void getStoreInfo() {
        SharedPreferences store = this.getSharedPreferences("USER", MODE_PRIVATE);
        String shop_id = store.getString("userID", "");
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

}