package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
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

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMapFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private FloatingActionButton fab_my_position;
    public final int COARSE_PERMISSION_CODE = 11;
    public final int FINE_LOCATION_CODE = 12;
    private GoogleMap googleMap;
    private CustomerGps gps;
    private List<CustomerRestaurantInfo> restaurantInfoList = new ArrayList<>();

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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        gps = new CustomerGps(getActivity(), googleMap);
        gpsPermission();

    }
    public void setMap(){
        new ApiPromotion(gps).execute(24.0, 121.0);
        gps.execute();
        fab_my_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps.goToMyPosition();
            }
        });
        //gps.setMarker(new LatLng(25.05, 121.545));
    }


    private void gpsPermission(){
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
    public class CustomerGps {
        private static final String TAG = "GPSService";
        private LocationManager locationMgr;
        private String provider;
        private GoogleMap googleMap;
        private LatLng latLng = null;
        private MarkerOptions markerOptions;
        private Marker marker;
        private Marker pre_marker = null;
        private Activity c;
        private View bottom_sheet;
        private BottomSheetBehavior bottom_sheet_behavior;

        public CustomerGps(Activity c, GoogleMap googleMap) {
            this.c = c;
            this.googleMap = googleMap;
            init();
        }
        private void init(){
            googleMap.setOnMarkerClickListener(markerClickListener);
            bottom_sheet = (View)view.findViewById(R.id.customer_map_bottom_sheet);
            bottom_sheet_behavior = BottomSheetBehavior.from(bottom_sheet);

        }
        GoogleMap.OnMarkerClickListener markerClickListener = new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(!marker.getSnippet().equals("me")) {
                    if (pre_marker != null) {
                        //Set prevMarker back to default color
                        pre_marker.setIcon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_restaurant)));
                        bottom_sheet.setVisibility(View.INVISIBLE);
                        bottom_sheet_behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }

                    //leave Marker default color if re-click current Marker
                    if (!marker.equals(pre_marker)) {
                        marker.setIcon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_choosedrestaurant)));
                        pre_marker = marker;
                        bottom_sheet.setVisibility(View.VISIBLE);
                        bottom_sheet_behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        TextView tv_name = (TextView) bottom_sheet.findViewById(R.id.customer_map_bottom_sheet_tv_name);
                        int index = Integer.valueOf(marker.getSnippet());
                        tv_name.setText(restaurantInfoList.get(index).name);
                    } else {
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
            initMarker();
            if (initLocationProvider()) {
                Log.d("DEBUG", "PROVIDER");
                whereAmI();
            } else {
                Toast.makeText(c, "No Location Provider", Toast.LENGTH_SHORT).show();
                Log.d("CustomerGps", "No Location Provider");
            }
        }

        private void initMarker() {
            markerOptions = new MarkerOptions();
            latLng = new LatLng(25.021918, 121.535285);
            markerOptions.anchor(0.75f, 0.5f)
                    .position(latLng)
                    .snippet("me")
                    .icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.customer_map_me)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            marker = googleMap.addMarker(markerOptions);

        }

        private void whereAmI() {
            //取得上次已知的位置
            Location location = locationMgr.getLastKnownLocation(provider);
            updateWithNewLocation(location);
            //GPS Listener
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationMgr.addGpsStatusListener(gpsListener);
            //Location Listener
            int minTime = 0;//ms
            int minDist = 3;//meter
            locationMgr.requestLocationUpdates(provider, minTime, minDist, locationListener);
        }

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("CustomerMapFragment", "location change");
                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                location = locationMgr.getLastKnownLocation(provider);
                updateWithNewLocation(location);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("CustomerMapFragment", "Disable");
                updateWithNewLocation(null);
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d("CustomerMapFragment", "Enable");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.v(TAG, "Status Changed: Out of Service");
                        Toast.makeText(c, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.v(TAG, "Status Changed: Temporarily Unavailable");
                        Toast.makeText(c, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.v(TAG, "Status Changed: Available");
                        Toast.makeText(c, "Status Changed: Available", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        GpsStatus.Listener gpsListener = new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        Log.d(TAG, "GPS_EVENT_STARTED");
                        Toast.makeText(c, "GPS_EVENT_STARTED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        Log.d(TAG, "GPS_EVENT_STOPPED");
                        Toast.makeText(c, "GPS_EVENT_STOPPED", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        Log.d(TAG, "GPS_EVENT_FIRST_FIX");
                        Toast.makeText(c, "GPS_EVENT_FIRST_FIX", Toast.LENGTH_SHORT).show();
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                        Log.d(TAG, "GPS_EVENT_SATELLITE_STATUS");
                        break;
                }
            }
        };

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
                    moveMap(latLng);
                    marker.setPosition(latLng);
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
                    .icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_restaurant)))
                    .snippet(index+"");
            Marker restaurant_marker = googleMap.addMarker(options);
            return restaurant_marker;
        }

        private boolean initLocationProvider() {
            locationMgr = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            provider = locationMgr.getBestProvider(criteria, true);
            if (provider != null) {
                Log.d(TAG, "Provider: " + provider);
                return true;
            }
            return false;
        }

        private Bitmap createScaledMarker(int resource) {
            int height = 120;
            int width = 120;
            BitmapDrawable bitmapdraw = (BitmapDrawable) c.getResources().getDrawable(resource);
            Bitmap b = bitmapdraw.getBitmap();
            return Bitmap.createScaledBitmap(b, width, height, false);
        }

    }
    private class ApiPromotion extends AsyncTask<Double, Void, String> {
        HttpClient httpClient = new DefaultHttpClient();
        CustomerGps gps;
        public ApiPromotion(CustomerGps gps){
            this.gps = gps;
        }
        @Override
        protected String doInBackground(Double... params) {
            SqlHandler sqlHandler = new SqlHandler(getActivity());
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
            for(int i = 0; i < restaurantInfoList.size(); i++){
                gps.setMarker(restaurantInfoList.get(i).latLng, i);
                System.out.println(restaurantInfoList.get(i).latLng.longitude);
                System.out.println(restaurantInfoList.get(i).latLng.latitude);
            }
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
