package com.example.yang.flashtable;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class StoreAppointAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<RecordInfo> list = new ArrayList<>();
    private Context context;
    private Handler handler = new Handler();
    String domain;
    private Runnable countDownRunnable = new Runnable() {
        @Override
        public void run() {
            for(int i=0;i<list.size();i++){

                long timeDiff = list.get(i).due_time - System.currentTimeMillis();
                if(timeDiff <= 0) {
                    list.get(i).isActive = false;
                }
            }
            notifyDataSetChanged();
        }
    };
    private static final int TIMEOUT = 0;
    private static final int WAITING = 1;

    public StoreAppointAdapter(Context context, List<RecordInfo> reservation_list, String domain) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = reservation_list;
        this.domain = domain;
        countDown();
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.store_appoint_row, parent, false);
            holder = new ViewHolder(position,
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_name),
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_date),
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_state),
                    (TextView) convertView.findViewById(R.id.tv_countdown),
                    (TextView) convertView.findViewById(R.id.tv_remaining_time),
                    (TextView) convertView.findViewById(R.id.store_appoint_row_tv_point),
                    (ImageView) convertView.findViewById(R.id.store_appoint_row_iv_icon),
                    (ImageButton) convertView.findViewById(R.id.bt_cancel));
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.setData(list.get(position));
        if(list.get(position).picture != null) {
            holder.im_photo.setImageBitmap(list.get(position).picture);
            Log.d("Session","set picture "+list.get(position).name);
        }
        else
            Log.d("Session","picture null "+list.get(position).name);

        return  convertView;
    }
    private class ViewHolder{
        TextView tv_name;
        TextView tv_date;
        TextView tv_state;
        TextView tv_countdown;
        TextView tv_remaintime;
        TextView tv_point;
        ImageView im_photo;
        ImageButton bt_cancel;
        int position;
        RecordInfo info;
        public ViewHolder(int position,TextView tv_name,TextView tv_date,TextView tv_state,TextView tv_countdown,TextView tv_remaintime, TextView tv_point, ImageView im_photo,ImageButton bt_cancel){
            this.position = position;
            this.tv_name = tv_name;
            this.tv_date = tv_date;
            this.tv_state = tv_state;
            this.tv_countdown = tv_countdown;
            this.tv_remaintime = tv_remaintime;
            this.tv_point = tv_point;
            this.im_photo = im_photo;
            this.bt_cancel = bt_cancel;
        }
        public void setData(RecordInfo info){
            this.info = info;
            tv_name.setText(info.name);
            tv_point.setText("  (信譽"+String.valueOf(info.point)+") ");
            long val = info.due_time;
            Date date=new Date(val);
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy/MM/dd  a hh: mm", Locale.ENGLISH);
            String dateText = df2.format(date);
            tv_date.setText(dateText);
            tv_state.setText("已成功向您預約 ("+Integer.toString(info.number)+") 人桌位");
            tv_remaintime.setText("剩餘抵達時間");
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("PressButton","Pressed"+Integer.toString(position));
                    new AlertDialogController(domain).confirmCancelDialog(context,"提醒","選擇未見該客戶\n將扣除客戶的信譽分數喔",AlertDialogController.NOTICE1_APPOINT,position);
                }
            });
            /*if(info.picture != null) {
                im_photo.setImageBitmap(info.picture);
                Log.d("Session","set picture "+info.name);
            }
            else
                Log.d("Session","picture null "+info.name);*/
            if(info.isActive && (info.due_time - System.currentTimeMillis())>0) {
                buttonControl(this, WAITING);
                int remain_time = (int)(info.due_time - System.currentTimeMillis())/1000;
                //TODO:
                tv_countdown.setText(String.format("%02d:%02d",remain_time/60,remain_time%60));
            }
            else
                buttonControl(this,TIMEOUT);
        }
    }
    private void countDown(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(countDownRunnable);
            }
        },1000,1000);
    }
    private void buttonControl(ViewHolder holder,int stat){
        if(stat == TIMEOUT){
            holder.bt_cancel.setEnabled(true);
            holder.bt_cancel.setImageResource(R.drawable.bt_store_appoint_cancel);
            holder.tv_countdown.setText("");
            holder.tv_remaintime.setText("");
        }
        else if(stat == WAITING){
            holder.bt_cancel.setEnabled(false);
            holder.bt_cancel.setImageResource(0);
        }
    }
}
