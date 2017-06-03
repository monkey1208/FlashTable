package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by CS on 2017/5/22.
 */

public class CustomerCouponActivity extends AppCompatActivity {

    SharedPreferences user;
    String userID, username;

    SerializableCouponInfo info;
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
        getUserInfo();
        dialog_builder = new DialogBuilder(this);

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        info = (SerializableCouponInfo) getIntent().getExtras().getSerializable("info");
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

        new ApiImage().execute(info.picture_url_large);
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    private void redeemCoupon() {
        new ApiRedeemCoupon().execute(userID, String.valueOf(info.coupon_id));
    }

    private class ApiImage extends AsyncTask<String, Void, Void> {

        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCouponActivity.this);
        private Bitmap image;

        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String ...value) {
            try {
                URL url = new URL(value[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                image = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            progress_dialog.dismiss();
            iv_title.setImageBitmap(image);
        }
    }

    class ApiRedeemCoupon extends AsyncTask<String, Void, String> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCouponActivity.this);
        private String status;

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            status = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        "https://"+getString(R.string.server_domain)+"/api/redeem_coupon");
                StringEntity se = new StringEntity("{ \"user_id\":\"" + params[0] +
                        "\", \"coupon_id\":\"" + params[1] + "\"}", HTTP.UTF_8);
                request.addHeader("Content-Type", "application/json");
                request.setEntity(se);
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);

                // invalid verification code
                status = responseJSON.getString("status_code");
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }

            return status;
        }
        @Override
        protected void onPostExecute(String status) {

            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }
            dialog_builder = new DialogBuilder(CustomerCouponActivity.this);

            if (status.equals("0")) {
                DialogEventListener success_listener = new DialogEventListener() {
                    @Override
                    public void clickEvent(boolean ok, int status) {
                        if (ok) {
                            Intent intent = new Intent(CustomerCouponActivity.this, CustomerCouponRecordActivity.class);
                            startActivity(intent);
                        }
                    }
                };
                dialog_builder.dialogEvent("恭喜成功兌換好禮！是否前往歷史紀錄查看好禮序號？", "withCancel", success_listener);
            }
            else if (status.equals("-3"))
                dialog_builder.dialogEvent("您的FLASH Points點數不足", "normal", null);
            else if (status.equals("-4"))
                dialog_builder.dialogEvent("此優惠已搶購一空，去看看其他優惠吧！", "normal", null);
            else    dialog_builder.dialogEvent("網路連線錯誤", "normal", null);

        }
    }
}
