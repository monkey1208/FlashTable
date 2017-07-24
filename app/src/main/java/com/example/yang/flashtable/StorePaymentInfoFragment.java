package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class StorePaymentInfoFragment extends Fragment {

    public StorePaymentInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_payment_info_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_payment_info_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_BILL);
            }
        });

        setValues(v);
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

    private void setValues(View v){
        TextView tv_notice = (TextView)v.findViewById(R.id.store_payment_info_tv_notice);
        String notice_part1 = "匯款完成後請將您的";
        String notice_part2 = "<font color='#FF6600'>匯款帳號後五碼</font>";
        String notice_part3 = "<font color='#FF6600'>餐廳名稱</font>";
        String notice_part4 = "寄至我們的E-mail信箱，以供我們確認您的款項。";
        tv_notice.setText(Html.fromHtml(notice_part1+notice_part2+"、"+notice_part3+"，"+notice_part4));
    }
}
