package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class StoreHomeFragment extends Fragment {

    private Context context;
    private ImageView im_photo;
    private ImageButton bt_QRcode;
    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_discount;
    private TextView tv_gift;
    private ImageButton bt_active;
    private GifImageView bt_active_gif;
    private TextView tv_active;
    private TextView tv_active_running;
    private TextView tv_active_remind;
    private View v;
    private StoreInfo storeInfo;
    private AlertDialog alertDialog;
    private BackGroundWorker worker = new BackGroundWorker(getContext());


    private static final int SCAN_REQUEST_ZXING_SCANNER = 1;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

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
        //func_Test(storeInfo.discountList);
        //TODO: get promotion from server
        //Start Here---------------------
        v = inflater.inflate(R.layout.store_home_fragment, container, false);
        v.setPadding(0, getStatusBarHeight(), 0, 0);
        //Image---------
        im_photo = (ImageView) v.findViewById(R.id.im_photo);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_temp_store);
        im_photo.setImageBitmap(icon);
        //--------------
        //TextView init-
        tv_storename = (TextView) v.findViewById(R.id.tv_storename);
        tv_storename.setText(storeInfo.name);
        tv_address = (TextView) v.findViewById(R.id.tv_address);
        tv_address.setText(storeInfo.address);
        tv_discount = (TextView) v.findViewById(R.id.tv_discount);
        tv_discount.setText(Integer.toString(storeInfo.discountList.get(StoreMainActivity.storeInfo.discountDefault).discount) + "折");
        tv_gift = (TextView) v.findViewById(R.id.tv_gift);
        tv_gift.setText(storeInfo.discountList.get(0).description);
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
                alertDialog = new AlertDialogController().discountDialog(getActivity(), storeInfo, tv_discount, tv_gift, bt_active, bt_active_gif, tv_active, tv_active_remind);
                alertDialog.show();
            }
        });

        bt_active_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_active.setText("立即尋客");
                tv_active_remind.setText("");
                bt_active.setVisibility(View.VISIBLE);
                bt_active.setEnabled(true);
                bt_active_gif.setVisibility(View.INVISIBLE);
                bt_active_gif.setEnabled(false);
                new APIHandler().postPromotionInactive();
                worker.killTimer();
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
        //--------------
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult(): requestCode = " + requestCode);
        if (requestCode == SCAN_REQUEST_ZXING_SCANNER) {
            if (resultCode == Activity.RESULT_OK) {
                String mResult = data.getStringExtra(QrcodeScannerActivity.SCAN_RESULT);
                Toast.makeText(getContext(), mResult, Toast.LENGTH_SHORT).show();
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                //   mResult = result.getContents();
                //   mTxtResult.setText(mResult);
            }
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
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_promotions?shop_id=" + params[0]);
                JSONArray responsePromotion = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                Log.d("NO~~~", Integer.toString(responsePromotion.length()));
                for (int i = 1; i < responsePromotion.length(); i++) {
                    int id = responsePromotion.getJSONObject(i).getInt("promotion_id");
                    HttpGet getPromotion = new HttpGet("https://flash-table.herokuapp.com/api/promotion_info?promotion_id=" + Integer.toString(id));
                    JSONObject promotion = new JSONObject(new BasicResponseHandler().handleResponse(httpClient.execute(getPromotion)));
                    int discount = promotion.getInt("name");
                    String description = promotion.getString("description");
                    StoreDiscountInfo info = new StoreDiscountInfo(id, discount, description);
                    StoreMainActivity.storeInfo.discountList.add(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {
            if (alertdialog_active) {
                alertDialog.dismiss();
                alertDialog = new AlertDialogController().discountDialog(getActivity(), storeInfo, tv_discount, tv_gift, bt_active, bt_active_gif, tv_active, tv_active_remind);
                alertDialog.show();
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
        return;
    }
    public void startUpdate(){
        worker.updateRequestList();
        return;
    }
    public void stopUpdate(){
        worker.killTimer();
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
}
