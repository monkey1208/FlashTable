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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.R.id.list;
import static android.content.Context.MODE_PRIVATE;


public class StoreManageDiscountFragment extends ListFragment {

    private static List<StoreDiscountInfo> discountList;
    public static StoreManageDiscountAdapter adapter;
    private String shop_id;

    public StoreManageDiscountFragment() {
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
        final View v = inflater.inflate(R.layout.store_manage_discount_fragment, container, false);

        ListView lv = (ListView) v.findViewById(list);
        discountList = new ArrayList<>(StoreMainActivity.storeInfo.not_delete_discountList);
        adapter = new StoreManageDiscountAdapter(getContext(), discountList);
        lv.setAdapter(adapter);

        Toolbar bar = (Toolbar) v.findViewById(R.id.store_manage_discount_tb_toolbar);
        bar.setPadding(0, getStatusBarHeight(), 0, 0);
        bar.inflateMenu(R.menu.store_manage_discount_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_DISCOUNT_DELETE);
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
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });

        LinearLayout add = (LinearLayout) v.findViewById(R.id.store_manage_discount_ll_add);
        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialogController(getString(R.string.server_domain)).addDiscountDialog(getContext(), false);
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

    public void onListItemClick(ListView l, View v, int position, long id) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("DefaultDiscount", MODE_PRIVATE);
        String default_description;
        int default_id;
        //Click the same promotion twice , cancel the default promotion
        if (StoreMainActivity.storeInfo.discountDefault == position) {
            discountList.get(StoreMainActivity.storeInfo.discountDefault).isDefault = false;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = -1;
            default_id = -1;
            default_description = "";
        } else if (StoreMainActivity.storeInfo.discountDefault != -1) { //There is default promotion
            discountList.get(StoreMainActivity.storeInfo.discountDefault).isDefault = false;
            discountList.get(position).isDefault = true;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = position;
            default_id = discountList.get(position).getId();
            default_description = discountList.get(position).description;
        } else { //There is "no" default promotion
            discountList.get(position).isDefault = true;
            adapter.notifyDataSetChanged();
            StoreMainActivity.storeInfo.discountDefault = position;
            default_id = discountList.get(position).getId();
            default_description = discountList.get(position).description;
        }
        sharedPreferences.edit().putString("description", default_description).putInt("promotion_id", default_id).apply();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void addPromotionList(StoreDiscountInfo info) {
        discountList.add(info);
        adapter.notifyDataSetChanged();
    }

    public static void deletePromotionList(int promotion_id) {
        for (int i = 0; i < discountList.size(); i++) {
            if (promotion_id == discountList.get(i).getId()) {
                discountList.remove(i);
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void updateValues() {
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
                HttpGet get = new HttpGet(getString(R.string.server_domain) + "/api/shop_promotions?shop_id=" + params[0] + "&verbose=1");
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
                    StoreDiscountInfo info = new StoreDiscountInfo(id, description, isRemoved, count, isActive);
                    tmp_discount_list.add(info);
                    if (!isRemoved) {
                        tmp_not_deleted_discount_list.add(info);
                    }
                }
            } catch (Exception e) {
                exception = true;
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params) {
            if (exception) {
                new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "網路連線失敗，請檢查您的網路");
            } else {
                StoreMainActivity.storeInfo.discountList = new ArrayList<>(tmp_discount_list);
                StoreMainActivity.storeInfo.not_delete_discountList = new ArrayList<>(tmp_not_deleted_discount_list);
                discountList = new ArrayList<>(tmp_not_deleted_discount_list);
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

