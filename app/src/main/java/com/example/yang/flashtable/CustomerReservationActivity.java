package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    String promotion_id;
    String request_id;

    CountDownTimer timer;

    boolean request_flag = true;//true means no request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        promotion_id = getIntent().getStringExtra("promotion_id");
        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_reservation_activity);
        new ApiRequest().execute();
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
                timer.cancel();
                new ApiCancel().execute(request_id);
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

    private void startCountDown(String state, final int countdown_millis) {
        if (state.equals("waiting")) {
            timer = new CountDownTimer(countdown_millis, 1000) {
                String time_left;
                long pre_millis = countdown_millis;
                public void onTick(long millis_left) {
                    if((pre_millis-millis_left)>2000 && request_flag == true){
                        pre_millis = millis_left;
                        new ApiCheckSuccess().execute();
                    }
                    time_left = (millis_left / 1000) + seconds;
                    tv_time.setText(time_left);
                }
                public void onFinish() {
                    time_left = 0 + seconds;
                    tv_time.setText(time_left);
                    tv_status.setText(no_response);
                    new ApiCancel().execute(request_id);
                }
            }.start();
        } else if (state.equals("success")) {
            timer = new CountDownTimer(countdown_millis, 1000) {
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
    class ApiRequest extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        @Override
        protected String doInBackground(Void... voids) {
            HttpPost httpPost = new HttpPost("http://"+getString(R.string.server_domain)+"/api/new_request");
            List<NameValuePair> params = new ArrayList<>();
            System.out.println("userid="+getUserId()+"  promotion_id="+promotion_id);
            params.add(new BasicNameValuePair("user_id", getUserId()));
            params.add(new BasicNameValuePair("number", "1"));
            params.add(new BasicNameValuePair("promotion_id", promotion_id));
            String r_id = null;
            try {
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(ent);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity resEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(resEntity);
                //String json = handler.handleResponse(httpResponse);
                System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                String status_code = jsonObject.get("status_code").toString();
                if(status_code.equals("0")){
                    r_id = jsonObject.get("request_id").toString();
                }else if(status_code.equals("-2")){
                    //No promotion Id
                    finish();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return r_id;
        }

        @Override
        protected void onPostExecute(String s) {
            request_id = s;
            new ApiCheckSuccess().execute();
            super.onPostExecute(s);
        }


    }
    class ApiCheckSuccess extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected void onPreExecute() {
            request_flag = false;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            HttpGet request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/user_sessions?"+param.toString());
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String session_response = handler.handleResponse(response);
                JSONArray session_array = new JSONArray(session_response);
                JSONObject session_object = session_array.getJSONObject(0);
                System.out.println("session = "+session_response);
                if(session_object.get("status_code").equals("0")){
                    if(session_object.get("size").equals("0")){
                        //no sessions
                        request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/user_requests?"+param.toString());
                        request.addHeader("Content-Type", "application/json");
                        response = httpClient.execute(request);
                        String request_response = handler.handleResponse(response);
                        JSONArray request_array = new JSONArray(request_response);
                        JSONObject request_object = request_array.getJSONObject(0);
                        System.out.println("request = "+request_response);
                        if(request_object.get("status_code").equals("0")){
                            int size = Integer.valueOf(request_object.get("size").toString());
                            if(size == 0){
                                //no request -> request is rejected
                                return "reject";
                            }else{
                                for(int i = 1; i <= size; i++){
                                    if(request_array.getJSONObject(i).get("request_id").toString().equals(request_id)){
                                        // request still waiting
                                        return "waiting";
                                    }
                                }
                            }
                        }
                        //continue here~~~~
                    }else{
                        //accept
                        return session_array.getJSONObject(1).get("session_id").toString();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return "waiting";
        }

        @Override
        protected void onPostExecute(String s) {
            request_flag = true;
            switch (s){
                case "waiting":
                    break;
                case "reject":
                    timer.cancel();
                    CustomerReservationActivity.this.finish();
                    break;
                default:
                    reservationAccepted();
            }

            super.onPostExecute(s);
        }
    }

    class ApiCancel extends AsyncTask<String, Void, Void>{
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        @Override
        protected Void doInBackground(String... id) {
            NameValuePair param = new BasicNameValuePair("request_id", id[0]);
            HttpPost httpPost = new HttpPost("http://"+getString(R.string.server_domain)+"/api/cancel_request?"+param.toString());
            httpPost.addHeader("Content-Type", "application/json");
            try {
                HttpResponse httpResponse = httpClient.execute(httpPost);
                String json = handler.handleResponse(httpResponse);
                System.out.println("cancel:"+json);
                JSONObject jsonObject = new JSONObject(json);
                String status_code = jsonObject.get("status_code").toString();
                //0: cancel success
                //-1: cancel fail
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private String getUserId(){
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        return preferences.getString("userID", "");
    }
}
