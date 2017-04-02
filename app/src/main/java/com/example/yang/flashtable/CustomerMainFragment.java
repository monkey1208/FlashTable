package com.example.yang.flashtable;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View view;
    ViewFlipper vf_flipper;
    Spinner sp_dis, sp_food, sp_sort;
    ArrayAdapter<CharSequence> dis_adapter, food_adapter, sort_adapter;
    ListView lv_shops;
    List<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    EditText et_search;
    SqlHandler sqlHandler = null;
    LocationManager locationMgr;

    // Views for show
    ImageButton ib_show_back;
    SliderLayout sl_restaurant;
    Button bt_show_reserve;

    // Textview in restaurant detail
    TextView tv_show_name, tv_show_consumption, tv_show_discount, tv_show_offer, tv_show_location, tv_show_category, tv_show_intro;

    final int FINE_LOCATION_CODE = 13;
    LatLng current_location;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        gpsPermission();
        //getShopStatus(false);
        initData();
        return view;
    }

    private void initView() {
        vf_flipper = (ViewFlipper) view.findViewById(R.id.customer_main_vf_flipper);
        sp_dis = (Spinner) view.findViewById(R.id.customer_main_sp_distance);
        sp_food = (Spinner) view.findViewById(R.id.customer_main_sp_food);
        sp_sort = (Spinner) view.findViewById(R.id.customer_main_sp_sort);
        lv_shops = (ListView) view.findViewById(R.id.customer_main_lv);
        et_search = (EditText) view.findViewById(R.id.customer_main_et_search);

        // Views related to show
        sl_restaurant = (SliderLayout) view.findViewById(R.id.customer_main_sl_restaurant);
        ib_show_back = (ImageButton) view.findViewById(R.id.customer_main_ib_show_back);
        bt_show_reserve = (Button) view.findViewById(R.id.customer_main_bt_show_reserve);

        tv_show_name = (TextView) view.findViewById(R.id.customer_main_tv_show_shop);
        tv_show_discount = (TextView) view.findViewById(R.id.customer_main_tv_show_discount);
        tv_show_offer = (TextView) view.findViewById(R.id.customer_main_tv_show_gift);
        tv_show_consumption = (TextView) view.findViewById(R.id.customer_main_tv_show_price);
        tv_show_location = (TextView) view.findViewById(R.id.customer_main_tv_show_location);
        tv_show_category = (TextView) view.findViewById(R.id.customer_main_tv_show_category);
        tv_show_intro = (TextView) view.findViewById(R.id.customer_main_tv_show_description);
    }

    private void initData() {
        //restaurant_list = getListFromDB();
        //setListView(restaurant_list);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int key_code, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && key_code == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(getActivity(), "Search " + et_search.getText(), Toast.LENGTH_SHORT).show();
                    et_search.setText("");
                }
                return true;
            }
        });

        dis_adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.customer_sp_distance, R.layout.customer_main_spinner_item);
        dis_adapter.setDropDownViewResource(R.layout.customer_main_spinner_dropdown_item);
        sp_dis.setAdapter(dis_adapter);
        food_adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.customer_sp_food, R.layout.customer_main_spinner_item);
        food_adapter.setDropDownViewResource(R.layout.customer_main_spinner_dropdown_item);
        sp_food.setAdapter(food_adapter);
        sort_adapter = ArrayAdapter.createFromResource(getActivity().getBaseContext(),
                R.array.customer_sp_sort, R.layout.customer_main_spinner_item);
        sort_adapter.setDropDownViewResource(R.layout.customer_main_spinner_dropdown_item);
        sp_sort.setAdapter(sort_adapter);

        // TODO: Override back button so that it will return to main if at show

        // Data in show view
        ib_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeRestaurantDetail();
            }
        });
        bt_show_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerReservationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setListView(List<CustomerRestaurantInfo> res_list) {
        System.out.println("set list!");
        adapter = new CustomerMainAdapter(view.getContext(), res_list, current_location);
        adapter.notifyDataSetChanged();
        lv_shops.setAdapter(adapter);
        lv_shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                showRestaurantDetail(i);
            }
        });
    }

    public void getShopStatus(boolean active) {
        openDB();

        if (active == true) {
            new CurrentLocation().execute();
        }else{
            new ApiPromotion().execute(24.05, 121.545);
        }

    }


    // DB related functions
    private void openDB() {
        sqlHandler = new SqlHandler(view.getContext());
    }

    private List<CustomerRestaurantInfo> getListFromDB() {
        return sqlHandler.getList();
    }

    private void closeDB() {
        sqlHandler.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //closeDB();
    }

    private List<CustomerRestaurantInfo> generateTestList() {
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            LatLng latLng = new LatLng(25.01, 121.5);
            CustomerRestaurantInfo info = new CustomerRestaurantInfo("Restaurant " + Integer.toString(i), i, 100, "tag", latLng);
            info.detailInfo.setInfo("address", "garbage store");
            list.add(info);
            latLng = null;
            info = null;
        }
        return list;
    }

    private void showRestaurantDetail(int position) {
        // TODO: Fix issues - sometimes selected ListView flickers on setDisplayedChild()
        sl_restaurant.removeAllSliders();

        CustomerRestaurantInfo info = adapter.getItem(position);
        vf_flipper.setDisplayedChild(1);

        HashMap<String, Integer> image_map = new HashMap<>();
        image_map.put("1", R.drawable.slide_1);
        image_map.put("2", R.drawable.slide_2);
        image_map.put("3", R.drawable.slide_3);

        for (String name : image_map.keySet()) {
            // Change DefaultSliderView to TextSliderView if you want text below it
            DefaultSliderView slider_view = new DefaultSliderView(getActivity().getBaseContext());
            slider_view
                    .description(name)
                    .image(image_map.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
            sl_restaurant.addSlider(slider_view);
        }
        sl_restaurant.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sl_restaurant.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sl_restaurant.setDuration(4000);
        sl_restaurant.addOnPageChangeListener(this);
        sl_restaurant.startAutoCycle();

        tv_show_name.setText(info.name);
        tv_show_consumption.setText("均消$" + info.consumption);
        if (info.discount == 101 || info.discount == 100) {
            tv_show_discount.setText("暫無折扣");
        } else {
            int point = info.discount % 10;
            int discount = info.discount / 10;
            if (point != 0) {
                tv_show_discount.setText(discount + point + "折");
            } else {
                tv_show_discount.setText(discount + "折");
            }
        }
        tv_show_offer.setText(info.offer);
        tv_show_location.setText(info.detailInfo.address);
        tv_show_category.setText(info.category);
        tv_show_intro.setText(info.detailInfo.intro);
    }

    private void closeRestaurantDetail() {
        vf_flipper.setDisplayedChild(0);
        sl_restaurant.stopAutoCycle();
    }

    @Override
    public void onStop() {
        sl_restaurant.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        sl_restaurant.moveNextPosition();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
    private void gpsPermission(){
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getShopStatus(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_CODE);
        }
    }

    private class CurrentLocation implements LocationListener {
        String provider;
        public void execute(){
            locationMgr = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            provider = locationMgr.getBestProvider(criteria, true);
            criteria = null;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            } else{
                Location location = locationMgr.getLastKnownLocation(provider);
                if(location != null) {
                    new ApiPromotion().execute(location.getLatitude(), location.getLongitude());
                }else{
                    locationMgr.requestLocationUpdates(provider, 0, 0, this);
                }
            }
        }

        @Override
        public void onLocationChanged(Location location) {
            if(location != null){
                new ApiPromotion().execute(location.getLatitude(), location.getLongitude());
            }else{
                new ApiPromotion().execute(24.0, 121.0);
            }
        }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onProviderDisabled(String s) { }
    }

    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        HttpClient httpClient = new DefaultHttpClient();
        List<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        @Override
        protected String doInBackground(Double... params) {
            current_location = new LatLng(params[0], params[1]);
            String lat = String.valueOf(params[0]);
            String lng = String.valueOf(params[1]);
            String latlng = lat + "," + lng;//need current location
            Log.d("APIPromotion", "latlng = "+latlng);
            List<Description> list = getPromotionId(latlng);
            for(int i = 0; i < list.size(); i++) {
                CustomerRestaurantInfo info = sqlHandler.getDetail(list.get(i).shop_id);
                info.discount = list.get(i).discount;
                info.offer = list.get(i).offer;
                restaurantInfoList.add(info);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            setListView(restaurantInfoList);
            super.onPostExecute(s);

        }

        private List<Description> getPromotionId(String latlng){
            List<Description> list = new ArrayList<>();
            NameValuePair nameValuePair = new BasicNameValuePair("location", latlng);
            String s = nameValuePair.toString();
            HttpGet request = new HttpGet("https://flash-table.herokuapp.com/api/surrounding_promotions"+"?"+s);
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(http_response);
                JSONArray jsonArray = new JSONArray(json);
                if(jsonArray.getJSONObject(0).get("status_code").equals("0")) {
                    int size = Integer.valueOf(jsonArray.getJSONObject(0).get("size").toString());
                    for(int i = 1; i <= size; i++){
                        String id = jsonArray.getJSONObject(i).get("promotion_id").toString();
                        nameValuePair = null;
                        nameValuePair = new BasicNameValuePair("promotion_id", id);
                        s = nameValuePair.toString();
                        request = new HttpGet("https://flash-table.herokuapp.com/api/promotion_info"+"?"+s);
                        request.addHeader("Content-Type", "application/json");
                        http_response = httpClient.execute(request);
                        json = handler.handleResponse(http_response);
                        JSONObject jsonObject = new JSONObject(json);
                        Description description = new Description(Integer.valueOf(jsonObject.get("shop_id").toString()),
                                Integer.valueOf(jsonObject.get("name").toString()),
                                jsonObject.get("description").toString());
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
            public Description(int shop_id, int discount, String offer){
                this.shop_id = shop_id;
                this.discount = discount;
                this.offer = offer;
            }
            int shop_id;
            int discount;
            String offer;
        }
    }
}
