package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.SearchRecentSuggestions;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import com.example.yang.flashtable.customer.provider.SearchSuggestionProvider;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by CS on 2017/4/15.
 */

public class CustomerSearchActivity extends AppCompatActivity {
    ArrayList<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    ListView listView;
    LatLng latLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.customer_search_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();
        setTitle(getResources().getString(R.string.customer_search_title));
        listView = (ListView)findViewById(R.id.customer_search_lv);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        ArrayList<DetailInfo> detailInfos = bundle.getParcelableArrayList("list");
        restaurant_list = new ArrayList<>();
        openDB(detailInfos);
        latLng = new LatLng(bundle.getDouble("latitude"), bundle.getDouble("longitude"));
    }

    private void openDB(ArrayList<DetailInfo> detailInfos){
        SqlHandler sqlHandler = new SqlHandler(this);
        for(DetailInfo item:detailInfos) {
            CustomerRestaurantInfo info = sqlHandler.getDetail(item.id);
            info.discount = item.discount;
            info.offer = item.offer;
            info.promotion_id = item.promotion_id;
            info.rating = item.rating;
            restaurant_list.add(info);
        }
        sqlHandler.close();
    }

    private void doSearch(String query) {
        if (adapter == null) {
            adapter = new CustomerMainAdapter(this, new ArrayList(), latLng);
        }else{
            adapter.clear();
        }

        if (restaurant_list != null){
            for (int i = 0; i < restaurant_list.size(); i++){
                System.out.println(restaurant_list.get(i).category);
                if (restaurant_list.get(i).category.contains(query)){
                    adapter.add(restaurant_list.get(i));
                }else if(restaurant_list.get(i).name.contains(query)){
                    adapter.add(restaurant_list.get(i));
                }
            }
            setList();
        }else{
            //Api
        }
    }

    private void setList(){
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showRestaurantDetail(i);
            }
        });
    }

    private void showRestaurantDetail(final int position) {

        final CustomerRestaurantInfo info = adapter.getItem(position);
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
        Intent intent = new Intent(this, CustomerShopActivity.class);
        intent.putExtra("info", showInfo);
        intent.putExtra("shop_id", Integer.toString(info.id));
        startActivity(intent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.View view = getLayoutInflater().inflate(R.layout.actionbar_customized_home, null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView search_view = (SearchView) MenuItemCompat.getActionView(searchItem);
        search_view.setQueryHint(getResources().getString(R.string.customer_search_hint));

        Drawable ic_search = searchItem.getIcon();
        ic_search = DrawableCompat.wrap(ic_search);
        DrawableCompat.setTint(ic_search, ContextCompat.getColor(this, R.color.gray));

        final String[] history = {"bullshit", "shit"};

        //int completeTextId = search_view.getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView completeText = (AutoCompleteTextView) search_view.findViewById(R.id.search_src_text); ;
        completeText.setThreshold(0);
        completeText.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, history));
        completeText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search_view.setQuery(history[position], false);
            }
        });
        search_view.setIconifiedByDefault(true);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                doSearch(query);

                search_view.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        searchItem.expandActionView();
        search_view.requestFocus();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public static class DetailInfo implements Parcelable {
        int id;
        int discount;
        String offer;
        String promotion_id;
        float rating;
        public DetailInfo(int id, int discount, String offer, String promotion_id, float rating){
            this.id = id;
            this.discount = discount;
            this.offer = offer;
            this.promotion_id = promotion_id;
            this.rating = rating;
        }

        protected DetailInfo(Parcel in) {
            id = in.readInt();
            discount = in.readInt();
            offer = in.readString();
            promotion_id = in.readString();
            rating = in.readFloat();
        }

        public static final Creator<DetailInfo> CREATOR = new Creator<DetailInfo>() {
            @Override
            public DetailInfo createFromParcel(Parcel in) {
                return new DetailInfo(in);
            }

            @Override
            public DetailInfo[] newArray(int size) {
                return new DetailInfo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(id);
            parcel.writeInt(discount);
            parcel.writeString(offer);
            parcel.writeString(promotion_id);
            parcel.writeFloat(rating);
        }
    }

}
