package com.example.yang.flashtable;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.yang.flashtable.customer.CustomerFlashPointFragment;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        checkBlock();
        initView();
        initData();
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
            case "points":
                fab_map.setVisibility(View.GONE);
                fragment = new CustomerFlashPointFragment();
                break;
            case "map":
                fab_map.setVisibility(View.VISIBLE);
                //fragment = new CustomerMapFragment();
                fragment = new CustomerParentMainFragment();
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
        //Do something!!!!
        new ApiSessionSuccess().execute();

        /*if(CustomerReservationActivity.GetBlockInfo.getBlockStatus(this)){
            //Go to block page
            Intent intent = new Intent(this, CustomerReservationActivity.class);
            startActivity(intent);
        }*/
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
        } else if (id == R.id.customer_drawer_points) {
            navigate("points");
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

    class ApiSessionSuccess extends AsyncTask<Void, Void, Boolean> {
        HttpClient httpClient = new DefaultHttpClient();
        private String promotion_id, offer, name, address, shop_id, time, session_id;
        private int discount, person;
        private float rating;
        @Override
        protected Boolean doInBackground(Void... voids) {
            //NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_id", getUserId()));
            params.add(new BasicNameValuePair("verbose", "1"));
            HttpGet request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/user_sessions?"+ URLEncodedUtils.format(params, "utf-8"));
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String session_response = handler.handleResponse(response);
                JSONArray session_array = new JSONArray(session_response);
                JSONObject session_object = session_array.getJSONObject(0);
                System.out.println("session = "+session_response);
                if(session_object.get("status_code").equals("0")){
                    int size = Integer.valueOf(session_object.get("size").toString());
                    if(size == 0){
                        return false;
                    }else{
                        JSONObject object = session_array.getJSONObject(1);
                        this.promotion_id = object.getString("promotion_id");
                        this.discount = object.getInt("promotion_name");
                        this.address = object.getString("shop_address");
                        this.name = object.getString("shop_name");
                        this.offer = object.getString("promotion_description");
                        this.shop_id = object.getString("shop_id");
                        this.person = object.getInt("number");
                        this.time = object.getString("due_time");
                        this.session_id = object.getString("session_id");
                        request = new HttpGet("http://"+getString(R.string.server_domain)+"/api/shop_comments?shop_id="+shop_id);
                        request.addHeader("Content-Type", "application/json");
                        JSONArray responseShopRating = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(request)));
                        String status = responseShopRating.getJSONObject(0).getString("status_code");
                        if (status.equals("0"))
                            this.rating = Float.parseFloat(responseShopRating.getJSONObject(0).getString("average_score"))/2;
                        else
                            this.rating = 0;
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(s){
                //block => goto reservation activity
                Intent intent = new Intent(CustomerMainActivity.this, CustomerReservationActivity.class);
                intent.putExtra("promotion_id", this.promotion_id);
                intent.putExtra("discount", this.discount);
                intent.putExtra("offer", this.offer);
                intent.putExtra("persons", this.person);
                intent.putExtra("shop_name", this.name);
                intent.putExtra("rating", Float.toString(this.rating));
                intent.putExtra("shop_location", this.address);
                intent.putExtra("shop_id", this.shop_id);
                intent.putExtra("time", this.time);
                intent.putExtra("session_id", this.session_id);
                intent.putExtra("block", true);
                startActivity(intent);

            } else {

            }
        }
    }

    private String getUserId(){
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        return preferences.getString("userID", "");
    }
}
