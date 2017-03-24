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

public class HomeFragment extends Fragment {

    private ImageView im_photo;
    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_discount;
    private TextView tv_gift;
    private ImageButton but_active;
    private View v;
    private StoreInfo storeInfo;

    public HomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Test
        storeInfo = new StoreInfo("Tasty","台北市南港區");
        func_Test(storeInfo.discountList);
        v = inflater.inflate(R.layout.store_home_fragment, container, false);
        //Image
        im_photo = (ImageView)v.findViewById(R.id.im_photo);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.ic_temp_store);
        im_photo.setImageBitmap(icon);

        tv_storename = (TextView)v.findViewById(R.id.tv_storename);
        tv_storename.setText(storeInfo.name);
        tv_address = (TextView)v.findViewById(R.id.tv_address);
        tv_address.setText(storeInfo.address);
        tv_discount = (TextView)v.findViewById(R.id.tv_discount);
        tv_discount.setText(Integer.toString(storeInfo.discountList.get(0).discount)+"折");
        tv_gift = (TextView)v.findViewById(R.id.tv_gift);
        tv_gift.setText(storeInfo.discountList.get(0).gift);
        but_active = (ImageButton)v.findViewById(R.id.bt_active);
        but_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {AlertDialogController.discountDialog(getActivity(),storeInfo,tv_discount,tv_gift);
            }
        });
        return v;
    }
    public void func_Test(List<StoreInfo.DiscountInfo> list){
        StoreInfo.DiscountInfo temp1 = new StoreInfo.DiscountInfo(95,"蛋餅");
        StoreInfo.DiscountInfo temp2 = new StoreInfo.DiscountInfo(85,"可愛臭臭人");
        StoreInfo.DiscountInfo temp3 = new StoreInfo.DiscountInfo(75,"肥宅臭臭人");
        list.add(temp1);
        list.add(temp2);
        list.add(temp3);
    }
}
