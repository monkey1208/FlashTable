package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.content.Context.MODE_PRIVATE;


public class StoreManageDiscountFragment extends ListFragment {

    private static List<StoreDiscountInfo> discountList;
    public static StoreManageDiscountAdapter adapter;
    private String shop_id;

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

        ListView lv = (ListView) v.findViewById(list);
        discountList = new ArrayList<>(StoreMainActivity.storeInfo.not_delete_discountList);
        adapter = new StoreManageDiscountAdapter(getContext(), discountList);
        lv.setAdapter(adapter);

        Toolbar bar = (Toolbar) v.findViewById(R.id.store_manage_discount_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_discount_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_DISCOUNT_DELETE);
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
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });

        LinearLayout add = (LinearLayout) v.findViewById(R.id.store_manage_discount_ll_add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialogController(getString(R.string.server_domain)).addDiscountDialog(getContext(), false);
            }
        });
        return v;
    }


    public void onListItemClick(ListView l, View v, int position, long id) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DefaultDiscount", MODE_PRIVATE);
        String default_description;
        int default_id;
        //Click the same promotion twice , cancel the default promotion
        if (StoreMainActivity.storeInfo.discountDefault == position) {
            discountList.get(StoreMainActivity.storeInfo.discountDefault).isDefault = false;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = -1;
            default_id = -1;
            default_description = "";
        } else if (StoreMainActivity.storeInfo.discountDefault != -1) { //There is default promotion
            discountList.get(StoreMainActivity.storeInfo.discountDefault).isDefault = false;
            discountList.get(position).isDefault = true;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = position;
            default_id = discountList.get(position).getId();
            default_description = discountList.get(position).description;
        } else { //There is "no" default promotion
            discountList.get(position).isDefault = true;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = position;
            default_id = discountList.get(position).getId();
            default_description = discountList.get(position).description;
        }
        sharedPreferences.edit().putString("description", default_description).putInt("promotion_id", default_id).apply();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void addPromotionList(StoreDiscountInfo info) {
        discountList.add(info);
        adapter.notifyDataSetChanged();
    }

    public static void deletePromotionList(int promotion_id) {
        for (int i = 0; i < discountList.size(); i++) {
            if (promotion_id == discountList.get(i).getId()) {
                discountList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }
}

