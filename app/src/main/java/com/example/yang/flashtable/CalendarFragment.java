package com.example.yang.flashtable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class CalendarFragment extends DialogFragment {
    private final static int DAYS_COUNT = 42;

    private ImageView calendar_prev_button;
    private ImageView calendar_next_button;
    private TextView calendar_date_display;
    private GridView calendar_grid;
    private Calendar currentDate;
    private CalendarAdapter adapter;
    private static final int REQUEST_CODE = 1;
    private String[] digit_month = {"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
    private String[] chinese_month = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"};


    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        calendar_prev_button = (ImageView) view.findViewById(R.id.calendar_prev_button);
        calendar_next_button = (ImageView) view.findViewById(R.id.calendar_next_button);
        calendar_date_display = (TextView) view.findViewById(R.id.calendar_date_display);
        calendar_grid = (GridView) view.findViewById(R.id.calendar_grid);
        adapter = new CalendarAdapter(getContext(), new ArrayList<Date>());
        calendar_grid.setAdapter(adapter);

        setCancelable(false);

        currentDate = Calendar.getInstance(Locale.TAIWAN);
        if(getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        calendar_prev_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev_month();
            }
        });

        calendar_next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_month();
            }
        });

        calendar_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> view, View cell, int position, long id) {
                //TODO:  Get the checked date
                updateCalendar(position);
                Date date = (Date)view.getItemAtPosition(position);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                if(cal.get(Calendar.MONTH) == currentDate.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR)) {
                    sendResult(REQUEST_CODE, date);
                    getDialog().dismiss();
                }
            }
        });

        updateCalendar(-1);
        return view;
    }


    private void prev_month(){
        currentDate.add(Calendar.MONTH, -1);
        updateCalendar(-1);
    }

    private void next_month(){
        currentDate.add(Calendar.MONTH, 1);
        updateCalendar(-1);
    }

    private void updateCalendar(int position) {

        ArrayList<Date> cells = new ArrayList<>();
        Calendar calendar = (Calendar)currentDate.clone();

        // determine the cell for current month's beginning
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // move calendar backwards to the beginning of the week
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        // fill cells (42 days calendar as per our business logic)
        while (cells.size() < DAYS_COUNT) {
            cells.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // update grid
        Date date =  currentDate.getTime();
        ((CalendarAdapter)calendar_grid.getAdapter()).updateData(cells, date, position);

        // update title
        SimpleDateFormat sdf = new SimpleDateFormat("MMM yyyy", Locale.TAIWAN);
        String title = sdf.format(currentDate.getTime());
        for(int i = 0; i < 12; i++){
            title = title.replace(digit_month[i], chinese_month[i]);
        }
        calendar_date_display.setText(title);
    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        /*DisplayMetrics dm = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        lp.width = (int) (displayWidth * 0.78);
        lp.height = (int) (displayHeight * 0.62);*/
        lp.width = 850;
        lp.height = 1170;
        lp.dimAmount=0.65f;
        getDialog().getWindow().setAttributes(lp);
        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        super.onResume();
    }

    private void sendResult(int REQUEST_CODE, Date date) {
        Intent intent = new Intent();
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        intent.putExtra("Checked_Date", df.format(date));
        getTargetFragment().onActivityResult(
                getTargetRequestCode(), REQUEST_CODE, intent);
    }

}
