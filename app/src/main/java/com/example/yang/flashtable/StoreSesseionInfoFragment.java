package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StoreSesseionInfoFragment extends Fragment {

    private TextView tv_name;
    private TextView tv_number;
    private ImageButton bt_click;
    private ImageView iv_photo;
    private TextView tv_point;
    public StoreSesseionInfoFragment() {
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
        String pic_url = getArguments().getString("picture_url");
        int promtion_id = getArguments().getInt("promotion_id");
        int point = getArguments().getInt("point");
        Long due_time = getArguments().getLong("due_time");
        Date create_time_date = new Date(due_time-900*1000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
        String create_time_str = sdf.format(create_time_date).replace("AM", "am").replace("PM","pm");

        View v = inflater.inflate(R.layout.store_session_info_fragment, container, false);
        LinearLayout ll_user_info = (LinearLayout) v.findViewById(R.id.store_session_info_fragment_ll_info);
        LinearLayout ll_arrive_time = (LinearLayout) v.findViewById(R.id.store_session_info_fragment_ll_arrive_time);
        ll_user_info.setPadding(0, getStatusBarHeight(), 0, 0);
        ll_arrive_time.setVisibility(View.GONE);
        tv_name = (TextView)v.findViewById(R.id.tv_name);
        tv_point = (TextView)v.findViewById(R.id.tv_point);
        tv_number = (TextView)v.findViewById(R.id.tv_number);

        tv_name.setText(name);
        tv_point.setText("  (信譽"+String.valueOf(point)+")");
        tv_number.setText(number);

        for(int i=0;i<StoreMainActivity.storeInfo.discountList.size();i++){
            if(StoreMainActivity.storeInfo.discountList.get(i).id==promtion_id) {
                TextView tv_gift = (TextView)v.findViewById(R.id.store_session_info_fragment_tv_gift);
                TextView tv_create_time = (TextView)v.findViewById(R.id.store_session_info_fragment_tv_appoint_time);
                TextView tv_info_last = (TextView)v.findViewById(R.id.store_session_info_fragment_tv_info_last);
                TextView tv_info_first = (TextView)v.findViewById(R.id.store_session_info_fragment_tv_info_first);

                tv_gift.setText(StoreMainActivity.storeInfo.discountList.get(i).description);
                tv_create_time.setText(create_time_str);
                tv_info_first.setText("已成功預約 ");
                tv_info_last.setText(" 人");
            }
        }


        bt_click = (ImageButton)v.findViewById(R.id.bt_click);
        iv_photo = (ImageView)v.findViewById(R.id.iv_photo);
        if(pic_url!=null && !pic_url.equals("")){
            Picasso.with(getContext()).load(pic_url).into(iv_photo);
        }else{
            iv_photo.setImageResource(R.drawable.default_avatar);
        }
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
