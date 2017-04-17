package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by CS on 2017/4/15.
 */

public class CustomerSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.customer_search_activity);
        setupActionBar();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.View view = getLayoutInflater().inflate(R.layout.actionbar_customized_home, null);
            //ActionBar.LayoutParams layout = new ActionBar.LayoutParams(ActionBar.LayoutParams.FILL_PARENT, ActionBar.LayoutParams.FILL_PARENT);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setCustomView(R.layout.customer_search_bar);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
