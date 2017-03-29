package com.example.yang.flashtable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StoreHomeFragment extends Fragment {

    private ImageView im_photo;
    private ImageButton bt_QRcode;
    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_discount;
    private TextView tv_gift;
    private ImageButton bt_active;
    private View v;
    private StoreInfo storeInfo;

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
        bt_active = (ImageButton)v.findViewById(R.id.bt_active);
        bt_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {AlertDialogController.discountDialog(getActivity(),storeInfo,tv_discount,tv_gift);
            }
        });
        //--------------
        //QRcode button-
        bt_QRcode = (ImageButton)v.findViewById(R.id.bt_QRcode);
        bt_QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.CONFIRM);
            }
        });
        //--------------
        return v;
    }
    public void func_Test(List<StoreDiscountInfo> list){
        StoreDiscountInfo temp1 = new StoreDiscountInfo(95,"蛋餅");
        StoreDiscountInfo temp2 = new StoreDiscountInfo(85,"可愛臭臭人");
        StoreDiscountInfo temp3 = new StoreDiscountInfo(75,"肥宅臭臭人");
        list.add(temp1);
        list.add(temp2);
        list.add(temp3);
    }
}
