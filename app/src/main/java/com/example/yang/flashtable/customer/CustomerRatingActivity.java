package com.example.yang.flashtable.customer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.DialogEventListener;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

/**
 * Created by CS on 2017/3/26.
 */

public class CustomerRatingActivity extends AppCompatActivity {

    SharedPreferences user;
    private String shop_id, user_id, record_id;

    DialogBuilder dialog_builder;

    TextView tv_shop, tv_location;
    RatingBar rb_rating;
    EditText et_content;
    Button bt_submit;
    ImageView iv_shop;

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

        dialog_builder = new DialogBuilder(this);

        tv_shop = (TextView) findViewById(R.id.customer_rating_tv_shop);
        tv_location = (TextView) findViewById(R.id.customer_rating_tv_location);
        rb_rating = (RatingBar) findViewById(R.id.customer_rating_rb_rating);
        et_content = (EditText) findViewById(R.id.customer_rating_et_content);
        bt_submit = (Button) findViewById(R.id.customer_rating_bt_submit);
        iv_shop = (ImageView) findViewById(R.id.customer_rating_iv_shop);
    }

    private void initData() {
        getUserInfo();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tv_shop.setText(extras.getString("shop"));
            tv_location.setText(extras.getString("shop_location"));
            shop_id = extras.getString("shop_id");
            record_id = String.valueOf(extras.getInt("record_id", -1));
        }

        new ApiShopImage().execute();
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newComment();
            }
        });
    }

    private void newComment() {
        if (rb_rating.getRating() == 0f) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_comments_error_no_rating),
                    "normal", null);
        }
        else if (et_content.getText().length() > 100) {
            dialog_builder.dialogEvent("字數已超過限制100字，請編輯後再重新送出！",
                    "normal", null);
        }
        else {
            String content = et_content.getText().toString();
            content = content.replace("\n", "%0D%0A");
            new CustomerAPINewComment().execute(content,
                    Integer.toString((int) (rb_rating.getRating() * 2)),
                    user_id, shop_id);
        }
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        user_id = user.getString("userID", "");
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
        closeActivity();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        closeActivity();
    }

    private void closeActivity() {
        if (rb_rating.getRating() != 0 || !et_content.getText().toString().equals("")) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_comments_error_not_sent), "withCancel",
                    new DialogEventListener() {
                        @Override
                        public void clickEvent(boolean ok, int status) {
                            if (ok) {
                                CustomerRatingActivity.this.finish();
                            }
                        }
                    });
        }
        else{
            finish();
        }
    }

    class ApiShopImage extends AsyncTask<String, Void, Void> {
        SqlHandler sqlHandler = new SqlHandler(CustomerRatingActivity.this);
        Bitmap image = null;

        @Override
        protected Void doInBackground(String... params) {
            CustomerRestaurantInfo info = sqlHandler.getDetail(Integer.valueOf(shop_id));
            image = info.getImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {
            iv_shop.setImageBitmap(image);
        }
    }

    class CustomerAPINewComment extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerRatingActivity.this);
        private String status = null;

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        getString(R.string.server_domain)+"api/new_comment");
                StringEntity se;
                if (!record_id.equals("-1")) {
                    se = new StringEntity("{ \"body\":\"" + params[0] +
                            "\", \"score\":\"" + params[1] +
                            "\", \"user_id\":\"" + params[2] +
                            "\", \"shop_id\":\"" + params[3] +
                            "\", \"record_id\":\"" + record_id + "\"}", HTTP.UTF_8);
                }
                else {
                    se = new StringEntity("{ \"body\":\"" + params[0] +
                            "\", \"score\":\"" + params[1] +
                            "\", \"user_id\":\"" + params[2] +
                            "\", \"shop_id\":\"" + params[3] +"\"}", HTTP.UTF_8);
                }
                request.addHeader("Content-Type", "application/json");
                request.setEntity(se);
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);

                status = responseJSON.getString("status_code");

            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {

            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }

            if( status == null )
                dialog_builder.dialogEvent(
                        "資料送出失敗，請重試", "normal", null);
            else {
                DialogEventListener listener = new DialogEventListener() {
                    @Override
                    public void clickEvent(boolean ok, int status) {
                        Intent intent = new Intent(CustomerRatingActivity.this, CustomerMainActivity.class);
                        startActivity(intent);
                        CustomerRatingActivity.this.finish();
                    }
                };
                dialog_builder.dialogEvent(
                        "發送成功", "normal", listener);

            }
        }
    }
}
