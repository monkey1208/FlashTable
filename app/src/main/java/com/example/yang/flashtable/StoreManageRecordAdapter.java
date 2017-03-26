package com.example.yang.flashtable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 奕先 on 2017/3/25.
 */

public class StoreManageRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ReservationInfo> reservation_list = new ArrayList<>();
    private Context c;

    public StoreManageRecordAdapter(Context c, List<ReservationInfo> reservation_list) {
        this.c = c;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reservation_list = reservation_list;
    }

    @Override
    public int getCount() {
        return reservation_list.size();
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
        convertView = inflater.inflate(R.layout.store_manage_record_row, parent, false);
        TextView tv1 = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_name);
        TextView tv2 = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_date);
        TextView tv3 = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_state);
        tv1.setText(reservation_list.get(position).name);
        tv2.setText("2017/07/21");
        int num = 6;
        tv3.setText("已成功向您預約("+num+")人桌位");
        return  convertView;

    }
}
