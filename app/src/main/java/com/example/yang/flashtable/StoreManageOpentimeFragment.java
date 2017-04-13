package com.example.yang.flashtable;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ValueFormatter;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.example.yang.flashtable.AlertDialogController.listPosition;

public class StoreManageOpentimeFragment extends Fragment {
    private static int[] DATA_COUNT = {24, 7, 12};
    private static final int DAY = 0;
    private static final int WEEK = 1;
    private static final int MONTH = 2;
    private static int current_state;
    private TextView tv_info;
    private TextView tv_period;
    private TextView tv_time_choose;
    private static BarChart chart_bar;
    private View v;
    private List<Date> dateList;
    private final int OPENTIME_CHOOSE = 4;
    private final int OPENTIME_CHOOSE_DETAIL = 5;
    private final int OPENTIME_CHOOSE_DAY = 6;
    private final int OPENTIME_CHOOSE_WEEK = 7;
    private final int OPENTIME_CHOOSE_MONTH = 8;
    private static int opentime_choose_result;
    private static final String[] month_to_Chinese = {"一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二"};
    private static final String[] period_type = {"當日時段整理", "一週時段整理", "每月時段整理"};

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
        new APITimeDetail().execute();

        v = inflater.inflate(R.layout.store_manage_opentime_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_opentime_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
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
                chart_listConfirmDialog(getContext(),"圖表期間選擇", items, OPENTIME_CHOOSE);
            }
        });

        setValues();
        int[] value = new int[24];
        setBarChart(value, 0);
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

    private void setBarChart(int[] value, int max_value){
        chart_bar = (BarChart)v.findViewById(R.id.chart_bar);
        chart_bar.setDescription("");
        XAxis xAxis = chart_bar.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelsToSkip(0);   //Display all x-axis values(for total 24 hours)
        chart_bar.getAxisRight().setEnabled(false);
        BarData barData = getBarData(value);
        barData.setDrawValues(false);
        chart_bar.getLegend().setEnabled(false); //Hide left down color labels
        chart_bar.setDrawGridBackground(false);  //Hide origin background of the chart
        chart_bar.setDragEnabled(false);
        chart_bar.setTouchEnabled(false);
        chart_bar.getAxisLeft().setValueFormatter((new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) { return ((int) v)+"";}
        }));

        YAxis yAxis = chart_bar.getAxisLeft();
        yAxis.setAxisMaxValue(max_value+1);
        yAxis.setAxisMinValue(0);
        yAxis.setLabelCount(Math.min(max_value+1, 9));
        chart_bar.setData(barData);
    }

    public BarData getBarData(int[] value){
        BarDataSet dataSetA = new BarDataSet(getChartData(value), "");
        dataSetA.setDrawValues(false);
        dataSetA.setBarSpacePercent(50f);
        dataSetA.setColors(new int[]{R.color.colorOrangeLighter}, getContext()); //set bar color
        List<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSetA);
        return new BarData(getLabels(), dataSets);
    }

    private List<BarEntry> getChartData(int[] value){
        List<BarEntry> chartData = new ArrayList<>();
        for(int i=0; i < DATA_COUNT[current_state]; i++){
            chartData.add(new BarEntry(value[i], i));
        }
        return chartData;
    }


    private List<String> getLabels(){
        List<String> chartLabels = new ArrayList<>();
        for(int i=0; i < DATA_COUNT[current_state]; i++){
            switch(current_state) {
                case DAY:
                    chartLabels.add(String.format("%02d", i));
                    break;
                case WEEK:
                    //TODO: Get date
                    chartLabels.add(String.format("%s\n" , month_to_Chinese[i]));
                    break;
                case MONTH:
                    chartLabels.add(String.format("%s月\n" , month_to_Chinese[i]));
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

    private class APITimeDetail extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {
            dateList = new ArrayList<>();
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordsInfo = new HttpGet("https://flash-table.herokuapp.com/api/shop_records?shop_id="+ String.valueOf(1)+"&verbose=1");
                JSONArray recordsInfo = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordsInfo)));
                for (int i = 1; i < recordsInfo.length(); i++) {
                    JSONObject recordInfo = recordsInfo.getJSONObject(i);
                    String is_success = recordInfo.getString("is_succ");

                    if(is_success.equals("true")){
                        String time = recordInfo.getString("created_at");
                        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                        Date date =  df.parse(time);
                        dateList.add(date);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            Collections.sort(dateList, new Comparator<Date>() {
                @Override
                public int compare(Date d1, Date d2) {
                    return d2.compareTo(d1);
                }
            });
            Log.e("opentime", "finish list");
        }
    }


    public void chart_listConfirmDialog(final Context context, String title, List<String> items, final int mode){
        View view = LayoutInflater.from(context).inflate(R.layout.store_dialog_list, null);
        View titleBar = LayoutInflater.from(context).inflate(R.layout.store_title_bar, null);
        TextView tv_title = (TextView)titleBar.findViewById(R.id.title);
        tv_title.setText(title);
        tv_title.setTextSize(18);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(view)
                .create();

        try {
            alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.store_alert_dialog_bg);
        } catch(NullPointerException e) {
            e.printStackTrace();
        }

        alertDialog.setCanceledOnTouchOutside(false);
        final ListView lv_item = (ListView) view.findViewById(R.id.lv_item);
        final StoreDialogAdapter adapter = new StoreDialogAdapter(context, items);
        lv_item.setAdapter(adapter);
        lv_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listPosition = position;
                adapter.notifyDataSetChanged();
                opentime_choose_result = position+OPENTIME_CHOOSE_DAY;
                if(mode == OPENTIME_CHOOSE)
                    current_state = position;
            }
        });

        ImageButton bt_confirm = (ImageButton) view.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                List<String> items = new ArrayList<String>();
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
                int thisYear = calendar.get(Calendar.YEAR);
                int thisMonth = calendar.get(Calendar.MONTH);
                int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
                Calendar cal = Calendar.getInstance();
                int value[] = new int[DATA_COUNT[current_state]];
                switch (mode){
                    case OPENTIME_CHOOSE:
                        switch (opentime_choose_result){
                            case OPENTIME_CHOOSE_DAY:
                                String date = String.format("%d/%02d/%02d", thisYear, thisMonth+1, thisDay);
                                tv_period.setText(date);
                                tv_info.setText(date+" 時段整理");
                                tv_time_choose.setText(StoreManageOpentimeFragment.period_type[listPosition]);
                                tv_time_choose.setBackgroundResource(R.color.colorHalfTransparent);
                                for(int i = 0; i < dateList.size(); i+=1){
                                    cal.setTime(dateList.get(i));
                                    if(cal.get(Calendar.DAY_OF_MONTH) == thisDay){
                                        value[cal.get(Calendar.HOUR_OF_DAY)] += 1;
                                    }else{
                                        break;
                                    }
                                }
                                chart_bar.clear();
                                int max_value = 0;
                                for(int i = 0; i < DATA_COUNT[current_state]; i++){
                                    max_value = value[i]>max_value? value[i] : max_value;
                                }
                                setBarChart(value, max_value);
                                break;
                            case OPENTIME_CHOOSE_WEEK:
                                calendar.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
                                DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                                int[] gap = {-14, 6, 1, 6, 1, 6};
                                for(int i = 0; i < 6; i += 2){
                                    calendar.add(Calendar.DATE, gap[i]);
                                    String startDate = df.format(calendar.getTime());
                                    calendar.add(Calendar.DATE, gap[i+1]);
                                    String endDate = df.format(calendar.getTime());
                                    items.add(startDate+" - "+endDate);
                                }
                                chart_listConfirmDialog(getContext(),"圖表期間選擇",items, OPENTIME_CHOOSE_DETAIL);
                                break;
                            case OPENTIME_CHOOSE_MONTH:
                                thisMonth -= 2;
                                if(thisMonth < 0){
                                    thisMonth += 12;
                                    thisYear -= 1;
                                }
                                items.add(thisYear+" "+month_to_Chinese[thisMonth]+"月");
                                items.add((thisMonth==11? thisYear+1: thisYear) +" "+month_to_Chinese[thisMonth==11? 0 : (thisMonth+1)]+"月");
                                items.add((thisMonth==11? thisYear+1: thisYear) +" "+month_to_Chinese[thisMonth==11? 1 : (thisMonth+2)]+"月");
                                chart_listConfirmDialog(context,"圖表期間選擇",items, OPENTIME_CHOOSE_DETAIL);
                                break;
                        }
                        break;
                    case OPENTIME_CHOOSE_DETAIL:
                        String selected_period = (String)lv_item.getItemAtPosition(listPosition);
                        if(current_state == WEEK){
                            String[] dates = selected_period.split(" - ");
                            DateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN);
                            try {
                                Date s_date = df.parse(dates[0]);
                                String start_date = String.format("%02d/%02d", s_date.getMonth()+1, s_date.getDate());
                                Date e_date = df.parse(dates[1]);
                                String end_date = String.format("%02d/%02d", e_date.getMonth()+1, e_date.getDate());
                                tv_period.setText(start_date+" - "+end_date);
                                long diff = e_date.getTime() - s_date.getTime();
                                for(int i = 0; i < dateList.size(); i+=1){
                                    cal.setTime(dateList.get(i));
                                    if(dateList.get(i).getTime()-s_date.getTime() <= diff){
                                        value[cal.get(Calendar.DAY_OF_WEEK)-2] += 1;
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }else if(current_state == MONTH){
                            tv_period.setText(selected_period);
                            String month = selected_period.split(" ")[1];
                            int month_num = 0;
                            for(; month_num < 12; month_num++){
                                if(month.equals(month_to_Chinese[month_num]+"月")){
                                    break;
                                }
                            }
                            for(int i = 0; i < dateList.size(); i+=1){
                                cal.setTime(dateList.get(i));
                                if((cal.get(Calendar.MONTH)) == month_num){
                                    value[month_num] += 1;
                                }
                            }
                        }
                        tv_time_choose.setText(period_type[current_state]);
                        tv_time_choose.setBackgroundResource(R.color.colorHalfTransparent);
                        tv_info.setText(selected_period+" 時段整理");

                        int max_value = 0;
                        for(int i = 0; i < DATA_COUNT[current_state]; i++){
                            max_value = value[i]>max_value? value[i] : max_value;
                        }
                        chart_bar.clear();
                        setBarChart(value, max_value);
                        break;
                }

            }
        });
        ImageButton bt_cancel = (ImageButton) view.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        lp.width = (int) (displayWidth * 0.75);
        lp.height = (int) (displayHeight * 0.6);
        alertDialog.getWindow().setAttributes(lp);
    }

}
