package com.example.yang.flashtable;

import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

public class StoreManageRecordFragment extends ListFragment {
    SharedPreferences store;
    String shop_id;
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
        final View v = inflater.inflate(R.layout.store_manage_record_fragment, container, false);

        getStoreInfo();
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void getStoreInfo() {
        store = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        shop_id = store.getString("userID", "");
    }

    private class APIRecordDetail extends AsyncTask<String, Void, Void> {
        boolean new_record_flag = true;
        @Override
        protected Void doInBackground(String...params) {
            list.clear();
            list.addAll(StoreMainActivity.storeInfo.getRecordList());
            int origin_size = list.size();
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ shop_id+"&verbose=1");
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                int new_size = recordsInfo.getJSONObject(0).getInt("size");
                if(new_size <= origin_size){
                    new_record_flag = false;
                }
                for (int i = origin_size+1; i <= new_size; i++) {
                    JSONObject recordInfo = recordsInfo.getJSONObject(i);
                    int num = recordInfo.getInt("number");
                    String is_success = recordInfo.getString("is_succ");
                    String account = recordInfo.getString("user_account");
                    int point = recordInfo.getInt("user_point");

                    String time = recordInfo.getString("created_at");
                    DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date date =  df.parse(time);
                    df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.getDefault());
                    time = df.format(date);
                    final ReservationInfo info = new ReservationInfo(account, num, point, time, is_success);
                    list.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            if(new_record_flag) {
                adapter.notifyDataSetChanged();
                Log.e("record", "update");
                StoreMainActivity.storeInfo.setRecordList(list);
                int sum = 0;
                for (int i = 0; i < list.size(); i++) {
                    String is_success = list.get(i).is_succ;
                    if(is_success.equals("true")){
                        sum += 1;
                    }
                }
                StoreMainActivity.storeInfo.setSuccess_record_num(sum);
            }else{
                adapter.notifyDataSetChanged();
                Log.e("record", "remain");
            }
        }
    }
}
