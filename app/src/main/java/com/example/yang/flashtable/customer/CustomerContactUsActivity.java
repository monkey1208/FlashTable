package com.example.yang.flashtable.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.yang.flashtable.R;

/**
 * Created by CS on 2017/5/29.
 */

public class CustomerContactUsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_contact_activity);

        ImageView bt_back = (ImageView) findViewById(R.id.customer_contact_bt_back);
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayout ll_email = (LinearLayout) findViewById(R.id.customer_contact_ll_email);
        ll_email.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(CustomerContactUsActivity.this, "電子信箱已複製到剪貼簿", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FlashEmail", getResources().getString(R.string.service_email));
                clipboard.setPrimaryClip(clip);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
                return false;
            }
        });

        LinearLayout ll_facebook = (LinearLayout) findViewById(R.id.customer_contact_ll_facebook);
        ll_facebook.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(CustomerContactUsActivity.this, "FB連結已複製到剪貼簿", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FlashFB", getResources().getString(R.string.service_facebook));
                clipboard.setPrimaryClip(clip);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
                return false;
            }
        });
    }

}
