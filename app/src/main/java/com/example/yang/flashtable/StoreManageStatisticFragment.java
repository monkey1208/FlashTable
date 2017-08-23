package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class StoreManageStatisticFragment extends ListFragment {
    private List<StoreDiscountInfo> promotion_succ_num;
    private String shop_id;
    private StoreManageStatisticAdapter adapter;

    public StoreManageStatisticFragment() {
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
        View v = inflater.inflate(R.layout.store_manage_statistic_fragment, container, false);

        promotion_succ_num = new ArrayList<>(StoreMainActivity.storeInfo.discountList);
        Collections.sort(promotion_succ_num, new Comparator<StoreDiscountInfo>(){
            public int compare(StoreDiscountInfo item1, StoreDiscountInfo item2) {
                return item1.getCount()<item2.getCount()? 1:-1;
            }
        });
        adapter = new StoreManageStatisticAdapter(getActivity(), promotion_succ_num);
        setListAdapter(adapter);

        Toolbar bar = (Toolbar)v.findViewById(R.id.store_manage_statistic_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        Drawable dr = getResources().getDrawable(R.drawable.icon_back_white);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });

        getStoreInfo();
        new APIpromotion().execute(shop_id);

        return v;
    }

    private void getStoreInfo() {
        SharedPreferences store = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        shop_id = store.getString("userID", "");
    }

    private void updateValues(){
        Collections.sort(promotion_succ_num, new Comparator<StoreDiscountInfo>(){
            public int compare(StoreDiscountInfo item1, StoreDiscountInfo item2) {
                return item1.getCount()<item2.getCount()? 1:-1;
            }
        });
        adapter.notifyDataSetChanged();
    }

    public class APIpromotion extends AsyncTask<String, Void, Void> {
        boolean exception = false;
        List<StoreDiscountInfo> tmp_discount_list = new ArrayList<>();
        List<StoreDiscountInfo> tmp_not_deleted_discount_list = new ArrayList<>();
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet get = new HttpGet(getString(R.string.server_domain)+"/api/shop_promotions?shop_id=" + params[0]+"&verbose=1");
                JSONArray responsePromotion = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                for (int i = 1; i < responsePromotion.length(); i++) {
                    JSONObject promotion = responsePromotion.getJSONObject(i);
                    int id = promotion.getInt("promotion_id");
                    String description = promotion.getString("description");
                    int count = promotion.getInt("n_succ");
                    String notDelete = promotion.getString("is_removed");
                    String isActive_str = promotion.getString("is_active");
                    boolean isActive = isActive_str.equals("true");
                    boolean isRemoved = notDelete.equals("true");
                    StoreDiscountInfo info = new StoreDiscountInfo(id, description,isRemoved, count, isActive);
                    tmp_discount_list.add(info);
                    if(!isRemoved){
                        tmp_not_deleted_discount_list.add(info);
                    }
                }
            } catch (Exception e) {
                exception = true;
            }  finally{
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {
            if(isAdded()) {
                if (exception) {
                    new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "資料載入失敗，請重試");
                } else {
                    StoreMainActivity.storeInfo.discountList = new ArrayList<>(tmp_discount_list);
                    StoreMainActivity.storeInfo.not_delete_discountList = new ArrayList<>(tmp_not_deleted_discount_list);
                    promotion_succ_num = new ArrayList<>(tmp_discount_list);
                    updateValues();

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DefaultDiscount", MODE_PRIVATE);
                    int default_id = sharedPreferences.getInt("promotion_id", -1);
                    String default_description = sharedPreferences.getString("description", "");
                    for (int i = 0; i < StoreMainActivity.storeInfo.not_delete_discountList.size(); i++) {
                        if (default_id != -1 && StoreMainActivity.storeInfo.not_delete_discountList.get(i).description.equals(default_description)) {
                            StoreMainActivity.storeInfo.not_delete_discountList.get(i).isDefault = true;
                            StoreMainActivity.storeInfo.discountDefault = i;
                        }
                    }
                }
            }
        }
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
