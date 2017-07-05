package com.example.yang.flashtable.customer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.example.yang.flashtable.CustomerCommentHistory;
import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.adapter.CustomerCommentAdapter;
import com.example.yang.flashtable.customer.infos.CustomerCommentInfo;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/24.
 */

public class CustomerCommentActivity extends AppCompatActivity {

    SharedPreferences user;
    String userID;
    DialogBuilder dialog_builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_comment_activity);

        initView();
    }

    private void initView() {
        //setupActionBar();
        //setTitle(getResources().getString(R.string.customer_comments_my_title));

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("未評論", CustomerCommentTabFragment.class));
        pages.add(FragmentPagerItem.of("已評論", CustomerCommentTabFragment.class));
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        ViewPager viewPager = (ViewPager) findViewById(R.id.customer_comment_vp_content);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.customer_comment_vp_tabs);
        viewPagerTab.setViewPager(viewPager);

        dialog_builder = new DialogBuilder(this);
    }

    public void finish(View view){
        this.finish();
    }

    public void goToHistory(View view){
        Intent intent = new Intent(this, CustomerCommentHistory.class);
        startActivity(intent);
    }

}
