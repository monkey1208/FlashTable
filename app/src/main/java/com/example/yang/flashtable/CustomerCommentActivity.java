package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/24.
 */

public class CustomerCommentActivity extends AppCompatActivity {

    ListView lv_comments;
    CustomerCommentAdapter comment_adapter;
    List<CustomerCommentInfo> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_comment_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();
        setTitle(getResources().getString(R.string.customer_comments_my_title));

        lv_comments = (ListView) findViewById(R.id.customer_comment_lv_comments);
    }

    private void initData() {
        comments = new ArrayList<>();
        comments.add(new CustomerCommentInfo("Elise", "McDonald's", "BAD", 1));
        comments.add(new CustomerCommentInfo("der3318", "McDonald's", "Great!! I can eat every day.", 5));
        comments.add(new CustomerCommentInfo("wariard", "KFC", "", 4.5f));
        comment_adapter = new CustomerCommentAdapter(this, comments);
        lv_comments.setAdapter(comment_adapter);

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
