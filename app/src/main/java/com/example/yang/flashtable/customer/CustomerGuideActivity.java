package com.example.yang.flashtable.customer;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.yang.flashtable.R;

/**
 * Created by CS on 2017/6/6.
 */

public class CustomerGuideActivity extends AppCompatActivity /* implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener */ {
//    SliderLayout sl_guide;
    ViewFlipper sl_guide;
//    HashMap<String, Integer> image_map;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private ViewFlipper mViewFlipper;
    private Context mContext;
    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    ImageView iv_00, iv_01, iv_02, iv_03, iv_04;
    View v_next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_guide_activity);

        mContext = this;
        mViewFlipper = (ViewFlipper) this.findViewById(R.id.customer_guide_sl_guide);
        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                return true;
            }
        });

        iv_00 = (ImageView) findViewById(R.id.customer_guide_iv_00);
        iv_01 = (ImageView) findViewById(R.id.customer_guide_iv_01);
        iv_02 = (ImageView) findViewById(R.id.customer_guide_iv_02);
        iv_03 = (ImageView) findViewById(R.id.customer_guide_iv_03);
        iv_04 = (ImageView) findViewById(R.id.customer_guide_iv_04);

        iv_00.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.guide_00, 450, 800));
        iv_01.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.guide_01, 450, 800));
        iv_02.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.guide_02, 450, 800));
        iv_03.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.guide_03, 450, 800));
        iv_04.setImageBitmap(decodeSampledBitmapFromResource(getResources(), R.drawable.guide_04, 450, 800));

        v_next = findViewById(R.id.customer_guide_v_next);
        v_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CustomerGuideActivity.this, "歡迎使用Flash Table!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(CustomerGuideActivity.this, CustomerMainActivity.class);
                CustomerGuideActivity.this.startActivity(intent);
                finish();
            }
        });
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_from_left));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_to_left));
                    mViewFlipper.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fab_slide_in_from_right));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.fab_slide_out_to_right));
                    mViewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
}
