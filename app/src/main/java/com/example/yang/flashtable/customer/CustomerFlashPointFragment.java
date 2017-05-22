package com.example.yang.flashtable.customer;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.yang.flashtable.CustomerFlashPointAdapter;
import com.example.yang.flashtable.FlashCouponInfo;
import com.example.yang.flashtable.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import jp.co.recruit_mp.android.headerfootergridview.HeaderFooterGridView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by CS on 2017/5/21.
 */

public class CustomerFlashPointFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    View view;
    LayoutInflater inflater;
    SharedPreferences user;
    String userID, username;

    HeaderFooterGridView lv_coupons;
    SliderLayout sl_coupons;

    CustomerFlashPointAdapter adapter;
    List<FlashCouponInfo> coupons;

    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = _inflater;
        view = inflater.inflate(R.layout.customer_flash_point_fragment, container, false);
        initView();
        initData();
        new ApiPoints().execute(userID);
        new ApiCoupons().execute();
        return view;
    }

    private void initView() {
        lv_coupons = (HeaderFooterGridView) view.findViewById(R.id.customer_points_lv_coupons);
        View header = inflater.inflate(
                R.layout.customer_flash_point_header, lv_coupons, false);
        lv_coupons.addHeaderView(header);

        sl_coupons = (SliderLayout) header.findViewById(R.id.customer_points_sl_coupons);
    }

    private void initData() {

        // TODO: Change list to promotion content
        coupons = new ArrayList<>();
        coupons.add(new FlashCouponInfo("1", "WHADDUP", 10, "I ain't telling you shit"));
        coupons.add(new FlashCouponInfo("1", "WHADDUP", 10, "I ain't telling you shit"));
        coupons.add(new FlashCouponInfo("1", "WHADDUP", 10, "I ain't telling you shit"));
        adapter = new CustomerFlashPointAdapter(getActivity().getBaseContext(), coupons);
        lv_coupons.setAdapter(adapter);

        getUserInfo();
        setSlider();
    }

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }
    
    private void setSlider() {
        if (sl_coupons != null)
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

    private class ApiPoints extends AsyncTask<String, Void, Integer>{

        @Override
        protected Integer doInBackground(String ...value) {
            NameValuePair param = new BasicNameValuePair("user_id", value[0]);
            HttpGet httpGet = new HttpGet("http://" + getString(R.string.server_domain) + "/api/user_info?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            int points = 0;
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);
                System.out.println("user info:"+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.get("status_code").equals("0")){
                    points = jsonArray.getJSONObject(1).getInt("flash_point");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return points;
        }

        @Override
        protected void onPostExecute(Integer points) {
            //get points
        }
    }

    private class ApiCoupons extends AsyncTask<Void, Void, ArrayList<FlashCouponInfo>>{

        @Override
        protected ArrayList<FlashCouponInfo> doInBackground(Void ...value) {
            NameValuePair param = new BasicNameValuePair("verbose", "1");
            HttpGet httpGet = new HttpGet("http://" + getString(R.string.server_domain) + "/api/flash_coupons?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            ArrayList<FlashCouponInfo> infos = new ArrayList<>();
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);
                System.out.println("coupons : "+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                FlashCouponInfo info = new FlashCouponInfo(null, null, 0, null);
                for(int i = 1; i <= jsonObject.getInt("size"); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    info.coupon_id = jsonObject1.getInt("coupon_id");
                    info.description = jsonObject1.getString("description");
                    info.flash_point = jsonObject1.getInt("flash_point");
                    info.tutorial = jsonObject1.getString("toturial");
                    info.name = jsonObject1.getString("name");
                    info.picture_url_large = jsonObject1.getString("picture_url_large");
                    info.picture_url_small = jsonObject1.getString("picture_url_small");
                    infos.add(info);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return infos;
        }

        @Override
        protected void onPostExecute(ArrayList<FlashCouponInfo> infos) {
            //get coupon list

        }
    }

}
