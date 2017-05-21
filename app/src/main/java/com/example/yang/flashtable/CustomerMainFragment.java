package com.example.yang.flashtable;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.yang.flashtable.customer.database.SqlHandler;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment {

    DialogBuilder dialog_builder;
    View view;
    Spinner sp_dis, sp_food, sp_sort;
    String filter_mode = "all";
    int filter_distance = -1;
    ArrayAdapter<CharSequence> dis_adapter, food_adapter, sort_adapter;
    ListView lv_shops;
    SwipeRefreshLayout swipe_refresh_layout;
    ArrayList<CustomerRestaurantInfo> restaurant_list;
    ImageButton ib_search;
    CustomerMainAdapter adapter;
    CustomerMainAdapter adjusted_adapter;
    SqlHandler sqlHandler = null;
    LocationManager locationManager;

    // Location
    final int FINE_LOCATION_CODE = 13;
    LatLng current_location;
    Location my_location;

    private boolean first_loading = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        initData();

        return view;
    }

    private void initView() {
        sp_dis = (Spinner) view.findViewById(R.id.customer_main_sp_distance);
        sp_food = (Spinner) view.findViewById(R.id.customer_main_sp_food);
        sp_sort = (Spinner) view.findViewById(R.id.customer_main_sp_sort);
        lv_shops = (ListView) view.findViewById(R.id.customer_main_lv);
        swipe_refresh_layout = (SwipeRefreshLayout) view.findViewById(R.id.customer_main_srl);

        ib_search = (ImageButton) view.findViewById(R.id.customer_main_ib_search);
    }

    private void initData() {

        my_location = new Location("");
        my_location.setLatitude(25.018);
        my_location.setLongitude(121.54);
        gpsPermission();
        getShopStatus(false);

        //restaurant_list = getListFromDB();
        //setListView(restaurant_list);
        dialog_builder = new DialogBuilder(getActivity());

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

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerSearchActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<CustomerSearchActivity.DetailInfo> detail_list = new ArrayList<>();
                for (CustomerRestaurantInfo item:restaurant_list) {
                    CustomerSearchActivity.DetailInfo tmp = new CustomerSearchActivity.DetailInfo(item.id, item.discount, item.offer, item.promotion_id, item.rating);
                    detail_list.add(tmp);
                }
                bundle.putParcelableArrayList("list", detail_list);
                bundle.putDouble("longitude", my_location.getLongitude());
                bundle.putDouble("latitude", my_location.getLatitude());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        setSpinner();
        setRefreshLayout();

        sp_dis.setSelection(3);
    }

    private void setListView(ArrayList<CustomerRestaurantInfo> res_list) {
        if (restaurant_list != null)
            restaurant_list.clear();
        restaurant_list = res_list;
        System.out.println("set list!");
        if (adapter != null)
            adapter.clear();
        if (adjusted_adapter != null)
            adjusted_adapter.clear();
        adapter = new CustomerMainAdapter(view.getContext(), res_list, current_location);
        adapter = sortAdapter(adapter, "default");
        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
        adapter.notifyDataSetChanged();
        if (lv_shops != null) {
            lv_shops.setAdapter(adjusted_adapter);
            lv_shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                    showRestaurantDetail(i);
                }
            });
        }
    }

    private void setRefreshLayout() {
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gpsPermission();
            }
        });
    }

    private void setSpinner() {
        sp_dis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        filter_distance = -1;
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 1://0.5km
                        filter_distance = 500;
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 2:
                        filter_distance = 1000;
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 3:
                        filter_distance = 1500;
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 4:
                        filter_distance = 2000;
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        sp_food.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                adjusted_adapter = null;
                switch (i) {
                    case 0:
                        filter_mode = "all";
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 1:
                        filter_mode = "chinese";
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        filter_mode = "japanese";
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);

                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        filter_mode = "usa";
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 4:
                        filter_mode = "korean";
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
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
                switch (i) {
                    case 0:
                        sort_mode = "default";
                        break;
                    case 1:
                        sort_mode = "time";
                        adapter = sortAdapter(adapter, sort_mode);
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 2:
                        sort_mode = "distance";
                        adapter = sortAdapter(adapter, sort_mode);
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                    case 3:
                        sort_mode = "rate";
                        adapter = sortAdapter(adapter, sort_mode);
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        break;
                    case 4:
                        sort_mode = "discount";
                        adapter = sortAdapter(adapter, sort_mode);
                        adjusted_adapter = filtAdapter(adapter, filter_mode, filter_distance);
                        lv_shops.setAdapter(adjusted_adapter);
                        adapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
        return new CustomerMainAdapter(getActivity(), r_list, current_location);
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

    public void getShopStatus(boolean active) {


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

    private void closeDB() {
        sqlHandler.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void showRestaurantDetail(final int position) {

        final CustomerRestaurantInfo info = adjusted_adapter.getItem(position);
        CustomerShopActivity.ShowInfo showInfo = new CustomerShopActivity.ShowInfo(
                info.name,
                info.consumption,
                info.discount,
                info.offer,
                info.address,
                info.category,
                info.intro,
                info.rating,
                info.promotion_id);
        Intent intent = new Intent(getActivity(), CustomerShopActivity.class);
        intent.putExtra("info", showInfo);
        intent.putExtra("shop_id", Integer.toString(info.id));
        startActivity(intent);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CustomerMainActivity.LOCATION_SETTING_CODE) {
            System.out.println("RESULTCODE=" + resultCode);
            gpsPermission();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void locationPermission() {
        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), CustomerMainActivity.LOCATION_SETTING_CODE);
    }

    private class CurrentLocation {
        boolean flag = true;
        public void execute() {
            Location location = getLocation();
            if (location != null) {
                my_location = location;
                new ApiPromotion().execute(location.getLatitude(), location.getLongitude());
            } else {
                if(flag) {
                    new ApiPromotion().execute(my_location.getLatitude(), my_location.getLongitude());
                }
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
                    flag = false;
                    locationPermission();
                    return null;
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
                                    0, listener);
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
                Log.d("CurrentLocationUpdate", location.getLatitude() + "," + location.getLongitude());

                //locationManager.removeUpdates(this);
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
        ArrayList<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        private ProgressDialog progress_dialog;
        private String status = null;

        @Override
        protected void onPreExecute() {
            openDB();
            if(first_loading) {
                progress_dialog = new ProgressDialog(view.getContext());
                progress_dialog.setMessage("載入中...");
                progress_dialog.show();
            }
        }
        @Override
        protected String doInBackground(Double... params) {
            current_location = new LatLng(params[0], params[1]);
            String lat = String.valueOf(params[0]);
            String lng = String.valueOf(params[1]);
            String latlng = lat + "," + lng;//need current location
            Log.d("APIPromotion", "latlng = "+latlng);
            ArrayList<Description> list = getPromotionId(latlng);
            for(int i = 0; i < list.size(); i++) {
                CustomerRestaurantInfo info = sqlHandler.getDetail(list.get(i).shop_id);
                info.id = list.get(i).shop_id;
                info.discount = list.get(i).discount;
                info.offer = list.get(i).offer;
                info.promotion_id = list.get(i).promotion_id;
                info.date = list.get(i).date;

                String shop_rating;
                try {
                    HttpGet requestShopRating = new HttpGet("https://flash-table.herokuapp.com/api/shop_comments?shop_id=" + list.get(i).shop_id);
                    requestShopRating.addHeader("Content-Type", "application/json");
                    JSONArray responseShopRating = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(requestShopRating)));
                    status = responseShopRating.getJSONObject(0).getString("status_code");
                    if (!status.equals("0")) break;
                    shop_rating = responseShopRating.getJSONObject(0).getString("average_score");
                } catch (Exception e) {
                    shop_rating = "0";
                }
                info.rating = Float.parseFloat(shop_rating) / 2;

                restaurantInfoList.add(info);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            closeDB();
            setListView(restaurantInfoList);
            if(first_loading) {
                progress_dialog.dismiss();
                first_loading = false;
            }else{
                swipe_refresh_layout.setRefreshing(false);
            }
            super.onPostExecute(s);

        }

        private ArrayList<Description> getPromotionId(String latlng){
            ArrayList<Description> list = new ArrayList<>();
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
