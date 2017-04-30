package com.example.yang.flashtable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class CustomerMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private FloatingActionButton fab_map;
    private Fragment fragment;
    private boolean map_showing;
    private NavigationView nv_view;
    public final int COARSE_PERMISSION_CODE = 11;
    public final int FINE_LOCATION_MAP_CODE = 12;
    public final int FINE_LOCATION_MAIN_CODE = 13;
    public final static int LOCATION_SETTING_CODE = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_main_activity);
        initView();
        initData();
        checkBlock();
    }

    private void initView() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        nv_view = (NavigationView) findViewById(R.id.nav_view);
    }

    private void initData() {
        setDrawer();
        setMapButton();
        navigate("main");
        nv_view.getMenu().getItem(0).setChecked(true);
        map_showing = false;
    }

    // Change setFragment to navigate for more general-purposed naming
    private void navigate(String input){
        switch (input){
            case "main":
                fab_map.setVisibility(View.VISIBLE);
                fragment = new CustomerMainFragment();
                map_showing = false;
                fab_map.setImageResource(R.drawable.ic_float_map);
                break;
            case "detail":
                // TODO: Handle checked item properly.
                Intent intent = new Intent(this, CustomerDetailActivity.class);
                startActivity(intent);
                break;
            case "profile":
                fab_map.setVisibility(View.GONE);
                fragment = new CustomerProfileFragment();
                break;
            case "map":
                fab_map.setVisibility(View.VISIBLE);
                fragment = new CustomerMapFragment();
                map_showing = true;
                fab_map.setImageResource(R.drawable.ic_float_back);
                break;
            default:
                break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.customer_frame, fragment).commit();
    }
    private void setMapButton(){
        fab_map = (FloatingActionButton) findViewById(R.id.customer_fab_map);
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
    private void setDrawer(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.customer_fab_menu);
        fab.setImageResource(R.drawable.ic_float_menu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        nv_view = (NavigationView) findViewById(R.id.nav_view);
        nv_view.setNavigationItemSelectedListener(this);
    }
    private void checkBlock(){
        if(CustomerReservationActivity.GetBlockInfo.getBlockStatus(this)){
            //Go to block page
            Intent intent = new Intent(this, CustomerReservationActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.customer_drawer_reservation) {
            // Handle the camera action
            navigate("main");
        } else if (id == R.id.customer_drawer_detail) {
            navigate("detail");
        } else if (id == R.id.customer_drawer_profile) {
            navigate("profile");
        } else if (id == R.id.customer_drawer_logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case FINE_LOCATION_MAP_CODE :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted

                    ((CustomerMapFragment)fragment).setMap();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            case FINE_LOCATION_MAIN_CODE :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ((CustomerMainFragment)fragment).getShopStatus(true);
                } else {
                    ((CustomerMainFragment)fragment).getShopStatus(false);
                }

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



    private void logout() {
        SharedPreferences preferences = this.getSharedPreferences("USER", MODE_PRIVATE);
        preferences.edit().clear().apply();
        Intent intent = new Intent(CustomerMainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
