package com.example.yang.flashtable;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarData;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import pl.droidsonroids.gif.GifImageView;

import static com.example.yang.flashtable.StoreMainActivity.fragmentController;
import static com.example.yang.flashtable.StoreManageOpentimeFragment.tv_time_choose;

public class AlertDialogController {

    private static AlertDialog alertDialog;
    public static StoreHomeDiscountDialogAdapter adapter;
    private static View titleBar;
    public static final int NOTICE1_APPOINT = 0;
    public static final int NOTICE2_APPOINT = 1;
    public static final int NOTICELIST_APPOINT = 2;
    public static final int UNFINISHED_CONTENT = 3;
    public static final int OPENTIME_CHOOSE = 4;
    public static final int OPENTIME_CHOOSE_DETAIL = 5;
    public static final int OPENTIME_CHOOSE_DAY = 6;
    public static final int OPENTIME_CHOOSE_WEEK = 7;
    public static final int OPENTIME_CHOOSE_MONTH = 8;
    public static int listPosition = -1;
    public static int opentime_choose_result;
    public static int result = 0;
    private static boolean first = true;
    private static final String[] month_to_Chinese = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};


    public static void discountDialog(final Context context, final StoreInfo storeInfo, final TextView tv_discount, final TextView tv_gift, final ImageButton bt_active, final GifImageView bt_active_gif, final TextView tv_active, final TextView tv_active_remind){
        //init view---------
        View item = LayoutInflater.from(context).inflate(R.layout.store_discount_list, null);
        //listview adapt----
        ListView lv_discount = (ListView)item.findViewById(R.id.lv_discount);
        adapter = new StoreHomeDiscountDialogAdapter(context,storeInfo.discountList);
        lv_discount.setAdapter(adapter);
        lv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StoreMainActivity.storeInfo.discountCurrent = position;
                adapter.notifyDataSetChanged();
            }
        });

        setTitle(context, "折扣優惠", 18);

        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(item)
                .create();
        setBackground(context);

        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_discount.setText(Integer.toString(storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).discount)+"折");
                tv_gift.setText(storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).description);
                //TODO: notify server dicount change
                new APIpromotion_modify().execute(Integer.toString(storeInfo.discountList.get(StoreMainActivity.storeInfo.discountCurrent).id));
                StoreMainActivity.fragmentController.storeAppointFragment.startUpdate();
                StoreMainActivity.apiHandler.changePromotions();
                bt_active.setVisibility(View.INVISIBLE);
                tv_active.setVisibility(View.INVISIBLE);

                bt_active_gif.setVisibility(View.VISIBLE);
                bt_active_gif.setImageResource(R.drawable.bt_resize_activate);
                StoreHomeFragment.tv_active_running.setText("開啟中");

                bt_active.setEnabled(false);
                if(first) {
                    bt_active_gif.setImageResource(R.drawable.bt_resize_activate);
                    first = false;
                }
                bt_active_gif.setVisibility(View.VISIBLE);
                bt_active_gif.setEnabled(true);
                tv_active.setText("開啟中");

                tv_active_remind.setText("按下後暫停");
                alertDialog.dismiss();
            }
        });
        ImageButton bt_cancel = (ImageButton)item.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        setDialogSize(context, 0.8, 0.8);
    }
    public static class APIpromotion_modify extends AsyncTask<String,Void,Void> {
        private String result = "-1";
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/activate_promotion");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("promotion_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null)
                    result = resEntity.toString();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static void addDiscountDialog(final Context context){
        //init view---------
        View item = LayoutInflater.from(context).inflate(R.layout.store_add_discount_dialog, null);
        setTitle(context, "折扣優惠", 18);

        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(item)
                .create();
        setBackground(context);

        final TextView tv_no_discount = (TextView)item.findViewById(R.id.store_add_discount_dialog_tv_no_discount);
        tv_no_discount.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                tv_no_discount.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
                //discount = 100/101?;
            }
        });
        final TextView tv_discount_num = (TextView)item.findViewById(R.id.store_add_discount_dialog_tv_discount_num);
        ImageButton ib_minus = (ImageButton)item.findViewById(R.id.store_add_discount_dialog_ib_minus);
        ib_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf(tv_discount_num.getText().toString());
                tv_discount_num.setText(String.valueOf(value-1));
            }
        });
        ImageButton ib_plus = (ImageButton)item.findViewById(R.id.store_add_discount_dialog_ib_plus);
        ib_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.valueOf(tv_discount_num.getText().toString());
                tv_discount_num.setText(String.valueOf(value+1));
            }
        });

        final EditText et_gift = (EditText) item.findViewById(R.id.store_add_discount_dialog_et_gift);

        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.store_add_discount_dialog_bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tv_name = tv_discount_num.getText().toString();
                String gift_content = et_gift.getText().toString();
                new APIHandler.Post_promotion().execute(tv_name, gift_content, String.valueOf(1));
                alertDialog.dismiss();
            }
        });

        ImageButton bt_cancel = (ImageButton)item.findViewById(R.id.store_add_discount_dialog_bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                confirmCancelDialog(context, "提醒", "填寫的內容尚未送出\n確定回到上一頁嗎？",UNFINISHED_CONTENT, -1);
            }
        });

        alertDialog.show();
        setDialogSize(context, 0.8, 0.75);
    }

    public static void warningConfirmDialog(final Context context, String title, String content){
        View item = LayoutInflater.from(context).inflate(R.layout.store_warning_confirm_dialog, null);
        setTitle(context, title, 18);

        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(item)
                .create();
        setBackground(context);

        TextView tv_content = (TextView)item.findViewById(R.id.store_warning_confirm_tv_content);
        tv_content.setText(content);
        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.store_warning_confirm_bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
        setDialogSize(context, 0.75, 0.3);
    }


    public static void listConfirmDialog(final Context context, String title, List<String> items, final int mode, final int position){
        StoreMainActivity.alertDialogController.result = 0;
        View view = LayoutInflater.from(context).inflate(R.layout.store_dialog_list, null);
        setTitle(context, title, 18);
        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(view)
                .create();
        setBackground(context);
        ListView lv_item = (ListView) view.findViewById(R.id.lv_item);
        final StoreDialogAdapter adapter = new StoreDialogAdapter(context, items);
        lv_item.setAdapter(adapter);
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPosition = position;
                adapter.notifyDataSetChanged();
            }
        });

        ImageButton bt_confirm = (ImageButton) view.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                List<String> items = new ArrayList<String>();
                switch (mode){
                    case NOTICELIST_APPOINT:
                        //TODO: send FAIL msg
                        Log.d("Accept","Denying "+Integer.toString(StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(position).id));
                        StoreMainActivity.apiHandler.postSessionDeny( StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(position).id);
                        StoreMainActivity.fragmentController.storeAppointFragment.appointList.remove(position);
                        StoreMainActivity.fragmentController.storeAppointFragment.adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        ImageButton bt_cancel = (ImageButton) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
            setDialogSize(context, 0.75 , 0.5);
    }


    public static void confirmCancelDialog(final Context context, String title, String content, final int mode, final int position){
        View item = LayoutInflater.from(context).inflate(R.layout.store_confirm_cancel_dialog, null);
        setTitle(context, title, 18);

        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(item)
                .create();
        setBackground(context);

        final TextView tv_content = (TextView)item.findViewById(R.id.store_confirm_cancel_tv_content);
        tv_content.setText(content);
        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.store_confirm_cancel_bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switch (mode){
                    case NOTICE1_APPOINT:
                        StoreMainActivity.alertDialogController.confirmCancelDialog(context,"提醒", "選擇店內已無空位\n會影響您的訂位成功率喔",NOTICE2_APPOINT, position);
                        break;
                    case NOTICE2_APPOINT:
                        List<String> items = new ArrayList<String>();
                        items.add("未見該客戶");
                        items.add("店內已無空位");
                        Log.d("Accept","Denying "+Integer.toString(StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(position).id));
                        StoreMainActivity.alertDialogController.listConfirmDialog(context,"取消原因",items,NOTICELIST_APPOINT, position);
                        break;
                    case NOTICELIST_APPOINT:
                        //TODO: send FAIL msg
                        Log.d("Accept","Denying "+Integer.toString(StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(position).id));
                        StoreMainActivity.apiHandler.postSessionDeny( StoreMainActivity.fragmentController.storeAppointFragment.appointList.get(position).id);
                        StoreMainActivity.fragmentController.storeAppointFragment.appointList.remove(position);
                        StoreMainActivity.fragmentController.storeAppointFragment.adapter.notifyDataSetChanged();
                        break;
                }
            }
        });
        ImageButton bt_cancel = (ImageButton)item.findViewById(R.id.store_confirm_cancel_bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switch (mode){
                    case UNFINISHED_CONTENT:
                        //Resume the filled content
                        addDiscountDialog(context);
                        break;
                }
            }
        });
        alertDialog.show();
        setDialogSize(context, 0.72, 0.45);
    }


    public static void chart_listConfirmDialog(final Context context, String title, List<String> items, final int mode, final int position){
        StoreMainActivity.alertDialogController.result = 0;
        View view = LayoutInflater.from(context).inflate(R.layout.store_dialog_list, null);
        setTitle(context, title, 18);
        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(view)
                .create();
        setBackground(context);
        final ListView lv_item = (ListView) view.findViewById(R.id.lv_item);
        final StoreDialogAdapter adapter = new StoreDialogAdapter(context, items);
        lv_item.setAdapter(adapter);
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                listPosition = position;
                opentime_choose_result = position+OPENTIME_CHOOSE_DAY;
                if(mode == OPENTIME_CHOOSE) StoreManageOpentimeFragment.current_state = position;
            }
        });

        ImageButton bt_confirm = (ImageButton) view.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                List<String> items = new ArrayList<String>();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                int thisYear = calendar.get(Calendar.YEAR);
                int thisMonth = calendar.get(Calendar.MONTH);
                int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
                switch (mode){
                    case OPENTIME_CHOOSE:
                        switch (opentime_choose_result){
                            case OPENTIME_CHOOSE_DAY:
                                String date = String.format("%d/%02d/%02d", thisYear, thisMonth+1, thisDay);
                                StoreManageOpentimeFragment.tv_period.setText(date);
                                StoreManageOpentimeFragment.tv_info.setText(date+" 時段整理");
                                StoreManageOpentimeFragment.chart_bar.clear();
                                BarData barData = StoreManageOpentimeFragment.getBarData();
                                StoreManageOpentimeFragment.chart_bar.setData(barData);
                                tv_time_choose.setText(StoreManageOpentimeFragment.period_type[listPosition]);
                                tv_time_choose.setBackgroundResource(R.color.colorHalfTransparent);
                                break;
                            case OPENTIME_CHOOSE_WEEK:
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                                int[] gap = {-14, 6, 1, 6, 1, 6};
                                for(int i = 0; i < 6; i += 2){
                                    calendar.add(Calendar.DATE, gap[i]);
                                    String startDate = df.format(calendar.getTime());
                                    calendar.add(Calendar.DATE, gap[i+1]);
                                    String endDate = df.format(calendar.getTime());
                                    items.add(startDate+" - "+endDate);
                                }
                                StoreMainActivity.alertDialogController.chart_listConfirmDialog(context,"圖表期間選擇",items, OPENTIME_CHOOSE_DETAIL, position);
                                break;
                            case OPENTIME_CHOOSE_MONTH:
                                thisMonth -= 2;
                                if(thisMonth < 0){
                                    thisMonth += 12;
                                    thisYear -= 1;
                                }
                                items.add(thisYear+" "+month_to_Chinese[thisMonth]+"月");
                                items.add((thisMonth==11? thisYear+1: thisYear) +" "+month_to_Chinese[thisMonth==11? 0 : (thisMonth+1)]+"月");
                                items.add((thisMonth==11? thisYear+1: thisYear) +" "+month_to_Chinese[thisMonth==11? 1 : (thisMonth+2)]+"月");
                                StoreMainActivity.alertDialogController.chart_listConfirmDialog(context,"圖表期間選擇",items, OPENTIME_CHOOSE_DETAIL, position);
                                break;
                        }
                        break;
                    case OPENTIME_CHOOSE_DETAIL:
                        String selected_period = (String)lv_item.getItemAtPosition(listPosition);
                        if(StoreManageOpentimeFragment.current_state == StoreManageOpentimeFragment.WEEK){
                            String[] dates = selected_period.split(" - ");
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
                            try {
                                Date date = df.parse(dates[0]);
                                String start_date = String.format("%02d/%02d", date.getMonth()+1, date.getDate());
                                date = df.parse(dates[1]);
                                String end_date = String.format("%02d/%02d", date.getMonth()+1, date.getDate());
                                StoreManageOpentimeFragment.tv_period.setText(start_date+" - "+end_date);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else{
                            StoreManageOpentimeFragment.tv_period.setText(selected_period);
                        }
                        tv_time_choose.setText(StoreManageOpentimeFragment.period_type[StoreManageOpentimeFragment.current_state]);
                        tv_time_choose.setBackgroundResource(R.color.colorHalfTransparent);
                        StoreManageOpentimeFragment.tv_info.setText(selected_period+" 時段整理");
                        BarData barData = StoreManageOpentimeFragment.getBarData();
                        StoreManageOpentimeFragment.chart_bar.clear();
                        StoreManageOpentimeFragment.chart_bar.setData(barData);
                        //TODO: set chart values......
                        break;
                }
            }
        });
        ImageButton bt_cancel = (ImageButton) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        setDialogSize(context, 0.75, 0.6);
    }

    //Change dialog size
    //Must be called after alertdialog.show()
    //relativeWidth(Height) is the rate relative to width/height of device screen
    private static void setDialogSize(Context context, double relativeWidth, double relativeHeight){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        lp.width = (int) (displayWidth * relativeWidth);
        lp.height = (int) (displayHeight * relativeHeight);
        alertDialog.getWindow().setAttributes(lp);
    }

    private static void setTitle(Context context, String title, int fontSize){
        titleBar = LayoutInflater.from(context).inflate(R.layout.store_title_bar, null);
        TextView tv_title = (TextView)titleBar.findViewById(R.id.title);
        tv_title.setText(title);
        tv_title.setTextSize(fontSize);
    }

    //Should be called after alert dialog created
    private static void setBackground(Context context){
        try {
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.store_alert_dialog_bg);
        } catch(NullPointerException e) {
            Toast.makeText(context, "Set alert dialog error", Toast.LENGTH_SHORT).show();
        }
    }


    public static class Finish_session extends AsyncTask<String,Void,Void> {
        int record_id;
        @Override
        protected Void doInBackground(String... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
        }
    }
}
