package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
public class StoreManageBillFragment extends Fragment {
    public StoreManageBillFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_manage_bill_fragment, container, false);
        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_bill_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //Logout
                Toast.makeText(v.getContext(),"Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);

        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(3);
            }
        });

        /* Check if the bill was paid,  set visibility.
        RelativeLayout rl = (RelativeLayout)v.findViewById(R.id.store_manage_bill_rl);
        rl.setVisibility(RelativeLayout.GONE);*/

        LinearLayout prev_bt = (LinearLayout)v.findViewById(R.id.store_manage_bill_ll_prev);
        prev_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Check if the bill wad paid
                //setValues(v);
                Toast.makeText(v.getContext(),"Previous", Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout next_bt = (LinearLayout)v.findViewById(R.id.store_manage_bill_ll_next);
        next_bt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                //Check if the bill wad paid
               // setValues(v);
                Toast.makeText(v.getContext(),"Next", Toast.LENGTH_SHORT).show();
            }
        });

        Button bt_pay = (Button)v.findViewById(R.id.store_manage_bill_bt_pay);
        bt_pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Pay for the bill", Toast.LENGTH_SHORT).show();
            }
        });
        setValues(v);
        return v;
    }

    private void setValues(View v){
        //Get information
        TextView tv1 = (TextView)v.findViewById(R.id.store_manage_bill_tv_period);
        TextView tv2 = (TextView)v.findViewById(R.id.store_manage_bill_tv_success);
        TextView tv3 = (TextView)v.findViewById(R.id.store_manage_bill_tv_money);
        TextView tv4 = (TextView)v.findViewById(R.id.store_manage_bill_tv_totalmoney);
        tv1.setText("2017/01/06 - 2017/01/26");
        tv2.setText("10");
        tv3.setText("50");
        tv4.setText("500");
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
