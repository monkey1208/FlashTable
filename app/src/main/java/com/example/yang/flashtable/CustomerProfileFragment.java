package com.example.yang.flashtable;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerProfileFragment extends Fragment {

    private View view;
    LinearLayout ll_comments, ll_reservations;
    ImageView iv_avatar;
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
        ll_reservations = (LinearLayout) view.findViewById(R.id.customer_profile_ll_reservations);
        ll_comments = (LinearLayout)  view.findViewById(R.id.customer_profile_ll_comments);
        iv_avatar = (ImageView) view.findViewById(R.id.customer_profile_iv_avatar);
        bt_edit = (Button) view.findViewById(R.id.customer_profile_bt_edit);
        bt_about_credits = (Button) view.findViewById(R.id.customer_profile_bt_about_credit);
    }

    private void initData() {
        ll_reservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerDetailActivity.class);
                startActivity(intent);
            }
        });
        ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), CustomerCommentActivity.class);
                startActivity(intent);
            }
        });
        iv_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        bt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

}
