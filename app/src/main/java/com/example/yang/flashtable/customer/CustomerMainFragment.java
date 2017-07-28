package com.example.yang.flashtable.customer;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.adapter.CustomerMainAdapter;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerAppInfo;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment implements Observer {

    private SharedPreferences user;
    private Boolean viewed_guide;

    DialogBuilder dialog_builder;
    View view;
    ListView lv_shops;
    SwipeRefreshLayout swipe_refresh_layout;
    TextView tv_nothing;
    ArrayList<CustomerRestaurantInfo> restaurant_list;

    CustomerMainAdapter adapter;
    CustomerMainAdapter adjusted_adapter;
    SqlHandler sqlHandler = null;

    ApiPromotion apiPromotion;
    // Location

    Location my_location;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        initData();

        return view;
    }

    private void initView() {

        lv_shops = (ListView) view.findViewById(R.id.customer_main_lv);
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.customer_main_srl);
        tv_nothing = (TextView) view.findViewById(R.id.customer_main_tv_nothing);
    }

    private void initData() {

        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);

        SharedPreferences guide = this.getActivity().getSharedPreferences("GUIDE", MODE_PRIVATE);
        viewed_guide = guide.getBoolean("viewed_guide", false);

        if (!viewed_guide) {
            Intent intent = new Intent(this.getActivity(), CustomerGuideActivity.class);
            startActivity(intent);
            guide.edit().putBoolean("viewed_guide", true).apply();
        }

        my_location = CustomerAppInfo.getInstance().getLocation();
        if(my_location == null){
            my_location = new Location("");
            my_location.setLatitude(25.018);
            my_location.setLongitude(121.54);
        }
        dialog_builder = new DialogBuilder(getActivity());

        setRefreshLayout();
        setListView();
        CustomerObservable.getInstance().addObserver(this);
        //sp_dis.setSelection(3);
    }

    private void setListView() {
        restaurant_list = CustomerAppInfo.getInstance().getRestaurantList();
        System.out.println("size = "+restaurant_list.size());
        System.out.println("set list!");
        if(restaurant_list.size() == 0) {
            tv_nothing.setVisibility(View.VISIBLE);
            lv_shops.setVisibility(View.INVISIBLE);
        }else{
            tv_nothing.setVisibility(View.INVISIBLE);
            lv_shops.setVisibility(View.VISIBLE);
        }

        adapter = new CustomerMainAdapter(view.getContext(), restaurant_list, my_location);
        setSortedList();
    }

    private void setSortedList(){
        adapter = sortAdapter(adapter, CustomerObservable.getInstance().mode);
        adjusted_adapter = filtAdapter(adapter, CustomerObservable.getInstance().food, Integer.parseInt(CustomerObservable.getInstance().distance));
        adapter.notifyDataSetChanged();
        if (lv_shops != null) {
            lv_shops.setAdapter(adjusted_adapter);
            lv_shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                    showRestaurantDetail(i);
                }
            });
            if(adjusted_adapter.getCount() == 0) {
                tv_nothing.setVisibility(View.VISIBLE);
                lv_shops.setVisibility(View.INVISIBLE);
            }else{
                tv_nothing.setVisibility(View.INVISIBLE);
                lv_shops.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setRefreshLayout() {
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                my_location = CustomerAppInfo.getInstance().getLocation();
                apiPromotion = new ApiPromotion();
                apiPromotion.execute(my_location.getLatitude(), my_location.getLongitude());
            }
        });
    }



    private CustomerMainAdapter filtAdapter(CustomerMainAdapter filt_adapter, String mode, int distance) {
        ArrayList<CustomerRestaurantInfo> r_list = new ArrayList<CustomerRestaurantInfo>();
        switch (mode) {
            case "chinese":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("中式料理")) {
                        Location l = new Location("");
                        l.setLatitude(filt_adapter.getItem(j).latLng.latitude);
                        l.setLongitude(filt_adapter.getItem(j).latLng.longitude);
                        if (distance < 0)
                            r_list.add(filt_adapter.getItem(j));
                        else if ((int) my_location.distanceTo(l) <= distance)
                            r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "usa":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("美式料理")) {
                        Location l = new Location("");
                        l.setLatitude(filt_adapter.getItem(j).latLng.latitude);
                        l.setLongitude(filt_adapter.getItem(j).latLng.longitude);
                        if (distance < 0)
                            r_list.add(filt_adapter.getItem(j));
                        else if ((int) my_location.distanceTo(l) <= distance)
                            r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "japanese":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("日式料理")) {
                        Location l = new Location("");
                        l.setLatitude(filt_adapter.getItem(j).latLng.latitude);
                        l.setLongitude(filt_adapter.getItem(j).latLng.longitude);
                        if (distance < 0)
                            r_list.add(filt_adapter.getItem(j));
                        else if ((int) my_location.distanceTo(l) <= distance)
                            r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "korean":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("韓式料理")) {
                        Location l = new Location("");
                        l.setLatitude(filt_adapter.getItem(j).latLng.latitude);
                        l.setLongitude(filt_adapter.getItem(j).latLng.longitude);
                        if (distance < 0)
                            r_list.add(filt_adapter.getItem(j));
                        else if ((int) my_location.distanceTo(l) <= distance)
                            r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            default:
                if (filt_adapter != null) {
                    for (int j = 0; j < filt_adapter.getCount(); j++) {
                        Location l = new Location("");
                        l.setLatitude(filt_adapter.getItem(j).latLng.latitude);
                        l.setLongitude(filt_adapter.getItem(j).latLng.longitude);
                        if (distance < 0)
                            r_list.add(filt_adapter.getItem(j));
                        else if ((int) my_location.distanceTo(l) <= distance)
                            r_list.add(filt_adapter.getItem(j));
                    }
                } else {
                    return filt_adapter;
                }
        }
        return new CustomerMainAdapter(getActivity(), r_list, my_location);
    }

    private CustomerMainAdapter sortAdapter(CustomerMainAdapter sort_adapter, String mode) {
        switch (mode) {
            case "time":
                sort_adapter.sort(new Comparator<CustomerRestaurantInfo>() {
                    @Override
                    public int compare(CustomerRestaurantInfo info1, CustomerRestaurantInfo info2) {
                        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy", Locale.ENGLISH);
                        try {
                            Date date1 = dateFormat.parse(info1.date);
                            Date date2 = dateFormat.parse(info2.date);
                            if(date1.after(date2)){
                                return -1;
                            }else{
                                return 1;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                break;
            case "distance":
                sort_adapter.sort(new Comparator<CustomerRestaurantInfo>() {
                    @Override
                    public int compare(CustomerRestaurantInfo info1, CustomerRestaurantInfo info2) {
                        Location l1 = new Location("");
                        l1.setLatitude(info1.latLng.latitude);
                        l1.setLongitude(info1.latLng.longitude);
                        Location l2 = new Location("");
                        l2.setLatitude(info2.latLng.latitude);
                        l2.setLongitude(info2.latLng.longitude);
                        if (my_location.distanceTo(l1) > my_location.distanceTo(l2)) {
                            return 1;
                        } else {
                            return -1;
                        }
                    }
                });
                break;
            case "discount":
                sort_adapter.sort(new Comparator<CustomerRestaurantInfo>() {
                    @Override
                    public int compare(CustomerRestaurantInfo info1, CustomerRestaurantInfo info2) {
                        if (info1.discount > info2.discount) {
                            return 1;
                        } else if (info1.discount < info2.discount) {
                            return -1;
                        } else {
                            if (info2.offer.equals("暫無優惠")) {
                                return -1;
                            }
                            return 0;
                        }
                    }
                });
                break;
            case "rate":
                sort_adapter.sort(new Comparator<CustomerRestaurantInfo>() {
                    @Override
                    public int compare(CustomerRestaurantInfo info1, CustomerRestaurantInfo info2) {
                        if (info1.rating > info2.rating) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });
                break;
            case "default":
                break;

        }
        return sort_adapter;
    }



    // DB related functions
    private void openDB() {
        sqlHandler = new SqlHandler(view.getContext());
    }

    private void closeDB() {
        sqlHandler.close();
    }

    @Override
    public void onDestroy() {
        CustomerObservable.getInstance().deleteObserver(this);
        if (apiPromotion != null) apiPromotion.cancel(true);
        super.onDestroy();
    }

    private void showRestaurantDetail(final int position) {

        final CustomerRestaurantInfo info = adjusted_adapter.getItem(position);
        CustomerShopActivity.ShowInfo showInfo = new CustomerShopActivity.ShowInfo(
                info.name,
                info.consumption,
                info.minconsumption,
                info.discount,
                info.offer,
                info.address,
                info.phone,
                info.business,
                info.category,
                info.web,
                info.intro,
                info.rating,
                info.promotion_id);
        Intent intent = new Intent(getActivity(), CustomerShopActivity.class);
        intent.putExtra("info", showInfo);
        intent.putExtra("shop_id", Integer.toString(info.id));
        startActivity(intent);
    }


    @Override
    public void update(Observable o, Object arg) {
        String[] param = (String[])arg;
        setSortedList();
        System.out.println("observer result = "+param);
    }

    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        HttpClient httpClient = new DefaultHttpClient();
        ArrayList<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        DialogBuilder dialog = new DialogBuilder(view.getContext());
        private String status = null;
        private String shop_rating;

        @Override
        protected void onPreExecute() {
            openDB();
        }
        @Override
        protected String doInBackground(Double... params) {
            String lat = String.valueOf(params[0]);
            String lng = String.valueOf(params[1]);
            String latlng = lat + "," + lng;//need current location
            Log.d("APIPromotion", "latlng = "+latlng);
            if(isCancelled())
                return null;
            ArrayList<Description> list = getPromotionId(latlng);
            for(int i = 0; i < list.size(); i++) {
                if(isCancelled())
                    return null;
                CustomerRestaurantInfo info = sqlHandler.getDetail(list.get(i).shop_id);
                info.id = list.get(i).shop_id;
                info.discount = list.get(i).discount;
                info.offer = list.get(i).offer;
                info.promotion_id = list.get(i).promotion_id;
                info.date = list.get(i).date;
                String shop_rating = "0";
                try {
                    if(isCancelled())
                        return null;
                    HttpGet requestShopRating = new HttpGet(getString(R.string.server_domain)+"api/shop_comments?shop_id=" + list.get(i).shop_id);
                    requestShopRating.addHeader("Content-Type", "application/json");
                    JSONArray responseShopRating = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(requestShopRating)));
                    status = responseShopRating.getJSONObject(0).getString("status_code");
                    if (!status.equals("0")) break;
                    shop_rating = responseShopRating.getJSONObject(0).getString("average_score");
                } catch (Exception e) {
                    //shop_rating = "0";
                } finally {
                    httpClient.getConnectionManager().shutdown();
                }
                info.rating = Float.parseFloat(shop_rating) / 2;
                restaurantInfoList.add(info);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            closeDB();
            if( status == null  || !status.equals("0") ) {
                //Log.e("MainStatus", status);
                dialog.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
                swipe_refresh_layout.setRefreshing(false);
                return;
            }
            if(restaurant_list != null)
                restaurant_list.clear();
            CustomerAppInfo.getInstance().setRestaurantList(restaurantInfoList);
            setListView();
            swipe_refresh_layout.setRefreshing(false);
            super.onPostExecute(s);
        }

        private ArrayList<Description> getPromotionId(String latlng){
            ArrayList<Description> list = new ArrayList<>();
            NameValuePair nameValuePair = new BasicNameValuePair("location", latlng);
            String s = nameValuePair.toString();
            if(isCancelled())
                return null;
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/surrounding_promotions"+"?"+s);
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(http_response);
                JSONArray jsonArray = new JSONArray(json);
                if(jsonArray.getJSONObject(0).get("status_code").equals("0")) {
                    int size = Integer.valueOf(jsonArray.getJSONObject(0).get("size").toString());
                    for(int i = 1; i <= size; i++){
                        if(isCancelled()){
                            return null;
                        }
                        String id = jsonArray.getJSONObject(i).get("promotion_id").toString();
                        nameValuePair = null;
                        nameValuePair = new BasicNameValuePair("promotion_id", id);
                        s = nameValuePair.toString();
                        if(isCancelled())
                            return null;
                        request = new HttpGet(getString(R.string.server_domain)+"api/promotion_info"+"?"+s);
                        request.addHeader("Content-Type", "application/json");
                        http_response = httpClient.execute(request);
                        json = handler.handleResponse(http_response);
                        System.out.println(json);
                        JSONObject jsonObject = new JSONObject(json);
                        Description description = new Description(Integer.valueOf(jsonObject.get("shop_id").toString()),
                                Integer.valueOf(jsonObject.get("name").toString()),
                                jsonObject.get("description").toString(),
                                id,
                                jsonObject.getString("updated_at"));
                        list.add(description);
                    }
                }
                Log.d("PromotionAPI", json);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        private class Description {
            public Description(int shop_id, int discount, String offer, String promotion_id, String date){
                this.shop_id = shop_id;
                this.discount = discount;
                this.offer = offer;
                this.promotion_id = promotion_id;
                this.date = date;
            }
            String promotion_id;
            int shop_id;
            int discount;
            String offer;
            String date;
        }
    }
}
