package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class StoreDialogAdapter extends BaseAdapter{

    public List<String> list = new ArrayList<>();
    public Context context;
    public LayoutInflater inflater;
    public int listPosition;

    public StoreDialogAdapter(Context context, List<String> items){
        this.list = items;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.store_dialog_item,null);
        TextView tv_item = (TextView) convertView.findViewById(R.id.tv_content);
        tv_item.setText(list.get(position));
        if(position == listPosition)
            tv_item.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
        return convertView;
    }
    public void setItemClick(int position){
        listPosition = position;
        return;
    }
}
