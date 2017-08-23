package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreManageSuccessFragment extends Fragment {
    private TextView tv_rate;
    private TextView tv_total;
    private TextView tv_fail;
    private TextView tv_success;
    private static boolean was_browsed = false;

    public StoreManageSuccessFragment() {
        // Required empty public constructor
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_manage_success_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_success_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });

        if(!was_browsed){
            was_browsed = true;
        }
        setValues(v);
        return v;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    void setValues(View v){
        tv_rate = (TextView)v.findViewById(R.id.store_manage_success_tv_rate);
        tv_total = (TextView)v.findViewById(R.id.store_manage_success_tv_amount);
        tv_fail = (TextView)v.findViewById(R.id.store_manage_success_tv_fail);
        tv_success = (TextView)v.findViewById(R.id.store_manage_success_tv_success);

        int total =  StoreMainActivity.storeInfo.getRecordList().size();
        int success_num = StoreMainActivity.storeInfo.getSuccess_record_num();

        tv_rate.setText(total==0? "0" : String.valueOf((int)((success_num+0.0)/total * 100)));
        tv_total.setText(String.valueOf(total));
        tv_fail.setText(String.valueOf(total - success_num));
        tv_success.setText(String.valueOf(success_num));

        new ReservationSuccessDetail().execute();
    }

    private class ReservationSuccessDetail extends AsyncTask<Object, Void, Void> {
        int sum = 0, new_size;
        List<RecordInfo> list = new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
        boolean new_records_flag = true;
        boolean exception = false;
        @Override
        protected Void doInBackground(Object... params) {
            int origin_size = list.size();
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet(getString(R.string.server_domain)+"/api/shop_records?shop_id="+ StoreMainActivity.storeInfo.id+"&verbose=1");
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                new_size = recordsInfo.getJSONObject(0).getInt("size");
                if(new_size <= origin_size){
                    new_records_flag = false;
                }

                //Refresh the record list for all fragments.
                for (int i = origin_size+1; i <= new_size; i++) {
                    Log.e("record", "update");
                    JSONObject recordInfo = recordsInfo.getJSONObject(i);
                    int num = recordInfo.getInt("number");
                    String is_success = recordInfo.getString("is_succ");
                    String account = recordInfo.getString("user_account");
                    int point = recordInfo.getInt("user_point");
                    String url = recordInfo.getString("user_picture_url");
                    String promotion_des = recordInfo.getString("promotion_description");

                    String time = recordInfo.getString("created_at");
                    String session_time = recordInfo.getString("session_created_at");
                    DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date date =  df.parse(time);
                    Date session_date = df.parse(session_time);
                    df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                    time = df.format(date);
                    session_time = df.format(session_date);
                    RecordInfo info = new RecordInfo(account, num, point, time, session_time, is_success, url, promotion_des);
                    list.add(info);

                }
            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            if(!exception) {
                if (new_records_flag || !was_browsed) {
                    Log.e("success", "refresh");
                    StoreMainActivity.storeInfo.setRecordList(list);

                    for (int i = 0; i < list.size(); i++) {
                        String is_success = list.get(i).is_succ;
                        if (is_success.equals("true")) {
                            sum += 1;
                        }
                    }
                    if(isAdded()) {
                        DecimalFormat df2 = new DecimalFormat(".##");
                        tv_rate.setText(df2.format((sum + 0.0) / list.size() * 100));
                        tv_total.setText(String.valueOf(list.size()));
                        tv_fail.setText(String.valueOf(list.size() - sum));
                        tv_success.setText(String.valueOf(sum));
                    }
                    StoreMainActivity.storeInfo.setSuccess_record_num(sum);
                }
            }else{
                if(isAdded()) {
                    new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
                }
            }
        }
    }
}
