package com.example.yang.flashtable;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    TextView tv_points, tv_records;

    CustomerFlashPointAdapter adapter;
    List<FlashCouponInfo> coupons;

    @Override
    public View onCreateView(LayoutInflater _inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = _inflater;
        view = inflater.inflate(R.layout.customer_flash_point_fragment, container, false);
        initView();
        initData();
        return view;
    }

    private void initView() {
        lv_coupons = (HeaderFooterGridView) view.findViewById(R.id.customer_points_lv_coupons);
        View header = inflater.inflate(
                R.layout.customer_flash_point_header, lv_coupons, false);
        lv_coupons.addHeaderView(header);

        // Header views
        sl_coupons = (SliderLayout) header.findViewById(R.id.customer_points_sl_coupons);
        tv_points = (TextView) header.findViewById(R.id.customer_points_tv_points);
        tv_records = (TextView) header.findViewById(R.id.customer_points_tv_records);
    }

    private void initData() {
        getUserInfo();

        coupons = new ArrayList<>();
        adapter = new CustomerFlashPointAdapter(getActivity().getBaseContext(), coupons);
        lv_coupons.setAdapter(adapter);
        lv_coupons.setOnItemClickListener(new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapter_view, View view, int i, long l) {
                FlashCouponInfo info = coupons.get(i - lv_coupons.getNumColumns());
                Intent intent = new Intent(getActivity(), CustomerCouponActivity.class);
                Bundle bundle = new Bundle();

                SerializableCouponInfo _info = new SerializableCouponInfo();
                _info.name = info.name;
                _info.description = info.description;
                _info.coupon_id = info.coupon_id;
                _info.flash_point = info.flash_point;
                _info.picture_url_small = info.picture_url_small;
                _info.picture_url_large = info.picture_url_large;
                _info.tutorial = info.tutorial;
                _info.description = info.description;

                bundle.putSerializable("info", _info);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        new ApiPoints().execute(userID);
        new ApiCoupons().execute();

        tv_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CustomerCouponRecordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUserInfo() {
        user = this.getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
        username = user.getString("username", "");
    }
    
    private void setSlider() {
        if (sl_coupons != null)
            sl_coupons.removeAllSliders();
        HashMap<String, String> image_map = new HashMap<>();
        for (int i = 0; i < coupons.size(); i++)
            image_map.put(String.valueOf(i), coupons.get(i).picture_url_large);

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

    private void updateCoupons() {
        adapter.notifyDataSetChanged();
        lv_coupons.setAdapter(adapter);
    }

    private class ApiPoints extends AsyncTask<String, Void, Void>{
        // private ProgressDialog progress_dialog = new ProgressDialog(CustomerFlashPointFragment.this.getActivity());
        private String status;
        private int points = 0;

//        @Override
//        protected void onPreExecute() {
//            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
//            progress_dialog.show();
//        }

        @Override
        protected Void doInBackground(String ...value) {
            NameValuePair param = new BasicNameValuePair("user_id", value[0]);
            HttpGet httpGet = new HttpGet("http://" + getString(R.string.server_domain) + "/api/user_info?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);

                JSONObject responseJSON = new JSONObject(json);
                status = responseJSON.getString("status_code");
                if (status.equals("0")) {
                    points = Integer.parseInt(responseJSON.getString("flash_point"));
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
//            progress_dialog.dismiss();
            Log.e("UserInfo", status);

            //get points
            tv_points.setText(Integer.toString(points));
        }
    }

    private class ApiCoupons extends AsyncTask<Void, Void, Void>{

//        private ProgressDialog progress_dialog = new ProgressDialog(CustomerFlashPointFragment.this.getActivity());
//
//        @Override
//        protected void onPreExecute() {
//            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
//            progress_dialog.show();
//        }

        @Override
        protected Void doInBackground(Void ...value) {
            NameValuePair param = new BasicNameValuePair("verbose", "1");
            HttpGet httpGet = new HttpGet("http://" + getString(R.string.server_domain) + "/api/flash_coupons?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String json = handler.handleResponse(httpResponse);
                System.out.println("coupons : "+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                for(int i = 1; i <= jsonObject.getInt("size"); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                    FlashCouponInfo info = new FlashCouponInfo();
                    info.coupon_id = jsonObject1.getInt("coupon_id");
                    info.description = jsonObject1.getString("description");
                    info.flash_point = jsonObject1.getInt("flash_point");
                    info.tutorial = jsonObject1.getString("tutorial");
                    info.name = jsonObject1.getString("name");
                    info.picture_url_large = jsonObject1.getString("picture_url_large");
                    info.picture_url_small = jsonObject1.getString("picture_url_small");

                    URL url = new URL(info.picture_url_small);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    info.picture_small = BitmapFactory.decodeStream(input);

                    coupons.add(info);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void params) {
//            progress_dialog.dismiss();

            //get coupon list
            updateCoupons();
            setSlider();
        }
    }

}
