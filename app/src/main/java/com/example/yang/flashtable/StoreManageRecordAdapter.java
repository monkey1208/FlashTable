package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Color;
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
        TextView tv_name= (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_name);
        TextView tv_date = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_date);
        TextView tv_state = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_state);
        TextView tv_point= (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_point);
        tv_name.setText(reservation_list.get(position).name);
        tv_point.setText("（信譽"+reservation_list.get(position).point+"）");
        tv_date.setText(reservation_list.get(position).record_time);
        if(reservation_list.get(position).is_succ.equals("true")){
            tv_state.setTextColor(Color.parseColor("#E41E1B"));
            tv_state.setText("預約 ("+reservation_list.get(position).number+") 人已到達");
        }else {
            tv_state.setTextColor(Color.parseColor("#6DBD61"));
            tv_state.setText("店家已取消預約 (" +reservation_list.get(position).number + ") 人桌位");
        }
        return  convertView;

    }
}
