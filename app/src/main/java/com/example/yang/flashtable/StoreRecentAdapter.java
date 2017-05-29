package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class StoreRecentAdapter extends BaseAdapter{

    private Context context;
    private List<CustomerAppointInfo> list = new ArrayList<>();
    private LayoutInflater layoutInflater;

    public StoreRecentAdapter(Context context,List<CustomerAppointInfo> list){
        this.context =context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public CustomerAppointInfo getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.store_recent_item, null);
            holder = new ViewHolder(
                    (ImageView) convertView.findViewById(R.id.im_photo),
                    (TextView) convertView.findViewById(R.id.tv_name),
                    (TextView) convertView.findViewById(R.id.tv_number),
                    (TextView) convertView.findViewById(R.id.tv_countdown),
                    (ImageButton) convertView.findViewById(R.id.bt_confirm),
                    (ImageButton) convertView.findViewById(R.id.bt_cancel),
                    (ImageView) convertView.findViewById(R.id.im_confirmed)
            );
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final CustomerAppointInfo info = list.get(position);
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),info.im_id);
        if(info.picture !=null)
            icon = info.picture;
        holder.im_photo.setImageBitmap(icon);
        holder.tv_name.setText(info.name+"(信譽"+ Integer.toString(info.honor) +")");
        holder.tv_number.setText("正向您即將預約("+info.number+"人)");
        holder.tv_countdown.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        holder.tv_countdown.setText(Integer.toString(info.expireTime));
        holder.im_confirmed.setImageResource(0);
        holder.bt_confirm.setImageResource(R.drawable.bt_confirm_appoint);
        holder.bt_cancel.setImageResource(R.drawable.bt_cancel_appoint);
        holder.bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.storeInfo.addAppointment(info);
                StoreMainActivity.fragmentController.storeRecentFragment.removeItem(position);
            }
        });
        holder.bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new APIHandler().postRequestDeny(list.get(position).id,list.get(position).name);
                StoreMainActivity.fragmentController.storeRecentFragment.removeItem(position);
            }
        });
        holder.bt_confirm.setTag(this);
        holder.bt_cancel.setTag(this);
        return convertView;
    }
    private class ViewHolder{
        public ImageView im_photo;
        public TextView tv_name;
        public TextView tv_number;
        public TextView tv_countdown;
        public ImageButton bt_confirm;
        public ImageButton bt_cancel;
        public ImageView im_confirmed;
        public ViewHolder(ImageView im,TextView name, TextView number,TextView countdown,ImageButton confirm,ImageButton cancel,ImageView im_confirmed){
            this.im_photo = im;
            this.tv_name = name;
            this.tv_number = number;
            this.tv_countdown = countdown;
            this.bt_confirm = confirm;
            this.bt_cancel = cancel;
            this.im_confirmed = im_confirmed;
        }
    }
    public void postRequestDeny(int id){
        Toast.makeText(context,Integer.toString(id),Toast.LENGTH_LONG).show();
    }
}
