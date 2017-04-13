package com.example.yang.flashtable;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.io.Serializable;
import java.util.HashMap;

public class CustomerMainShopActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private ShowInfo info;
    private String shop_id;
    TextView tv_show_name, tv_show_consumption, tv_show_discount, tv_show_offer, tv_show_location, tv_show_category, tv_show_intro;
    SliderLayout sl_restaurant;
    Button bt_show_reserve;
    RatingBar rb_show_rating;
    ImageButton ib_show_back;
    DialogBuilder dialog_builder;
    LinearLayout ll_comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.customer_main_show);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.login_activity);

        getInfo();
        initView();
        initData();

        super.onCreate(savedInstanceState);
    }

    private void getInfo(){
        info = (ShowInfo) getIntent().getSerializableExtra("info");
        shop_id = getIntent().getStringExtra("shop_id");
        Toast.makeText(getBaseContext(), shop_id, Toast.LENGTH_LONG).show();
    }

    private void initView() {

        // Views related to show
        sl_restaurant = (SliderLayout) findViewById(R.id.customer_main_sl_restaurant);
        ib_show_back = (ImageButton) findViewById(R.id.customer_main_ib_show_back);
        bt_show_reserve = (Button) findViewById(R.id.customer_main_bt_show_reserve);
        rb_show_rating = (RatingBar) findViewById(R.id.customer_main_rb_show_rating);

        tv_show_name = (TextView) findViewById(R.id.customer_main_tv_show_shop);
        tv_show_discount = (TextView) findViewById(R.id.customer_main_tv_show_discount);
        tv_show_offer = (TextView) findViewById(R.id.customer_main_tv_show_gift);
        tv_show_consumption = (TextView) findViewById(R.id.customer_main_tv_show_price);
        tv_show_location = (TextView) findViewById(R.id.customer_main_tv_show_location);
        tv_show_category = (TextView) findViewById(R.id.customer_main_tv_show_category);
        tv_show_intro = (TextView) findViewById(R.id.customer_main_tv_show_description);

        ll_comments = (LinearLayout) findViewById(R.id.customer_main_ll_show_comments);

        setDetail();
    }

    private void initData(){
        dialog_builder = new DialogBuilder(this);
        ib_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerMainShopActivity.this, CustomerCommentActivity.class);
                intent.putExtra("type", "shop");
                intent.putExtra("shop_id", shop_id);
                startActivity(intent);
            }
        });
    }

    private void setDetail(){
        sl_restaurant.removeAllSliders();
        HashMap<String, Integer> image_map = new HashMap<>();
        image_map.put("1", R.drawable.slide_1);
        image_map.put("2", R.drawable.slide_2);
        image_map.put("3", R.drawable.slide_3);

        for (String name : image_map.keySet()) {
            // Change DefaultSliderView to TextSliderView if you want text below it
            DefaultSliderView slider_view = new DefaultSliderView(getBaseContext());
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

        tv_show_name.setText(info.name);
        tv_show_consumption.setText("均消$" + info.consumption);
        if (info.discount == 101 || info.discount == 100) {
            tv_show_discount.setText("暫無折扣");
        } else {
            int point = info.discount % 10;
            int discount = info.discount / 10;
            if (point != 0) {
                tv_show_discount.setText(info.discount + "折");
            } else {
                tv_show_discount.setText(discount + "折");
            }
        }
        tv_show_offer.setText(info.offer);
        tv_show_location.setText(info.address);
        tv_show_category.setText(info.category);
        tv_show_intro.setText(info.intro);
        rb_show_rating.setRating(info.rating);
        bt_show_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_builder.dialogEvent("請選擇人數", "personsPicker",
                        new DialogEventListener() {
                            @Override
                            public void clickEvent(boolean ok, int status) {
                                if (ok) {
                                    Intent intent = new Intent(CustomerMainShopActivity.this, CustomerReservationActivity.class);
                                    intent.putExtra("promotion_id", info.promotion_id);
                                    intent.putExtra("discount", info.discount);
                                    intent.putExtra("offer", info.offer);
                                    intent.putExtra("persons", status);
                                    startActivity(intent);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        sl_restaurant.moveNextPosition();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStop() {
        sl_restaurant.stopAutoCycle();
        super.onStop();
    }

    public static class ShowInfo implements Serializable{
        private String name;
        private int consumption;
        private int discount;
        private String offer;
        private String address;
        private String category;
        private String intro;
        private float rating;
        private String promotion_id;
        public ShowInfo(String name, int consumption, int discount, String offer, String address, String category, String intro, float rating, String promotion_id){
            this.name = name;
            this.consumption = consumption;
            this.discount = discount;
            this.offer = offer;
            this.address = address;
            this.category = category;
            this.intro = intro;
            this.rating = rating;
            this.promotion_id = promotion_id;
        }
    }
}
