package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/24.
 */

public class CustomerCommentActivity extends AppCompatActivity {

    SharedPreferences user;
    String userID;
    DialogBuilder dialog_builder;

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

        dialog_builder = new DialogBuilder(this);
        lv_comments = (ListView) findViewById(R.id.customer_comment_lv_comments);
    }

    private void initData() {
        getUserInfo();

        comments = new ArrayList<>();
        getComments();
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
    }

    private void getComments() {
        comment_adapter = new CustomerCommentAdapter(CustomerCommentActivity.this, comments);
        lv_comments.setAdapter(comment_adapter);
        new APIComments().execute(userID);
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

    public void updateComments() {
        comment_adapter.notifyDataSetChanged();
        lv_comments.setAdapter(comment_adapter);
    }


    class APIComments extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCommentActivity.this);
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
                HttpGet request = new HttpGet("https://flash-table.herokuapp.com/api/user_comments?user_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {
                        HttpGet requestInfo = new HttpGet( "https://flash-table.herokuapp.com/api/comment_info?comment_id=" + responseJSON.getJSONObject(i).getString("comment_id") );
                        requestInfo.addHeader("Content-Type", "application/json");
                        JSONObject responseInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestInfo) ) );
                        status =  responseInfo.getString("status_code");
                        if( !status.equals("0") )   break;
                        String body = responseInfo.getString("body"), score = responseInfo.getString("score"), user_id = responseInfo.getString("user_id"), shop_id = responseInfo.getString("shop_id");
                        HttpGet requestUser = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id=" + user_id);
                        requestUser.addHeader("Content-Type", "application/json");
                        JSONObject responseUser = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestUser) ) );
                        status = responseUser.getString("status_code");
                        if( !status.equals("0") )   break;
                        String userAccount = responseUser.getString("account");
                        HttpGet requestShop = new HttpGet("https://flash-table.herokuapp.com/api/shop_info?shop_id=" + shop_id);
                        requestShop.addHeader("Content-Type", "application/json");
                        JSONObject responseShop = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestShop) ) );
                        status = responseShop.getString("status_code");
                        if( !status.equals("0") )   break;
                        String shopName = responseShop.getString("name");
                        comments.add( new CustomerCommentInfo( userAccount, shopName, body, Float.parseFloat(score) / 2, Integer.parseInt(user_id), Integer.parseInt(shop_id) ) );
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

}
