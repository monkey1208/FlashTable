package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.MODE_PRIVATE;

public class StoreHomeFragment extends Fragment {

    private Context context;
    private ImageView im_photo;
    private ImageButton bt_QRcode;
    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_gift;
    private ImageButton bt_active;
    private GifImageView bt_active_gif;
    private TextView tv_active;
    private TextView tv_active_running;
    private TextView tv_active_remind;
    private TextView tv_active_time;
    private ImageView iv_gift_icon;
    private View v;
    private StoreInfo storeInfo;
    private AlertDialog alertDialog;
    private Timer timer = new Timer();
    private Handler handler = new Handler();
    private BackGroundWorker worker = new BackGroundWorker(getContext());
    private long promotion_start_time;


    private static final int SCAN_REQUEST_ZXING_SCANNER = 1;

    private boolean alertdialog_active = false;

    public StoreHomeFragment() {
        context = getContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Test
        storeInfo = StoreMainActivity.storeInfo;

        //TODO: get promotion from server
        //Start Here---------------------
        v = inflater.inflate(R.layout.store_home_fragment, container, false);
        v.setPadding(0, getStatusBarHeight(), 0, 0);
        //Image---------
        im_photo = (ImageView) v.findViewById(R.id.im_photo);
        iv_gift_icon = (ImageView) v.findViewById(R.id.store_home_iv_gift_icon);
        iv_gift_icon.setVisibility(View.INVISIBLE);
        Picasso.with(getContext()).load(storeInfo.url).into(im_photo);
        //--------------
        //TextView init-
        tv_storename = (TextView) v.findViewById(R.id.tv_storename);
        tv_storename.setText(storeInfo.name);
        tv_address = (TextView) v.findViewById(R.id.tv_address);
        tv_address.setText(storeInfo.address);
        tv_gift = (TextView) v.findViewById(R.id.tv_gift);
        tv_gift.setText("");
        tv_active_time = (TextView) v.findViewById(R.id.tv_active_time);
        //--------------
        //立即尋客button
        bt_active_gif = (GifImageView) v.findViewById(R.id.bt_active_gif);
        bt_active_gif.setVisibility(View.INVISIBLE);
        tv_active = (TextView) v.findViewById(R.id.tv_active);
        tv_active_remind = (TextView) v.findViewById(R.id.tv_active_remind);
        tv_active_running = (TextView) v.findViewById(R.id.tv_active_running);
        bt_active = (ImageButton) v.findViewById(R.id.bt_active);
        bt_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog_active = true;
                alertDialog = new AlertDialogController(getString(R.string.server_domain)).discountDialog(getContext(), tv_gift, bt_active, bt_active_gif, tv_active, tv_active_remind, iv_gift_icon);
                alertDialog.show();
                setDialogSize();
            }
        });

        bt_active_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_gift.setVisibility(View.INVISIBLE);
                iv_gift_icon.setVisibility(View.INVISIBLE);
                tv_active.setText("立即尋客");
                tv_active.setVisibility(View.VISIBLE);
                tv_active_remind.setText("");
                bt_active.setVisibility(View.VISIBLE);
                bt_active.setEnabled(true);
                bt_active_gif.setVisibility(View.INVISIBLE);
                bt_active_gif.setEnabled(false);
                new APIHandler(getString(R.string.server_domain)).postPromotionInactive();
                stopUpdate();
            }
        });
        //--------------
        //QRcode button-
        bt_QRcode = (ImageButton) v.findViewById(R.id.bt_QRcode);
        bt_QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                Intent intent = new Intent(getContext(), QrcodeScannerActivity.class);
                startActivityForResult(intent, SCAN_REQUEST_ZXING_SCANNER);
                //StoreMainActivity.fragmentController.act(FragmentController.CONFIRM);
            }
        });
        new APIpromotion().execute(StoreMainActivity.storeInfo.id);
        Log.d("HomeInit","done");
        //--------------
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Session", "onActivityResult(): requestCode = " + requestCode);
        Log.d("Session","GetBundle");
        if (requestCode == SCAN_REQUEST_ZXING_SCANNER) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Session","GetBundle");
                String session_id =  data.getExtras().getString("session_id");
                StoreMainActivity.fragmentController.storeAppointFragment.deleteSession(session_id);
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, 2);
        }
    }

    public class APIpromotion extends AsyncTask<String, Void, Void> {
        boolean exception = false;
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet(getString(R.string.server_domain)+"/api/shop_promotions?shop_id=" + params[0]+"&verbose=1");
                JSONArray responsePromotion = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                for (int i = 1; i < responsePromotion.length(); i++) {
                    JSONObject promotion = responsePromotion.getJSONObject(i);
                    int id = promotion.getInt("promotion_id");
                    String description = promotion.getString("description");
                    int count = promotion.getInt("n_succ");
                    String notDelete = promotion.getString("is_removed");
                    String isActive_str = promotion.getString("is_active");
                    boolean isActive = isActive_str.equals("true");
                    boolean isRemoved = notDelete.equals("true");
                    StoreDiscountInfo info = new StoreDiscountInfo(id, description,isRemoved, count, isActive);
                    StoreMainActivity.storeInfo.discountList.add(info);
                    if(!isRemoved){
                        StoreMainActivity.storeInfo.not_delete_discountList.add(info);
                    }
                }
            } catch (IOException e) {
               exception = true;
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {
            if(exception){
                new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
            }else {
                if (alertdialog_active) {
                    alertDialog.dismiss();
                    alertDialog = new AlertDialogController(getString(R.string.server_domain)).discountDialog(getContext(), tv_gift, bt_active, bt_active_gif, tv_active, tv_active_remind, iv_gift_icon);
                    alertDialog.show();
                    setDialogSize();
                }

                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DefaultDiscount", MODE_PRIVATE);
                int default_id = sharedPreferences.getInt("promotion_id", -1);
                String default_description = sharedPreferences.getString("description", "");
                for (int i = 0; i < StoreMainActivity.storeInfo.not_delete_discountList.size(); i++) {
                    if (StoreMainActivity.storeInfo.not_delete_discountList.get(i).isActive) {
                        startUpdate();
                        bt_active.setVisibility(View.INVISIBLE);
                        tv_active.setVisibility(View.INVISIBLE);
                        iv_gift_icon.setVisibility(View.VISIBLE);
                        bt_active_gif.setVisibility(View.VISIBLE);
                        bt_active_gif.setImageResource(R.drawable.bt_resize_activate);
                        StoreMainActivity.fragmentController.storeHomeFragment.setActive();
                        StoreMainActivity.storeInfo.changeDiscountCurrentId(StoreMainActivity.storeInfo.not_delete_discountList.get(i).getId());
                        bt_active.setEnabled(false);
                        bt_active_gif.setImageResource(R.drawable.bt_resize_activate);
                        bt_active_gif.setEnabled(true);
                        tv_active_remind.setText("按下後暫停");
                        tv_gift.setText(StoreMainActivity.storeInfo.not_delete_discountList.get(i).description);

                    }
                    if (default_id != -1 && StoreMainActivity.storeInfo.not_delete_discountList.get(i).description.equals(default_description)) {
                        StoreMainActivity.storeInfo.not_delete_discountList.get(i).isDefault = true;
                        StoreMainActivity.storeInfo.discountDefault = i;
                    }
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

    public void setActive() {
        tv_active_running.setText("開啟中");
        promotion_start_time = System.currentTimeMillis();
        counting();
        return;
    }
    public void startUpdate(){
        worker.updateRequestList(getString(R.string.server_domain));
        return;
    }
    public void stopUpdate(){
        worker.killTimer();
        timer.cancel();
        timer = new Timer();
        tv_active_time.setText("");
        tv_active_running.setText("");
        return;
    }
    public void stopGIF(){
        bt_active_gif.setImageResource(0);
        return;
    }
    public void startGIF(){
        bt_active_gif.setImageResource(R.drawable.bt_resize_activate);
        return;
    }

    private void setDialogSize(){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        lp.width = (int) (displayWidth * 0.8);
        lp.height = (int) (displayHeight * 0.75);
        lp.dimAmount=0.65f;
        alertDialog.getWindow().setAttributes(lp);
        alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }
    private void counting(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long time = System.currentTimeMillis() - promotion_start_time;
                        int sec = (int)time/1000;
                        int min = sec/60;sec = sec%60;
                        int hr = min/60;min = min%60;
                        String str_time = String.format("%02d：%02d：%02d",hr,min,sec);
                        tv_active_time.setText(str_time);
                    }
                });
            }
        },0,1000);
    }

}
