package com.example.yang.flashtable;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;


public class StoreRecentCountDownThread implements Runnable{

    private View v;
    private TextView tv_countdown;

    public StoreRecentCountDownThread(View v){
        this.v = v;
    }

    @Override
    public void run() {
        tv_countdown = (TextView)v.findViewById(R.id.tv_countdown);
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_countdown.setText(millisUntilFinished / 1000+"s");
            }
            public void onFinish() {
                tv_countdown.setText("done!");
            }
        }.start();
    }
}
