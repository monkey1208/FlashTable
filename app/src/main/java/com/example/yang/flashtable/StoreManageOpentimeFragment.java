package com.example.yang.flashtable;

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

import java.util.ArrayList;
import java.util.List;

public class StoreManageOpentimeFragment extends Fragment {
    private int DATA_COUNT;
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
        final View v = inflater.inflate(R.layout.store_manage_opentime_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_opentime_tb_toolbar);
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

        setValues(v);
        //DATA_COUNT = 24(當日)/12(月)/7(周)
        DATA_COUNT = 24;
        setBarChart(v);

        return v;
    }
    private void setValues(View v){
        String period = "2017/02/16";
        TextView tv1 = (TextView)v.findViewById(R.id.store_manage_opentime_tv_period);
        tv1.setText(period);
        TextView tv2 = (TextView)v.findViewById(R.id.store_manage_opentime_tv_info);
        tv2.setText(period+" "+"時段整理");

    }
    private void setBarChart(View v){
        BarChart chart_bar = (BarChart)v.findViewById(R.id.chart_bar);
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
        chart_bar.setData(barData);
    }

    private BarData getBarData(){
        BarDataSet dataSetA = new BarDataSet(getChartData(), "");
        dataSetA.setColors(new int[]{R.color.colorOrangeLighter}, getContext()); //set bar color
        List<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA); // add the datasets
        return new BarData(getLabels(), dataSets);
    }

    private List<BarEntry> getChartData(){
        int[] value = {0,0,0,0,0,0,0,0,2,6,8,7,5,1,6,4,2,5,8,9,2,0,0,0}; //
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i < DATA_COUNT; i++){
            chartData.add(new BarEntry(value[i], i));
        }
        return chartData;
    }

    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0; i < DATA_COUNT; i++){
            if(i < 10)
                chartLabels.add("0"+i);
            else
                chartLabels.add(String.valueOf(i));
        }
        return chartLabels;
    }
}
