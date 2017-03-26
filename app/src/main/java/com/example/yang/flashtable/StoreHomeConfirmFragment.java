package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreHomeConfirmFragment extends Fragment {

    private TextView tv_test;
    private TextView tv_number;
    private ImageButton bt_click;
    private ImageView iv_photo;
    public StoreHomeConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_home_confirm_fragment, container, false);
        tv_test = (TextView)v.findViewById(R.id.tv_name);
        tv_test.setText("張庭維 信譽無限");
        tv_number = (TextView)v.findViewById(R.id.tv_number);
        tv_number.setText("預約100人已到達");
        bt_click = (ImageButton)v.findViewById(R.id.bt_click);
        iv_photo = (ImageView)v.findViewById(R.id.iv_photo);
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_temp_user1);
        iv_photo.setImageBitmap(icon);
        bt_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.HOME);
            }
        });
        return v;
    }
}
