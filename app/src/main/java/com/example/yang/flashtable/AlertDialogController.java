package com.example.yang.flashtable;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class AlertDialogController {
    private static int selected;
    private static AlertDialog alertDialog;
    public static void discountDialog(final Context context, final StoreInfo storeInfo,final TextView tv_discount,final TextView tv_gift){
        //init view---------
        View item = LayoutInflater.from(context).inflate(R.layout.store_discount_list, null);
        List<StoreInfo.DiscountInfo> discountList = storeInfo.discountList;
        //listview adapt----
        ListView lv_discount = (ListView)item.findViewById(R.id.lv_discount);
        DiscountDialogAdapter adapter = new DiscountDialogAdapter(context,discountList);
        lv_discount.setAdapter(adapter);
        lv_discount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                parent.getChildAt(selected).setBackgroundColor(context.getResources().getColor(R.color.white));
                parent.getChildAt(position).setBackgroundColor(context.getResources().getColor(R.color.btListviewPressColor));
                selected = position;
            }
        });
        //-------------------
        //titleBar-----------
        View titleBar = LayoutInflater.from(context).inflate(R.layout.store_title_bar, null);
        TextView tv_title = (TextView)titleBar.findViewById(R.id.title);
        tv_title.setText("折扣優惠");
        //-------------------
        //action-------------
        alertDialog = new AlertDialog.Builder(context)
                .setCustomTitle(titleBar)
                .setView(item)
                .create();
        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_discount.setText(Integer.toString(storeInfo.discountList.get(selected).discount)+"折");
                tv_gift.setText(storeInfo.discountList.get(selected).gift);
                alertDialog.dismiss();
            }
        });
        ImageButton bt_cancel = (ImageButton)item.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
        //--------------------
    }
}
