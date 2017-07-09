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
import android.widget.ListView;

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
    public static List<RecordInfo> list;

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
        list =  new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
        adapter = new StoreManageRecordAdapter(getActivity(), list);
        setListAdapter(adapter);
        new APIRecordDetail().execute();

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_record_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(StoreMainActivity.fragmentController.get_prev_fragment());
            }
        });

        return v;

    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        Bundle content = new Bundle();
        RecordInfo info = list.get(position);
        content.putString("id", String.valueOf(info.id));
        content.putString("name", info.name);
        content.putString("number", String.valueOf(info.number));
        content.putString("point", String.valueOf(info.point));
        content.putString("record_time", info.record_time);
        content.putString("session_time", info.session_time);
        content.putString("is_succ", info.is_succ);
        content.putString("image_url", info.get_Image_Url());
        content.putString("promotion_des", info.promotion_des);
        StoreMainActivity.fragmentController.sendBundle(content, FragmentController.MANAGE_RECORD_INFO);
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
        boolean exception = false;
        List<RecordInfo> tmp_list = new ArrayList<>();
        List<String> tmp_time_list = new ArrayList<>();
        @Override
        protected Void doInBackground(String...params) {
            tmp_list.addAll(StoreMainActivity.storeInfo.getRecordList());
            tmp_time_list = new ArrayList<>(StoreMainActivity.storeInfo.getCommentTimeList());
            int origin_size = tmp_list.size();
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet(getString(R.string.server_domain)+"/api/shop_records?shop_id="+ shop_id+"&verbose=1");
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

                    final RecordInfo info = new RecordInfo(account, num, point, time, session_time, is_success, url, promotion_des);
                    tmp_list.add(info);

                    if(recordInfo.getString("is_used").equals("true")){
                        df = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
                        tmp_time_list.add( df.format(date).replace("AM", "am").replace("PM","pm") );
                    }
                }
            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            if(exception){
                new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
            }else {
                if (new_record_flag) {
                    list.clear();
                    list.addAll(tmp_list);
                    adapter.notifyDataSetChanged();
                    Log.e("record", "update");
                    StoreMainActivity.storeInfo.setRecordList(list);
                    StoreMainActivity.storeInfo.setCommentTimeList(tmp_time_list);
                    int sum = 0;
                    for (int i = 0; i < list.size(); i++) {
                        String is_success = list.get(i).is_succ;
                        if (is_success.equals("true")) {
                            sum += 1;
                        }
                    }
                    StoreMainActivity.storeInfo.setSuccess_record_num(sum);
                }
            }
        }
    }
}
