package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2017/3/24.
 */

public class RecentAdapter extends BaseAdapter{

    private Context context;
    private List<CustomerAppointInfo> list;
    private LayoutInflater layoutInflater;

    public RecentAdapter(Context context, List<CustomerAppointInfo> list){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
    }

    private class ViewHolder{
        public ImageView im_photo;
        public TextView tv_name;
        public TextView tv_number;
        public ViewHolder(ImageView im,TextView tv1, TextView tv2){
            this.im_photo = im;
            this.tv_name = tv1;
            this.tv_number = tv2;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.store_recent_item, null);
            holder = new ViewHolder(
                    (ImageView) convertView.findViewById(R.id.im_photo),
                    (TextView) convertView.findViewById(R.id.tv_name),
                    (TextView) convertView.findViewById(R.id.tv_number)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CustomerAppointInfo customerAppointInfo = list.get(position);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),customerAppointInfo.im_id);
        holder.im_photo.setImageBitmap(icon);
        holder.tv_name.setText(customerAppointInfo.name+"("+ Integer.toString(customerAppointInfo.honor) +")");
        holder.tv_number.setText("正向您即將預約("+customerAppointInfo.number+"人)");
        return convertView;
    }
}
