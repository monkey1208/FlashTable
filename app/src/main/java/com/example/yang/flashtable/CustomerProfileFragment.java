package com.example.yang.flashtable;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerProfileFragment extends Fragment {

    private View view;
    LinearLayout ll_comments, ll_reservations;
    Button bt_edit, bt_about_credits;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_profile_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        ll_comments = (LinearLayout)  view.findViewById(R.id.customer_profile_ll_comments);
        ll_reservations = (LinearLayout) view.findViewById(R.id.customer_profile_ll_reservations);
        bt_edit = (Button) view.findViewById(R.id.customer_profile_bt_edit);
        bt_about_credits = (Button) view.findViewById(R.id.customer_profile_bt_about_credit);
    }

    private void initData() {
        ll_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CustomerDetailActivity.class);
                startActivity(intent);
            }
        });
    }
}
