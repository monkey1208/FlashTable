package com.example.yang.flashtable;

import android.app.ProgressDialog;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
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
 * Created by CS on 2017/4/5.
 */

public class StoreRegisterActivity extends AppCompatActivity {
    DialogBuilder dialog_builder;

    ImageButton ib_back;
    EditText et_shop, et_address, et_contact, et_email,
            et_cellphone_2, et_cellphone_3, et_cellphone_4;
    Button bt_submit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.store_register_activity);

        initView();
        initData();
    }

    private void initView() {
        dialog_builder = new DialogBuilder(this);

        ib_back = (ImageButton) findViewById(R.id.store_register_ib_info_back);
        et_shop = (EditText) findViewById(R.id.store_register_et_name);
        et_address = (EditText) findViewById(R.id.store_register_et_address);
        et_contact = (EditText) findViewById(R.id.store_register_et_contact);
        et_email = (EditText) findViewById(R.id.store_register_et_email);
        et_cellphone_2 = (EditText) findViewById(R.id.store_register_et_cellphone_2);
        et_cellphone_3 = (EditText) findViewById(R.id.store_register_et_cellphone_3);
        et_cellphone_4 = (EditText) findViewById(R.id.store_register_et_cellphone_4);
        bt_submit = (Button) findViewById(R.id.store_register_bt_submit);
    }

    private void initData() {
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeRegister();
            }
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

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private void storeRegister() {
        String store_name = et_shop.getText().toString();
        String address = et_address.getText().toString();
        String contact = et_contact.getText().toString();
        String cellphone =
                "+886-" + et_cellphone_2.getText().toString()
                        + "-" + et_cellphone_3.getText().toString()
                        + "-" + et_cellphone_3.getText().toString();
        String email = et_email.getText().toString();

        int fail = 0;

        if (store_name.equals(""))
            fail = 1;
        else if (address.equals(""))
            fail = 2;
        else if (contact.equals(""))
            fail = 3;
        else if (!isCellphoneValid(cellphone))
            fail = 4;
        else if (!isEmailValid(email))
            fail = 5;

        if (fail == 1) {
            dialog_builder.dialogEvent(getResources().getString(R.string.store_register_error_store),
                    "normal", null);
            et_shop.setText("");
        } else if (fail == 2) {
            dialog_builder.dialogEvent(getResources().getString(R.string.store_register_error_address),
                    "normal", null);
            et_address.setText("");
        } else if (fail == 3) {
            dialog_builder.dialogEvent(getResources().getString(R.string.store_register_error_contact),
                    "normal", null);
            et_contact.setText("");
        } else if (fail == 4) {
            dialog_builder.dialogEvent(getResources().getString(R.string.customer_register_error_cellphone),
                    "normal", null);
            et_cellphone_2.setText("");
            et_cellphone_3.setText("");
            et_cellphone_4.setText("");
        } else if (fail == 5) {
            dialog_builder.dialogEvent(getResources().getString(R.string.store_register_error_email),
                    "normal", null);
        } else new StoreAPIApply().execute(contact, store_name, address, cellphone, email);

    }

    class StoreAPIApply extends AsyncTask<String, Void, String> {
        private ProgressDialog progress_dialog = new ProgressDialog(StoreRegisterActivity.this);

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
                        "https://"+ getString(R.string.server_domain) + "/api/new_shop");
                StringEntity se = new StringEntity("{ \"contact\":\"" + params[0] +
                        "\", \"name\":\"" + params[1] +
                        "\", \"address\":\"" + params[2] +
                        "\", \"phone_number\":\"" + params[3] +
                        "\", \"email\":\"" + params[4] +"\"}", HTTP.UTF_8);
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

            return status;
        }
        @Override
        protected void onPostExecute(String status) {

            if (progress_dialog.isShowing()) {
                progress_dialog.dismiss();
            }

            if(status == null) {
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
                return;
            }

            if (status.equals("-1"))
                dialog_builder.dialogEvent(
                        getResources().getString(R.string.store_register_error_exists), "normal", null);
            else if (status.equals("0")) {
                dialog_builder.dialogEvent(getResources().getString(R.string.store_register_success),
                        "normal", new DialogEventListener() {
                            @Override
                            public void clickEvent(boolean ok, int status) {
                                StoreRegisterActivity.this.finish();
                            }
                        });
            }
        }
    }
}
