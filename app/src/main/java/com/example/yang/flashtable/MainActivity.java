package com.example.yang.flashtable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

    }

    private void dialogEvent() {

        final View view_dialog = LayoutInflater.from(MainActivity.this).inflate(R.layout.alert_dialog, null);
        new AlertDialog.Builder(MainActivity.this)
                .setView(view_dialog)
                .show();
    }
}
