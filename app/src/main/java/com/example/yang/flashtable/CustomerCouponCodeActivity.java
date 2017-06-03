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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
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
 * Created by CS on 2017/5/29.
 */

public class CustomerCouponCodeActivity extends AppCompatActivity {
    SharedPreferences user;
    String userID, username;

    SerializableCouponInfo info;
    DialogBuilder dialog_builder;

    FloatingActionButton fab_back;
    ImageView iv_title;
    TextView tv_title, tv_points, tv_description, tv_tutorial, tv_code;

    String code_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_coupon_code_activity);

        initView();
        initData();

        super.onCreate(savedInstanceState);
    }

    private void initView() {
        fab_back = (FloatingActionButton) findViewById(R.id.customer_code_fab_back);

        iv_title = (ImageView) findViewById(R.id.customer_code_iv_coupon);
        tv_title = (TextView) findViewById(R.id.customer_code_tv_title);
        tv_points = (TextView) findViewById(R.id.customer_code_tv_points);
        tv_description = (TextView) findViewById(R.id.customer_code_tv_description);
        tv_tutorial = (TextView) findViewById(R.id.customer_code_tv_tutorial);
        tv_code = (TextView) findViewById(R.id.customer_code_tv_code);
    }

    private void initData() {
        getUserInfo();
        dialog_builder = new DialogBuilder(this);

        code_id = getIntent().getStringExtra("code_id");

        fab_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        new ApiCoupon().execute(code_id);
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    private class ApiCoupon extends AsyncTask<String, Void, Void> {

        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCouponCodeActivity.this);
        private String status;
        private Bitmap image;
        private String name, code, description, flash_point, picture_url_large, tutorial;

        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String ...value) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        "https://flash-table.herokuapp.com/api/code_info?code_id=" + value[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {

                    name = responseJSON.getString("name");
                    description = responseJSON.getString("description");
                    flash_point = responseJSON.getString("flash_point");
                    picture_url_large = responseJSON.getString("picture_url_large");
                    tutorial = responseJSON.getString("tutorial");
                    code = responseJSON.getString("code");

                    URL url = new URL(picture_url_large);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    image = BitmapFactory.decodeStream(input);
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            catch (Exception e)
            {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            }
            finally
            {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            progress_dialog.dismiss();
            iv_title.setImageBitmap(image);
            tv_title.setText(name);
            tv_code.setText(code);
            tv_description.setText(description);
            tv_tutorial.setText(tutorial);
            tv_points.setText(flash_point);
        }
    }

}
