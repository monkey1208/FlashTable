package com.example.yang.flashtable;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainAdapter extends ArrayAdapter<CustomerRestaurantInfo> {
    private Context c;
    private LayoutInflater inflater;
    ImageView iv_shop;
    TextView tv_shop, tv_price, tv_distance, tv_discount, tv_gift;
    RatingBar rb_rating;
    LinearLayout ll_reserve;

    public CustomerMainAdapter(Context context, List objects) {
        super(context, R.layout.customer_main_item, objects);
        c = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflater.inflate(R.layout.customer_main_item, parent, false);
            iv_shop = (ImageView) convertView.findViewById(R.id.customer_main_iv_shop);
            tv_shop = (TextView) convertView.findViewById(R.id.customer_main_tv_name);
            tv_price = (TextView) convertView.findViewById(R.id.customer_main_tv_price);
            tv_distance = (TextView) convertView.findViewById(R.id.customer_main_tv_distance);
            tv_discount = (TextView) convertView.findViewById(R.id.customer_main_tv_discount);
            tv_gift = (TextView) convertView.findViewById(R.id.customer_main_tv_gift);
            rb_rating = (RatingBar) convertView.findViewById(R.id.customer_main_rb_rating);
            ll_reserve = (LinearLayout) convertView.findViewById(R.id.customer_main_ll_reserve);
            setView(position);
            return convertView;
        }
        return convertView;
    }

    private void setView(int position) {
        rb_rating.setIsIndicator(true);
        ll_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(c, CustomerReservationActivity.class);
                c.startActivity(intent);
            }
        });
    }
}
