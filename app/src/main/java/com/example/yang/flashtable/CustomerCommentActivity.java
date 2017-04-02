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

    String user_name, shop_name;

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
        new CustomerAPIGetComments().execute(userID);
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

    // APIs
    class CustomerAPIGetComments extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCommentActivity.this);
        private String status = null;
        private int size = -1;

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray content = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        "https://flash-table.herokuapp.com/api/user_comments?user_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONArray responseJSON = new JSONArray(httpResponse);
                status = responseJSON.getJSONObject(0).getString("status_code");
                if (status.equals("0")) {
                    size = responseJSON.getJSONObject(0).getInt("size");
                    content = responseJSON;
                }
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return content;
        }

        @Override
        protected void onPostExecute(JSONArray content) {

            if (status.equals("-1") || status == null || content == null || size == -1)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else if (size > 0) {
                try {
                    for (int i = 1; i <= size; i++) {
                        String comment_id = content.getJSONObject(i).getString("comment_id");
                        new CustomerAPIGetCommentContent().execute(comment_id, Integer.toString(i-1));
                    }

                } catch (Exception e) {
                    Log.d("GetCode", "Request exception:" + e.getMessage());
                }
            }

            if (progress_dialog.isShowing())
                progress_dialog.dismiss();
        }
    }

    class CustomerAPIGetCommentContent extends AsyncTask<String, Void, CustomerCommentInfo> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerCommentActivity.this);
        private String status = null;
        private int index;

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected CustomerCommentInfo doInBackground(String... params) {
            CustomerCommentInfo content = null;
            index = Integer.parseInt(params[1]);
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        "https://flash-table.herokuapp.com/api/comment_info?comment_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    content = new CustomerCommentInfo(null,
                            null,
                            responseJSON.getString("body"),
                            (float) responseJSON.getInt("score") / 2,
                            responseJSON.getInt("user_id"), responseJSON.getInt("shop_id"));
                }
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return content;
        }
        @Override
        protected void onPostExecute(CustomerCommentInfo _content) {

            if(status.equals("-1") || _content == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                try {
                    new CustomerAPIGetCustomer().execute(Integer.toString(_content.userID), Integer.toString(index));
                    new CustomerAPIGetShop().execute(Integer.toString(_content.shopID), Integer.toString(index));

                    comments.add(_content);

                    // TODO: Change to set once?
                    comment_adapter = new CustomerCommentAdapter(CustomerCommentActivity.this, comments);
                    lv_comments.setAdapter(comment_adapter);
                } catch (Exception e) {
                    Log.e("GET_COMMENT", e.toString());
                }
            }

            if (progress_dialog.isShowing())
                progress_dialog.dismiss();
        }
    }

    class CustomerAPIGetCustomer extends AsyncTask<String, Void, String> {
        private String status = null;
        private int index;

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            index = Integer.parseInt(params[1]);
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        "https://flash-table.herokuapp.com/api/user_info?user_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    content = responseJSON.getString("account");
                }
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return content;
        }
        @Override
        protected void onPostExecute(String _content) {

            if(status.equals("-1") || _content == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                comments.get(index).user = _content;
            }
        }
    }

    class CustomerAPIGetShop extends AsyncTask<String, Void, String> {
        private String status = null;
        private int index;

        @Override
        protected String doInBackground(String... params) {
            String content = null;
            index = Integer.parseInt(params[1]);
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        "https://flash-table.herokuapp.com/api/shop_info?shop_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    content = responseJSON.getString("name");
                }
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return content;
        }
        @Override
        protected void onPostExecute(String _content) {

            if(status.equals("-1") || _content == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                comments.get(index).shop = _content;
                updateComments();
            }
        }
    }

}
