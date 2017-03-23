package com.example.yang.flashtable;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerReservationActivity extends AppCompatActivity {

    GifImageView gv_time;
    GifDrawable gif_drawable;
    TextView tv_status, tv_time;
    String seconds, no_response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_reservation_activity);

        initView();
        initData();
    }

    private void initView() {
        gv_time = (GifImageView) findViewById(R.id.customer_reservation_gv_time);
        gif_drawable = (GifDrawable) gv_time.getDrawable();
        tv_status = (TextView) findViewById(R.id.customer_reservation_tv_status);
        tv_time = (TextView) findViewById(R.id.customer_reservation_tv_time);
        seconds = getResources().getString(R.string.customer_reservation_seconds);
        no_response = getResources().getString(R.string.customer_reservation_no_response);
    }

    private void initData() {
        gif_drawable.setSpeed(2.0f);

        // Setup countdown
        int countdown_millis = 10000;
        String time = countdown_millis + seconds;
        tv_time.setText(time);
        new CountDownTimer(countdown_millis, 1000) {
            String time_left;
            public void onTick(long millis_left) {
                time_left = (millis_left / 1000) + seconds;
                tv_time.setText(time_left);
            }
            public void onFinish() {
                time_left = 0 + seconds;
                tv_time.setText(time_left);
                tv_status.setText(no_response);
            }
        }.start();

    }
}
