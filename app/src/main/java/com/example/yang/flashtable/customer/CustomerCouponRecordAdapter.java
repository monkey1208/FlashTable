package com.example.yang.flashtable.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.infos.CustomerCouponRecordInfo;

import java.util.List;

/**
 * Created by CS on 2017/5/29.
 */

public class CustomerCouponRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    List<CustomerCouponRecordInfo> records;

    TextView tv_description, tv_time, tv_points, tv_plus;

    public CustomerCouponRecordAdapter(Context _context, List<CustomerCouponRecordInfo> _records) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        records = _records;
    }

    @Override
    public int getCount() { return records.size(); }

    @Override
    public Object getItem(int index) { return records.get(index); }

    @Override
    public long getItemId(int position) { return records.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_coupon_record_item, parent, false);
            tv_description = (TextView) convertView.findViewById(R.id.customer_coupon_tv_records_description);
            tv_time = (TextView) convertView.findViewById(R.id.customer_coupon_tv_records_time);
            tv_points = (TextView) convertView.findViewById(R.id.customer_coupon_tv_records_points);
            tv_plus = (TextView) convertView.findViewById(R.id.customer_coupon_tv_records_plus);
            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        CustomerCouponRecordInfo info = records.get(position);
        tv_description.setText(info.name);
        tv_time.setText(info.time);
        tv_points.setText(String.valueOf(info.points));
        if (info.type == 1) tv_plus.setText("-");
        else tv_plus.setText("+");
    }


}
