package com.example.yang.flashtable.customer.adapter;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;

import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainAdapter extends ArrayAdapter<CustomerRestaurantInfo> {
    private LayoutInflater inflater;


    DialogBuilder dialog_builder;
    ImageView iv_shop;
    TextView tv_shop, tv_price, tv_distance, tv_gift;
    RatingBar rb_rating;
    Location current_location;

    public CustomerMainAdapter(Context context, List _objects, Location location) {
        super(context, R.layout.customer_main_item, _objects);
        inflater = LayoutInflater.from(context);
        this.current_location = location;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            CustomerRestaurantInfo info = getItem(position);
            convertView = inflater.inflate(R.layout.customer_main_item, parent, false);
            dialog_builder = new DialogBuilder(getContext());
            iv_shop = (ImageView) convertView.findViewById(R.id.customer_main_iv_shop);
            tv_shop = (TextView) convertView.findViewById(R.id.customer_main_tv_name);
            tv_price = (TextView) convertView.findViewById(R.id.customer_main_tv_price);
            tv_distance = (TextView) convertView.findViewById(R.id.customer_main_tv_distance);
            tv_gift = (TextView) convertView.findViewById(R.id.customer_main_tv_gift);
            rb_rating = (RatingBar) convertView.findViewById(R.id.customer_main_rb_rating);
            rb_rating.setRating(info.rating);
            rb_rating.setIsIndicator(true);
            iv_shop.setImageBitmap(info.getImage());
            tv_shop.setText(info.name);
            tv_price.setText("均消"+Integer.toString(info.consumption)+"元");

            tv_gift.setText(info.offer);

            Location loc_shop = new Location("");
            loc_shop.setLatitude(info.latLng.latitude);
            loc_shop.setLongitude(info.latLng.longitude);
            float dis = current_location.distanceTo(loc_shop);
            if(dis>1000){
                tv_distance.setText((int)dis/1000+"."+(int)(dis%1000)/100+"km");
            }else {
                tv_distance.setText((int)dis+"m");
            }
            setView(position);
            return convertView;
        }
        return convertView;
    }

    private void setView(final int position) {
        rb_rating.setIsIndicator(true);

    }
}
