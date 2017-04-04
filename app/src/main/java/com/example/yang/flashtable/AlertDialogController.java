package com.example.yang.flashtable;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class AlertDialogController {

    private static AlertDialog alertDialog;
    public static StoreHomeDiscountDialogAdapter adapter;
    private static View titleBar;
    public static final int NOTICE1_APPOINT = 0;
    public static final int NOTICE2_APPOINT = 1;
    public static final int NOTICELIST_APPOINT = 2;
    public static int listPosition = -1;
    public static int result = 0;


    public static void discountDialog(final Context context, final StoreInfo storeInfo,final TextView tv_discount,final TextView tv_gift){
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
                StoreMainActivity.apiHandler.changePromotions();
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
                String gift_content = et_gift.getText().toString();
                //Add new discount of the store
                alertDialog.dismiss();
            }
        });
        ImageButton bt_cancel = (ImageButton)item.findViewById(R.id.store_add_discount_dialog_bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
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
                    case NOTICE1_APPOINT:
                        items.add("選擇店內已無空位\n會影響您的定位成功率");
                        StoreMainActivity.alertDialogController.listConfirmDialog(context,"提醒",items,NOTICE2_APPOINT,position);
                        break;
                    case NOTICE2_APPOINT:
                        items.add("未見該客戶");
                        items.add("店內已無空位");
                        StoreMainActivity.alertDialogController.listConfirmDialog(context,"取消原因",items,NOTICELIST_APPOINT,position);
                        break;
                    case NOTICELIST_APPOINT:
                        //TODO: send FAIL msg
                        StoreMainActivity.apiHandler.postSessionDeny();
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
        setDialogSize(context, 0.75, 0.5);
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
}
