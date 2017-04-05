package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreManageRecordFragment extends ListFragment {
    public static StoreManageRecordAdapter adapter;
    public static List<ReservationInfo> list =  new ArrayList<>();

    public StoreManageRecordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.store_manage_record_fragment, container, false);

        new APIRecordDetail().execute();
        adapter = new StoreManageRecordAdapter(getActivity(), list);
        setListAdapter(adapter);


        ListView lv =(ListView) v.findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            }
        });

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_record_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });


        return v;

    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        //To each record detail
        Toast.makeText(getContext(), "Jump to page ", Toast.LENGTH_SHORT).show();
    }

    private void setValues(List<ReservationInfo> list){
        ReservationInfo tmp = new ReservationInfo("Cindy Chen",100,System.currentTimeMillis());
        list.add(tmp);
        tmp = new ReservationInfo("Bing Bing",10,System.currentTimeMillis());
        list.add(tmp);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public class APIRecordDetail extends AsyncTask<Object, Void, Void> {
        //List<ReservationInfo> list = new ArrayList<>();
        @Override
        protected Void doInBackground(Object... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ String.valueOf(1));
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                for (int i = 1; i < recordsInfo.length(); i++) {
                    JSONObject jsonItem = recordsInfo.getJSONObject(i);
                    int record_id = jsonItem.getInt("record_id");
                    Log.e("QQ", String.valueOf(record_id));
                    HttpGet getRecordInfo = new HttpGet("https://flash-table.herokuapp.com/api/record_info?record_id="+record_id);
                    JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordInfo)));
                    int num = recordInfo.getInt("number");
                    String is_success = recordInfo.getString("is_succ");

                    String time = recordInfo.getString("created_at");
                    DateFormat df = new SimpleDateFormat("EEE MMM dd h:mm:ss yyyy", Locale.getDefault());
                    Date date =  df.parse(time);
                    df = new SimpleDateFormat("yyyy/MM/dd  ahh:mm", Locale.getDefault());
                    time = df.format(date);

                    int user_id = recordInfo.getInt("user_id");
                    HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+user_id);
                    JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
                    String account = userInfo.getString("account");
                    int point = userInfo.getInt("point");
                    final ReservationInfo info = new ReservationInfo(account, num, point, time, is_success);
                   // list.add(info);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            StoreManageRecordFragment.list.add(info);
                            StoreManageRecordFragment.adapter.notifyDataSetChanged();
                        }

                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
        }
    }
}
