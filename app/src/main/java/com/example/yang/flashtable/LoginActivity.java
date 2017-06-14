package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CS on 2017/3/27.
 */

public class LoginActivity extends AppCompatActivity {

    private String preference_account, preference_password, preference_username;

    ViewFlipper vf_flipper;
    DialogBuilder dialog_builder;
    private StoreInfo storeInfo = new StoreInfo();

    // Main
    Button bt_as_customer, bt_as_store;

    // Customer
    LinearLayout customer_ll_back;
    EditText customer_et_account, customer_et_password;
    Button customer_bt_submit, customer_bt_register;

    // Store
    LinearLayout store_ll_back;
    EditText store_et_account, store_et_password;
    Button store_bt_submit, store_bt_apply;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);

        String type = checkPreference();
        if(type != null) {
            if (type.equals("customer"))
                startCustomer();
            else if (type.equals("store"))
                startStore();
        }

        initView();
        initData();
    }

    private void initView() {
        vf_flipper = (ViewFlipper) findViewById(R.id.login_vf_flipper);
        dialog_builder = new DialogBuilder(this);

        // Main
        bt_as_customer = (Button) findViewById(R.id.login_bt_as_customer);
        bt_as_store = (Button) findViewById(R.id.login_bt_as_store);

        // Customer
        customer_ll_back = (LinearLayout) findViewById(R.id.login_customer_ll_back);
        customer_et_account = (EditText) findViewById(R.id.login_customer_et_account);
        customer_et_password = (EditText) findViewById(R.id.login_customer_et_password);
        customer_bt_submit = (Button) findViewById(R.id.login_customer_bt_submit);
        customer_bt_register = (Button) findViewById(R.id.login_customer_bt_register);

        // Store
        store_ll_back = (LinearLayout) findViewById(R.id.login_store_ll_back);
        store_et_account = (EditText) findViewById(R.id.login_store_et_account);
        store_et_password = (EditText) findViewById(R.id.login_store_et_password);
        store_bt_submit = (Button) findViewById(R.id.login_store_bt_submit);
        store_bt_apply = (Button) findViewById(R.id.login_store_bt_apply);
    }
    private void initData() {

        // Main
        bt_as_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { vf_flipper.setDisplayedChild(1); }
        });
        bt_as_store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { vf_flipper.setDisplayedChild(2); }
        });

        // Customer
        customer_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { vf_flipper.setDisplayedChild(0); }
        });
        customer_bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerLogin();
            }
        });
        customer_bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, CustomerRegisterActivity.class);
                startActivity(intent);
            }
        });

        // Store
        store_ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { vf_flipper.setDisplayedChild(0); }
        });
        store_bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeLogin();
            }
        });
        store_bt_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, StoreRegisterActivity.class);
                startActivity(intent);
            }
        });
    }


    private boolean isAccountValid(String account) {
        return !account.equals("");
    }

    private boolean isCustomerAccountValid(String cellphone) {
        Pattern pattern = Pattern.compile("\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|\n" +
                "2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|\n" +
                "4[987654310]|3[9643210]|2[70]|7|1)\n" +
                "\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*(\\d{1,2})$");
        Matcher matcher = pattern.matcher(cellphone);

        return (!matcher.find() && !cellphone.equals("") && cellphone.length() == 10);
    }

    private boolean isPasswordValid(String password) {
        // Password valid: 0-9 and a-z

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);

        return (!matcher.find() && !password.equals(""));
    }

    private void startStore() {
        Bundle bundle = new Bundle();
        bundle.putString("name",storeInfo.name);
        bundle.putString("address",storeInfo.address);
        bundle.putString("url",storeInfo.url);
        Intent intent = new Intent(LoginActivity.this, StoreMainActivity.class);
        intent.putExtras(bundle);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    private void storeLogin() {
        String account = store_et_account.getText().toString();
        String password = store_et_password.getText().toString();

        int fail = 0;

        if (!isAccountValid(account)) {
            fail = 1;
        } else if (!isPasswordValid(password)) {
            fail = 2;
        }

        if (fail != 0) {
            dialog_builder.dialogEvent(getResources().getString(R.string.login_error_typo), "normal", null);
        } else {
            preference_account = account;
            preference_password = password;
            new StoreAPILogin().execute(account, password);
        }
    }

    private void startCustomer() {
        Intent intent = new Intent(LoginActivity.this, CustomerLoadingActivity.class);
        LoginActivity.this.startActivity(intent);
        LoginActivity.this.finish();
    }

    private void customerLogin() {
        String account = customer_et_account.getText().toString();
        String password = customer_et_password.getText().toString();

        int fail = 0;

        if (!isCustomerAccountValid(account)) {
            fail = 1;
        } else if (!isPasswordValid(password)) {
            fail = 2;
        }

        if (fail != 0) {
            dialog_builder.dialogEvent(getResources().getString(R.string.login_error_typo), "normal", null);
        } else {
            String cellphone = "%2B886-" + account.substring(1, 4)
                    + "-" + account.substring(4, 7)
                    + "-" + account.substring(7, 10);
            preference_account = cellphone;
            preference_password = password;
            new CustomerAPILogin().execute(cellphone, password);
        }
    }

    private void setLoginPreference(String userID, String type){
        //Set SharedPreference with userId, account, password
        SharedPreferences preferences = this.getSharedPreferences("USER", MODE_PRIVATE);
        preferences.edit().putString("userID", userID)
                .putString("type", type)
                .putString("account", preference_account)
                .putString("password", preference_password)
                .putString("username", preference_username)
                .apply();
    }

    private void setStoreLoginPreference(String userID, String type){
        //Set SharedPreference with userId, account, password
        SharedPreferences preferences = this.getSharedPreferences("USER", MODE_PRIVATE);
        preferences.edit().putString("userID", userID)
                .putString("type", type)
                .putString("account", preference_account)
                .putString("password", preference_password)
                .putString("username", preference_username)
                .putString("name",storeInfo.name)
                .putString("address",storeInfo.address)
                .putString("url",storeInfo.url)
                .apply();
    }

    private String checkPreference(){
        SharedPreferences preferences = this.getSharedPreferences("USER", MODE_PRIVATE);
        if(preferences.contains("userID")) {
            return preferences.getString("type", "");
        }
        else return null;
    }

    class CustomerAPILogin extends AsyncTask<String, Void, String> {
        private ProgressDialog progress_dialog = new ProgressDialog(LoginActivity.this);
        private String status = null;

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String _userID = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        getString(R.string.server_domain) + "api/sign_in?phone_number=" + params[0] + "&password=" + params[1]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0"))
                    _userID = responseJSON.getString("user_id");

                HttpGet infoRequest = new HttpGet(
                        getString(R.string.server_domain) + "api/user_info?user_id=" + _userID);
                infoRequest.addHeader("Content-Type", "application/json");
                HttpResponse infoResponse = httpClient.execute(infoRequest);
                ResponseHandler<String> infoHandler = new BasicResponseHandler();
                String infoHttpResponse = infoHandler.handleResponse(infoResponse);
                JSONObject infoResponseJSON = new JSONObject(infoHttpResponse);
                status = infoResponseJSON.getString("status_code");
                if (status.equals("0")) {
                    preference_username = infoResponseJSON.getString("account");
                }

            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return _userID;
        }
        @Override
        protected void onPostExecute(String _userID) {
            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }

            if(status == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else if (_userID == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_invalid), "normal", null);
            else {
                Toast.makeText(LoginActivity.this,
                        getResources().getString(R.string.login_success) + preference_username,  Toast.LENGTH_LONG).show();
                setLoginPreference(_userID, "customer");

                startCustomer();
            }
        }
    }

    class StoreAPILogin extends AsyncTask<String, Void, String> {
        private ProgressDialog progress_dialog = new ProgressDialog(LoginActivity.this);
        private String status = null;
        private String name;
        private String address;
        private String url;
        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String _userID = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(
                        getString(R.string.server_domain) + "api/shop_login?account=" + params[0] + "&password=" + params[1]);
                request.addHeader("Content-Type", "application/json");
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    _userID = responseJSON.getString("shop_id");
                    HttpGet getRequestInfo = new HttpGet(getString(R.string.server_domain) + "api/shop_info?shop_id="+_userID);
                    JSONObject shopInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRequestInfo)));
                    name = shopInfo.getString("name");
                    address = shopInfo.getString("address");
                    url = shopInfo.getString("picture_url");
                    storeInfo = new StoreInfo(name,address,url);
                    storeInfo.id = _userID;
                }

            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return _userID;
        }
        @Override
        protected void onPostExecute(String _userID) {
            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }

            if(status == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else if (_userID == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_invalid), "normal", null);
            else {
                Toast.makeText(LoginActivity.this,
                        getResources().getString(R.string.login_success) + storeInfo.name,  Toast.LENGTH_LONG).show();
                setStoreLoginPreference(_userID, "store");
                startStore();
            }
        }
    }
    public StoreInfo getStoreInfo(){
        return storeInfo;
    }
}
