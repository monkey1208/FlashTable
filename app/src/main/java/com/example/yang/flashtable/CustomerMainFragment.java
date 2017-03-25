package com.example.yang.flashtable;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment {

    View view;
    ViewFlipper vf_flipper;
    Spinner spinner_dis, spinner_food, spinner_default;
    ListView lv_shops;
    List<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    EditText et_search;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_main_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView(){
        vf_flipper = (ViewFlipper) view.findViewById(R.id.customer_main_vf_flipper);
        spinner_dis = (Spinner) view.findViewById(R.id.customer_main_sp_distance);
        spinner_food = (Spinner) view.findViewById(R.id.customer_main_sp_food);
        spinner_default = (Spinner) view.findViewById(R.id.customer_main_sp_default);
        lv_shops = (ListView) view.findViewById(R.id.customer_main_lv);
        et_search = (EditText) view.findViewById(R.id.customer_main_et_search);
    }

    private void initData(){
        restaurant_list = generateTestList();
        adapter = new CustomerMainAdapter(view.getContext(), restaurant_list);
        lv_shops.setAdapter(adapter);
        et_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int key_code, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && key_code == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(getActivity(), "Search " + et_search.getText(), Toast.LENGTH_SHORT).show();
                    et_search.setText("");
                }
                return true;
            }
        });
    }

    private List<CustomerRestaurantInfo> generateTestList(){
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            LatLng latLng = new LatLng(25, 25);
            CustomerRestaurantInfo info = new CustomerRestaurantInfo("a", "a", "b", latLng);
            list.add(info);
        }
        return list;
    }
}
