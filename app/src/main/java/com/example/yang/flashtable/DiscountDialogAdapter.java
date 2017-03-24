package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class DiscountDialogAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private List<StoreInfo.DiscountInfo> discount;

    public DiscountDialogAdapter(Context context,List<StoreInfo.DiscountInfo> discount){
        layoutInflater = LayoutInflater.from(context);
        this.discount = discount;
    }

    private class ViewHolder{
        TextView tv_discount;
        public ViewHolder(TextView tv){
            tv_discount = tv;
        }
    }

    @Override
    public int getCount() {
        return discount.size();
    }

    @Override
    public Object getItem(int position) {
        return discount.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.store_discount_item, null);
            holder = new ViewHolder(
                    (TextView) convertView.findViewById(R.id.tv_discount)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        StoreInfo.DiscountInfo discountInfo = discount.get(position);
        holder.tv_discount.setText(Integer.toString(discountInfo.discount)+"折 "+discountInfo.gift);
        return convertView;
    }
}
