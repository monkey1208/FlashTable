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
    public final int COARSE_PERMISSION_CODE = 11;
    public final int FINE_LOCATION_CODE = 12;
    private GoogleMap googleMap;

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
        gpsPermission();

    }
    public void setMap(){
        final CustomerGps gps = new CustomerGps(getActivity(), googleMap, true);
        gps.execute();
        fab_my_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gps.goToMyPosition();
            }
        });
        gps.setMarker(new LatLng(25.05, 121.545));
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
        private Activity c;
        private boolean map_active;

        public CustomerGps(Activity c, GoogleMap googleMap, boolean map_active) {
            this.c = c;
            if (map_active == true)
                this.googleMap = googleMap;
            this.map_active = map_active;
        }
        public void execute() {
            gpsUpdate();
        }

        private void gpsUpdate() {
            if (map_active == true) {
                initMarker();
            }
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
                    if (map_active == true) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        marker.setPosition(latLng);
                    } else {

                    }
                }
            }
        }

        public void goToMyPosition() {
            if (latLng != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }

        public Marker setMarker(LatLng latLng) {
            MarkerOptions options = new MarkerOptions();
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
}
