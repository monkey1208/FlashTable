package com.example.yang.flashtable;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CS on 2017/5/22.
 */

public class CustomerFlashPointAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<FlashCouponInfo> coupons;

    private ImageView iv_1;
    private TextView tv_title_1;
    private TextView tv_points_1;

    public CustomerFlashPointAdapter(Context _context, List<FlashCouponInfo> _coupons) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        coupons = _coupons;
    }

    @Override
    public int getCount() { return coupons.size(); }

    @Override
    public Object getItem(int index) { return coupons.get(index); }

    @Override
    public long getItemId(int position) { return coupons.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_flash_point_item, parent, false);
            iv_1 = (ImageView) convertView.findViewById(R.id.customer_points_iv_1);
            tv_title_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_title_1);
            tv_points_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_price_1);

            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {

        FlashCouponInfo coupon_1 = coupons.get(position);
        tv_title_1.setText(coupon_1.name);
        tv_points_1.setText(Integer.toString(coupon_1.flash_point));

    }
}
