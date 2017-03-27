package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
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
    List<CustomerDetailInfo> reservations;
    Button bt_comment;

    // Elements in show
    TextView tv_record_success, tv_record_arrival_time, tv_record_shop,
            tv_discount, tv_gift, tv_description;
    ImageView iv_record_credit;
    RatingBar rb_record_rating;

    String time, success, fail, persons, discount_off;
    String no_discount, no_gift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_detail_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();
        setTitle(getResources().getString(R.string.customer_detail_title));

        vf_flipper = (ViewFlipper) findViewById(R.id.customer_detail_vf_flipper);
        lv_reservations = (ListView) findViewById(R.id.customer_detail_lv_details);
        bt_comment = (Button) findViewById(R.id.customer_detail_bt_comment);

        // Elements in show
        tv_record_success = (TextView) findViewById(R.id.customer_detail_tv_record_success);
        tv_record_arrival_time = (TextView) findViewById(R.id.customer_detail_tv_record_arrival_time);
        tv_record_shop = (TextView) findViewById(R.id.customer_detail_tv_record_shop);
        tv_discount = (TextView) findViewById(R.id.customer_detail_tv_discount);
        tv_gift = (TextView) findViewById(R.id.customer_detail_tv_gift);
        tv_description = (TextView) findViewById(R.id.customer_detail_tv_description);
        iv_record_credit = (ImageView) findViewById(R.id.customer_detail_iv_record_credit);
        rb_record_rating = (RatingBar) findViewById(R.id.customer_detail_rb_record_rating);
    }

    private void initData() {

        // Set reservation ListView
        // TODO: Set item attributes
        reservations = new ArrayList<>();
        CustomerDetailInfo reservation_1 = new CustomerDetailInfo(
                "McDonald's", "台北市大安區辛亥路", 3.5f, "2016/3/24 14:00pm", 95, "", false, 12);
        CustomerDetailInfo reservation_2 = new CustomerDetailInfo(
                "肯德基", "台北市大安區辛亥路", 4.2f, "2016/3/24 14:00pm", 10, "送瑋德一隻", true, 10);
        CustomerDetailInfo reservation_3 = new CustomerDetailInfo(
                "辛殿", "台北市大安區辛亥路", 4.5f, "2016/3/25 15:00pm", 85, "", true, 5);
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
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerRatingActivity.class);
                startActivity(intent);
            }
        });
        rb_record_rating.setIsIndicator(true);

        time = getResources().getString(R.string.customer_detail_record_arrival_time);
        success = getResources().getString(R.string.customer_detail_success);
        fail = getResources().getString(R.string.customer_detail_fail);
        persons = getResources().getString(R.string.customer_detail_persons);
        discount_off = getResources().getString(R.string.discount);

        no_discount = getResources().getString(R.string.customer_detail_record_discount);
        no_gift = getResources().getString(R.string.customer_detail_record_gift);
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
        // TODO: Store information can be read from phone

        vf_flipper.setDisplayedChild(1);
        setTitle(getResources().getString(R.string.customer_detail_record_title));

        CustomerDetailInfo record = reservations.get(position);
        if (record.success) {
            tv_record_success.setText(success + Integer.toString(record.persons) + persons);
            tv_record_success.setTextColor(getResources().getColor(R.color.textColorOrange));
            iv_record_credit.setImageResource(R.drawable.customer_detail_credit_plus);
        } else {
            tv_record_success.setText(fail + Integer.toString(record.persons) + persons);
            tv_record_success.setTextColor(getResources().getColor(R.color.textColorRed));
            iv_record_credit.setImageResource(R.drawable.customer_detail_credit_minus);
        }
        tv_record_arrival_time.setText(time + record.time);
        tv_record_shop.setText(record.shop);
        rb_record_rating.setRating(record.rating);

        String discount;
        if (record.discount == 101) {
            discount = no_discount;
        } else if (record.discount % 10 == 0) {
            discount = Integer.toString(record.discount / 10) + discount_off;
        } else {
            discount = Integer.toString(record.discount) + discount_off;
        }
        tv_discount.setText(discount);
        if (!record.gift.equals("")) {
            tv_gift.setText(record.gift);
        } else {
            tv_gift.setText(no_gift);
        }
    }
}
