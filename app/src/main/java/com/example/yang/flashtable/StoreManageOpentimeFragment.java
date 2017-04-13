package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class StoreManageOpentimeFragment extends Fragment {
    private static int[] DATA_COUNT = {24, 7, 12};
    public static final int DAY = 0;
    public static final int WEEK = 1;
    public static final int MONTH = 2;
    public static int current_state;
    public static TextView tv_info;
    public static TextView tv_period;
    public static TextView tv_time_choose;
    public static BarChart chart_bar;
    public static View v;
    private static Context context;
    static String[] num_to_Chinese = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    public static final String[] period_type = {"當日時段整理", "一週時段整理", "每月時段整理"};

    public StoreManageOpentimeFragment() {
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
        v = inflater.inflate(R.layout.store_manage_opentime_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_opentime_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Logout
                Toast.makeText(v.getContext(),"Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });

        current_state = DAY;
        tv_info = (TextView)v.findViewById(R.id.store_manage_opentime_tv_info);
        tv_period = (TextView)v.findViewById(R.id.store_manage_opentime_tv_period);
        tv_time_choose = (TextView)v.findViewById(R.id.store_manage_bill_tv_time_choose);
        tv_time_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_time_choose.setBackgroundResource(R.color.btListviewPressColor);
                List<String> items = Arrays.asList(period_type);
                new AlertDialogController().chart_listConfirmDialog(getContext(),"圖表期間選擇", items, AlertDialogController.OPENTIME_CHOOSE, -1);
            }
        });
        context = getContext();
        setValues();
        setBarChart();
        return v;
    }
    private void setValues(){
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        String date = String.format("%d/%02d/%02d", thisYear, thisMonth+1, thisDay);
        tv_period.setText(date);
        tv_info.setText(date+" "+"時段整理");
    }

    private void setBarChart(){
        chart_bar = (BarChart)v.findViewById(R.id.chart_bar);
        chart_bar.setDescription("");
        XAxis xAxis = chart_bar.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(0);   //Display all x-axis values(for total 24 hours)
        chart_bar.getAxisRight().setEnabled(false);
        BarData barData = getBarData();
        barData.setDrawValues(false);
        chart_bar.getLegend().setEnabled(false); //Hide left down color labels
        chart_bar.setDrawGridBackground(false);  //Hide origin background of the chart
        chart_bar.setDragEnabled(false);
        chart_bar.setTouchEnabled(false);
        chart_bar.getAxisLeft().setValueFormatter((new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) { return ((int) v)+"";}
        }));
        chart_bar.setData(barData);
    }

    public static BarData getBarData(){
        BarDataSet dataSetA = new BarDataSet(getChartData(), "");
        dataSetA.setDrawValues(false);
        dataSetA.setBarSpacePercent(50f);
        dataSetA.setColors(new int[]{R.color.colorOrangeLighter}, context); //set bar color
        List<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        return new BarData(getLabels(), dataSets);
    }

    private static List<BarEntry> getChartData(){
        //TODO: Request and handle for the data
        int[] value = {0,0,0,0,0,0,0,0,2,6,8,7,5,1,6,4,2,5,8,9,2,0,0,0};
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i < DATA_COUNT[current_state]; i++){
            chartData.add(new BarEntry(value[i], i));
        }
        return chartData;
    }


    private static List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0; i < DATA_COUNT[current_state]; i++){
            switch(current_state) {
                case DAY:
                    chartLabels.add(String.format("%02d", i));
                    break;
                case WEEK:
                    //TODO: Get date
                    chartLabels.add(String.format("%s\n" , num_to_Chinese[i]));
                    break;
                case MONTH:
                    chartLabels.add(String.format("%s月\n" , num_to_Chinese[i]));
                    break;
            }
        }
        return chartLabels;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
