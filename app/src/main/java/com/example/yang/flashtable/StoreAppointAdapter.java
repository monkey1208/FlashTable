package com.example.yang.flashtable;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class StoreAppointAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private List<ReservationInfo> list = new ArrayList<>();
    private Context context;
    private Handler handler = new Handler();
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

    public StoreAppointAdapter(Context context, List<ReservationInfo> reservation_list) {
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = reservation_list;
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
                    (ImageButton) convertView.findViewById(R.id.bt_cancel));
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();
        holder.setData(list.get(position));

        return  convertView;
    }
    private class ViewHolder{
        TextView tv_name;
        TextView tv_date;
        TextView tv_state;
        TextView tv_countdown;
        TextView tv_remaintime;
        ImageButton bt_cancel;
        int position;
        ReservationInfo info;
        public ViewHolder(int position,TextView tv_name,TextView tv_date,TextView tv_state,TextView tv_countdown,TextView tv_remaintime,ImageButton bt_cancel){
            this.position = position;
            this.tv_name = tv_name;
            this.tv_date = tv_date;
            this.tv_state = tv_state;
            this.tv_countdown = tv_countdown;
            this.tv_remaintime = tv_remaintime;
            this.bt_cancel = bt_cancel;
        }
        public void setData(ReservationInfo info){
            this.info = info;
            tv_name.setText(info.name);
            tv_date.setText("2017/07/21");
            tv_state.setText("已成功向您預約("+Integer.toString(info.number)+")人桌位");
            tv_countdown.setText(Integer.toString((int)((info.due_time - System.currentTimeMillis())/1000)));
            if(info.isActive) {
                buttonControl(this, WAITING);
                bt_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        List<String> items = new ArrayList<String>();
                        items.add("選擇未見該客戶\n將扣除客戶的信譽分數喔");
                        StoreMainActivity.alertDialogController.listConfirmDialog(context,"提醒",items,AlertDialogController.NOTICE1_APPOINT,position);
                    }
                });
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
