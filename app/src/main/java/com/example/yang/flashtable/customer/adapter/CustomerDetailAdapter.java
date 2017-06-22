package com.example.yang.flashtable.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.infos.CustomerDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    private List<CustomerDetailInfo> reservations;
    ImageView iv_shop, iv_credit;
    TextView tv_shop, tv_location, tv_time, tv_success;
    List<String> success_types;
    String persons;

    public CustomerDetailAdapter(Context _context, List<CustomerDetailInfo> _reservations) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        reservations = _reservations;
        success_types = new ArrayList<>();
        success_types.add(_context.getString(R.string.customer_detail_success));
        success_types.add(_context.getString(R.string.customer_detail_fail));
        persons = context.getResources().getString(R.string.customer_detail_persons);
    }

    @Override
    public int getCount() { return reservations.size(); }

    @Override
    public Object getItem(int index) { return reservations.get(index); }

    @Override
    public long getItemId(int position) { return reservations.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_detail_item, parent, false);
            iv_shop = (ImageView) convertView.findViewById(R.id.customer_detail_iv_shop);
            iv_credit = (ImageView) convertView.findViewById(R.id.customer_detail_iv_credit);
            tv_shop = (TextView) convertView.findViewById(R.id.customer_detail_tv_shop);
            tv_location = (TextView) convertView.findViewById(R.id.customer_detail_tv_location);
            tv_time = (TextView) convertView.findViewById(R.id.customer_detail_tv_time);
            tv_success = (TextView) convertView.findViewById(R.id.customer_detail_tv_success);
            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        tv_shop.setText(reservations.get(position).shop);
        tv_location.setText(reservations.get(position).location);
        tv_time.setText(reservations.get(position).time);
        iv_shop.setImageBitmap(reservations.get(position).image);
        boolean success = reservations.get(position).success;
        if (success) {
            iv_credit.setImageResource(R.drawable.customer_detail_credit_plus);
            tv_success.setTextColor(context.getResources().getColor(R.color.textColorOrange));
            tv_success.setText(success_types.get(0) + reservations.get(position).persons + persons);
        } else {
            iv_credit.setImageResource(R.drawable.customer_detail_credit_minus);
            tv_success.setTextColor(context.getResources().getColor(R.color.textColorRed));
            tv_success.setText(success_types.get(1) + reservations.get(position).persons + persons);
        }

    }
}
