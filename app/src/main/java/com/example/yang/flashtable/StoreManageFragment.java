package com.example.yang.flashtable;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import static com.example.yang.flashtable.AlertDialogController.LOGOUT;


public class StoreManageFragment extends ListFragment {
    private String shop_id;
    private StoreManageAdapter adapter;

    public  StoreManageFragment () {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_manage_fragment, container, false);
        adapter = new StoreManageAdapter(getActivity(), itemname, imgid, value);
        setListAdapter(adapter);

        getStoreInfo();
        new APIFirstRecordDetail().execute();

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new AlertDialogController().confirmCancelDialog(getContext(), "提醒", "確定要登出嗎？", LOGOUT, -1);
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        StoreMainActivity.fragmentController.act(position+4);
    }

    String[] itemname ={
            "預約成功率",
            "開啟時段整理",
            "應付帳款明細",
            "折扣優惠",
            "優惠統計詳情",
            "預約紀錄",
            "顧客評價及評論"
    };

    Integer[] imgid={
            R.drawable.ic_store_manage_success,
            R.drawable.ic_store_manage_opentime,
            R.drawable.ic_store_manage_bill,
            R.drawable.ic_store_manage_discount,
            R.drawable.ic_store_manage_statistic,
            R.drawable.ic_store_manage_record,
            R.drawable.ic_store_manage_comment
    };

    Integer[] value={0, 0, 0, 0, 0, 0, 0};

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void getStoreInfo() {
        SharedPreferences store = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        shop_id = store.getString("userID", "");
    }

    private class APIFirstRecordDetail extends AsyncTask<String, Void, Void> {
        List<ReservationInfo> list;
        ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setMessage("請稍後");
            pd.show();
        }
        @Override
        protected Void doInBackground(String...params) {
            list = new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ shop_id+"&verbose=1");
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                for (int i = 1; i < recordsInfo.length(); i++) {
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
            if (pd != null) {
                pd.dismiss();
            }
            StoreMainActivity.storeInfo.setRecordList(list);
            int sum = 0;
            for (int i = 0; i < list.size(); i++) {
                String is_success = list.get(i).is_succ;
                if(is_success.equals("true")){
                    sum += 1;
                }
            }
            StoreMainActivity.storeInfo.setSuccess_record_num(sum);
            value[0]= (int)((sum+0.0)/list.size()*100);
            adapter.notifyDataSetChanged();

        }
    }

}
