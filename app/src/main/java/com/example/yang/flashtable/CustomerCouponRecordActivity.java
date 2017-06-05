package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by CS on 2017/5/29.
 */

public class CustomerCouponRecordActivity extends AppCompatActivity {
    SharedPreferences user;
    String userID, username;

    TextView tv_points;
    FloatingActionButton fab_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_coupon_record_activity);

        initView();
        initData();

        super.onCreate(savedInstanceState);
    }

    private void initView() {
        // Set up header view
        tv_points = (TextView) findViewById(R.id.customer_coupon_tv_points);
        fab_back = (FloatingActionButton) findViewById(R.id.customer_coupon_fab_records_back);

        // Set up fragments
        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of("獲得紀錄", CustomerCouponRecordFragment.class));
        pages.add(FragmentPagerItem.of("兌換紀錄", CustomerCouponRecordFragment.class));

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), pages);

        ViewPager viewPager = (ViewPager) findViewById(R.id.customer_coupon_vp_records);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.customer_coupon_vp_tabs);
        viewPagerTab.setViewPager(viewPager);

    }

    private void initData() {
        getUserInfo();
        new ApiPoints().execute(userID);

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    private class ApiPoints extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCouponRecordActivity.this);
        private String status;
        private int points = 0;

        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String ...value) {
            NameValuePair param = new BasicNameValuePair("user_id", value[0]);
            HttpGet httpGet = new HttpGet(getString(R.string.server_domain) + "/api/user_info?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);

                JSONObject responseJSON = new JSONObject(json);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    points = Integer.parseInt(responseJSON.getString("flash_point"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            progress_dialog.dismiss();

            //get points
            tv_points.setText(Integer.toString(points));
        }
    }
}
