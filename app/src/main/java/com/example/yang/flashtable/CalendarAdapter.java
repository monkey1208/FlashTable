package com.example.yang.flashtable;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class CalendarAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<Date> dates = new ArrayList<>();
    private Date currentDate;
    private int checked_position = -1;

    public CalendarAdapter(Context context, ArrayList<Date> dates){
        this.context = context;
        this.dates = dates;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.currentDate = new Date();
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Date getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Date date = getItem(position);
        Date today = new Date();

        // inflate item if it does not exist yet
        if (view == null)
            view = inflater.inflate(R.layout.calendar_day_cell, parent, false);

        // if this day has an event, specify event image
        boolean is_today = date.getMonth() == today.getMonth() && date.getYear() == today.getYear() && date.getDate() == today.getDate();
        //view.setBackgroundResource(is_today ? R.drawable.orange_bold_rectangle_frame : R.drawable.full_gray_rectangle_frame);
        TextView tv_date = (TextView)view.findViewById(R.id.calendar_day_cell_tv);
        if (date.getMonth() != currentDate.getMonth() || date.getYear() != currentDate.getYear()) {
            // if this day is outside current month, grey it out
            tv_date.setTextColor(Color.rgb(180,180,180));
            if(position % 7 == 0){
                view.setBackgroundResource(R.drawable.full_gray_top_stroke_frame);
            }else{
                view.setBackgroundResource(R.drawable.full_gray_rectangle_frame);
            }

        } else{
            if(is_today){
                if(position == checked_position){
                    view.setBackgroundResource(R.drawable.orange_bold_rectangle_frame_clicked_bg);
                }else{
                    view.setBackgroundResource(R.drawable.orange_bold_rectangle_frame);
                }
            }else if(position == checked_position){
                view.setBackgroundResource(R.color.btListviewPressColor);
            }else if(position % 7 == 0){
                view.setBackgroundResource(R.drawable.white_gray_top_stroke_frame);
            }else{
                view.setBackgroundResource(R.drawable.white_gray_rectangle_frame);
            }
            tv_date.setTextColor(context.getResources().getColor(R.color.textColor));
        }

        // set text
        tv_date.setText(String.valueOf(date.getDate()));

        return view;
    }

    public void updateData(ArrayList<Date> new_dates, Date today, int checked_position){
        dates.clear();
        dates.addAll(new_dates);
        this.currentDate = today;
        this.checked_position = checked_position;
        notifyDataSetChanged();
    }
}
