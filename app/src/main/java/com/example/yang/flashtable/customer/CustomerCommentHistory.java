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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.adapter.CustomerCommentAdapter;
import com.example.yang.flashtable.customer.infos.CustomerCommentInfo;

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

public class CustomerCommentHistory extends AppCompatActivity {
    private String mode, shopID;

    SharedPreferences user;
    String userID;
    DialogBuilder dialog_builder;
    ListView lv_comments;
    ImageView bt_back;
    CustomerCommentAdapter comment_adapter;
    List<CustomerCommentInfo> comments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_comment_history_activity);
        initView();
        initData();
    }
    private void initView() {
        // setupActionBar();
        // setTitle(getResources().getString(R.string.customer_comments_my_title));

        dialog_builder = new DialogBuilder(this);
        lv_comments = (ListView) findViewById(R.id.customer_comment_history_lv);
        bt_back = (ImageView) findViewById(R.id.customer_comment_bt_back);
    }

    private void initData() {
        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mode = getIntent().getStringExtra("type");
        if (mode.equals("shop")) {
            shopID = getIntent().getStringExtra("shop_id");
            setTitle(getResources().getString(R.string.customer_comments_title));
        }
        getUserInfo();

        comments = new ArrayList<>();
        getComments();
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
    }

    private void getComments() {
        comment_adapter = new CustomerCommentAdapter(this, comments);
        lv_comments.setAdapter(comment_adapter);
        if (mode.equals("user")) new APIUserComments().execute(userID);
        else if (mode.equals("shop")) new APIShopComments().execute(shopID);
    }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void setupActionBar() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                // Show the Up button in the action bar.
                //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        Intent intent = new Intent(this, CustomerMainActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    public void updateComments() {
        comment_adapter.notifyDataSetChanged();
        lv_comments.setAdapter(comment_adapter);
    }

    // Trim rounded shape from image
    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {

        int targetWidth = 200;
        int targetHeight = 200;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight,Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }

    public void finish(View view){
        this.finish();
    }

    class APIShopComments extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCommentHistory.this);
        private String status = null;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/shop_comments?shop_id=" + params[0]
                        + "&verbose=1");
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {
                        JSONObject jsonObject1 = responseJSON.getJSONObject(i);

                        String body = jsonObject1.getString("body"), score = jsonObject1.getString("score"),
                                user_id = jsonObject1.getString("user_id"), shop_id = params[0];

                        String userAccount = jsonObject1.getString("user_account");
                        String userImage = jsonObject1.getString("user_picture_url");

                        Bitmap avatar = null;
                        if (!userImage.equals("")) {
                            URL url = new URL(userImage);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setDoInput(true);
                            connection.connect();
                            InputStream input = connection.getInputStream();
                            avatar = BitmapFactory.decodeStream(input);
                        }

                        HttpGet requestShop = new HttpGet(getString(R.string.server_domain)+"api/shop_info?shop_id=" + shop_id);
                        requestShop.addHeader("Content-Type", "application/json");
                        JSONObject responseShop = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestShop) ) );
                        status = responseShop.getString("status_code");
                        if( !status.equals("0") )   break;
                        String shopName = responseShop.getString("name");

                        CustomerCommentInfo comment = new CustomerCommentInfo( userAccount, shopName, body, Float.parseFloat(score) / 2, Integer.parseInt(user_id), Integer.parseInt(shop_id) );
                        if (avatar != null) comment.avatar = getRoundedShape(avatar);
                        comments.add( comment );
                    }
                }
            } catch (Exception e) {
                status = null;
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else    updateComments();
            progress_dialog.dismiss();
        }
    }

    class APIUserComments extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCommentHistory.this);
        private String status = null;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_comments?user_id=" + params[0]
                        + "&verbose=1");
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );

                Bitmap avatar = null;

                String user_id = params[0];
                HttpGet requestUser = new HttpGet(getString(R.string.server_domain)+"api/user_info?user_id=" + user_id);
                requestUser.addHeader("Content-Type", "application/json");
                JSONObject responseUser = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestUser) ) );
                status = responseUser.getString("status_code");
                String userAccount = responseUser.getString("account");
                String picture_url = responseUser.getString("picture_url");

                if (!picture_url.equals("")) {
                    URL url = new URL(picture_url);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    avatar = getRoundedShape(BitmapFactory.decodeStream(input));
                }

                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {
                        JSONObject jsonObject1 = responseJSON.getJSONObject(i);

                        String body = jsonObject1.getString("body"), score = jsonObject1.getString("score"),
                                shop_id = jsonObject1.getString("shop_id");

                        String shopName = jsonObject1.getString("shop_name");
                        CustomerCommentInfo comment = new CustomerCommentInfo( userAccount, shopName, body, Float.parseFloat(score) / 2, Integer.parseInt(user_id), Integer.parseInt(shop_id) );
                        if (avatar != null) comment.avatar = avatar;
                        comments.add( comment );
                    }


                }
            } catch (Exception e) {
                status = null;
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent("資料載入失敗，請重試", "normal", null);
            else    updateComments();
            progress_dialog.dismiss();
        }
    }
}
