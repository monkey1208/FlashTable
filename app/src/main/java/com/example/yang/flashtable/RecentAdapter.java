package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RecentAdapter extends ArrayAdapter<CustomerAppointInfo> {

    private Context context;
    private List<CustomerAppointInfo> list;
    private LayoutInflater layoutInflater;
    //private CustomerAppointInfo customerAppointInfo;
    private List<ViewHolder> lstholder;
    private Handler handler;
    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstholder){
                long currentTime = System.currentTimeMillis();
                for (ViewHolder holder : lstholder) {
                    holder.updateTimeRemaining(currentTime);
                    //holder.setTest();
                }
            }
        }
    };

    public RecentAdapter(Context context, List<CustomerAppointInfo> list){
        super(context,0,list);
        layoutInflater = LayoutInflater.from(context);
        lstholder = new ArrayList<>();
        handler = new Handler();
        this.context = context;
        this.list = list;
        countDown();
    }
    public void countDown(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(countDownRunnable);
            }
        },1000,1000);
    }

    private class ViewHolder{
        public ImageView im_photo;
        public TextView tv_name;
        public TextView tv_number;
        public TextView tv_countdown;
        public ImageButton bt_confirm;
        public ImageButton bt_cancel;
        public ImageView im_confirmed;
        public CustomerAppointInfo customerAppointInfo;
        public ViewHolder(ImageView im,TextView name, TextView number,TextView countdown,ImageButton confirm,ImageButton cancel,ImageView im_confirmed){
            this.im_photo = im;
            this.tv_name = name;
            this.tv_number = number;
            this.tv_countdown = countdown;
            this.bt_confirm = confirm;
            this.bt_cancel = cancel;
            this.im_confirmed = im_confirmed;
        }
        public void setData(CustomerAppointInfo info,final int position){
            customerAppointInfo = info;
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),customerAppointInfo.im_id);
            im_photo.setImageBitmap(icon);
            tv_name.setText(customerAppointInfo.name+"("+ Integer.toString(customerAppointInfo.honor) +")");
            tv_number.setText("正向您即將預約("+customerAppointInfo.number+"人)");
            tv_countdown.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            im_confirmed.setImageResource(0);
            bt_confirm.setImageResource(R.drawable.bt_confirm_appoint);
            bt_cancel.setImageResource(R.drawable.bt_cancel_appoint);
            bt_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder Clickholder = (ViewHolder) v.getTag();
                    Clickholder.im_confirmed.setImageResource(R.drawable.ic_confirmed);
                    Clickholder.bt_confirm.setImageResource(0);
                    Clickholder.bt_confirm.setEnabled(false);
                    Clickholder.bt_cancel.setImageResource(0);
                    Clickholder.bt_cancel.setEnabled(false);
                    Clickholder.im_photo.setImageResource(0);
                    Clickholder.tv_name.setText("");
                    Clickholder.tv_number.setText("");
                    Clickholder.tv_countdown.setTextColor(context.getResources().getColor(R.color.transparentGreen));
                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.store_slide_left2right);
                    Clickholder.im_confirmed.setAnimation(animation);
                    FragmentController.storeRecentFragment.setItemStat(position,Clickholder.im_confirmed);
                }
            });
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewHolder Clickholder = (ViewHolder)v.getTag();
                    Clickholder.im_confirmed.setImageResource(R.drawable.ic_confirmed_false);
                    Clickholder.bt_confirm.setImageResource(0);
                    Clickholder.bt_confirm.setEnabled(false);
                    Clickholder.bt_cancel.setImageResource(0);
                    Clickholder.bt_cancel.setEnabled(false);
                    Clickholder.im_photo.setImageResource(0);
                    Clickholder.tv_name.setText("");
                    Clickholder.tv_number.setText("");
                    Clickholder.tv_countdown.setTextColor(context.getResources().getColor(R.color.transparentGreen));
                    Animation animation = AnimationUtils.loadAnimation(context,R.anim.store_slide_right2left);
                    Clickholder.im_confirmed.setAnimation(animation);
                    animation.setDuration(1000);
                    animation.startNow();
                    FragmentController.storeRecentFragment.setItemStat(position,Clickholder.im_confirmed);
                }
            });
            this.bt_confirm.setTag(this);
            this.bt_cancel.setTag(this);
        }
        public void updateTimeRemaining(long currenttime){
            long timeDiff = customerAppointInfo.expireTime - currenttime;
            if (timeDiff > 0) {
                int seconds = (int) (timeDiff / 1000);
                tv_countdown.setText(seconds + "s");
            } else {
                tv_countdown.setText("Expired!!");
            }
        }
        public void setTest(){
            tv_countdown.setText("HELLO");
        }
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
        //holder init----------
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
            synchronized (lstholder) {
                lstholder.add(holder);
            }
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        //-------------------
        //set text-----------
        //customerAppointInfo = list.get(position);
        holder.setData(getItem(position),position);
        return convertView;
    }
}
