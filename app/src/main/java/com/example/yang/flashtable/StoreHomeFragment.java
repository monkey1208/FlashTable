package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
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

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

import static com.github.mikephil.charting.charts.Chart.LOG_TAG;

public class StoreHomeFragment extends Fragment {

    private ImageView im_photo;
    private ImageButton bt_QRcode;
    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_discount;
    private TextView tv_gift;
    private ImageButton bt_active;
    private GifImageView bt_active_gif;
    private TextView tv_active;
    public static TextView tv_active_running;
    private TextView tv_active_remind;
    private View v;
    private StoreInfo storeInfo;


    private static final int SCAN_REQUEST_ZXING_SCANNER = 1;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    public StoreHomeFragment() {
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
        func_Test(storeInfo.discountList);
        //Start Here---------------------
        v = inflater.inflate(R.layout.store_home_fragment, container, false);
        v.setPadding(0,getStatusBarHeight(),0,0);
        //Image---------
        im_photo = (ImageView)v.findViewById(R.id.im_photo);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_temp_store);
        im_photo.setImageBitmap(icon);
        //--------------
        //TextView init-
        tv_storename = (TextView)v.findViewById(R.id.tv_storename);
        tv_storename.setText(storeInfo.name);
        tv_address = (TextView)v.findViewById(R.id.tv_address);
        tv_address.setText(storeInfo.address);
        tv_discount = (TextView)v.findViewById(R.id.tv_discount);
        tv_discount.setText(Integer.toString(storeInfo.discountList.get(StoreMainActivity.storeInfo.discountDefault).discount)+"折");
        tv_gift = (TextView)v.findViewById(R.id.tv_gift);
        tv_gift.setText(storeInfo.discountList.get(0).description);
        //--------------
        //立即尋客button
        bt_active_gif = (GifImageView)v.findViewById(R.id.bt_active_gif);
        bt_active_gif.setVisibility(View.INVISIBLE);
        tv_active = (TextView)v.findViewById(R.id.tv_active);
        tv_active_remind = (TextView)v.findViewById(R.id.tv_active_remind);
        tv_active_running = (TextView)v.findViewById(R.id.tv_active_running);
        bt_active = (ImageButton)v.findViewById(R.id.bt_active);
        bt_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialogController.discountDialog(getActivity(),storeInfo,tv_discount,tv_gift, bt_active, bt_active_gif, tv_active, tv_active_remind);
            }
        });

        bt_active_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Stop", Toast.LENGTH_SHORT).show();
            }
        });
        //--------------
        //QRcode button-
        bt_QRcode = (ImageButton)v.findViewById(R.id.bt_QRcode);
        bt_QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCameraPermission();
                Intent intent = new Intent(getContext(), QrcodeScannerActivity.class);
                startActivityForResult(intent, SCAN_REQUEST_ZXING_SCANNER);
                //StoreMainActivity.fragmentController.act(FragmentController.CONFIRM);
            }
        });
        //--------------
        return v;
    }
    public void func_Test(List<StoreDiscountInfo> list){
        StoreDiscountInfo temp1 = new StoreDiscountInfo(95,"蛋餅");
        StoreDiscountInfo temp2 = new StoreDiscountInfo(85,"可愛臭臭人",10,false);
        StoreDiscountInfo temp3 = new StoreDiscountInfo(75,"肥宅臭臭人");
        list.add(temp1);
        list.add(temp2);
        list.add(temp3);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(LOG_TAG, "onActivityResult(): requestCode = " + requestCode);
        if (requestCode == SCAN_REQUEST_ZXING_SCANNER) {
            if(resultCode == Activity.RESULT_OK){
                String mResult = data.getStringExtra(QrcodeScannerActivity.SCAN_RESULT);
                Toast.makeText(getContext(),mResult, Toast.LENGTH_SHORT).show();
            }
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if(result != null && result.getContents() != null) {
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

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
