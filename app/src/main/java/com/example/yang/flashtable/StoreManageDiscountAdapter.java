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

    public  StoreManageDiscountAdapter(Context context, List<StoreDiscountInfo> list) {
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
        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.store_manage_discount_row, parent, false);
            holder.tv_description = (TextView) view.findViewById(R.id.store_manage_discount_row_tv_description);
            holder.row = (LinearLayout)view.findViewById(R.id.store_manage_discount_row);
            holder._default = (ImageView) view.findViewById(R.id.store_manage_discount_row_iv_default);
            holder.to_default = (TextView) view.findViewById(R.id.store_manage_discount_row_tv_todefault);
            view.setTag(holder);
        }else{
            holder = (ViewHolder)view.getTag();
        }

        holder.tv_description.setText(list.get(position).description);

        if(list.get(position).isDefault){
            holder._default.setVisibility(View.VISIBLE);
            holder.to_default.setVisibility(View.GONE);
            holder._default.setImageResource(R.drawable.icon_default_discount);
            view.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
        }else{
            view.setBackgroundColor(context.getResources().getColor(R.color.colorHalfTransparent));
            holder._default.setVisibility(View.GONE);
            holder.to_default.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private class ViewHolder {
        TextView tv_description;
        LinearLayout row;
        ImageView _default;
        TextView to_default;
    }
}
