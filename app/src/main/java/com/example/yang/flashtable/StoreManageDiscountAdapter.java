package com.example.yang.flashtable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;


public class StoreManageDiscountAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private final Context context;
    private final List<StoreDiscountInfo> list;

    public StoreManageDiscountAdapter(Context context, List<StoreDiscountInfo> list) {
        this.context=context;
        this.list = list;
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
        view = inflater.inflate(R.layout.store_manage_discount_row, null,true);

        TextView tv = (TextView) view.findViewById(R.id.store_manage_discount_row_tv_description);
        tv.setText(list.get(position).description);

        LinearLayout row = (LinearLayout)view.findViewById(R.id.store_manage_discount_row);
        ImageView _default = (ImageView) view.findViewById(R.id.store_manage_discount_row_iv_default);
        if(list.get(position).isDefault){
            _default.setImageResource(R.drawable.icon_default_discount);
            TextView to_default = (TextView) view.findViewById(R.id.store_manage_discount_row_tv_todefault);
            view.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
            row.removeView(to_default);
        }else{
            view.setBackgroundColor(context.getResources().getColor(R.color.colorHalfTransparent));
            row.removeView(_default);
        }
        return view;
    }
}
