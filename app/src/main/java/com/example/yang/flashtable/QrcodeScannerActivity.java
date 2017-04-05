package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.zxing.Result;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.example.yang.flashtable.R.layout.store_qrcode_scan;

public class QrcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final String LOG_TAG = QrcodeScannerActivity.class.getSimpleName();

    static final String SCAN_RESULT = "scan_result";
    static final String SCAN_FORMAT = "scan_format";

    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(store_qrcode_scan);

        requestCameraPermission();

        mScannerView = new ZXingScannerView(this) {
            @Override
            protected IViewFinder createViewFinderView(Context context) {
                return new CustomViewFinderView(context);
            }
        };
        mScannerView.setAutoFocus(true);

        LinearLayout scan_fragment = (LinearLayout) findViewById(R.id.scanner_fragment);
        scan_fragment.addView(mScannerView);

        LinearLayout top_bar = (LinearLayout)findViewById(R.id.store_qrcode_scan_ll_top_bar);
        top_bar.setPadding(0, getStatusBarHeight(), 0, 0);

        ImageView iv = (ImageView)findViewById(R.id.store_qrcode_scan_iv_back) ;
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.store_qrcode_scan_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_flash:
                if (mScannerView != null) {
                    boolean isFlashOn = mScannerView.getFlash();
                    if (isFlashOn) {
                        mScannerView.setFlash(false);
                        item.setIcon(R.drawable.ic_flash_on_white_24dp);
                    } else {
                        mScannerView.setFlash(true);
                        item.setIcon(R.drawable.ic_flash_off_white_24dp);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleResult(Result result) {
        Log.v(LOG_TAG, result.getText() + ", " + result.getBarcodeFormat().toString());
        String session = result.toString();
        String session_id = session.substring(session.indexOf("=")+1);

        AlertDialogController.warningConfirmDialog(getApplicationContext(), "提醒", "恭喜掃描成功");
        //mScannerView.resumeCameraPreview(this);
        //finish_session(session_id);
       Intent returnIntent = new Intent();
        returnIntent.putExtra(SCAN_RESULT, result.toString());
        returnIntent.putExtra(SCAN_FORMAT, result.getBarcodeFormat().toString());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }


    public static class CustomViewFinderView extends View implements IViewFinder {
        private static final String TAG = "ViewFinderView";

        private Rect mFramingRect;

        private static final float PORTRAIT_WIDTH_RATIO = 6f/8;
        private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;

        private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
        private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
        private static final int MIN_DIMENSION_DIFF = 50;

        private static final float SQUARE_DIMENSION_RATIO = 6f/8;

        private static final int[] SCANNER_ALPHA = {0, 64, 128, 192, 255, 192, 128, 64};
        private int scannerAlpha;
        private static final int POINT_SIZE = 10;
        private static final long ANIMATION_DELAY = 80l;

        private final int mDefaultLaserColor = getResources().getColor(R.color.viewfinder_laser);
        private final int mDefaultMaskColor = getResources().getColor(R.color.viewfinder_mask);
        private final int mDefaultBorderColor = getResources().getColor(R.color.viewfinder_border);
        private final int mDefaultBorderStrokeWidth = getResources().getInteger(R.integer.viewfinder_border_width);
        private final int mDefaultBorderLineLength = getResources().getInteger(R.integer.viewfinder_border_length);

        protected Paint mLaserPaint;
        protected Paint mFinderMaskPaint;
        protected Paint mBorderPaint;
        protected int mBorderLineLength;
        protected boolean mSquareViewFinder = true;

        public CustomViewFinderView(Context context) {
            super(context);
            init();
        }

        public CustomViewFinderView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        private void init() {
            //set up laser paint
            mLaserPaint = new Paint();
            mLaserPaint.setColor(mDefaultLaserColor);
            mLaserPaint.setStyle(Paint.Style.FILL);

            //finder mask paint
            mFinderMaskPaint = new Paint();
            mFinderMaskPaint.setColor(mDefaultMaskColor);

            //border paint
            mBorderPaint = new Paint();
            mBorderPaint.setColor(mDefaultBorderColor);
            mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setStrokeWidth(mDefaultBorderStrokeWidth);

            mBorderLineLength = mDefaultBorderLineLength;
        }

        public void setupViewFinder() {
            updateFramingRect();
            invalidate();
        }

        public Rect getFramingRect() {
            return mFramingRect;
        }

        @Override
        public void onDraw(Canvas canvas) {
            if(getFramingRect() == null) {
                return;
            }

            drawViewFinderMask(canvas);
            drawViewFinderBorder(canvas);
            //drawLaser(canvas);
        }

        public void drawViewFinderMask(Canvas canvas) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            Rect framingRect = getFramingRect();

            canvas.drawRect(0, 0, width, framingRect.top, mFinderMaskPaint);
            canvas.drawRect(0, framingRect.top, framingRect.left, framingRect.bottom + 1, mFinderMaskPaint);
            canvas.drawRect(framingRect.right + 1, framingRect.top, width, framingRect.bottom + 1, mFinderMaskPaint);
            canvas.drawRect(0, framingRect.bottom + 1, width, height, mFinderMaskPaint);
        }

        public void drawViewFinderBorder(Canvas canvas) {
            Rect framingRect = getFramingRect();

            canvas.drawLine(framingRect.left - 1, framingRect.top - 1, framingRect.left - 1, framingRect.top - 1 + mBorderLineLength, mBorderPaint);
            canvas.drawLine(framingRect.left - 1, framingRect.top - 1, framingRect.left - 1 + mBorderLineLength, framingRect.top - 1, mBorderPaint);

            canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1, framingRect.left - 1, framingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
            canvas.drawLine(framingRect.left - 1, framingRect.bottom + 1, framingRect.left - 1 + mBorderLineLength, framingRect.bottom + 1, mBorderPaint);

            canvas.drawLine(framingRect.right + 1, framingRect.top - 1, framingRect.right + 1, framingRect.top - 1 + mBorderLineLength, mBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.top - 1, framingRect.right + 1 - mBorderLineLength, framingRect.top - 1, mBorderPaint);

            canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1, framingRect.right + 1, framingRect.bottom + 1 - mBorderLineLength, mBorderPaint);
            canvas.drawLine(framingRect.right + 1, framingRect.bottom + 1, framingRect.right + 1 - mBorderLineLength, framingRect.bottom + 1, mBorderPaint);
        }

        @Override
        protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
            updateFramingRect();
        }

        public synchronized void updateFramingRect() {
            Point viewResolution = new Point(getWidth(), getHeight());
            int width;
            int height;
            int orientation = DisplayUtils.getScreenOrientation(getContext());

            if(mSquareViewFinder) {
                if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                    height = (int) (getHeight() * SQUARE_DIMENSION_RATIO);
                    width = height;
                } else {
                    width = (int) (getWidth() * SQUARE_DIMENSION_RATIO);
                    height = width;
                }
            } else {
                if(orientation != Configuration.ORIENTATION_PORTRAIT) {
                    height = (int) (getHeight() * LANDSCAPE_HEIGHT_RATIO);
                    width = (int) (LANDSCAPE_WIDTH_HEIGHT_RATIO * height);
                } else {
                    width = (int) (getWidth() * PORTRAIT_WIDTH_RATIO);
                    height = (int) (PORTRAIT_WIDTH_HEIGHT_RATIO * width);
                }
            }

            if(width > getWidth()) {
                width = getWidth() - MIN_DIMENSION_DIFF;
            }

            if(height > getHeight()) {
                height = getHeight() - MIN_DIMENSION_DIFF;
            }

            int leftOffset = (viewResolution.x - width) / 2;
            int topOffset = (viewResolution.y - height) / 2;
            mFramingRect = new Rect(leftOffset, topOffset, leftOffset + width, topOffset + height);
        }
    }


    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 2);
            return;
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void finish_session(String session_id){
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/finish_session");
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("session_id", session_id));
            post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
            HttpResponse response = httpClient.execute(post);
            HttpEntity resEntity = response.getEntity();
            if(resEntity != null) {
                JSONObject jsonResponse = new JSONObject(resEntity.toString());
                if(jsonResponse.getInt("status_code") == 0) {
                    AlertDialogController.warningConfirmDialog(getApplicationContext(), "提醒", "恭喜掃描成功");
                }else{
                    AlertDialogController.warningConfirmDialog(getApplicationContext(), "提醒", "掃描失敗，請再試一次");
                }
            }
        } catch (Exception e) {
            AlertDialogController.warningConfirmDialog(getApplicationContext(), "提醒", "網路連線失敗，請檢察您的網路");
            e.printStackTrace();
        }
    }



}