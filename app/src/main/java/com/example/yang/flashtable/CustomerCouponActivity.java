package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by CS on 2017/5/22.
 */

public class CustomerCouponActivity extends AppCompatActivity {

    FlashCouponInfo info;
    DialogBuilder dialog_builder;

    FloatingActionButton fab_back;
    ImageView iv_title;
    TextView tv_title, tv_points, tv_description, tv_tutorial;
    Button bt_exchange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_coupon_activity);

        initView();
        initData();

        super.onCreate(savedInstanceState);
    }

    private void initView() {
        fab_back = (FloatingActionButton) findViewById(R.id.customer_coupon_fab_back);

        iv_title = (ImageView) findViewById(R.id.customer_coupon_iv_coupon);
        tv_title = (TextView) findViewById(R.id.customer_coupon_tv_title);
        tv_points = (TextView) findViewById(R.id.customer_coupon_tv_points);
        tv_description = (TextView) findViewById(R.id.customer_coupon_tv_description);
        tv_tutorial = (TextView) findViewById(R.id.customer_coupon_tv_tutorial);

        bt_exchange = (Button) findViewById(R.id.customer_coupon_bt_exchange);
    }

    private void initData() {
        dialog_builder = new DialogBuilder(this);

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        info = (FlashCouponInfo) getIntent().getSerializableExtra("info");
        tv_title.setText(info.name);
        tv_points.setText(Integer.toString(info.flash_point));
        tv_description.setText(info.description);
        tv_tutorial.setText(info.tutorial);

        final DialogEventListener exchange_listener = new DialogEventListener() {
            @Override
            public void clickEvent(boolean ok, int status) {
                if (ok) redeemCoupon();

            }
        };
        bt_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_builder.dialogEvent("確認兌換嗎？", "withCancel", exchange_listener);
            }
        });
    }

    private void redeemCoupon() {
        dialog_builder = new DialogBuilder(this);
        dialog_builder.dialogEvent("恭喜成功兌換好禮！", "normal", null);
    }

}
