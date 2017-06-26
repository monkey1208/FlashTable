package com.example.yang.flashtable.customer;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerAppInfo;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMapFragment extends Fragment implements OnMapReadyCallback, Observer {
    private View view;
    private FloatingActionButton fab_my_position;
    public final int COARSE_PERMISSION_CODE = 11;
    public final int FINE_LOCATION_CODE = 12;
    private GoogleMap googleMap;
    private CustomerGps gps;
    private ArrayList<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_map_fragment, container, false);
        fab_my_position = (FloatingActionButton) view.findViewById(R.id.customer_fab_my_position);
        fab_my_position.setImageResource(R.drawable.ic_customer_map_mylocation);
        if (view != null) {
            return view;
        } else {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.customer_map);
        mapFragment.getMapAsync(this);
        CustomerObservable.getInstance().addObserver(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CustomerObservable.getInstance().deleteObserver(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        gps = new CustomerGps(getActivity(), googleMap);
        gpsPermission();

    }

    public void setMap() {
        new ApiPromotion(gps).execute(CustomerAppInfo.getInstance().getLocation().getLatitude(), CustomerAppInfo.getInstance().getLocation().getLongitude());
        gps.execute();
        fab_my_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps.goToMyPosition();
            }
        });
        //gps.setMarker(new LatLng(25.05, 121.545));
    }

    private void gpsPermission() {
        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            setMap();
        } else {
            // Show rationale and request permission.
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    FINE_LOCATION_CODE);
        }
    }

    public void setRestMap(){
        gps.removeMarker();
        gps.initMarker(false);
        restaurantInfoList = CustomerAppInfo.getInstance().getRestaurantList();
        ArrayList<CustomerRestaurantInfo> display_list = new ArrayList<>();
        for(CustomerRestaurantInfo item: restaurantInfoList){
            if(displayable(item)){
                display_list.add(item);
            }
        }
        restaurantInfoList = display_list;
        for(int i = 0; i < restaurantInfoList.size(); i++){
            gps.setMarker(restaurantInfoList.get(i).latLng, i);
        }
    }

    private boolean displayable(CustomerRestaurantInfo item){
        if(foodFilt(item) && distanceFilt(item))
            return true;
        else
            return false;
    }

    private boolean foodFilt(CustomerRestaurantInfo item){
        switch (CustomerObservable.getInstance().food){
            case "all":
                return true;
            case "chinese":
                if(item.category.equals("中式料理"))
                    return true;
                else
                    return false;
            case "usa":
                if(item.category.equals("美式料理"))
                    return true;
                else
                    return false;
            case "japanese":
                if(item.category.equals("日式料理"))
                    return true;
                else
                    return false;
            case "korean":
                if(item.category.equals("韓式料理"))
                    return true;
                else
                    return false;
        }
        return false;
    }

    private boolean distanceFilt(CustomerRestaurantInfo item){
        int distance = Integer.parseInt(CustomerObservable.getInstance().distance);
        if(distance == -1)
            return true;
        Location item_location = new Location("");
        Location my_location = CustomerAppInfo.getInstance().getLocation();
        item_location.setLatitude(item.latLng.latitude);
        item_location.setLongitude(item.latLng.longitude);
        if(item_location.distanceTo(my_location) < distance)
            return true;
        return false;
    }

    @Override
    public void update(Observable o, Object arg) {
        setRestMap();
    }

    public class CustomerGps implements GoogleApiClient.ConnectionCallbacks {
        private static final String TAG = "GPSService";
        private LocationManager locationManager;
        private String provider;
        private GoogleMap googleMap;
        private LatLng latLng = null;
        private MarkerOptions markerOptions;
        private Marker marker;
        private Marker pre_marker = null;
        private Activity c;
        private View bottom_sheet;
        private BottomSheetBehavior bottom_sheet_behavior;
        private BitmapDescriptor descriptor_origin, descriptor_clicked;

        public CustomerGps(Activity c, GoogleMap googleMap) {
            this.c = c;
            this.googleMap = googleMap;
            //descriptor_origin = BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_orange));
            //descriptor_clicked = BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_red));
            descriptor_origin = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_customer_map_orange));
            descriptor_clicked = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_customer_map_red));



            //descriptor_origin = BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_restaurant));
            //descriptor_clicked = BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_choosedrestaurant));
        }

        private void init() {
            googleMap.setOnMarkerClickListener(markerClickListener);
            bottom_sheet = (View) view.findViewById(R.id.customer_map_bottom_sheet);
            bottom_sheet_behavior = BottomSheetBehavior.from(bottom_sheet);

        }

        GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (!marker.getSnippet().equals("me")) {
                    if (pre_marker != null) {
                        //Set prevMarker back to default color
                        pre_marker.setIcon(descriptor_origin);
                        bottom_sheet.setVisibility(View.INVISIBLE);
                        bottom_sheet_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    //leave Marker default color if re-click current Marker
                    if (!marker.equals(pre_marker)) {
                        marker.setIcon(descriptor_clicked);
                        pre_marker = marker;
                        bottom_sheet.setVisibility(View.VISIBLE);
                        bottom_sheet_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        /*
                        TextView tv_name = (TextView) bottom_sheet.findViewById(R.id.customer_map_bottom_sheet_tv_name);
                        TextView tv_discount = (TextView) bottom_sheet.findViewById(R.id.customer_map_bottom_sheet_tv_discount);
                        TextView tv_offer = (TextView) bottom_sheet.findViewById(R.id.customer_map_bottom_sheet_tv_offer);
                        TextView tv_dis = (TextView) bottom_sheet.findViewById(R.id.customer_map_bottom_sheet_tv_distance);
                        */
                        TextView tv_name = (TextView) bottom_sheet.findViewById(R.id.customer_main_tv_name);
                        TextView tv_offer = (TextView) bottom_sheet.findViewById(R.id.customer_main_tv_gift);
                        TextView tv_dis = (TextView) bottom_sheet.findViewById(R.id.customer_main_tv_distance);
                        TextView tv_consume = (TextView) bottom_sheet.findViewById(R.id.customer_main_tv_price);
                        RatingBar rb = (RatingBar) bottom_sheet.findViewById(R.id.customer_main_rb_rating);
                        ImageView iv_shop = (ImageView) bottom_sheet.findViewById(R.id.customer_main_iv_shop);
                        int index = Integer.valueOf(marker.getSnippet());
                        tv_name.setText(restaurantInfoList.get(index).name);
                        tv_offer.setText(restaurantInfoList.get(index).offer);
                        Location l = new Location("");
                        l.setLongitude(restaurantInfoList.get(index).latLng.longitude);
                        l.setLatitude(restaurantInfoList.get(index).latLng.latitude);
                        Location m = new Location("");
                        m.setLongitude(latLng.longitude);
                        m.setLatitude(latLng.latitude);
                        tv_dis.setText("< "+ (int)l.distanceTo(m) +" m");
                        iv_shop.setImageBitmap(restaurantInfoList.get(index).getImage());
                        tv_consume.setText("均消$" + restaurantInfoList.get(index).consumption);
                        rb.setRating(restaurantInfoList.get(index).rating);
                        rb.setIsIndicator(true);

                    } else {
                        bottom_sheet.setVisibility(View.INVISIBLE);
                        bottom_sheet_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        pre_marker = null;
                    }
                }
                return false;
            }
        };

        public void execute() {
            gpsUpdate();
        }

        private void gpsUpdate() {
            if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            initMarker(true);
            Location location = getLocation();
            moveMap(new LatLng(location.getLatitude(), location.getLongitude()));
            updateWithNewLocation(location);
            init();
        }

        private void initMarker(boolean moveCamera) {
            markerOptions = new MarkerOptions();
            double lng = CustomerAppInfo.getInstance().getLocation().getLongitude();
            double lat = CustomerAppInfo.getInstance().getLocation().getLatitude();
            latLng = new LatLng(lat, lng);
            if (latLng == null) {
                latLng = new LatLng(25.021918, 121.535285);
            }
            markerOptions.anchor(0.75f, 0.5f)
                    .position(latLng)
                    .snippet("me")
                    .icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.customer_map_me)));
            if(moveCamera)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            marker = googleMap.addMarker(markerOptions);

        }



        private void updateWithNewLocation(Location location) {
            if (location != null) {
                //經度
                double lng = location.getLongitude();
                //緯度
                double lat = location.getLatitude();
                Log.d(TAG, "new location latlng=(" + lat + "," + lng + ")");
                latLng = null;
                latLng = new LatLng(lat, lng);
                if (latLng != null) {
                    marker.setPosition(latLng);
                    CustomerAppInfo.getInstance().setLocation(location);
                }
            }
        }

        private void moveMap(LatLng place) {
            CameraPosition cameraPosition =
                    new CameraPosition.Builder()
                            .target(place)
                            .zoom(17)
                            .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        public void goToMyPosition() {
            if (latLng != null) {
                moveMap(latLng);
            }
        }

        public Marker setMarker(LatLng latLng, int index) {
            MarkerOptions options = new MarkerOptions();
            options.position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_customer_map_orange)))
                    //.icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_orange)))
                    .snippet(index + "");
            Marker restaurant_marker = googleMap.addMarker(options);
            return restaurant_marker;
        }

        public void removeMarker(){
            googleMap.clear();
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
                                0, new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        updateWithNewLocation(location);
                                        Log.d(TAG, location.getLatitude()+","+location.getLongitude());
                                    }

                                    @Override
                                    public void onStatusChanged(String s, int i, Bundle bundle) {
                                        Log.d(TAG, "change");
                                    }

                                    @Override
                                    public void onProviderEnabled(String s) {
                                        Log.d(TAG, "enable");
                                    }

                                    @Override
                                    public void onProviderDisabled(String s) {
                                        Log.d(TAG, "disable");
                                    }
                                });
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
                                    0,  new LocationListener() {
                                        @Override
                                        public void onLocationChanged(Location location) {
                                            updateWithNewLocation(location);
                                            Log.d(TAG, location.getLatitude()+","+location.getLongitude());
                                        }

                                        @Override
                                        public void onStatusChanged(String s, int i, Bundle bundle) {
                                            Log.d(TAG, "change");
                                        }

                                        @Override
                                        public void onProviderEnabled(String s) {
                                            Log.d(TAG, "enable");
                                        }

                                        @Override
                                        public void onProviderDisabled(String s) {
                                            Log.d(TAG, "disable");
                                        }
                                    });
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


            return location;
        }

        private Bitmap createScaledMarker(int resource) {
            int height = 120;
            int width = 120;
            BitmapDrawable bitmapdraw = (BitmapDrawable) c.getResources().getDrawable(resource);
            Bitmap b = bitmapdraw.getBitmap();
            return Bitmap.createScaledBitmap(b, width, height, false);
        }

        @Override
        public void onConnected(Bundle bundle) {
            
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        HttpClient httpClient = new DefaultHttpClient();
        CustomerGps gps;
        ArrayList<CustomerRestaurantInfo> mlist = new ArrayList<>();
        private ProgressDialog progress_dialog = new ProgressDialog(view.getContext());
        public ApiPromotion(CustomerGps gps) {
            this.gps = gps;
        }

        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( "載入中..." );
            progress_dialog.setCanceledOnTouchOutside(false);
            progress_dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Double... params) {
            SqlHandler sqlHandler = new SqlHandler(getActivity());
            String lat = String.valueOf(params[0]);
            String lng = String.valueOf(params[1]);
            String latlng = lat + "," + lng;//need current location
            Log.d("APIPromotion", "latlng = " + latlng);
            List<Description> list = getPromotionId(latlng);
            for (int i = 0; i < list.size(); i++) {
                CustomerRestaurantInfo info = sqlHandler.getDetail(list.get(i).shop_id);
                info.discount = list.get(i).discount;
                info.offer = list.get(i).offer;
                mlist.add(info);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            CustomerAppInfo.getInstance().setRestaurantList(mlist);
            setRestMap();
            restaurantInfoList = mlist;
            progress_dialog.dismiss();
            super.onPostExecute(s);

        }

        private List<Description> getPromotionId(String latlng) {
            List<Description> list = new ArrayList<>();
            NameValuePair nameValuePair = new BasicNameValuePair("location", latlng);
            String s = nameValuePair.toString();
            HttpGet request = new HttpGet(getString(R.string.server_domain) + "api/surrounding_promotions" + "?" + s);
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(http_response);
                JSONArray jsonArray = new JSONArray(json);
                if (jsonArray.getJSONObject(0).get("status_code").equals("0")) {
                    int size = Integer.valueOf(jsonArray.getJSONObject(0).get("size").toString());
                    for (int i = 1; i <= size; i++) {
                        String id = jsonArray.getJSONObject(i).get("promotion_id").toString();
                        nameValuePair = null;
                        nameValuePair = new BasicNameValuePair("promotion_id", id);
                        s = nameValuePair.toString();
                        request = new HttpGet(getString(R.string.server_domain) + "api/promotion_info" + "?" + s);
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
            public Description(int shop_id, int discount, String offer) {
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
