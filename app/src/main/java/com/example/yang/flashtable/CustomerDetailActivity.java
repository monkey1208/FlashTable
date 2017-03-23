package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailActivity extends AppCompatActivity {

    ViewFlipper vf_flipper;
    ListView lv_reservations;
    CustomerDetailAdapter reservation_adapter;
    List<CustomerDetailItem> reservations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_detail_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();

        vf_flipper = (ViewFlipper) findViewById(R.id.customer_detail_vf_flipper);
        lv_reservations = (ListView) findViewById(R.id.customer_detail_lv_details);
    }

    private void initData() {

        // Set reservation ListView
        // TODO: Set item attributes

        reservations = new ArrayList<>();
        CustomerDetailItem reservation_1 = new CustomerDetailItem("McDonald's", "台北市大安區辛亥路", "2016/3/24 14:00pm", 0, 12);
        CustomerDetailItem reservation_2 = new CustomerDetailItem("肯德基", "台北市大安區辛亥路", "2016/3/24 14:00pm", 1, 10);
        CustomerDetailItem reservation_3 = new CustomerDetailItem("辛殿", "台北市大安區辛亥路", "2016/3/25 15:00pm", 2, 5);
        reservations.add(reservation_1);
        reservations.add(reservation_2);
        reservations.add(reservation_3);
        reservation_adapter = new CustomerDetailAdapter(this, reservations);
        lv_reservations.setAdapter(reservation_adapter);
        lv_reservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                showRecord(i);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (vf_flipper.getDisplayedChild() == 0) {
                finish();
            }
            else if (vf_flipper.getDisplayedChild() == 1) {
                showList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showList() {
        vf_flipper.setDisplayedChild(0);
        setTitle(getResources().getString(R.string.customer_detail_title));
    }

    private void showRecord(int position) {
        vf_flipper.setDisplayedChild(1);
        setTitle(getResources().getString(R.string.customer_detail_record_title) + position);
    }
}
