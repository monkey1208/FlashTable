package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by CS on 2017/4/1.
 */

public class CustomerRegisterActivity extends AppCompatActivity {
    ViewFlipper vf_flipper;
    DialogBuilder dialog_builder;

    // Info
    ImageButton ib_info_back;
    EditText et_username, et_password, et_password_again,
            et_cellphone_2, et_cellphone_3, et_cellphone_4;
    Button bt_submit;

    // Verify
    ImageButton ib_verify_back;
    EditText et_verify_1, et_verify_2, et_verify_3, et_verify_4;
    Button bt_register;
    private String verification_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_register_activity);

        initView();
        initData();
    }

    private void initView() {
        vf_flipper = (ViewFlipper) findViewById(R.id.customer_register_vf_flipper);
        dialog_builder = new DialogBuilder(this);

        // Info
        ib_info_back = (ImageButton) findViewById(R.id.customer_register_ib_info_back);
        et_username = (EditText) findViewById(R.id.customer_register_et_username);
        et_password = (EditText) findViewById(R.id.customer_register_et_password);
        et_password_again = (EditText) findViewById(R.id.customer_register_et_password_again);
        et_cellphone_2 = (EditText) findViewById(R.id.customer_register_et_cellphone_2);
        et_cellphone_3 = (EditText) findViewById(R.id.customer_register_et_cellphone_3);
        et_cellphone_4 = (EditText) findViewById(R.id.customer_register_et_cellphone_4);
        bt_submit = (Button) findViewById(R.id.customer_register_bt_submit);

        // Verify
        ib_verify_back = (ImageButton) findViewById(R.id.customer_register_ib_verify_back);
        et_verify_1 = (EditText) findViewById(R.id.customer_register_et_verification_1);
        et_verify_2 = (EditText) findViewById(R.id.customer_register_et_verification_2);
        et_verify_3 = (EditText) findViewById(R.id.customer_register_et_verification_3);
        et_verify_4 = (EditText) findViewById(R.id.customer_register_et_verification_4);
        bt_register = (Button) findViewById(R.id.customer_register_bt_register);
    }

    private void initData() {
        verification_id = null;

        // Info
        ib_info_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerRegister();
            }
        });
        et_cellphone_2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_cellphone_2.getText().toString().length() == 3)
                    et_cellphone_3.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        et_cellphone_3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_cellphone_3.getText().toString().length() == 3)
                    et_cellphone_4.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        // Verify
        ib_verify_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vf_flipper.setDisplayedChild(0);
            }
        });
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customerVerify();
            }
        });
        et_verify_1.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_verify_1.getText().toString().length() == 1)
                    et_verify_2.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        et_verify_2.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_verify_2.getText().toString().length() == 1)
                    et_verify_3.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
        et_verify_3.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_verify_3.getText().toString().length() == 1)
                    et_verify_4.requestFocus();
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });
    }

    private boolean isAccountValid(String account) {
        return !account.equals("");
    }

    private boolean isPasswordValid(String password) {
        // Password valid: 0-9 and a-z

        Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);

        return (!matcher.find() && !password.equals(""));
    }

    private boolean isCellphoneValid(String cellphone) {
        Pattern pattern = Pattern.compile("\\+(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|\n" +
                "2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|\n" +
                "4[987654310]|3[9643210]|2[70]|7|1)\n" +
                "\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*(\\d{1,2})$");
        Matcher matcher = pattern.matcher(cellphone);

        return (!matcher.find() && !cellphone.equals(""));
    }

    private void customerRegister() {
        String account = et_username.getText().toString();
        String password = et_password.getText().toString();
        String password_again = et_password_again.getText().toString();
        String cellphone =
                "+886-" + et_cellphone_2.getText().toString()
                + "-" + et_cellphone_3.getText().toString()
                + "-" + et_cellphone_4.getText().toString();

        int fail = 0;

        if (!isAccountValid(account))
            fail = 1;
        else if (!isPasswordValid(password))
            fail = 2;
        else if (!password.equals(password_again))
            fail = 3;
        else if (!isCellphoneValid(cellphone))
            fail = 4;

        if (fail == 1) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_username),
                    "normal", null);
            et_username.setText("");
        } else if (fail == 2) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_password),
                    "normal", null);
            et_password.setText("");
            et_password_again.setText("");
        } else if (fail == 3) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_password_again),
                    "normal", null);
            et_password.setText("");
            et_password_again.setText("");
        } else if (fail == 4) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_cellphone),
                    "normal", null);
            et_cellphone_2.setText("");
            et_cellphone_3.setText("");
            et_cellphone_4.setText("");
        } else new CustomerAPINewUser().execute(account, password, cellphone);

    }

    private void customerVerify() {
        String code = "";
        code = code.concat(et_verify_1.getText().toString());
        code = code.concat(et_verify_2.getText().toString());
        code = code.concat(et_verify_3.getText().toString());
        code = code.concat(et_verify_4.getText().toString());

        if (code.length() != 4) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_verify_length),
                    "normal", null);
            et_verify_1.setText("");
            et_verify_2.setText("");
            et_verify_3.setText("");
            et_verify_4.setText("");
        } else new CustomerAPIRegister().execute(verification_id, code);

    }

    class CustomerAPINewUser extends AsyncTask<String, Void, List<String>> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerRegisterActivity.this);

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected List<String> doInBackground(String... params) {
            List<String> list = new ArrayList<>();
            String status = null;
            String verification_id = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        "https://flash-table.herokuapp.com/api/new_user");
                StringEntity se = new StringEntity("{ \"account\":\"" + params[0] +
                        "\", \"password\":\"" + params[1] +
                        "\", \"phone_number\":\"" + params[2]+ "\"}", HTTP.UTF_8);
                request.addHeader("Content-Type", "application/json");
                request.setEntity(se);
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String httpResponse = handler.handleResponse(response);
                JSONObject responseJSON = new JSONObject(httpResponse);

                status = responseJSON.getString("status_code");
                if (status.equals("0"))
                    verification_id = responseJSON.getString("verification_id");

                list.add(status);
                list.add(verification_id);
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }

            return list;
        }
        @Override
        protected void onPostExecute(List response) {

            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }

            if(response.isEmpty()) {
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
                return;
            }

            String status = response.get(0).toString();
            if (status.equals("-1"))
                dialog_builder.dialogEvent(
                        getResources().getString(R.string.customer_register_error_username_used), "normal", null);
            else if (status.equals("-2"))
                dialog_builder.dialogEvent(
                        getResources().getString(R.string.customer_register_error_cellphone_used), "normal", null);
            else if (status.equals("0")) {
                verification_id = response.get(1).toString();
                vf_flipper.setDisplayedChild(1);
            }
        }
    }

    class CustomerAPIRegister extends AsyncTask<String, Void, String> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerRegisterActivity.this);

        @Override
        protected void onPreExecute() {
            // TODO: Style this.

            progress_dialog.setMessage(getResources().getString(R.string.login_wait));
            // progress_dialog.setContentView(R.layout.progress_dialog);
            progress_dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String status = null;
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpPost request = new HttpPost(
                        "https://flash-table.herokuapp.com/api/sign_up");
                StringEntity se = new StringEntity("{ \"verification_id\":\"" + params[0] +
                        "\", \"verification_code\":\"" + params[1] + "\"}", HTTP.UTF_8);
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

            if(status == null  || status.equals("-1")) {
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            } else if (status.equals("-2")) {
                dialog_builder.dialogEvent(
                        getResources().getString(R.string.customer_register_error_verify_wrong), "normal", null);
            } else if (status.equals("0")) {
                Toast.makeText(CustomerRegisterActivity.this, getResources().getString(R.string.customer_register_success), Toast.LENGTH_LONG)
                        .show();
                CustomerRegisterActivity.this.finish();
            }
        }
    }
}
