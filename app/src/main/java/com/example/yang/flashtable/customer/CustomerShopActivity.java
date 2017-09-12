package com.example.yang.flashtable.customer;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.DialogEventListener;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.slider.BitmapSliderView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class CustomerShopActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private ShowInfo info;
    private String shop_id;
    TextView tv_show_name, tv_show_consumption, tv_show_low, tv_show_offer;
    TextView tv_show_location, tv_show_phone, tv_show_open, tv_show_category, tv_show_website;
    LinearLayout ll_location, ll_phone, ll_website;
    SliderLayout sl_restaurant;
    Button bt_show_reserve;
    RatingBar rb_show_rating;
    ImageButton ib_show_back;
    DialogBuilder dialog_builder;
    LinearLayout ll_comments;
    TextView tv_notice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_shop_activity);

        getInfo();
        initView();
        initData();

        super.onCreate(savedInstanceState);
    }

    private void getInfo(){
        info = (ShowInfo) getIntent().getSerializableExtra("info");
    }

    private void initView() {

        // Views related to show
        sl_restaurant = (SliderLayout) findViewById(R.id.customer_main_sl_restaurant);
        ib_show_back = (ImageButton) findViewById(R.id.customer_main_ib_show_back);
        bt_show_reserve = (Button) findViewById(R.id.customer_main_bt_show_reserve);
        rb_show_rating = (RatingBar) findViewById(R.id.customer_main_rb_show_rating);

        tv_show_name = (TextView) findViewById(R.id.customer_main_tv_show_shop);
        tv_show_offer = (TextView) findViewById(R.id.customer_main_tv_show_gift);
        tv_show_consumption = (TextView) findViewById(R.id.customer_main_tv_show_price);
        tv_show_low = (TextView) findViewById(R.id.customer_main_tv_low);

        tv_show_location = (TextView) findViewById(R.id.customer_main_tv_show_location);
        tv_show_phone = (TextView) findViewById(R.id.customer_main_tv_show_phone);
        tv_show_open = (TextView) findViewById(R.id.customer_main_tv_show_open);
        tv_show_category = (TextView) findViewById(R.id.customer_main_tv_show_category);
        tv_show_website = (TextView) findViewById(R.id.customer_main_tv_show_website);

        ll_comments = (LinearLayout) findViewById(R.id.customer_main_ll_show_comments);

        ll_location = (LinearLayout) findViewById(R.id.customer_shop_ll_location);
        ll_phone = (LinearLayout) findViewById(R.id.customer_shop_ll_phone);
        ll_website = (LinearLayout) findViewById(R.id.customer_shop_ll_website);

        tv_notice = (TextView) findViewById(R.id.customer_shop_tv_notice);
    }

    private void initData(){
        dialog_builder = new DialogBuilder(this);
        ib_show_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shop_id = getIntent().getStringExtra("shop_id");

        ll_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CustomerShopActivity.this, CustomerCommentHistory.class);
                intent.putExtra("type", "shop");
                intent.putExtra("shop_id", shop_id);
                startActivity(intent);
            }
        });

        ll_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CustomerShopActivity.this, "店家地址已複製到剪貼簿", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FlashLocation", tv_show_location.getText());
                clipboard.setPrimaryClip(clip);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
            }
        });
        ll_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CustomerShopActivity.this, "店家聯絡電話已複製到剪貼簿", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FlashPhone", tv_show_phone.getText());
                clipboard.setPrimaryClip(clip);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
            }
        });
        ll_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CustomerShopActivity.this, "店家聯絡信箱已複製到剪貼簿", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("FlashWebsite", tv_show_website.getText());
                clipboard.setPrimaryClip(clip);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(100);
            }
        });

        tv_notice.setText(Html.fromHtml(
                "<font color=\"#808080\">* 本預約需在規定時間</font><font color=\"#E41E1B\">30分鐘</font><font>以內到達該店家領位。<br>" +
                "* 與預約者共同前往消費者，須支付店內最低消費金額，每名：</font><font color=\"#E41E1B\">" + String.valueOf(info.min_consumption)
                        + "元</font><font>。<br>" +
                "* 為保障您的權益，請於到達現場時，立即要求領位，出示行動裝置之預約認證頁面，並在店員面前完成QR-code掃描。<br>" +
                "* 預約客人需全數到齊，若未在規定時間內到齊，該店家有權取消此預約。<br>" +
                "* 本預約僅限內用使用，不提供餐點外帶。<br>" +
                "* 所有折扣優惠依照店家設定的為主，如有任何問題應與店家進行協調。與平台無關。<br>" +
                "* 因交通狀況較難隨時掌握，若您擔心無法在規定時間內趕到，建議您先以電話告知店家以保留以預約之桌位。<br>" +
                "* 餐點菜色依店家實際提供為主，平台照片僅供參考。<br>" +
                "* 若有其他疑問，請聯繫客服。</font>"));


        setDetail();
    }

    private void setDetail(){
        sl_restaurant.removeAllSliders();
        HashMap<String, Bitmap> img_map = new HashMap<>();
        SqlHandler sqlHandler = new SqlHandler(this);
        ArrayList<Bitmap> list = sqlHandler.getBitmapList(Integer.parseInt(shop_id));
        int index = 1;
        for(Bitmap item: list) {
            img_map.put(index+"", item);
            index++;
        }
        for (int i = 0; i < list.size(); i++) {
            BitmapSliderView slider_view = new BitmapSliderView(this);
            slider_view
                    .image(list.get(i))
                    .description(String.valueOf(i))
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
        tv_show_consumption.setText("均消" + info.consumption + "元");

        String low = info.min_consumption + getResources().getString(R.string.dollars);
        tv_show_low.setText(low);

        // Restaurant details
        tv_show_offer.setText(info.offer);
        tv_show_location.setText(info.address);
        tv_show_phone.setText(info.phone);
        String open = "營業時間 " + info.open_hours;
        tv_show_open.setText(open);
        tv_show_category.setText(info.category);
        tv_show_website.setText(info.website);

        rb_show_rating.setRating(info.rating);
        rb_show_rating.setIsIndicator(true);
        bt_show_reserve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_builder.dialogEvent("請選擇人數", "personsPicker",
                        new DialogEventListener() {
                            @Override
                            public void clickEvent(boolean ok, int status) {
                                if (ok) {
                                    Intent intent = new Intent(CustomerShopActivity.this, CustomerReservationActivity.class);
                                    intent.putExtra("promotion_id", info.promotion_id);
                                    intent.putExtra("discount", info.discount);
                                    intent.putExtra("offer", info.offer);
                                    intent.putExtra("persons", status);
                                    intent.putExtra("shop_name", info.name);
                                    intent.putExtra("rating", Float.toString(info.rating));
                                    intent.putExtra("shop_location", info.address);
                                    intent.putExtra("shop_id", shop_id);
                                    intent.putExtra("block", false);
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
        private int min_consumption;
        private int discount;
        private String offer;
        private String address;
        private String phone;
        private String open_hours;
        private String category;
        private String website;
        private String intro;
        private float rating;
        private String promotion_id;
        public ShowInfo(String name, int consumption, int min_consumption, int discount, String offer,
                        String address, String phone, String open_hours, String category, String website,
                        String intro, float rating, String promotion_id){
            this.name = name;
            this.consumption = consumption;
            this.min_consumption = min_consumption;
            this.discount = discount;
            this.offer = offer;
            this.address = address;
            this.phone = phone;
            this.open_hours = open_hours;
            this.category = category;
            this.website = website;
            this.intro = intro;
            this.rating = rating;
            this.promotion_id = promotion_id;
        }
    }
}
