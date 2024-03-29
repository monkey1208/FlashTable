package com.example.yang.flashtable.customer;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yang.flashtable.*;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerAppInfo;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceReport;

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
import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yang on 2017/5/24.
 */

public class CustomerParentMainFragment extends Fragment {
    boolean map_showing = false;
    boolean request_promotion = true;
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
        view = inflater.inflate(com.example.yang.flashtable.R.layout.customer_parent_main_fragment, container, false);
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
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(sp_food);

            // Set popupWindow height to 500px
            popupWindow.setHeight(500);
        }
        catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
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
                for (CustomerRestaurantInfo item: CustomerAppInfo.getInstance().getRestaurantList()) {
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
                    case 5:
                        filter_distance = "3000";
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
                    case 5:
                        filter_food = "tailand";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 6:
                        filter_food = "foreign";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 7:
                        filter_food = "hotpot";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 8:
                        filter_food = "barbecue";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 9:
                        filter_food = "cafe";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 10:
                        filter_food = "vegetarian";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 11:
                        filter_food = "fastfood";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 12:
                        filter_food = "buffet";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 13:
                        filter_food = "smalleat";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 14:
                        filter_food = "drink";
                        CustomerObservable.getInstance().setData(filter_distance, filter_food, filter_mode);
                        break;
                    case 15:
                        filter_food = "other";
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
            new CurrentLocation(view.getContext()).execute();
        } else {
            gpsPermission();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CustomerMainActivity.LOCATION_SETTING_CODE) {
            if(map_showing){
                fragment_map.onActivityResult(requestCode, resultCode, data);
            }else {
                System.out.println("RESULTCODE=" + resultCode);
                gpsPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void locationPermission() {
        new DialogBuilder(getActivity()).dialogEvent(getString(R.string.dialog_gps_permission), "withCancel", new DialogEventListener() {
            @Override
            public void clickEvent(boolean ok, int status) {
                if(!ok) {
                    ((CustomerMainActivity)getActivity()).logout(true);
                }else{
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), CustomerMainActivity.LOCATION_SETTING_CODE);
                }
            }
        });
    }

    private class CurrentLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
        boolean flag = true;
        private Location mLastLocation;
        private GoogleApiClient mGoogleApiClient;
        private LocationRequest mLocationRequest;
        private Context context;

        public CurrentLocation(Context context){
            this.context = context;
        }

        public void execute() {
            getLocationPermission();
            getCurrentLocation(context);
        }

        public void setLocation(Location location){
            if (location != null) {
                my_location = location;
                CustomerAppInfo.getInstance().setLocation(my_location);
                if(request_promotion){
                    request_promotion = false;
                    new ApiPromotion().execute(my_location.getLatitude(), my_location.getLongitude());
                }

            } else {
                if(flag) {
                    gpsPermission();
                }else{
                    locationPermission();
                }
            }
        }

        public void getLocationPermission() {
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
                }
            } catch (Exception e) {
                e.printStackTrace();
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

        public Location getCurrentLocation(Context context) {
            this.context = context;
            buildGoogleApiClient();
            mGoogleApiClient.connect();
            if ((ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                flag = false;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation != null)
                setLocation(mLastLocation);
            return mLastLocation;
        }

        public void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        protected void startLocationUpdates() {
            // Create the location request
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(0)
                    .setFastestInterval(0);

            // Request location updates
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                return;
            }
            startLocationUpdates();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if(mLastLocation == null){
                startLocationUpdates();
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                setLocation(mLastLocation);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            setLocation(location);
        }

    }

    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        SqlHandler sqlHandler;
        HttpClient httpClient = new DefaultHttpClient();
        ArrayList<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();
        private String status = null;
        private DialogBuilder dialog_builder = new DialogBuilder(CustomerParentMainFragment.this.getActivity());

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

                String shop_rating = "0";
                try {
                    HttpGet requestShopRating = new HttpGet(getString(R.string.server_domain)+"api/shop_comments?shop_id=" + list.get(i).shop_id);
                    requestShopRating.addHeader("Content-Type", "application/json");
                    String s = new BasicResponseHandler().handleResponse(httpClient.execute(requestShopRating));
                    JSONArray responseShopRating = new JSONArray(s);
                    status = responseShopRating.getJSONObject(0).getString("status_code");
                    if (!status.equals("0")) break;
                    shop_rating = responseShopRating.getJSONObject(0).getString("average_score");
                } catch (Exception e) {
                    e.printStackTrace();
                    //publishProgress();
                    //shop_rating = "0";
                } finally {
                    //httpClient.getConnectionManager().shutdown();
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
            if( status == null  || !status.equals("0") )
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
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
                //publishProgress();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {

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
