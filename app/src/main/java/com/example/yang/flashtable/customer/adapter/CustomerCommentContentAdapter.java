package com.example.yang.flashtable.customer.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.CustomerRatingActivity;
import com.example.yang.flashtable.customer.CustomerViewRatingActivity;
import com.example.yang.flashtable.customer.infos.CustomerCommentContentInfo;

import java.util.List;

/**
 * Created by Yang on 2017/7/5.
 */

public class CustomerCommentContentAdapter extends ArrayAdapter<CustomerCommentContentInfo> {
    LayoutInflater inflater;
    boolean has_comment;
    Context c;
    public CustomerCommentContentAdapter(Context context, List object, boolean has_comment){
        super(context, R.layout.customer_comment_content_item, object);
        c = context;
        this.has_comment = has_comment;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            final CustomerCommentContentInfo item = getItem(position);

            convertView = inflater.inflate(R.layout.customer_comment_content_item, parent, false);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.customer_comment_content_iv_shop);
            imageView.setImageBitmap(item.img);
            TextView shop_name = (TextView)convertView.findViewById(R.id.customer_comment_content_tv_shop);
            shop_name.setText(item.shop_name);
            TextView address = (TextView)convertView.findViewById(R.id.customer_comment_content_tv_location);
            address.setText(item.address);
            TextView time = (TextView)convertView.findViewById(R.id.customer_comment_content_tv_time);
            time.setText(item.time);
            if(!has_comment) {
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(c, CustomerRatingActivity.class);
                        intent.putExtra("shop", item.shop_name);
                        intent.putExtra("shop_location", item.address);
                        intent.putExtra("shop_id", item.shopId);
                        intent.putExtra("record_id", Integer.parseInt(item.recordId));
                        c.startActivity(intent);
                    }
                });
            }else{
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(c, CustomerViewRatingActivity.class);
                        intent.putExtra("shop", item.shop_name);
                        intent.putExtra("shop_location", item.address);
                        intent.putExtra("shop_id", item.shopId);
                        intent.putExtra("rating", item.rating);
                        intent.putExtra("comment", item.comment);
                        c.startActivity(intent);
                    }
                });
            }
        }
        return convertView;
    }
}
