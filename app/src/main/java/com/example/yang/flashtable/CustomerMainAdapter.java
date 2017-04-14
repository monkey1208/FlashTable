package com.example.yang.flashtable;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainAdapter extends ArrayAdapter<CustomerRestaurantInfo> {
    private Context c;
    private LayoutInflater inflater;

    List objects;
    DialogBuilder dialog_builder;
    ImageView iv_shop;
    TextView tv_shop, tv_price, tv_distance, tv_discount, tv_gift;
    RatingBar rb_rating;
    LinearLayout ll_reserve;
    Location current_location;

    public CustomerMainAdapter(Context context, List _objects, LatLng current_latlng) {
        super(context, R.layout.customer_main_item, _objects);
        objects = _objects;
        c = context;
        inflater = LayoutInflater.from(context);
        this.current_location = new Location("");
        current_location.setLatitude(current_latlng.latitude);
        current_location.setLongitude(current_latlng.longitude);
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
            tv_discount = (TextView) convertView.findViewById(R.id.customer_main_tv_discount);
            tv_gift = (TextView) convertView.findViewById(R.id.customer_main_tv_gift);
            rb_rating = (RatingBar) convertView.findViewById(R.id.customer_main_rb_rating);
            rb_rating.setRating(info.rating);
            rb_rating.setIsIndicator(true);
            ll_reserve = (LinearLayout) convertView.findViewById(R.id.customer_main_ll_reserve);
            iv_shop.setImageBitmap(info.getImage());
            tv_shop.setText(info.name);
            tv_price.setText("均消$"+Integer.toString(info.consumption));
            int discount = info.discount;
            if( discount == 101 ||discount == 100) {
                tv_discount.setText("暫無折扣");
            }else{
                int dis = discount/10;
                int point = discount%10;
                if(point == 0){
                    tv_discount.setText(dis+"折");
                }else{
                    tv_discount.setText(discount+"折");
                }
            }
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
        ll_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_builder.dialogEvent("請選擇人數", "personsPicker",
                        new DialogEventListener() {
                            @Override
                            public void clickEvent(boolean ok, int status) {
                                if (ok) {
                                    Intent intent = new Intent(c, CustomerReservationActivity.class);
                                    intent.putExtra("promotion_id", getItem(position).promotion_id);
                                    intent.putExtra("discount", getItem(position).discount);
                                    intent.putExtra("offer", getItem(position).offer);
                                    intent.putExtra("persons", status);
                                    intent.putExtra("shop_name", getItem(position).name);
                                    intent.putExtra("rating", Float.toString(getItem(position).rating));
                                    intent.putExtra("shop_location", getItem(position).detailInfo.address);
                                    intent.putExtra("shop_id", Integer.toString(getItem(position).id));
                                    c.startActivity(intent);
                                }
                            }
                        });
            }
        });
    }
}
