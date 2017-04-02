package com.example.yang.flashtable;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerProfileFragment extends Fragment {
    SharedPreferences user;
    String userID, username;

    DialogBuilder dialog_builder;

    private View view;
    TextView tv_username, tv_credit;
    LinearLayout ll_comments, ll_reservations;
    ImageView iv_avatar;
    Button bt_edit, bt_about_credits;

    private String credits;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_profile_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        dialog_builder = new DialogBuilder(getActivity());

        tv_username = (TextView) view.findViewById(R.id.customer_profile_tv_name);
        tv_credit = (TextView) view.findViewById(R.id.customer_profile_tv_credit);
        ll_reservations = (LinearLayout) view.findViewById(R.id.customer_profile_ll_reservations);
        ll_comments = (LinearLayout)  view.findViewById(R.id.customer_profile_ll_comments);
        iv_avatar = (ImageView) view.findViewById(R.id.customer_profile_iv_avatar);
        bt_edit = (Button) view.findViewById(R.id.customer_profile_bt_edit);
        bt_about_credits = (Button) view.findViewById(R.id.customer_profile_bt_about_credit);

        credits = getResources().getString(R.string.customer_profile_credit);
    }

    private void initData() {
        getUserInfo();

        tv_username.setText(username);
        tv_credit.setText(credits + "-");
        new CustomerAPICredits().execute(userID);

        ll_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerDetailActivity.class);
                startActivity(intent);
            }
        });
        ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerCommentActivity.class);
                startActivity(intent);
            }
        });
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("account", "");
    }

    class CustomerAPICredits extends AsyncTask<String, Void, String> {
        private String status = null;

        @Override
        protected String doInBackground(String... params) {
            String content = null;
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
                    content = responseJSON.getString("point");
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

            if(status == null || status.equals("-1") || _content == null)
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else {
                tv_credit.setText(credits + _content);
            }
        }
    }
}
