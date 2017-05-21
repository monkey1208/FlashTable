package com.example.yang.flashtable.customer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.yang.flashtable.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CS on 2017/5/21.
 */

public class CustomerFlashPointFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    View view;
    LayoutInflater inflater;
    SharedPreferences user;
    String userID, username;

    ListView lv_coupons;
    SliderLayout sl_coupons;

    String[] listValue = new String[]
            {
                    "ONE",
                    "TWO",
                    "THREE",
                    "FOUR",
                    "FIVE",
                    "SIX"
            };

    List<String> LISTSTRING;

    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = _inflater;
        view = inflater.inflate(R.layout.customer_flash_point_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        lv_coupons = (ListView) view.findViewById(R.id.customer_points_lv_coupons);
        ViewGroup header = (ViewGroup) inflater.inflate(
                R.layout.customer_flash_point_header, lv_coupons, false);

        // TODO: Change list to promotion content
        LISTSTRING = new ArrayList<>(Arrays.asList(listValue));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, LISTSTRING);

        lv_coupons.addHeaderView(header);
        lv_coupons.setAdapter(adapter);

        sl_coupons = (SliderLayout) view.findViewById(R.id.customer_points_sl_coupons);
    }

    private void initData() {
        getUserInfo();
        setSlider();
    }

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }
    
    private void setSlider() {
        sl_coupons.removeAllSliders();
        HashMap<String, Integer> image_map = new HashMap<>();
        image_map.put("1", R.drawable.slide_1);
        image_map.put("2", R.drawable.slide_2);
        image_map.put("3", R.drawable.slide_3);

        for (String name : image_map.keySet()) {
            // Change DefaultSliderView to TextSliderView if you want text below it
            DefaultSliderView slider_view = new DefaultSliderView(getActivity().getBaseContext());
            slider_view
                    .description(name)
                    .image(image_map.get(name))
                    .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                    .setOnSliderClickListener(this);
            sl_coupons.addSlider(slider_view);
        }
        sl_coupons.setPresetTransformer(SliderLayout.Transformer.DepthPage);
        sl_coupons.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sl_coupons.setDuration(4000);
        sl_coupons.addOnPageChangeListener(this);
        sl_coupons.startAutoCycle();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        sl_coupons.moveNextPosition();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

    @Override
    public void onPageSelected(int position) { }

    @Override
    public void onPageScrollStateChanged(int state) { }

    @Override
    public void onStop() {
        sl_coupons.stopAutoCycle();
        super.onStop();
    }

}
