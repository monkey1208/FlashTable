package com.example.yang.flashtable;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yang on 2017/3/23.
 */

public class CustomerMainFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    View view;
    ViewFlipper vf_flipper;
    Spinner spinner_dis, spinner_food, spinner_default;
    ListView lv_shops;
    List<CustomerRestaurantInfo> restaurant_list;
    CustomerMainAdapter adapter;
    EditText et_search;
    SqlHandler sqlHandler = null;

    // Views for show
    ImageButton ib_show_back;
    SliderLayout sl_restaurant;
    Button bt_show_reserve;

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

        // Views related to show
        sl_restaurant = (SliderLayout) view.findViewById(R.id.customer_main_sl_restaurant);
        ib_show_back = (ImageButton) view.findViewById(R.id.customer_main_ib_show_back);
        bt_show_reserve = (Button) view.findViewById(R.id.customer_main_bt_show_reserve);
    }

    private void initData(){
        openDB();
        restaurant_list = getListFromDB();
        adapter = new CustomerMainAdapter(view.getContext(), restaurant_list);
        lv_shops.setAdapter(adapter);
        lv_shops.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                showRestaurantDetail(i);
            }
        });
        et_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int key_code, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && key_code == KeyEvent.KEYCODE_ENTER) {
                    Toast.makeText(getActivity(), "Search " + et_search.getText(), Toast.LENGTH_SHORT).show();
                    et_search.setText("");
                }
                return true;
            }
        });

        // TODO: Override back button so that it will return to main if at show

        // Data in show view
        ib_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeRestaurantDetail();
            }
        });
        bt_show_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerReservationActivity.class);
                startActivity(intent);
            }
        });
    }

    // DB related functions
    private void openDB(){
        sqlHandler = new SqlHandler(view.getContext());
    }

    private List<CustomerRestaurantInfo> getListFromDB(){
        return sqlHandler.getList();
    }

    private void closeDB(){
        sqlHandler.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private List<CustomerRestaurantInfo> generateTestList(){
        List<CustomerRestaurantInfo> list = new ArrayList<>();
        for(int i = 1; i < 6; i++){
            LatLng latLng = new LatLng(121.5, 25.01);
            CustomerRestaurantInfo info = new CustomerRestaurantInfo("Restaurant "+Integer.toString(i), i, 100, "tag", latLng);
            info.detailInfo.setInfo("address", "garbage store");
            list.add(info);
            latLng = null;
            info = null;
        }
        return list;
    }

    private void showRestaurantDetail(int position) {
        // TODO: Fix issues - sometimes selected ListView flickers on setDisplayedChild()
        sl_restaurant.removeAllSliders();

        CustomerRestaurantInfo info = restaurant_list.get(position);
        vf_flipper.setDisplayedChild(1);

        HashMap<String,Integer> image_map = new HashMap<>();
        image_map.put("1", R.drawable.slide_1);
        image_map.put("2", R.drawable.slide_2);
        image_map.put("3", R.drawable.slide_3);

        for(String name : image_map.keySet()) {
            // Change DefaultSliderView to TextSliderView if you want text below it
            DefaultSliderView slider_view = new DefaultSliderView(getActivity().getBaseContext());
            slider_view
                    .description(name)
                    .image(image_map.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
            sl_restaurant.addSlider(slider_view);
        }
        sl_restaurant.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sl_restaurant.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sl_restaurant.setDuration(4000);
        sl_restaurant.addOnPageChangeListener(this);
        sl_restaurant.startAutoCycle();
    }

    private void closeRestaurantDetail() {
        vf_flipper.setDisplayedChild(0);
        sl_restaurant.stopAutoCycle();
    }

    @Override
    public void onStop() {
        sl_restaurant.stopAutoCycle();
        super.onStop();
    }
    @Override
    public void onSliderClick(BaseSliderView slider) {
        sl_restaurant.moveNextPosition();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {}
    @Override
    public void onPageScrollStateChanged(int state) {}

    private class ApiPromotion extends AsyncTask<Float, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();
        @Override
        protected String doInBackground(Float... params) {
            String latlng = params[0].toString()+","+params[1].toString();

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
