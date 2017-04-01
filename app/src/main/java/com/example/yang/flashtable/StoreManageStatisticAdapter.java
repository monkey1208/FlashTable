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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StoreManageStatisticAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<StoreDiscountInfo> discount_list = new ArrayList<>();
    private Context context;

    public StoreManageStatisticAdapter(Context context, List<StoreDiscountInfo> discount_list) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.discount_list = discount_list;
        Collections.sort(this.discount_list,
                new Comparator<StoreDiscountInfo>() {
                    public int compare(StoreDiscountInfo o1, StoreDiscountInfo o2) {
                        return o2.count - o1.count;
                    }
                });
    }
    @Override
    public int getCount() {
        return discount_list.size();
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
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.store_manage_statistic_row, parent, false);
        ImageView icon = (ImageView)convertView.findViewById(R.id.store_manage_statistic_row_iv_rank);
        TextView tv_rank = (TextView) convertView.findViewById(R.id.store_manage_statistic_row_tv_rank);
        LinearLayout container = (LinearLayout)convertView.findViewById(R.id.store_manage_statistic_row);
        switch (position) {
            case 0:
                icon.setImageResource(R.drawable.icon_rank1);
                container.removeView(tv_rank);
                break;
            case 1:
                icon.setImageResource(R.drawable.icon_rank2);
                container.removeView(tv_rank);
                break;
            case 2:
                icon.setImageResource(R.drawable.icon_rank3);
                container.removeView(tv_rank);
                break;
            default:
                tv_rank.setText(String.valueOf(position+1));
                container.removeView(icon);
                break;
        }
        TextView tv_detail = (TextView) convertView.findViewById(R.id.store_manage_statistic_row_tv_detail);
        tv_detail.setText(Integer.toString(discount_list.get(position).discount)+" "+discount_list.get(position).description);
        TextView tv_count = (TextView) convertView.findViewById(R.id.store_manage_statistic_row_tv_num);
        tv_count.setText(String.valueOf(discount_list.get(position).count) + "人");
        return  convertView;

    }
}
