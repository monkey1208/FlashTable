package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StoreManageStatisticFragment extends ListFragment {
    private List<StoreDiscountInfo> promotion_succ_num;

    public StoreManageStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.store_manage_statistic_fragment, container, false);

        promotion_succ_num = new ArrayList<>(StoreMainActivity.storeInfo.discountList);
        Collections.sort(promotion_succ_num, new Comparator<StoreDiscountInfo>(){
            public int compare(StoreDiscountInfo item1, StoreDiscountInfo item2) {
                return item1.getCount()<item2.getCount()? 1:-1;
            }
        });
        StoreManageStatisticAdapter adapter = new StoreManageStatisticAdapter(getActivity(), promotion_succ_num);
        setListAdapter(adapter);

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_statistic_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });
        return v;
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
