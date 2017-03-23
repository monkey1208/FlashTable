package com.example.yang.flashtable;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment {
    private View view;
    private Spinner spinner_dis, spinner_food, spinner_default;
    private SearchView searchView;
    private ListView listView;
    private List<RestaurantInfo> restaurantList;
    private CustomerMainAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initId();
        setList();
        return view;
    }
    private void initId(){
        spinner_dis = (Spinner)view.findViewById(R.id.customer_main_sp_distance);
        spinner_food = (Spinner)view.findViewById(R.id.customer_main_sp_food);
        spinner_default = (Spinner)view.findViewById(R.id.customer_main_sp_default);
        listView = (ListView)view.findViewById(R.id.customer_main_lv);
    }
    private void setList(){
        restaurantList = generateTestList();
        adapter = new CustomerMainAdapter(view.getContext(), restaurantList);
        listView.setAdapter(adapter);
    }
    private List<RestaurantInfo> generateTestList(){
        List<RestaurantInfo> list = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            RestaurantInfo info = new RestaurantInfo("a", "a","b","a","a","a");
            list.add(info);
        }
        return list;
    }
}
