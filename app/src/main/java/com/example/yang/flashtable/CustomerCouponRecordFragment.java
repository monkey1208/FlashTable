package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/5/29.
 */

public class CustomerCouponRecordFragment extends Fragment {
    ListView lv_records;
    List<CustomerCouponRecordInfo> records;
    CustomerCouponRecordAdapter adapter;
    int position;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.customer_coupon_record_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        position = FragmentPagerItem.getPosition(getArguments());

        lv_records = (ListView) view.findViewById(R.id.customer_coupon_lv_records);
        records = new ArrayList<>();
        CustomerCouponRecordInfo info = new CustomerCouponRecordInfo();
        info.name = "hi";
        info.time = "2017/5/29 11:10";
        info.points = 100;
        records.add(info);
        adapter = new CustomerCouponRecordAdapter(getActivity(), records);
        lv_records.setAdapter(adapter);
    }

    public void updateRecords() {
        adapter.notifyDataSetChanged();
        lv_records.setAdapter(adapter);
    }

    private class ApiRecords extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String ...value) {
            NameValuePair user = new BasicNameValuePair("user_id", value[0]);
            NameValuePair param = new BasicNameValuePair("verbose", "1");
            HttpGet httpGet = new HttpGet("http://" + getString(R.string.server_domain) + "/api/user_codes?" + user.toString() + param.toString());
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
                    CustomerCouponRecordInfo info = new CustomerCouponRecordInfo();
                    info.name = jsonObject1.getString("name");
                    info.code_id = jsonObject1.getString("code_id");
                    info.points = jsonObject1.getInt("flash_point");
                    info.time = jsonObject1.getString("tutorial");

                    records.add(info);
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
