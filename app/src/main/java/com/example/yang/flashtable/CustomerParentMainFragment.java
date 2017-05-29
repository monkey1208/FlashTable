package com.example.yang.flashtable;

import android.*;
import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.yang.flashtable.customer.CustomerAppInfo;
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
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yang on 2017/5/24.
 */

public class CustomerParentMainFragment extends Fragment {
    boolean map_showing;
    String filter_food = "all", filter_mode = "default", filter_distance = "-1";

    View view;
    private FloatingActionButton fab_map;
    Spinner sp_dis, sp_food, sp_sort;
    ImageButton ib_search;

    ArrayAdapter<CharSequence> dis_adapter, food_adapter, sort_adapter;

    Fragment fragment_map, fragment_main;

    Location my_location;
    LocationManager locationManager;

    ProgressDialog progress_dialog;

    final int FINE_LOCATION_CODE = 13;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_parent_main_fragment, container, false);
        initView();
        initData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        fragment_map = new CustomerMapFragment();
        fragment_main = new CustomerMainFragment();
    }

    private void initView(){
        setMapButton();
        sp_dis = (Spinner) view.findViewById(R.id.customer_main_sp_distance);
        sp_food = (Spinner) view.findViewById(R.id.customer_main_sp_food);
        sp_sort = (Spinner) view.findViewById(R.id.customer_main_sp_sort);
        ib_search = (ImageButton) view.findViewById(R.id.customer_main_ib_search);
    }

    private void initData(){
        CustomerObservable.getInstance().init();

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

        progress_dialog = new ProgressDialog(view.getContext());
        progress_dialog.setMessage("載入中...");
        progress_dialog.setCanceledOnTouchOutside(false);
        progress_dialog.show();
        setSpinner();
        gpsPermission();
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerSearchActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<CustomerSearchActivity.DetailInfo> detail_list = new ArrayList<>();
                for (CustomerRestaurantInfo item:CustomerAppInfo.getInstance().getRestaurantList()) {
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
    }

    private void setMapButton(){
        fab_map = (FloatingActionButton) view.findViewById(R.id.customer_fab_map);
        fab_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!map_showing) {
                    navigate("map");
                } else {
                    navigate("main");
                }
            }
        });
        fab_map.setImageResource(R.drawable.ic_float_map);
    }

    private void navigate(String command){
        if(command.equals("map")){
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_main_container, fragment_map).commit();
            map_showing = true;
            fab_map.setImageResource(R.drawable.ic_float_back);
        }else{
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.parent_main_container, fragment_main).commit();
            map_showing = false;
            fab_map.setImageResource(R.drawable.ic_float_map);
            if(progress_dialog.isShowing())
                progress_dialog.dismiss();
        }
    }

    private void setSpinner() {
        sp_dis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        filter_distance = "-1";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 1://0.5km
                        filter_distance = "500";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 2:
                        filter_distance = "1000";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 3:
                        filter_distance = "1500";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 4:
                        filter_distance = "2000";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
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
                switch (i) {
                    case 0:
                        filter_food = "all";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 1:
                        filter_food = "chinese";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 2:
                        filter_food = "japanese";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 3:
                        filter_food = "usa";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 4:
                        filter_food = "korean";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
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
                switch (i) {
                    case 0:
                        filter_mode = "default";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 1:
                        filter_mode = "time";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 2:
                        filter_mode = "distance";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 3:
                        filter_mode = "rate";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 4:
                        filter_mode = "discount";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void gpsPermission() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getShopStatus(true);
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_CODE);
        }
    }

    public void getShopStatus(boolean active) {
        if (active == true) {
            new CurrentLocation().execute();
        } else {
            gpsPermission();
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
        new DialogBuilder(getActivity()).dialogEvent(getString(R.string.dialog_gps_permission), "withCancel", new DialogEventListener() {
            @Override
            public void clickEvent(boolean ok, int status) {
                if(!ok) {
                    ((CustomerMainActivity)getActivity()).logout();
                }else{
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), CustomerMainActivity.LOCATION_SETTING_CODE);
                }
            }
        });
    }

    private class CurrentLocation {
        boolean flag = true;
        public void execute() {
            getLocation();

        }

        public void setLocation(Location location){
            if (location != null) {
                my_location = location;
                CustomerAppInfo.getInstance().setLocation(my_location);
                new ApiPromotion().execute(my_location.getLatitude(), my_location.getLongitude());
            } else {
                if(flag) {
                    gpsPermission();
                }else{
                    locationPermission();
                }
            }
        }

        public void getLocation() {
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
                    setLocation(null);
                } else {
                    if (isNetworkEnabled) {
                        locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                500,
                                0, listener);
                        Log.d("Network", "Network Enabled");
                        if (locationManager != null) {
                            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                setLocation(null);
                            }
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                        }
                    }
                    // if GPS Enabled get lat/long using GPS Services
                    if (isGPSEnabled) {
                        if (location == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    500,
                                    0, listener);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);

                            }
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(location != null){
                setLocation(location);
            }
        }

        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("CurrentLocationUpdate", location.getLatitude() + "," + location.getLongitude());
                setLocation(location);
                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                Log.d("ParentMainFragment" ,"status changed to "+s);
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
        SqlHandler sqlHandler;
        HttpClient httpClient = new DefaultHttpClient();
        ArrayList<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        private String status = null;

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
                    publishProgress();
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
            CustomerAppInfo.getInstance().setRestaurantList(restaurantInfoList);

            navigate("main");
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            new DialogBuilder(getActivity()).dialogEvent(getString(R.string.dialog_network_unable), "normal", new DialogEventListener() {
                @Override
                public void clickEvent(boolean ok, int status) {

                }
            });
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
                publishProgress();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return list;
        }

        private void openDB() {
            sqlHandler = new SqlHandler(view.getContext());
        }

        private void closeDB() {
            sqlHandler.close();
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
