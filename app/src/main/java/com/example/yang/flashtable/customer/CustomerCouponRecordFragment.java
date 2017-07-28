package com.example.yang.flashtable.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.infos.CustomerCouponRecordInfo;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CS on 2017/5/29.
 */

public class CustomerCouponRecordFragment extends Fragment {
    SharedPreferences user;
    String userID, username;

    ListView lv_records;
    List<CustomerCouponRecordInfo> records;
    CustomerCouponRecordAdapter adapter;
    int position;

    TextView tv_nothing;

    DialogBuilder dialog_builder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getUserInfo();
        return inflater.inflate(R.layout.customer_coupon_record_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog_builder = new DialogBuilder(getActivity());
        position = FragmentPagerItem.getPosition(getArguments());

        lv_records = (ListView) view.findViewById(R.id.customer_coupon_lv_records);
        records = new ArrayList<>();
        adapter = new CustomerCouponRecordAdapter(getActivity(), records);
        lv_records.setAdapter(adapter);
        lv_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (position == 1) {
                    Intent intent = new Intent(getActivity(), CustomerCouponCodeActivity.class);
                    intent.putExtra("code_id", records.get(i).code_id);
                    startActivity(intent);
                }
            }
        });
        tv_nothing = (TextView) view.findViewById(R.id.customer_coupon_tv_nothing);

        if (position == 1) new ApiRedeemRecords().execute(userID);
        else new ApiGetRecords().execute(userID);
    }

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }

    public void updateRecords() {
        adapter.notifyDataSetChanged();
        lv_records.setAdapter(adapter);

        if (records.size() > 0) tv_nothing.setVisibility(View.INVISIBLE);
        else {
            if (position == 1) tv_nothing.setText("您還沒有任何兌換紀錄喔");
            else tv_nothing.setText("您還沒有獲得任何FLASH Points喔");
            tv_nothing.setVisibility(View.VISIBLE);
        }
    }

    private class ApiRedeemRecords extends AsyncTask<String, Void, Void> {
        private boolean success = true;

        @Override
        protected Void doInBackground(String ...value) {
            NameValuePair user = new BasicNameValuePair("user_id", value[0]);
            NameValuePair param = new BasicNameValuePair("verbose", "1");
            HttpGet httpGet = new HttpGet(getString(R.string.server_domain) + "/api/user_codes?"
                    + user.toString() + "&"+ param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);
                System.out.println("records : "+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                for(int i = 1; i <= jsonObject.getInt("size"); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    System.out.println("record: " + jsonObject1.toString());
                    CustomerCouponRecordInfo info = new CustomerCouponRecordInfo();
                    info.name = "已成功兌換" + jsonObject1.getString("name");
                    info.type = 1;
                    info.code_id = jsonObject1.getString("code_id");
                    info.points = jsonObject1.getInt("flash_point");
                    info.time = jsonObject1.getString("redeem_time");

                    records.add(info);
                }
            } catch (IOException e) {
                e.printStackTrace();
                success = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
            if (!success) {
                dialog_builder.dialogEvent(
                        "資料載入失敗，請重試", "normal", null);
            }
            else {
                //get record list
                updateRecords();
            }
        }
    }

    private class ApiGetRecords extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String ...value) {
            NameValuePair user = new BasicNameValuePair("user_id", value[0]);
            NameValuePair param = new BasicNameValuePair("verbose", "1");
            HttpGet httpGet = new HttpGet(getString(R.string.server_domain) + "/api/user_records?"
                    + user.toString() + "&"+ param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);
                System.out.println("records : "+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                for(int i = 1; i <= jsonObject.getInt("size"); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    System.out.println("record: " + jsonObject1.toString());
                    CustomerCouponRecordInfo info = new CustomerCouponRecordInfo();
                    info.name = "已成功在" + jsonObject1.getString("shop_name")
                            + "領取" + jsonObject1.getString("number") + "人桌位";
                    info.code_id = jsonObject1.getString("promotion_id");
                    info.points = jsonObject1.getInt("delta_flash_point");
                    info.time = jsonObject1.getString("created_at");
                    info.type = 0;

                    if (info.points > 0) records.add(info);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {

            //get record list
            updateRecords();
        }
    }
}
