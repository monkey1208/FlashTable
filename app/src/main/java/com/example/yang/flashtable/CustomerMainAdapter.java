package com.example.yang.flashtable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainAdapter extends ArrayAdapter<RestaurantInfo> {
    private Context c;
    private LayoutInflater inflater;
    public CustomerMainAdapter(Context context, List objects) {
        super(context, R.layout.customer_main_adapter, objects);
        c = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.customer_main_adapter, parent, false);

        if(convertView != null) {
            return convertView;
        }else {
            return super.getView(position, convertView, parent);
        }
    }
}
