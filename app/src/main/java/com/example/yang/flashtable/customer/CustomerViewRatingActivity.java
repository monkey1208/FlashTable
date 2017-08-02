package com.example.yang.flashtable.customer;

import android.view.View;

/**
 * Created by Yang on 2017/7/11.
 */

public class CustomerViewRatingActivity extends CustomerRatingActivity {

    String comment;

    @Override
    public void initData() {
        getUserInfo();
        comment = getIntent().getStringExtra("comment");
        tv_shop.setText(getIntent().getStringExtra("shop"));
        tv_location.setText(getIntent().getStringExtra("shop_location"));
        rb_rating.setRating(getIntent().getFloatExtra("rating", 0));
        et_content.setText(getIntent().getStringExtra("comment"));
        bt_submit.setOnClickListener(null);
        bt_submit.setVisibility(View.GONE);
        et_content.setEnabled(false);
        rb_rating.setEnabled(false);

        bt_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void closeActivity() {
        finish();
    }
}
