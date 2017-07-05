package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;


public class StoreManageDiscountDeleteFragment extends Fragment {

    private List<StoreDiscountInfo> discountList;
    public static StoreManageDiscountDeleteAdapter adapter;
    private ListView lv;

    public StoreManageDiscountDeleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.store_manage_discount_delete_fragment, container, false);

        lv = (ListView)v.findViewById(list);

        discountList =  new ArrayList<>(StoreMainActivity.storeInfo.not_delete_discountList);
        adapter = new StoreManageDiscountDeleteAdapter(getContext(),discountList);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SparseBooleanArray checked = lv.getCheckedItemPositions();
                boolean[] checked_position = new boolean[discountList.size()];
                for (int i = 0; i < checked.size(); i++)
                    if (checked.valueAt(i)) {
                        checked_position[checked.keyAt(i)] = true;
                    }
                adapter.setItemClick(checked_position);
                adapter.notifyDataSetChanged();
            }
        });

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_discount_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_discount_delete_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_DISCOUNT);
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
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_DISCOUNT);
            }
        });

        LinearLayout add = (LinearLayout)v.findViewById(R.id.store_manage_discount_delete_ll_add);
        add.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                new AlertDialogController(getString(R.string.server_domain)).addDiscountDialog(getContext(), false);
            }
        });

        ImageButton ib_delete_confirm = (ImageButton)v.findViewById(R.id.store_discount_ib_delete_confirm);
        ib_delete_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:delete select item
                SparseBooleanArray checked = lv.getCheckedItemPositions();
                for (int i = 0; i < checked.size(); i++)
                    if (checked.valueAt(i)) {
                        StoreDiscountInfo promotion = discountList.get(checked.keyAt(i));
                        new APIremove_promotion().execute(String.valueOf(promotion.getId()));
                    }
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_DISCOUNT);
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

    private class APIremove_promotion extends AsyncTask<String,Void,Void> {
        int removed_promotion_id;
        boolean exception = false;
        @Override
        protected Void doInBackground(String... params) {
            try {
                removed_promotion_id = Integer.valueOf(params[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost(getString(R.string.server_domain)+"/api/remove_promotion");
                List<NameValuePair> param = new ArrayList<NameValuePair>();
                param.add(new BasicNameValuePair("promotion_id",params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                JSONObject response = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(post)));
                Log.e("status", response.getString("status_code"));
            } catch (Exception e) {
                exception = true;
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            if(exception){
                new AlertDialogController("").warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
            }else {
                for (int i = 0; i < StoreMainActivity.storeInfo.discountList.size(); i++) {
                    if (removed_promotion_id == StoreMainActivity.storeInfo.discountList.get(i).getId()) {
                        StoreMainActivity.storeInfo.discountList.get(i).deleteDiscount();
                        break;
                    }
                }
                for (int i = 0; i < StoreMainActivity.storeInfo.not_delete_discountList.size(); i++) {
                    if (removed_promotion_id == StoreMainActivity.storeInfo.not_delete_discountList.get(i).getId()) {
                        StoreMainActivity.storeInfo.not_delete_discountList.remove(i);
                    }
                }
                StoreManageDiscountFragment.deletePromotionList(removed_promotion_id);
            }
        }
    }

}
