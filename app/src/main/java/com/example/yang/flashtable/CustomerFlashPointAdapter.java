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

    private FrameLayout rl_1, rl_2;
    private LinearLayout ll_1, ll_2;
    private ImageView iv_1, iv_2;
    private TextView tv_title_1, tv_title_2;
    private TextView tv_points_1, tv_points_2;

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
            rl_1 = (FrameLayout)  convertView.findViewById(R.id.customer_points_rl_1);
            ll_1 = (LinearLayout) convertView.findViewById(R.id.customer_points_ll_coupon1);
            iv_1 = (ImageView) convertView.findViewById(R.id.customer_points_iv_1);
            tv_title_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_title_1);
            tv_points_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_price_1);

            /* rl_2 = (RoundedCornerLayout)  convertView.findViewById(R.id.customer_points_rl_2);
            ll_2 = (LinearLayout) convertView.findViewById(R.id.customer_points_ll_coupon2);
            iv_2 = (ImageView) convertView.findViewById(R.id.customer_points_iv_2);
            tv_title_2 = (TextView) convertView.findViewById(R.id.customer_points_tv_title_2);
            tv_points_2 = (TextView) convertView.findViewById(R.id.customer_points_tv_price_2);
*/
            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        /* if (coupons.size() < position * 2) {
            rl_1.removeView(ll_1);
            rl_2.removeView(ll_2);
            return;
        } */
        FlashCouponInfo coupon_1 = coupons.get(position);
        tv_title_1.setText(coupon_1.name);
        tv_points_1.setText(Integer.toString(coupon_1.flash_point));

        /* if (coupons.size() < position * 2 + 1) {
            rl_2.removeView(ll_2);
            Log.e("PointsPosition", "Returned at" + position);
            return;
        }
        FlashCouponInfo coupon_2 = coupons.get(position * 2 + 1);
        tv_title_2.setText(coupon_2.name);
        tv_points_2.setText(Integer.toString(coupon_2.flash_point));
        */
    }
}
