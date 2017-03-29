package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        ListView lv =(ListView) v.findViewById(android.R.id.list);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(getContext(), "Jump to next page", Toast.LENGTH_SHORT).show();
            }
        });

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_tb_toolbar);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Logout
                Toast.makeText(v.getContext(),"Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        return v;
    }

    public void onListItemClick(ListView l, View v, int position, long id) {
        StoreMainActivity.fragmentController.act(position+4);
        Toast.makeText(getContext(), "Jump to page "+Integer.toString(position), Toast.LENGTH_SHORT).show();
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
}
