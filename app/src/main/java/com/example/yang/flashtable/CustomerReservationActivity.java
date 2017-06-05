package com.example.yang.flashtable;

import android.content.Context;
import android.content.Intent;
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
import org.apache.http.client.ClientProtocolException;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
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
    TextView tv_status, tv_time, tv_arrival_time, tv_shop, tv_gift;
    String seconds, no_response, late;
    RatingBar rb_shop;
    LinearLayout ll_time_left;
    Button bt_cancel, bt_arrive_cancel;
    View.OnClickListener cancel_listener, cancel_arrive_listener;
    ImageView iv_qr_code;

    int persons;
    int discount;
    String offer;
    String promotion_id;
    String request_id;
    String session_id = null;

    String shop_name, shop_location, shop_id;
    float rating;

    CountDownTimer timer;
    Timer timer1;

    boolean request_flag = true;//true means no request
    boolean session_flag = true;

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
    /*
    public static class GetBlockInfo{


        public static boolean getBlockStatus(Context c){
            SharedPreferences pref = c.getSharedPreferences("BLOCK", MODE_PRIVATE);
            if(pref.getString("block", "false").equals("true")){
                return true;
            }
            return false;
        }
        public static long getTime(Context c){
            SharedPreferences pref = c.getSharedPreferences("BLOCK", MODE_PRIVATE);
            return pref.getLong("block_time", 0);
        }
        public static String getSession(Context c){
            SharedPreferences pref = c.getSharedPreferences("BLOCK", MODE_PRIVATE);
            return pref.getString("session", "");
        }
        public static int getDiscount(Context c){
            SharedPreferences pref = c.getSharedPreferences("BLOCK", MODE_PRIVATE);
            return pref.getInt("discount", 101);
        }
        public static String getOffer(Context c){
            SharedPreferences pref = c.getSharedPreferences("BLOCK", MODE_PRIVATE);
            return pref.getString("offer", "N");
        }
    }
    */
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
        tv_gift = (TextView) findViewById(R.id.customer_reservation_tv_gift);
        late = getResources().getString(R.string.customer_reservation_late);
        ll_time_left = (LinearLayout) findViewById(R.id.customer_reservation_ll_time_left);
        bt_arrive_cancel = (Button) findViewById(R.id.customer_reservation_bt_arrive_cancel);
        iv_qr_code = (ImageView) findViewById(R.id.customer_reservation_iv_qr_code);
    }

    private void initData() {
        gif_drawable.setSpeed(2.0f);

        cancel_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogBuilder(CustomerReservationActivity.this).dialogEvent(getString(R.string.customer_reservation_cancel_confirm), "withCancel", new DialogEventListener() {
                    @Override
                    public void clickEvent(boolean ok, int status) {
                        if(ok) {
                            timer.cancel();
                            new ApiCancel("request").execute(request_id);
                            finish();
                            Intent intent = new Intent(CustomerReservationActivity.this, CustomerMainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        cancel_arrive_listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new DialogBuilder(CustomerReservationActivity.this).dialogEvent(getString(R.string.dialog_cancel_success_reservation), "withCancel", new DialogEventListener() {
                    @Override
                    public void clickEvent(boolean ok, int status) {
                        if(ok) {
                            timer.cancel();
                            new ApiCancel("session").execute(session_id);
                            finish();
                            Intent intent = new Intent(CustomerReservationActivity.this, CustomerMainActivity.class);
                            startActivity(intent);
                        }
                    }
                });

            }
        };
        bt_cancel.setOnClickListener(cancel_listener);
        bt_arrive_cancel.setOnClickListener(cancel_arrive_listener);


        shop_name = getIntent().getStringExtra("shop_name");
        rating = Float.parseFloat(getIntent().getStringExtra("rating"));
        tv_shop.setText(shop_name);
        rb_shop.setRating(rating);
        rb_shop.setIsIndicator(true);
        promotion_id = getIntent().getStringExtra("promotion_id");
        discount = getIntent().getIntExtra("discount", 101);
        offer = getIntent().getStringExtra("offer");
        persons = getIntent().getIntExtra("persons", 1);
        shop_location = getIntent().getStringExtra("shop_location");
        shop_id = getIntent().getStringExtra("shop_id");
        if(!getIntent().getBooleanExtra("block", false)){
            new ApiRequest().execute();
        }else{
            System.out.println("BLOCK!!!!!!!");
            String time = getIntent().getStringExtra("time");
            session_id = getIntent().getStringExtra("session_id");
            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy", Locale.ENGLISH);
            try {
                Date date = dateFormat.parse(time);
                long nowtime = date.getTime();
                long remain_time = nowtime-Calendar.getInstance().getTimeInMillis();
                if(remain_time<0)
                    remain_time = 0;
                reservationAccepted((int)remain_time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void reservationAccepted(int sec) {
        vf_flipper.setDisplayedChild(1);

        tv_gift.setText(offer);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        startCountDown("success", sec);
        try {
            Bitmap bitmap = encodeAsBitmap("session_id="+session_id);
            iv_qr_code.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void requestSuccess(){
        timer.cancel();
        reservationAccepted(60000*15);
    }

    private void requestRejected(){
        timer.cancel();
        new DialogBuilder(this).dialogEvent(getString(R.string.dialog_restaurant_refuse_reservation), "normal", finish_listener);
    }

    private void qrRejected(){
        timer.cancel();
        new DialogBuilder(this).dialogEvent(getString(R.string.dialog_restaurant_cancel_reservation), "normal", finish_listener);
    }

    private void qrSuccess(){
        timer.cancel();
        Intent intent = new Intent(CustomerReservationActivity.this, CustomerRatingActivity.class);
        intent.putExtra("shop", shop_name);
        intent.putExtra("shop_location", "");
        intent.putExtra("shop_id", 0);
        startActivity(intent);
        CustomerReservationActivity.this.finish();
    }

    private void requestNoResponse(){
        new DialogBuilder(CustomerReservationActivity.this).dialogEvent(getString(R.string.customer_reservation_canceled), "normal", new DialogEventListener() {
            @Override
            public void clickEvent(boolean ok, int status) {
                if(ok) {
                    finish();
                }
            }
        });
    }

    DialogEventListener finish_listener = new DialogEventListener() {
        @Override
        public void clickEvent(boolean ok, int status) {
            finish();
            Intent intent = new Intent(CustomerReservationActivity.this, CustomerMainActivity.class);
            startActivity(intent);
        }
    };

    private void startCountDown(String state, final int countdown_millis) {
        if (state.equals("waiting")) {
            timer = new CountDownTimer(countdown_millis, 1000) {
                String time_left;
                long pre_millis = countdown_millis;
                public void onTick(long millis_left) {
                    if((pre_millis-millis_left)>2000 && request_flag == true){
                        pre_millis = millis_left;
                        new ApiRequestSuccess(request_id).execute();
                    }
                    time_left = (millis_left / 1000) + seconds;
                    tv_time.setText(time_left);
                }
                public void onFinish() {
                    time_left = 0 + seconds;
                    tv_time.setText(time_left);
                    tv_status.setText(no_response);
                    new ApiCancel("request").execute(request_id);
                    requestNoResponse();
                }
            }.start();
        } else if (state.equals("success")) {
            timer = new CountDownTimer(countdown_millis, 1000) {
                String time_left;
                long pre_millis = countdown_millis;
                public void onTick(long millis_left) {
                    if((pre_millis-millis_left)>2000 && session_flag == true){
                        pre_millis = millis_left;
                        new ApiSessionSuccess().execute();
                    }
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
                    timer1 = new Timer();
                    TimerTask sessionTask = new SessionTimer();
                    timer1.schedule(sessionTask, 1000, 2000);
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
        @Override
        protected String doInBackground(Void... voids) {
            HttpPost httpPost = new HttpPost(getString(R.string.server_domain)+"/api/new_request");
            List<NameValuePair> params = new ArrayList<>();
            System.out.println("userid="+getUserId()+"  promotion_id="+promotion_id);
            params.add(new BasicNameValuePair("user_id", getUserId()));
            params.add(new BasicNameValuePair("number", Integer.toString(persons)));
            params.add(new BasicNameValuePair("promotion_id", promotion_id));
            String r_id = null;
            try {
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                httpPost.setEntity(ent);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity resEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(resEntity);
                System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                String status_code = jsonObject.get("status_code").toString();
                if(status_code.equals("0")){
                    r_id = jsonObject.get("request_id").toString();
                }else if(status_code.equals("-2")){
                    //No promotion Id
                    finish();
                    Intent intent = new Intent(CustomerReservationActivity.this, CustomerMainActivity.class);
                    startActivity(intent);
                }
            } catch (IOException e) {
                publishProgress();
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return r_id;
        }

        @Override
        protected void onPostExecute(String s) {
            request_id = s;
            new ApiRequestSuccess(request_id).execute();
            startCountDown("waiting", 120000);
            super.onPostExecute(s);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            new DialogBuilder(CustomerReservationActivity.this).dialogEvent(getString(R.string.dialog_network_unable), "normal", new DialogEventListener() {
                @Override
                public void clickEvent(boolean ok, int status) {

                }
            });
        }

    }

    class ApiRequestSuccess extends AsyncTask<Void, String, String>{
        HttpClient httpClient = new DefaultHttpClient();
        private String requestId;
        public ApiRequestSuccess(String requestId){
            this.requestId = requestId;
        }

        @Override
        protected void onPreExecute() {
            request_flag = false;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_requests?"+param.toString());
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String request_response = handler.handleResponse(response);
                JSONArray request_array = new JSONArray(request_response);
                JSONObject request_object = request_array.getJSONObject(0);
                System.out.println("request = "+request_response);
                if(request_object.get("status_code").equals("0")){
                    int request_size = Integer.valueOf(request_object.getString("size"));
                    if(request_size == 0){
                        //no request
                        request = new HttpGet(getString(R.string.server_domain)+"api/user_sessions?"+param.toString());
                        request.addHeader("Content-Type", "application/json");
                        response = httpClient.execute(request);
                        String session_response = handler.handleResponse(response);
                        JSONArray session_array = new JSONArray(session_response);
                        JSONObject session_object = session_array.getJSONObject(0);
                        System.out.println("session = "+session_response);
                        if(session_object.get("status_code").equals("0")){
                            int size = Integer.valueOf(session_object.get("size").toString());
                            if(size == 0){
                                //no session -> request is rejected
                                return "reject";
                            }else{
                                return session_array.getJSONObject(1).get("session_id").toString();
                            }
                        }
                    }else{
                        //request still exists
                        System.out.println("id = "+requestId);
                        for (int i = 1; i <= request_size; i++) {
                            if(request_array.getJSONObject(i).getString("request_id").equals(requestId))
                                return "waiting";
                            else{
                                publishProgress(request_array.getJSONObject(i).getString("request_id"));
                            }
                        }

                        request = new HttpGet(getString(R.string.server_domain)+"api/user_sessions?"+param.toString());
                        request.addHeader("Content-Type", "application/json");
                        response = httpClient.execute(request);
                        String session_response = handler.handleResponse(response);
                        JSONArray session_array = new JSONArray(session_response);
                        JSONObject session_object = session_array.getJSONObject(0);
                        System.out.println("session = "+session_response);
                        if(session_object.get("status_code").equals("0")){
                            int size = Integer.valueOf(session_object.get("size").toString());
                            if(size == 0){
                                //no session -> request is rejected
                                return "reject";
                            }else{
                                return session_array.getJSONObject(1).get("session_id").toString();
                            }
                        }
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
        protected void onProgressUpdate(String... values) {
            new ApiCancel("request").execute(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            request_flag = true;
            switch (s){
                case "waiting":
                    break;
                case "reject":
                    Toast.makeText(CustomerReservationActivity.this, "Rejected", Toast.LENGTH_SHORT).show();
                    requestRejected();
                    break;
                default:
                    session_id = s;
                    requestSuccess();
                    break;
            }

            super.onPostExecute(s);
        }

    }

    class ApiSessionSuccess extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();

        @Override
        protected void onPreExecute() {
            session_flag = false;
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_sessions?"+param.toString());
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String session_response = handler.handleResponse(response);
                JSONArray session_array = new JSONArray(session_response);
                JSONObject session_object = session_array.getJSONObject(0);
                System.out.println("session = "+session_response);
                if(session_object.get("status_code").equals("0")){
                    int size = Integer.valueOf(session_object.get("size").toString());
                    if(size == 0){
                        //no sessions => rejected
                        //Do something when rejected.
                        return "finish";
                    }else{
                        //accept
                        for(int i = 1; i <= size; i++){
                            if(session_id.equals(session_array.getJSONObject(i).get("session_id").toString())){
                                return "waiting";
                            }
                        }
                        return "finish";
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

            switch (s){
                case "finish":
                    if(timer1 != null)
                        timer1.cancel();
                    session_flag = true;
                    new ApiRecord().execute();
                    break;
                default:
                    break;
            }
            session_flag = true;
            super.onPostExecute(s);
        }
    }

    class ApiCancel extends AsyncTask<String, Void, Void>{
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        boolean request_session_flag;
        public ApiCancel(String flag){
            if(flag.equals("request")) {
                request_session_flag = true;
            }
            else {
                request_session_flag = false;
            }
        }
        @Override
        protected Void doInBackground(String... id) {


            HttpPost httpPost;
            if(request_session_flag) {
                NameValuePair param = new BasicNameValuePair("request_id", id[0]);
                httpPost = new HttpPost(getString(R.string.server_domain) + "/api/cancel_request?" + param.toString());
            }else{
                NameValuePair param = new BasicNameValuePair("session_id", id[0]);
                httpPost = new HttpPost(getString(R.string.server_domain) + "/api/cancel_session?" + param.toString());
            }
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

    class ApiRecord extends AsyncTask<Void, Void, String>{
        HttpClient httpClient = new DefaultHttpClient();
        ResponseHandler<String> handler = new BasicResponseHandler();
        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            HttpGet httpGet = new HttpGet(getString(R.string.server_domain) + "/api/user_records?" + param.toString());
            httpGet.addHeader("Content-Type", "application/json");
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String json = handler.handleResponse(httpResponse);
                System.out.println("record:"+json);
                JSONArray jsonArray = new JSONArray(json);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                if(jsonObject.get("status_code").equals("0")){
                    int last = Integer.valueOf(jsonObject.get("size").toString());
                    String record_id = jsonArray.getJSONObject(last).getString("record_id");
                    param = new BasicNameValuePair("record_id", record_id);
                    httpGet = new HttpGet(getString(R.string.server_domain) + "/api/record_info?" + param.toString());
                    httpResponse = httpClient.execute(httpGet);
                    json = handler.handleResponse(httpResponse);
                    System.out.println("record:"+json);
                    jsonObject = new JSONObject(json);
                    if(jsonObject.getString("status_code").equals("0")){
                        return jsonObject.getString("is_succ");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "false";
        }

        @Override
        protected void onPostExecute(String s) {
            if(s.equals("true")){
                qrSuccess();
            }else{
                qrRejected();
            }
            super.onPostExecute(s);
        }
    }

    class SessionTimer extends TimerTask{

        @Override
        public void run() {
            if(session_flag == true){
                new ApiSessionSuccess().execute();
            }
        }
    }

    private String getUserId(){
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        return preferences.getString("userID", "");
    }
}
