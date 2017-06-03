package com.example.yang.flashtable;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StoreManageDiscountDeleteAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private final Context context;
    private final List<StoreDiscountInfo> list;
    private boolean[] checked_position;

    public StoreManageDiscountDeleteAdapter(Context context, List<StoreDiscountInfo> list) {
        this.context=context;
        this.list = list;
        this.checked_position =  new boolean[list.size()];
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        view = inflater.inflate(R.layout.store_manage_discount_delete_row, null,true);

        TextView tv = (TextView) view.findViewById(R.id.store_manage_discount_delete_row_tv_description);
        tv.setText(list.get(position).description);

        if(checked_position[position]){
            view.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
        }
        return view;
    }

    public void setItemClick(boolean[] checked){
        checked_position = checked;
    }
}
