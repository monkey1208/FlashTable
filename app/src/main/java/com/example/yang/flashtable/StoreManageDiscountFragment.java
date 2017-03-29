package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;


public class StoreManageDiscountFragment extends ListFragment {

    public static List<StoreDiscountInfo> discountList = StoreMainActivity.storeInfo.discountList;
    private StoreManageDiscountAdapter adapter;

    public StoreManageDiscountFragment() {
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
        final View v = inflater.inflate(R.layout.store_manage_discount_fragment, container, false);

        ListView lv =(ListView) v.findViewById(list);

        adapter = new StoreManageDiscountAdapter(getContext(),discountList);
        lv.setAdapter(adapter);

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_discount_tb_toolbar);
        bar.inflateMenu(R.menu.store_manage_discount_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Logout
                Toast.makeText(v.getContext(),"Delete", Toast.LENGTH_SHORT).show();
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
                StoreMainActivity.fragmentController.act(3);
            }
        });

        LinearLayout add = (LinearLayout)v.findViewById(R.id.store_manage_discount_ll_add);
        add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Add New Discount
                Toast.makeText(v.getContext(),"Add new discount", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        //To each record detail
        discountList.get(position).isDefault = true;
        discountList.get(StoreMainActivity.storeInfo.discountDefault).isDefault = false;
        adapter.notifyDataSetChanged();
        StoreMainActivity.storeInfo.discountDefault = position;
    }

}
