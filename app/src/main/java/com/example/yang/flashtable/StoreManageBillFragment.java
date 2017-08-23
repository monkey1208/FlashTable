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
    RelativeLayout rl_payment;
    private String shop_id;
    private int contract_fee;
    private Calendar currentDate;
    private int number_clicks = 0;
    List<RecordInfo> recordList;
    private int half_month; // 0 == first half(1-15), 1 == second half(16~)

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
        contract_fee = StoreMainActivity.storeInfo.getContract_fee();

        view = inflater.inflate(R.layout.store_manage_bill_fragment, container, false);
        Toolbar bar = (Toolbar)view.findViewById(R.id.store_manage_bill_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                new AlertDialogController(getString(R.string.server_domain)).confirmCancelDialog(getContext(), "提醒", "確定要登出嗎？", LOGOUT, -1);
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
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });

        /* Check if the bill was paid,  set visibility.*/
        rl_payment = (RelativeLayout)view.findViewById(R.id.store_manage_bill_rl);

        LinearLayout prev_bt = (LinearLayout)view.findViewById(R.id.store_manage_bill_ll_prev);
        prev_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                number_clicks -= 1;
                check_payment();
                change_values(PREV);
            }
        });
        LinearLayout next_bt = (LinearLayout)view.findViewById(R.id.store_manage_bill_ll_next);
        next_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                number_clicks += 1;
                check_payment();
                change_values(NEXT);
            }
        });

        Button bt_pay = (Button)view.findViewById(R.id.store_manage_bill_bt_pay);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.PAYMENT_INFO);
            }
        });

        //Get transactions detail, then set values
        setValues(view);
        new APITimeDetail().execute();
        currentDate = Calendar.getInstance(Locale.getDefault());

        return view;
    }

    private void getStoreInfo() {
        SharedPreferences store = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        shop_id = store.getString("userID", "");
    }

    private void setValues(View v){
        Calendar today = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        currentDate = Calendar.getInstance(Locale.getDefault());
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
            half_month = 0;
        }else{
            start = 16;
            end = today.getActualMaximum(Calendar.DAY_OF_MONTH);
            half_month = 1;
        }
        tv_period.setText(String.format(Locale.TAIWAN, "%d/%02d/%02d - %d%02d/%02d", thisYear, thisMonth+1, start, thisYear, thisMonth+1,  end));

        int num_succ = 0;
        recordList = new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
        for(int i = recordList.size()-1; i >= 0; i--) {
            if(recordList.get(i).is_succ.equals("true")) {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                try {
                    Calendar date = Calendar.getInstance(Locale.getDefault());
                    date.setTime(df.parse(recordList.get(i).record_time));
                    if (date.get(Calendar.YEAR) == thisYear && (date.get(Calendar.MONTH)) == thisMonth &&
                            start <= date.get(Calendar.DAY_OF_MONTH) && date.get(Calendar.DAY_OF_MONTH) <= end) {
                        num_succ += recordList.get(i).number;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        tv_success.setText(String.valueOf(num_succ));
        tv_money.setText(String.valueOf(contract_fee));
        tv_totalmoney.setText(String.valueOf(contract_fee*num_succ));
    }

    private void check_payment(){
        if(number_clicks == 0){
            rl_payment.setVisibility(RelativeLayout.VISIBLE);
        }else{
            rl_payment.setVisibility(RelativeLayout.GONE);
        }
    }

    private void change_values(int command){
        int start, end;
        if(command == PREV){
            if(half_month == 0){
                currentDate.add(Calendar.MONTH, -1);
                half_month = 1;
            }else{
                half_month = 0;
            }
        }else if(command == NEXT){
            if(half_month == 0){
                half_month = 1;
            }else{
                currentDate.add(Calendar.MONTH, 1);
                half_month = 0;
            }
        }

        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) +1;
        if(half_month == 0){
            start = 1;
            end = 15;
        }else{
            start = 16;
            end = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        }
        tv_period.setText(String.format(Locale.TAIWAN, "%d/%02d/%02d - %d%02d/%02d", currentYear, currentMonth, start, currentYear, currentMonth,  end));
        int num_succ = 0;
        for(int i = recordList.size()-1; i >= 0; i--) {
            if(recordList.get(i).is_succ.equals("true")) {
                DateFormat df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                try {
                    Calendar date = Calendar.getInstance(Locale.getDefault());
                    date.setTime(df.parse(recordList.get(i).record_time));
                    if (date.get(Calendar.YEAR) == currentYear && (date.get(Calendar.MONTH)) == (currentMonth - 1) &&
                            start <= date.get(Calendar.DAY_OF_MONTH) && date.get(Calendar.DAY_OF_MONTH) <= end) {
                        num_succ += recordList.get(i).number;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        tv_success.setText(String.valueOf(num_succ));
        tv_totalmoney.setText(String.valueOf(contract_fee*num_succ));
    }

    private class APITimeDetail extends AsyncTask<String, Void, Void> {
        boolean new_record_flag = true;
        boolean exception = false;
        List<RecordInfo> tmpList;
        @Override
        protected Void doInBackground(String...params) {
            tmpList = new ArrayList<>(StoreMainActivity.storeInfo.getRecordList());
            int origin_size = tmpList.size();
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
                    RecordInfo info = new RecordInfo(account, num, point, time, session_time, is_success, url, promotion_des);
                    tmpList.add(info);

                }
            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            if(isAdded()) {
                if (!exception) {
                    if (new_record_flag) {
                        StoreMainActivity.storeInfo.setRecordList(recordList);
                        recordList = new ArrayList<>(tmpList);
                        setValues(view);
                    }
                    try {
                        int sum = 0;
                        for (int i = 0; i < recordList.size(); i++) {
                            if (recordList.get(i).is_succ.equals("true")) {
                                sum += 1;
                            }
                        }
                        StoreMainActivity.storeInfo.setSuccess_record_num(sum);
                    } catch (Exception e) {
                        new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "資料載入失敗，請重試");
                        e.printStackTrace();
                    }
                } else {
                    new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
                }
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
