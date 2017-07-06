package com.example.yang.flashtable.customer;

import android.annotation.TargetApi;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.adapter.CustomerMainAdapter;
import com.example.yang.flashtable.customer.database.HistorySqlHandler;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerAppInfo;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;
import com.example.yang.flashtable.customer.provider.SearchSuggestionProvider;

import java.util.ArrayList;

/**
 * Created by CS on 2017/4/15.
 */

public class CustomerSearchActivity extends AppCompatActivity {
    ArrayList<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    TextView textView;
    ListView listView;
    EditText editText;
    Location my_location;
    SearchView search_view;
    ArrayAdapter<String> history_adapter;
    View header_history, header_result;

    HistorySqlHandler sqlHandler;

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        super.onNewIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            suggestions.saveRecentQuery(query, null);
            doSearch(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())){
            System.out.println(intent.getData());
            System.out.println(intent.getData().getLastPathSegment());
            search_view.setQuery(intent.getDataString(), false);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_search_activity);

        initView();
        initData();
    }

    private void initView() {
        //setupActionBar();
        //setTitle(getResources().getString(R.string.customer_search_title));
        listView = (ListView)findViewById(R.id.customer_search_lv);
        textView = (TextView)findViewById(R.id.customer_search_tv);
        editText = (EditText)findViewById(R.id.customer_search_et);

        LayoutInflater inflater = getLayoutInflater();
        header_history = inflater.inflate(R.layout.customer_search_history_header, listView, false);
        header_result = inflater.inflate(R.layout.customer_search_result_header, listView, false);

    }

    private void initData() {
        sqlHandler = new HistorySqlHandler(this);
        Bundle bundle = getIntent().getExtras();
        ArrayList<DetailInfo> detailInfos = bundle.getParcelableArrayList("list");

        restaurant_list = new ArrayList<>();
        openDB(detailInfos);
        setHistory();
        my_location = CustomerAppInfo.getInstance().getLocation();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String query = v.getText().toString();
                if(query != null && !query.equals("")) {
                    sqlHandler.insert(query);
                    doSearch(query);
                    v.setText("");
                }

                return false;
            }
        });
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
            adapter = new CustomerMainAdapter(this, new ArrayList(), my_location);
        }else{
            adapter.clear();
        }

        if (restaurant_list != null){
            for (CustomerRestaurantInfo item:restaurant_list){
                System.out.println(item.category);
                if (item.category.contains(query)){
                    adapter.add(item);
                }else if(item.name.contains(query)){
                    adapter.add(item);
                }
            }
            if(adapter.isEmpty()){
                listView.setVisibility(View.INVISIBLE);
                textView.setVisibility(View.VISIBLE);
            }else {
                listView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.INVISIBLE);
                setList();
            }
        }else{
            //Api
            listView.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private void setList(){

        if(listView.getHeaderViewsCount() > 0) {
            listView.removeHeaderView(header_history);
            listView.removeHeaderView(header_result);
        }
        listView.addHeaderView(header_result, null, false);
        TextView search_num = (TextView)findViewById(R.id.customer_search_result_num);
        search_num.setText(adapter.getCount()+"");
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showRestaurantDetail(i-1);
            }
        });
        //listView.addHeaderView(header_history, null, false);

    }

    private void setHistory(){
        final ArrayList<String> history = sqlHandler.getHistoryList();

        history_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, history);
        listView.addHeaderView(header_history, null, false);
        listView.setAdapter(history_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(history.get(position-1));
                //search_view.setQuery(history.get(position-1), false);
            }
        });
    }

    private void showRestaurantDetail(final int position) {

        final CustomerRestaurantInfo info = adapter.getItem(position);
        CustomerShopActivity.ShowInfo showInfo = new CustomerShopActivity.ShowInfo(
                info.name,
                info.consumption,
                info.minconsumption,
                info.discount,
                info.offer,
                info.address,
                info.phone,
                info.date,
                info.category,
                info.web,
                info.intro,
                info.rating,
                info.promotion_id);
        Intent intent = new Intent(this, CustomerShopActivity.class);
        intent.putExtra("info", showInfo);
        intent.putExtra("shop_id", Integer.toString(info.id));
        startActivity(intent);
    }

    public void finish(View view){
        this.finish();
    }
/*
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

        sqlHandler = new HistorySqlHandler(this);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        search_view = (SearchView) MenuItemCompat.getActionView(searchItem);
        search_view.setQueryHint(getResources().getString(R.string.customer_search_hint));
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search_view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search_view.setIconifiedByDefault(false);


        Drawable ic_search = searchItem.getIcon();
        ic_search = DrawableCompat.wrap(ic_search);
        DrawableCompat.setTint(ic_search, ContextCompat.getColor(this, R.color.gray));

        search_view.setIconifiedByDefault(true);
        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                doSearch(query);
                sqlHandler.insert(query);
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

        setHistory();

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    */

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
