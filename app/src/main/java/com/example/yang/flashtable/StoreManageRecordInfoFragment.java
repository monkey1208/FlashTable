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

import static com.example.yang.flashtable.R.id.store_home_confirm_fragment_tv_gift;

public class StoreManageRecordInfoFragment extends Fragment {
    private ImageView iv_photo;
    private TextView tv_name;
    private TextView tv_point;
    private TextView tv_info_first;
    private TextView tv_people_number;
    private TextView tv_info_last;
    private TextView tv_appoint_time;
    private TextView tv_arrive_time;
    private TextView tv_promotion_description;
    private ImageButton bt_confirm;


    public StoreManageRecordInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.store_home_confirm_fragment, container, false);
        iv_photo = (ImageView)view.findViewById(R.id.iv_photo);
        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_point = (TextView)view.findViewById(R.id.tv_point);
        tv_info_first = (TextView)view.findViewById(R.id.store_home_comfirm_fragment_tv_info_first);
        tv_people_number = (TextView)view.findViewById(R.id.tv_number);
        tv_info_last = (TextView)view.findViewById(R.id.store_home_comfirm_fragment_tv_info_last);
        tv_appoint_time = (TextView)view.findViewById(R.id.store_home_confirm_fragment_tv_appoint_time);
        tv_promotion_description  = (TextView)view.findViewById(store_home_confirm_fragment_tv_gift);
        bt_confirm = (ImageButton)view.findViewById(R.id.bt_click);

        LinearLayout ll_user_info = (LinearLayout)view.findViewById(R.id.store_home_confirm_fragment_ll_info);
        ll_user_info.setPadding(0, getStatusBarHeight(), 0, 0);
        LinearLayout ll_arrive_time = (LinearLayout)view.findViewById(R.id.store_home_confirm_fragment_ll_arrive_time);
        if(!getArguments().getString("is_succ").equals("true")){
            ll_arrive_time.setVisibility(View.GONE);
        }else{
            tv_arrive_time = (TextView)view.findViewById(R.id.store_home_confirm_fragment_tv_arrive_time);
        }

        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_RECORD);
            }
        });

        Bundle content = getArguments();
        setContent(content);
        return view ;
    }

    private void setContent(Bundle content){
        if(!content.getString("image_url").equals("")){
            Picasso.with(getContext()).load(content.getString("image_url")).into(iv_photo);
        }else{
            iv_photo.setImageResource(R.drawable.default_avatar);
        }
        tv_name.setText(content.getString("name"));
        tv_point.setText(" ( 信譽"+content.getString("point")+" )");
        tv_people_number.setText(content.getString("number"));

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
            Date date = df.parse(content.getString("session_time"));
            df = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
            String formatted_date = df.format(date).replace("AM", "am").replace("PM","pm");
            tv_appoint_time.setText(formatted_date);
        }catch (Exception e){
            e.printStackTrace();
        }

        tv_info_first.setText("預約 ");
        if(content.getString("is_succ").equals("true")){
            tv_info_last.setText(" 人已到達");
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd  a hh:mm", Locale.ENGLISH);
                Date date = df.parse(content.getString("record_time"));
                df = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
                String formatted_date = df.format(date).replace("AM", "am").replace("PM","pm");
                tv_arrive_time.setText(formatted_date);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            tv_info_last.setText(" 人已取消");
        }
        tv_promotion_description.setText(content.getString("promotion_des"));

    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
