package com.example.yang.flashtable.customer;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.DialogEventListener;
import com.example.yang.flashtable.R;

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
 * Created by CS on 2017/5/1.
 */

public class CustomerNameActivity extends AppCompatActivity {

    SharedPreferences user;
    String username, userID;

    DialogBuilder dialog_builder;

    EditText et_name;
    Button bt_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_name_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();

        et_name = (EditText) findViewById(R.id.customer_profile_et_name);
        bt_submit = (Button) findViewById(R.id.customer_profile_bt_name_submit);
    }

    private void initData() {
        getUserInfo();

        dialog_builder = new DialogBuilder(this);
        et_name.setText(username);
        et_name.setSelection(et_name.getText().length());
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_name = et_name.getText().toString();
                if (!new_name.equals(""))
                    new APIUsername().execute(userID, new_name);
                else dialog_builder.dialogEvent(getResources().getString(R.string.customer_profile_name_empty),
                        "normal", null);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        setTitle(getResources().getString(R.string.customer_profile_change_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            DialogEventListener listener = new DialogEventListener() {
                @Override
                public void clickEvent(boolean ok, int status) {
                    if (ok) CustomerNameActivity.this.finish();
                }
            };
            dialog_builder.dialogEvent("填寫內容尚未送出，確定回到上一頁嗎？", "withCancel", listener);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    // API to change username
    class APIUsername extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerNameActivity.this);
        private String status = null, new_username;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            new_username = params[1];
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        getString(R.string.server_domain) + "api/modify_user");
                StringEntity se = new StringEntity("{ \"user_id\":\"" + params[0] +
                        "\", \"new_account\":\"" + params[1] + "\"}", HTTP.UTF_8);
                request.addHeader("Content-Type", "application/json");
                request.setEntity(se);
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
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
            progress_dialog.dismiss();
            if( status.equals("-2") )    dialog_builder.dialogEvent(getResources().getString(R.string.customer_profile_name_used), "normal", null);
            else if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                DialogEventListener listener = new DialogEventListener() {
                    @Override
                    public void clickEvent(boolean ok, int status) {
                        CustomerNameActivity.this.finish();
                    }
                };
                dialog_builder.dialogEvent(
                        "修改成功", "normal", listener);
                user.edit().putString("username", new_username).apply();
                finish();
            }
        }
    }

}
