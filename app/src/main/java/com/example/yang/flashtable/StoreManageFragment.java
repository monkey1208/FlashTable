package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;


public class StoreManageFragment extends ListFragment {
    public  StoreManageFragment () {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_manage_fragment, container, false);
        StoreManageAdapter adapter = new StoreManageAdapter(getActivity(), itemname, imgid);
        setListAdapter(adapter);

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Logout
                AlertDialogController.warningConfirmDialog(getContext(), "提醒", "資料載入失敗，請重試");
                Toast.makeText(v.getContext(),"Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        StoreMainActivity.fragmentController.act(position+4);
    }

    String[] itemname ={
            "預約成功率",
            "開啟時段整理",
            "應付帳款明細",
            "折扣優惠",
            "優惠統計詳情",
            "預約紀錄",
    };

    Integer[] imgid={
            R.drawable.ic_store_manage_success,
            R.drawable.ic_store_manage_opentime,
            R.drawable.ic_store_manage_bill,
            R.drawable.ic_store_manage_discount,
            R.drawable.ic_store_manage_statistic,
            R.drawable.ic_store_manage_record,
    };

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
