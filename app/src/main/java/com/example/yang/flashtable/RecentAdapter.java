package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class RecentAdapter extends BaseAdapter{

    private Context context;
    private List<CustomerAppointInfo> list;
    private LayoutInflater layoutInflater;
    private CustomerAppointInfo customerAppointInfo;
    private ViewHolder holder;
    private Handler handler;

    public RecentAdapter(Context context, List<CustomerAppointInfo> list){
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.list = list;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        //holder init----------
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
        //-------------------
        //set text-----------
        customerAppointInfo = list.get(position);
        if(customerAppointInfo.stat == CustomerAppointInfo.READY){
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),customerAppointInfo.im_id);
            holder.im_photo.setImageBitmap(icon);
            holder.tv_name.setText(customerAppointInfo.name+"("+ Integer.toString(customerAppointInfo.honor) +")");
            holder.tv_number.setText("正向您即將預約("+customerAppointInfo.number+"人)");
            holder.tv_countdown.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            holder.im_confirmed.setImageResource(0);
            holder.bt_confirm.setImageResource(R.drawable.bt_confirm_appoint);
            holder.bt_cancel.setImageResource(R.drawable.bt_cancel_appoint);
            holder.bt_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentController.storeRecentFragment.setItemStat(position,CustomerAppointInfo.CONFIRM,holder.im_confirmed);
                }
            });
            holder.bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentController.storeRecentFragment.setItemStat(position,CustomerAppointInfo.CANCEL,holder.im_confirmed);
                }
            });
        }
        else if(customerAppointInfo.stat == CustomerAppointInfo.CONFIRM){
            holder.im_confirmed.setImageResource(R.drawable.ic_confirmed);
            holder.bt_confirm.setImageResource(0);
            holder.bt_confirm.setEnabled(false);
            holder.bt_cancel.setImageResource(0);
            holder.bt_cancel.setEnabled(false);
            holder.im_photo.setImageResource(0);
            holder.tv_name.setText("");
            holder.tv_number.setText("");
            holder.tv_countdown.setTextColor(context.getResources().getColor(R.color.transparentGreen));
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.store_slide_left2right);
            holder.im_confirmed.setAnimation(animation);
        }
        else{
            holder.im_confirmed.setImageResource(R.drawable.ic_confirmed_false);
            holder.bt_confirm.setImageResource(0);
            holder.bt_confirm.setEnabled(false);
            holder.bt_cancel.setImageResource(0);
            holder.bt_cancel.setEnabled(false);
            holder.im_photo.setImageResource(0);
            holder.tv_name.setText("");
            holder.tv_number.setText("");
            holder.tv_countdown.setTextColor(context.getResources().getColor(R.color.transparentGreen));
            Animation animation = AnimationUtils.loadAnimation(context,R.anim.store_slide_right2left);
            holder.im_confirmed.setAnimation(animation);
            animation.setDuration(1000);
            animation.startNow();
        }
        holder.bt_confirm.setTag(holder);
        holder.bt_cancel.setTag(holder);
        return convertView;
    }
}
