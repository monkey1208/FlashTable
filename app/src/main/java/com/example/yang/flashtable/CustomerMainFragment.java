package com.example.yang.flashtable;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View view;
    ViewFlipper vf_flipper;
    Spinner sp_dis, sp_food, sp_sort;
    String filter_mode = "all";
    ArrayAdapter<CharSequence> dis_adapter, food_adapter, sort_adapter;
    ListView lv_shops;
    List<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    EditText et_search;
    SqlHandler sqlHandler = null;
    LocationManager locationManager;

    // Views for show
    ImageButton ib_show_back;
    SliderLayout sl_restaurant;
    Button bt_show_reserve;

    // Textview in restaurant detail
    TextView tv_show_name, tv_show_consumption, tv_show_discount, tv_show_offer, tv_show_location, tv_show_category, tv_show_intro;

    final int FINE_LOCATION_CODE = 13;
    LatLng current_location;
    Location my_location;

    ApiPromotion api_promotion;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        gpsPermission();
        getShopStatus(false);
        initData();
        setSpinner();
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

    }

    private void setListView(List<CustomerRestaurantInfo> res_list) {
        restaurant_list = res_list;
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
    private void setSpinner(){
        sp_dis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0://0.5km
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                System.out.println("NOONONO");
            }
        });

        sp_food.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerMainAdapter category_adapter = null;
                switch (i){
                    case 0:
                        filter_mode = "all";
                        category_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(category_adapter);
                        break;
                    case 1:
                        filter_mode = "chinese";
                        category_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(category_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        filter_mode = "japanese";
                        category_adapter = filtAdapter(adapter, filter_mode);

                        lv_shops.setAdapter(category_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        filter_mode = "usa";
                        category_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(category_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 4:
                        filter_mode = "korean";
                        category_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(category_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String sort_mode = "default";
                CustomerMainAdapter filted_adapter;
                switch (i){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        sort_mode = "distance";
                        adapter = sortAdapter(adapter, sort_mode);
                        filted_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(filted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        sort_mode = "rate";
                        break;
                    case 4:
                        sort_mode = "discount";
                        adapter = sortAdapter(adapter, sort_mode);
                        filted_adapter = filtAdapter(adapter, filter_mode);
                        lv_shops.setAdapter(filted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private CustomerMainAdapter filtAdapter(CustomerMainAdapter filt_adapter, String mode){
        List<CustomerRestaurantInfo> r_list = new ArrayList<CustomerRestaurantInfo>();
        switch (mode) {
            case "chinese":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("中式料理")) {
                        r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "usa":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("美式料理")) {
                        r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "japanese":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("日式料理")) {
                        r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            case "korean":
                for (int j = 0; j < filt_adapter.getCount(); j++) {
                    if (filt_adapter.getItem(j).category.equals("韓式料理")) {
                        r_list.add(filt_adapter.getItem(j));
                    }
                }
                break;
            default:
                return filt_adapter;
        }
        return new CustomerMainAdapter(getActivity(), r_list, current_location);
    }
    private CustomerMainAdapter sortAdapter(CustomerMainAdapter sort_adapter, String mode){
        switch (mode){
            case "distance":
                sort_adapter.sort( new Comparator<CustomerRestaurantInfo>() {
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
                        if(info1.discount > info2.discount){
                            return 1;
                        }else if(info1.discount < info2.discount){
                            return -1;
                        }else {
                            if(info2.offer.equals("暫無優惠")){
                                return -1;
                            }
                            return 0;
                        }
                    }
                });
                break;


        }
        return sort_adapter;
    }

    public void getShopStatus(boolean active) {
        openDB();

        if (active == true) {
            new CurrentLocation().execute();
        } //else {
            //api_promotion = new ApiPromotion();
            //api_promotion.execute(24.05, 121.545);
        //}

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
        closeDB();
        super.onDestroy();
    }

    private void showRestaurantDetail(final int position) {
        // TODO: Fix issues - sometimes selected ListView flickers on setDisplayedChild()
        sl_restaurant.removeAllSliders();

        final CustomerRestaurantInfo info = adapter.getItem(position);
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
        bt_show_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerReservationActivity.class);
                intent.putExtra("promotion_id", info.promotion_id);
                intent.putExtra("discount", info.discount);
                intent.putExtra("offer", info.offer);
                startActivity(intent);
            }
        });
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

    private void gpsPermission() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getShopStatus(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_CODE);
        }
    }

    private class CurrentLocation {
        public void execute() {
            Location location = getLocation();
            my_location = location;
            if (location != null) {
                new ApiPromotion().execute(location.getLatitude(), location.getLongitude());
            } else {
                new ApiPromotion().execute(24.0, 121.0);
            }
        }
        public Location getLocation() {
            Location location = null;
            try {
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                // getting GPS status
                boolean isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                // getting network status
                boolean isNetworkEnabled = locationManager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!isGPSEnabled && !isNetworkEnabled) {
                    // no network provider is enabled
                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                0,
                                0, listener);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                return null;
                            }
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    0,
                                    0,  listener);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            }
                            locationManager.removeUpdates(listener);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return location;
        }
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("CurrentLocation", location.getLatitude()+","+location.getLongitude());
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
    }

    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        HttpClient httpClient = new DefaultHttpClient();
        List<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        private ProgressDialog progress_dialog = new ProgressDialog(view.getContext());
        private String status = null;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( "載入中..." );
            progress_dialog.show();
        }
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
                info.promotion_id = list.get(i).promotion_id;
                restaurantInfoList.add(info);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            setListView(restaurantInfoList);
            progress_dialog.dismiss();
            super.onPostExecute(s);

        }

        private List<Description> getPromotionId(String latlng){
            List<Description> list = new ArrayList<>();
            NameValuePair nameValuePair = new BasicNameValuePair("location", latlng);
            String s = nameValuePair.toString();
            HttpGet request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/surrounding_promotions"+"?"+s);
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
                        request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/promotion_info"+"?"+s);
                        request.addHeader("Content-Type", "application/json");
                        http_response = httpClient.execute(request);
                        json = handler.handleResponse(http_response);
                        JSONObject jsonObject = new JSONObject(json);
                        Description description = new Description(Integer.valueOf(jsonObject.get("shop_id").toString()),
                                Integer.valueOf(jsonObject.get("name").toString()),
                                jsonObject.get("description").toString(),
                                id);
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
            public Description(int shop_id, int discount, String offer, String promotion_id){
                this.shop_id = shop_id;
                this.discount = discount;
                this.offer = offer;
                this.promotion_id = promotion_id;
            }
            String promotion_id;
            int shop_id;
            int discount;
            String offer;
        }
    }
}
