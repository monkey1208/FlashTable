package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StoreHomeConfirmFragment extends Fragment {

    private TextView tv_name;
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
        String id = getArguments().getString("session_id");
        String name = getArguments().getString("name");
        String number = getArguments().getString("number");
        int promtion_id = getArguments().getInt("promotion_id");
        Long due_time = getArguments().getLong("due_time");
        Date due_time_date = new Date(due_time);
        Date create_time_date = new Date(due_time-900*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        String create_time_str = sdf.format(create_time_date);
        String due_time_str = sdf.format(due_time_date);
        Log.d("Session",id);
        View v = inflater.inflate(R.layout.store_home_confirm_fragment, container, false);
        LinearLayout ll_user_info = (LinearLayout) v.findViewById(R.id.store_home_confirm_fragment_ll_info);
        ll_user_info.setPadding(0, getStatusBarHeight(), 0, 0);
        tv_name = (TextView)v.findViewById(R.id.tv_name);
        tv_name.setText(name);
        tv_number = (TextView)v.findViewById(R.id.tv_number);
        for(int i=0;i<StoreMainActivity.storeInfo.discountList.size();i++){
            if(StoreMainActivity.storeInfo.discountList.get(i).id==promtion_id) {
                TextView tv_discount = (TextView)v.findViewById(R.id.store_home_confirm_fragment_tv_discount);
                TextView tv_gift = (TextView)v.findViewById(R.id.store_home_confirm_fragment_tv_gift);
                TextView tv_create_time = (TextView)v.findViewById(R.id.store_home_confirm_fragment_tv_appoint_time);
                TextView tv_due_time = (TextView)v.findViewById(R.id.store_home_confirm_fragment_tv_arrive_time) ;
                TextView tv_not_arrive = (TextView)v.findViewById(R.id.store_home_comfirm_fragment_tv_info_last);
                tv_discount.setText(Integer.toString(StoreMainActivity.storeInfo.discountList.get(i).discount));
                tv_gift.setText(StoreMainActivity.storeInfo.discountList.get(i).description);
                tv_create_time.setText(create_time_str);
                tv_due_time.setText(due_time_str);
                tv_not_arrive.setText("人未到達");
            }
        }
        tv_number.setText(number);
        bt_click = (ImageButton)v.findViewById(R.id.bt_click);
        iv_photo = (ImageView)v.findViewById(R.id.iv_photo);
        Bitmap icon = BitmapFactory.decodeResource(getContext().getResources(),R.drawable.ic_temp_user1);
        iv_photo.setImageBitmap(icon);
        bt_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.storeInfo.addSuccessAppointment();
                StoreMainActivity.fragmentController.act(FragmentController.APPOINT);
            }
        });
        return v;
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
