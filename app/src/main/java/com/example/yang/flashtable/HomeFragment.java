package com.example.yang.flashtable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {

    private TextView tv_storename;
    private TextView tv_address;
    private TextView tv_discount;
    private TextView tv_gift;
    private ImageButton but_active;
    private View v;
    private int count=0;
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
        storeInfo = new StoreInfo("Tasty","台北市南港區");
        v = inflater.inflate(R.layout.store_home_fragment, container, false);
        tv_storename = (TextView)v.findViewById(R.id.tv_storename);
        tv_storename.setText(storeInfo.name);
        tv_address = (TextView)v.findViewById(R.id.tv_address);
        tv_address.setText(storeInfo.address);
        tv_discount = (TextView)v.findViewById(R.id.tv_discount);
        tv_discount.setText(Integer.toString(storeInfo.discountList.get(0).discount));
        tv_gift = (TextView)v.findViewById(R.id.tv_gift);
        tv_gift.setText(storeInfo.discountList.get(0).gift);
        but_active = (ImageButton)v.findViewById(R.id.bt_active);
        but_active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog(getActivity());
            }
        });
        return v;
    }
    private void customDialog(Context context){
        final View item = LayoutInflater.from(context).inflate(R.layout.store_discount_dialog, null);
        new AlertDialog.Builder(context)
                .setTitle("折扣優惠")
                .setView(item)
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
    /*OnHeadlineSelectedListener mCallback;
    public interface OnHeadlineSelectedListener {
        public void onaitemSelected(String datestring);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }*/

}
