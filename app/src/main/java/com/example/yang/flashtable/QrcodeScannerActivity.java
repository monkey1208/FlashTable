package com.example.yang.flashtable;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.core.DisplayUtils;
import me.dm7.barcodescanner.core.IViewFinder;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrcodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    static final String SCAN_RESULT = "scan_result";
    private static ZXingScannerView mScannerView;
    private static Fragment userInfoFragment;
    private String status;
    private static String session_id;
    private static String record_id = "2";
    private static String session_create_time;
    private static FragmentManager fragmentManager;
    private static String name, arrive_time, promotionName, promotionDes, image_url;
    private static int num, point;
    private static int status_bar_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_qrcode_scan);

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

        ImageView iv_back= (ImageView)findViewById(R.id.store_qrcode_scan_iv_back) ;
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fragmentManager= this.getSupportFragmentManager();
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
    public void handleResult(Result result) {
        String session = result.toString();
        session_id = session.substring(session.indexOf("=")+1);
        new Get_Session_Info().execute(session_id);
        mScannerView.stopCamera();
        new Finish_Session().execute(session_id);
    }


    public static class CustomViewFinderView extends View implements IViewFinder {
        private static final String TAG = "ViewFinderView";

        private Rect mFramingRect;

        private static final float PORTRAIT_WIDTH_RATIO = 6f/8;
        private static final float PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f;

        private static final float LANDSCAPE_HEIGHT_RATIO = 5f/8;
        private static final float LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f;
        private static final int MIN_DIMENSION_DIFF = 50;

        private static final float SQUARE_DIMENSION_RATIO = 0.78f;

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

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        status_bar_height = result;
        return result;
    }

    private class Finish_Session extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost post = new HttpPost("https://flash-table.herokuapp.com/api/finish_session");
                List<NameValuePair> param = new ArrayList<>();
                param.add(new BasicNameValuePair("session_id", params[0]));
                post.setEntity(new UrlEncodedFormEntity(param, HTTP.UTF_8));
                HttpResponse response = httpClient.execute(post);
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null) {
                    String res_string = EntityUtils.toString(resEntity);
                    JSONObject jsonResponse = new JSONObject(res_string);
                    if(jsonResponse.getInt("status_code") == 0) {
                        status = "success";
                        record_id = jsonResponse.getString("record_id");
                    }else{
                        status = "fail";
                    }
                }
            } catch (Exception e) {
                status = "exception";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void _params){
            if(status.equals("success")){
                warningConfirmDialog(QrcodeScannerActivity.this, "提醒", "恭喜掃描成功");
            }else if(status.equals("fail")){
                warningConfirmDialog(QrcodeScannerActivity.this, "提醒", "掃描失敗，請再試一次");
            }else if(status.equals("exception")){
                warningConfirmDialog(QrcodeScannerActivity.this, "提醒", "網路連線失敗，請檢查您的網路");
            }
        }
    }

    private void warningConfirmDialog(final Context context, String title, String content){
        View item = LayoutInflater.from(context).inflate(R.layout.store_warning_confirm_dialog, null);
        TextView tv_title =  (TextView)item.findViewById(R.id.store_warning_confirm_tv_title);
        tv_title.setText(title);
        tv_title.setTextSize(18);

        final Dialog alertDialog = new Dialog(context, R.style.StoreDialog);
        alertDialog.setContentView(item);
        alertDialog.setCanceledOnTouchOutside(false);

        TextView tv_content = (TextView)item.findViewById(R.id.store_warning_confirm_tv_content);
        tv_content.setText(content);
        ImageButton bt_confirm = (ImageButton)item.findViewById(R.id.store_warning_confirm_bt_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(status.equals("success")) {
                    new Get_Record_Info().execute(record_id);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    alertDialog.dismiss();
                    mScannerView.setResultHandler(QrcodeScannerActivity.this);
                }else{
                    alertDialog.dismiss();
                    mScannerView.setResultHandler(QrcodeScannerActivity.this);
                    mScannerView.startCamera();
                }
            }
        });

        alertDialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        lp.width = (int) (displayWidth * 0.76);
        lp.height = (int) (displayHeight * 0.3);
        try {
            alertDialog.getWindow().setLayout( (int) (displayWidth * 0.76), (int) (displayHeight * 0.3));
        } catch(NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static class UserInfoFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.store_home_confirm_fragment, container, false);
            view.setPadding(0, status_bar_height, 0, 0);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
            TextView tv_point = (TextView) view.findViewById(R.id.tv_point);
            TextView tv_number = (TextView) view.findViewById(R.id.tv_number);
            TextView tv_arrive_time = (TextView) view.findViewById(R.id.store_home_confirm_fragment_tv_arrive_time);
            TextView tv_sessiont_time = (TextView) view.findViewById(R.id.store_home_confirm_fragment_tv_appoint_time);
            TextView tv_discount = (TextView) view.findViewById(R.id.store_home_confirm_fragment_tv_discount);
            TextView tv_description = (TextView) view.findViewById(R.id.store_home_confirm_fragment_tv_gift);
            tv_name.setText(name);
            tv_point.setText(" ( 信譽"+String.valueOf(point)+" )");
            tv_number.setText(arrive_time);
            tv_sessiont_time.setText(session_create_time);
            tv_arrive_time.setText(arrive_time);
            tv_discount.setText(promotionName);
            tv_description.setText(promotionDes);
            tv_number.setText(String.valueOf(num));

            ImageView iv_photo = (ImageView)view.findViewById(R.id.iv_photo);
            if(!image_url.equals("")){
                Picasso.with(getContext()).load(image_url).into(iv_photo);
            }else{
                iv_photo.setImageResource(R.drawable.default_avatar);
            }

            ImageButton ib_comfirm = (ImageButton) view.findViewById(R.id.bt_click);
            ib_comfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = getActivity().getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putString("session_id",session_id);
                    intent.putExtras(bundle);
                    getActivity().setResult(RESULT_OK,intent);
                    fragmentManager.beginTransaction().remove(userInfoFragment).commit();
                    getActivity().finish();
                }
            });
            return view;
        }
    }

    private class Get_Session_Info extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getSessionInfo = new HttpGet("https://flash-table.herokuapp.com/api/session_info?session_id="+session_id);
                JSONObject sessionInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getSessionInfo)));
                if(sessionInfo.getInt("status_code") == 0){
                    SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                    Date session_date = df.parse(sessionInfo.getString("created_at"));
                    df = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
                    session_create_time = df.format(session_date);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class Get_Record_Info extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet getRecordInfo = new HttpGet("https://flash-table.herokuapp.com/api/record_info?record_id="+params[0]);
                JSONObject recordInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getRecordInfo)));
                num = recordInfo.getInt("number");
                int user = recordInfo.getInt("user_id");
                int promotion = recordInfo.getInt("promotion_id");
                SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                Date arrive_date = df.parse(recordInfo.getString("created_at"));
                df = new SimpleDateFormat("yyyy MM/dd  hh:mm a", Locale.ENGLISH);
                arrive_time = df.format(arrive_date);

                HttpGet getUserInfo = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id="+String.valueOf(user));
                JSONObject userInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getUserInfo)));
                name = userInfo.getString("account");
                point = userInfo.getInt("point");
                image_url = userInfo.getString("picture_url");

                HttpGet getPromotionInfo = new HttpGet("https://flash-table.herokuapp.com/api/promotion_info?promotion_id="+String.valueOf(promotion));
                JSONObject promotionInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(getPromotionInfo)));
                promotionName = promotionInfo.getString("name");
                promotionDes = promotionInfo.getString("description");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params){
            userInfoHandler.sendEmptyMessage(0);
        }
    }

    private static Handler userInfoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    userInfoFragment = new UserInfoFragment();
                    fragmentManager.beginTransaction().add(R.id.store_qrcode_frame, userInfoFragment).addToBackStack(null).commit();
                    break;
                default:
                    break;
            }
        }
    };

}