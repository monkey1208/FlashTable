package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class StoreHomeDiscountDialogAdapter extends BaseAdapter{

    private LayoutInflater layoutInflater;
    private List<StoreDiscountInfo> discount;
    private Context context;

    public StoreHomeDiscountDialogAdapter(Context context, List<StoreDiscountInfo> discount){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
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
        StoreDiscountInfo discountInfo = discount.get(position);
        holder.tv_discount.setText(Integer.toString(discountInfo.discount)+"折 "+discountInfo.description);
        if(position == StoreMainActivity.storeInfo.discountCurrent)
            convertView.setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
        else
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        TextView tv_default = (TextView)convertView.findViewById(R.id.tv_default);
        if(position == StoreMainActivity.storeInfo.discountDefault)
            tv_default.setText("預設");
        else
            tv_default.setText("");
        return convertView;
    }
}
