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
    private final Integer[] value;

    public StoreManageAdapter(Activity context, String[] itemname, Integer[] imgid, Integer[] value) {
        super(context, R.layout.store_manage_row, itemname);
        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
        this.value=value;
    }

    @NonNull
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.store_manage_row, null,true);

        TextView tvTitle = (TextView) rowView.findViewById(R.id.store_manage_tv_title);
        ImageView ivIcon = (ImageView) rowView.findViewById(R.id.store_manage_iv_icon);
        TextView tvValue = (TextView) rowView.findViewById(R.id.store_manage_tv_value);

        tvTitle.setText(itemname[position]);
        ivIcon.setImageResource(imgid[position]);
        if(itemname[position].equals("預約成功率")){
            tvValue.setText((value[position]+" %"));
        }
        if(itemname[position].equals("應付帳款明細")){
            tvValue.setText(("NT$ "+value[position]));
        }
        return rowView;

    };
}
