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


public class StoreAppointAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<ReservationInfo> reservation_list = new ArrayList<>();
    private List<ViewHolder> lstholder = new ArrayList<>();
    private Context c;

    public StoreAppointAdapter(Context c, List<ReservationInfo> reservation_list) {
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
        ViewHolder holder;
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.store_appoint_row, parent, false);
            holder = new ViewHolder((TextView) convertView.findViewById(R.id.store_appoint_row_tv_name),
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_date),
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_state));
            convertView.setTag(holder);
            synchronized (lstholder) {
                lstholder.add(holder);
            }
        }
        else
            holder = (ViewHolder) convertView.getTag();
        holder.setData(reservation_list.get(position));

        return  convertView;

    }
    private class ViewHolder{
        TextView tv_name;
        TextView tv_date;
        TextView tv_state;
        public ViewHolder(TextView tv_name,TextView tv_date,TextView tv_state){
            this.tv_name = tv_name;
            this.tv_date = tv_date;
            this.tv_state = tv_state;
        }
        public void setData(ReservationInfo info){
            tv_name.setText(info.name);
            tv_date.setText("2017/07/21");
            tv_state.setText("已成功向您預約("+Integer.toString(info.number)+")人桌位");
        }
    }
}
