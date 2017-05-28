package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;
import static com.example.yang.flashtable.AlertDialogController.LOGOUT;

public class StoreManageBillFragment extends Fragment {
    private final static int PREV = 0;
    private final static int NEXT = 1;
    private View view;
    TextView tv_period;
    TextView tv_success;
    TextView tv_money;
    TextView tv_totalmoney;
    private String shop_id;
    private List<Date> dateList;
    private int contract_fee;

    public StoreManageBillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getStoreInfo();
        if(StoreMainActivity.storeInfo.getContract_fee() == -1) {
            new StoreContractFee().execute();
        }else{
            contract_fee = StoreMainActivity.storeInfo.getContract_fee();
        }

        view = inflater.inflate(R.layout.store_manage_bill_fragment, container, false);
        Toolbar bar = (Toolbar)view.findViewById(R.id.store_manage_bill_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new AlertDialogController().confirmCancelDialog(getContext(), "提醒", "確定要登出嗎？", LOGOUT, -1);
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });

        /* Check if the bill was paid,  set visibility.*/
        RelativeLayout rl = (RelativeLayout)view.findViewById(R.id.store_manage_bill_rl);
        rl.setVisibility(RelativeLayout.GONE);

        LinearLayout prev_bt = (LinearLayout)view.findViewById(R.id.store_manage_bill_ll_prev);
        prev_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                change_values(PREV);
            }
        });
        LinearLayout next_bt = (LinearLayout)view.findViewById(R.id.store_manage_bill_ll_next);
        next_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                change_values(NEXT);
            }
        });

        Button bt_pay = (Button)view.findViewById(R.id.store_manage_bill_bt_pay);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Pay for the bill
            }
        });

        //Get transactions detail, then set values
        new APITimeDetail().execute();

        return view;
    }

    private void getStoreInfo() {
        SharedPreferences store = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        shop_id = store.getString("userID", "");
    }

    private void setValues(View v){
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int thisYear = today.get(Calendar.YEAR);
        int thisMonth = today.get(Calendar.MONTH);
        int thisDay = today.get(Calendar.DAY_OF_MONTH);

        tv_period = (TextView)v.findViewById(R.id.store_manage_bill_tv_period);
        tv_success = (TextView)v.findViewById(R.id.store_manage_bill_tv_success);
        tv_money = (TextView)v.findViewById(R.id.store_manage_bill_tv_money);
        tv_totalmoney = (TextView)v.findViewById(R.id.store_manage_bill_tv_totalmoney);

        int start, end;
        if(thisDay <= 15){
            start = 1;
            end = 15;
        }else{
            start = 16;
            end = today.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        tv_period.setText(String.format(Locale.TAIWAN, "%d/%02d/%02d - %d%02d/%02d", thisYear, thisMonth+1, start, thisYear, thisMonth+1,  end));

        int num_succ = 0;
        for(int i = dateList.size()-1; i >= 0; i--) {
            Calendar date = Calendar.getInstance();
            date.setTime(dateList.get(i));
            if (date.get(Calendar.YEAR) == thisYear && date.get(Calendar.MONTH) == thisMonth &&
                    start <= date.get(Calendar.DAY_OF_MONTH) && date.get(Calendar.DAY_OF_MONTH) <= end) {
                num_succ++;
            }
        }
        tv_success.setText(String.valueOf(num_succ));
        tv_money.setText(String.valueOf(contract_fee));
        tv_totalmoney.setText(String.valueOf(contract_fee*num_succ));
    }

    private void change_values(int command){
        if(command == PREV){

        }else if(command == NEXT){

        }
    }

    private class APITimeDetail extends AsyncTask<String, Void, Void> {
        boolean new_record_flag = true;
        List<ReservationInfo> list;
        boolean exception = false;
        @Override
        protected Void doInBackground(String...params) {
            list = new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
            dateList = new ArrayList<>();
            int origin_size = list.size();
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+shop_id+"&verbose=1");
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
                    String promotion_name = recordInfo.getString("promotion_name");
                    String promotion_des = recordInfo.getString("promotion_description");

                    String time = recordInfo.getString("created_at");
                    DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date date =  df.parse(time);
                    df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                    time = df.format(date);
                    ReservationInfo info = new ReservationInfo(account, num, point, time, is_success, url, promotion_name, promotion_des);
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
                if (new_record_flag) {
                    StoreMainActivity.storeInfo.setRecordList(list);
                }
                try {
                    int sum = 0;
                    for (int i = 0; i < list.size(); i++) {
                        String is_success = list.get(i).is_succ;
                        if (is_success.equals("true")) {
                            sum += 1;
                            String time = list.get(i).record_time;
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                            Date date = df.parse(time);
                            dateList.add(date);
                        }
                    }
                    StoreMainActivity.storeInfo.setSuccess_record_num(sum);
                } catch (Exception e) {
                    new AlertDialogController().warningConfirmDialog(getContext(),"提醒", "資料載入失敗，請重試");
                    e.printStackTrace();
                }
                setValues(view);
            }else{
                new AlertDialogController().warningConfirmDialog(getContext(),"提醒", "資料載入失敗，請重試");
            }

        }
    }

    private class StoreContractFee extends AsyncTask<String, Void, Void> {
        boolean exception = false;
        @Override
        protected Void doInBackground(String...params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getShopInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_info?shop_id="+shop_id);
                JSONObject shopInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getShopInfo)));
                if(shopInfo.getInt("status_code") == 0){
                    contract_fee = shopInfo.getInt("contract_fee");
                }else{
                    exception = true;
                }

            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            if(exception) {
                new AlertDialogController().warningConfirmDialog(getContext(),"提醒", "資料載入失敗，請重試");
            }
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
