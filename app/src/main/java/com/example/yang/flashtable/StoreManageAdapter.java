package com.example.yang.flashtable;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class StoreManageAdapter extends ArrayAdapter {
    private final Activity context;
    private final String[] itemname;
    private final Integer[] imgid;

    public StoreManageAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.store_manage_row, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.store_manage_row, null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.store_manage_tv_title);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.store_manage_iv_icon);

        txtTitle.setText(itemname[position]);
        imageView.setImageResource(imgid[position]);
        return rowView;

    };
}
