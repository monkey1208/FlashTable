package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMapFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private FloatingActionButton fab_my_position;

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

        final GPSService gpsService = new GPSService(getActivity(), googleMap);
        gpsService.execute();
        fab_my_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gpsService.goToMyPosition();
            }
        });
        gpsService.setMarker(new LatLng(25.05, 121.545));
    }



    public class GPSService {
        private static final String TAG = "GPSService";
        private LocationManager locationMgr;
        private String provider;
        private GoogleMap googleMap;
        private LatLng latLng = null;
        private MarkerOptions markerOptions;
        private Marker marker;
        private final int COARSE_PERMISSION_CODE = 11;
        private final int FINE_LOCATION_CODE = 12;
        private Activity c;

        public GPSService(Activity c, GoogleMap googleMap) {
            this.c = c;
            this.googleMap = googleMap;
        }

        public void execute() {
            init();
            if (initLocationProvider()) {
                whereAmI();
            } else {

            }
            if (askpermission() == false) {
                System.out.println("GPS_permission_denied!");
                return;
            }
        }

        private void init() {
            markerOptions = new MarkerOptions();
            latLng = new LatLng(25.021918, 121.535285);
            markerOptions.anchor(0.75f, 0.5f)
                    .position(latLng)
                    .title("ME")
                    .icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.customer_map_me)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
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
            int minTime = 500;//ms
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
                Log.d(TAG, "new location latlng=("+lat+","+lng+")");
                latLng = null;
                latLng = new LatLng(lat, lng);
                if(latLng != null) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    marker.setPosition(latLng);
                }
            }else{
            }
        }
        public void goToMyPosition(){
            if(latLng != null){
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
        public Marker setMarker(LatLng latLng){
            MarkerOptions options =new MarkerOptions();
            options.position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(createScaledMarker(R.drawable.ic_customer_map_restaurant)));
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
                Log.d(TAG, "Provider: "+provider);
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
        private boolean askpermission(){
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(c, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        FINE_LOCATION_CODE);
                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(c, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        COARSE_PERMISSION_CODE);

                if (ActivityCompat.checkSelfPermission(c, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
            return true;
        }

    }

}
