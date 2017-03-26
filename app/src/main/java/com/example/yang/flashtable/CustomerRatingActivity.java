package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

/**
 * Created by CS on 2017/3/26.
 */

public class CustomerRatingActivity extends AppCompatActivity {

    RatingBar rb_rating;
    EditText et_content;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_rating_activity);

        initView();
        initData();
    }

    private void initView() {
        setTitle(getResources().getString(R.string.customer_rating_title));
        setTitleColor(R.color.white);
        setupActionBar();

        rb_rating = (RatingBar) findViewById(R.id.customer_rating_rb_rating);
        et_content = (EditText) findViewById(R.id.customer_rating_et_content);
        bt_submit = (Button) findViewById(R.id.customer_rating_bt_submit);
    }

    private void initData() {
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();

        return super.onOptionsItemSelected(item);
    }
}
