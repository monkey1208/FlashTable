package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerReservationActivity extends AppCompatActivity {

    ViewFlipper vf_flipper;
    GifImageView gv_time;
    GifDrawable gif_drawable;
    TextView tv_status, tv_time, tv_arrival_time, tv_shop, tv_discount, tv_gift;
    String seconds, no_response, late;
    RatingBar rb_shop;
    LinearLayout ll_time_left;
    Button bt_cancel, bt_arrive_cancel;
    View.OnClickListener cancel_listener;
    ImageView iv_qr_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_reservation_activity);

        initView();
        initData();
    }

    private void initView() {
        vf_flipper = (ViewFlipper) findViewById(R.id.customer_reservation_vf_flipper);

        // Waiting view
        gv_time = (GifImageView) findViewById(R.id.customer_reservation_gv_time);
        gif_drawable = (GifDrawable) gv_time.getDrawable();
        tv_status = (TextView) findViewById(R.id.customer_reservation_tv_status);
        tv_time = (TextView) findViewById(R.id.customer_reservation_tv_time);
        seconds = getResources().getString(R.string.customer_reservation_seconds);
        no_response = getResources().getString(R.string.customer_reservation_no_response);
        bt_cancel = (Button) findViewById(R.id.customer_reservation_bt_cancel);

        // Success view
        tv_arrival_time = (TextView) findViewById(R.id.customer_reservation_tv_arrival_time);
        tv_shop = (TextView) findViewById(R.id.customer_reservation_tv_shop);
        rb_shop = (RatingBar) findViewById(R.id.customer_reservation_rb_rating);
        tv_discount = (TextView) findViewById(R.id.customer_reservation_tv_discount);
        tv_gift = (TextView) findViewById(R.id.customer_reservation_tv_gift);
        late = getResources().getString(R.string.customer_reservation_late);
        ll_time_left = (LinearLayout) findViewById(R.id.customer_reservation_ll_time_left);
        bt_arrive_cancel = (Button) findViewById(R.id.customer_reservation_bt_arrive_cancel);
        iv_qr_code = (ImageView) findViewById(R.id.customer_reservation_iv_qr_code);
    }

    private void initData() {
        gif_drawable.setSpeed(2.0f);
        startCountDown("waiting", 10000);

        cancel_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        };
        bt_cancel.setOnClickListener(cancel_listener);
        bt_arrive_cancel.setOnClickListener(cancel_listener);
    }

    private void reservationAccepted() {
        vf_flipper.setDisplayedChild(1);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        startCountDown("success", 10000);

        try {
            Bitmap bitmap = encodeAsBitmap("elisaroo");
            iv_qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void startCountDown(String state, int countdown_millis) {
        if (state.equals("waiting")) {
            new CountDownTimer(countdown_millis, 1000) {
                String time_left;
                public void onTick(long millis_left) {
                    time_left = (millis_left / 1000) + seconds;
                    tv_time.setText(time_left);
                }
                public void onFinish() {
                    time_left = 0 + seconds;
                    tv_time.setText(time_left);
                    tv_status.setText(no_response);
                    reservationAccepted();
                }
            }.start();
        } else if (state.equals("success")) {
            new CountDownTimer(countdown_millis, 1000) {
                String time_left;
                public void onTick(long millis_left) {
                    time_left =
                            String.format(Locale.CHINESE, "%02d:%02d",
                                    TimeUnit.MILLISECONDS.toMinutes(millis_left) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis_left)),
                                    TimeUnit.MILLISECONDS.toSeconds(millis_left) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis_left)));
                    tv_arrival_time.setText(time_left);
                }
                public void onFinish() {
                    ll_time_left.removeAllViews();
                    ll_time_left.addView(tv_arrival_time);
                    tv_arrival_time.setText(late);
                    tv_arrival_time.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                    tv_arrival_time.setTextColor(getResources().getColor(R.color.textColorRed));
                }
            }.start();
        }
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            final int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
