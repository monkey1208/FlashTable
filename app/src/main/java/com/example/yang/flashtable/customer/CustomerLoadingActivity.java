package com.example.yang.flashtable.customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerRestaurantInfo;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerLoadingActivity extends AppCompatActivity {
    private SqlHandler sql_handler = null;
    private TextView progress_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set to fullscreen.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.customer_loading_layout);

        progress_tv = (TextView)CustomerLoadingActivity.this.findViewById(R.id.customer_loading_tv);

        //initDatabase();

        new ApiUpdate().execute();
    }

    private void checkBlock(){
        //Do something!!!!
        new ApiRequestSuccess().execute();

    }

    private void initDatabase(){
        SqlHandler.deleteDB(this);
        setVersion("0");
    }

    public void setProgress(String input){
        progress_tv.setText(input);
    }

    private class ApiUpdate extends AsyncTask<String, String, String>{
        HttpClient httpClient = new DefaultHttpClient();


        @Override
        protected String doInBackground(String... strings) {
            //check data version
            //if need update, do update, or else return
            String current_version = getCurrentVersion();
            String server_version = checkServerVersion();
            Log.d(getLocalClassName(), "current version = "+current_version);
            Log.d(getLocalClassName(), "server version = "+server_version);
            if(current_version.equals(server_version)){
                //don't need to update
                return null;
            }else{
                openDB();
                getServerShop();
                System.gc();
                return server_version;
            }
        }
        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);
            setProgress("100%");
            if(version != null){
                setVersion(version);
            }
            checkBlock();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            setProgress(values[0]);
            super.onProgressUpdate(values);
        }

        private void getServerShop(){
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/flash_shops");
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String shop_list_json = handler.handleResponse(http_response);
                JSONArray jsonArray = new JSONArray(shop_list_json);
                int size = Integer.valueOf(jsonArray.getJSONObject(0).get("size").toString());
                for(int i = 1; i<=size; i++){
                    CustomerRestaurantInfo info = null;
                    String id = jsonArray.getJSONObject(i).get("shop_id").toString();
                    request = null;

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("shop_id", id));
                    String paramsString = URLEncodedUtils.format(params, "UTF-8");
                    request = new HttpGet(getString(R.string.server_domain)+"api/shop_info"+"?"+paramsString);
                    http_response = httpClient.execute(request);
                    String shop_json = handler.handleResponse(http_response);
                    Log.d(getLocalClassName(), shop_json);
                    JSONObject shop_object = new JSONObject(shop_json);
                    String status = shop_object.get("status_code").toString();

                    if(status.equals("0")) {
                        String picture_url = shop_object.get("picture_url_mainpage").toString();
                        Bitmap image = getBitmapFromURL(picture_url);

                        String name = shop_object.get("name").toString();
                        String intro = shop_object.get("intro").toString();
                        String location = shop_object.get("location").toString();
                        String tag = shop_object.get("tag").toString();
                        int consumption = Integer.valueOf(shop_object.get("consumption").toString());
                        int minconsumption = shop_object.getInt("min_consumption");
                        String phone = shop_object.getString("public_phone_number");
                        String web = shop_object.getString("email");
                        String address = shop_object.get("address").toString();
                        String business_hours = shop_object.getString("business_hours");
                        String []tmp = location.split(",");
                        String lat = tmp[0];
                        String lng = tmp[1];
                        LatLng latlng = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
                        info = new CustomerRestaurantInfo(name, Integer.valueOf(id), consumption, tag, latlng, web, phone, minconsumption, business_hours);
                        info.setInfo(address, intro);
                        if(image != null){
                            info.turnBitmap2ByteArray(image);
                        }

                        byte[] img1 = bitmap2ByteArray(shop_object.getString("picture_url"));
                        byte[] img2 = bitmap2ByteArray(shop_object.getString("picture_url2"));
                        byte[] img3 = bitmap2ByteArray(shop_object.getString("picture_url3"));
                        byte[] img4 = bitmap2ByteArray(shop_object.getString("picture_url4"));
                        byte[] img5 = bitmap2ByteArray(shop_object.getString("picture_url5"));

                        sql_handler.insert(info, img1, img2, img3, img4, img5);

                        if(image != null && !image.isRecycled()){
                            image.recycle();
                        }
                    }
                    publishProgress(Integer.toString(100*i/size)+"%");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                request = null;
            }
        }

        private byte[] bitmap2ByteArray(String picture_url){
            if(picture_url.equals(""))
                return null;
            Bitmap image = getBitmapFromURL(picture_url);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 0, bos);
            byte[] img = bos.toByteArray();
            if(!image.isRecycled())
                image.recycle();
            return img;
        }

        private String checkServerVersion(){
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/check_version");
            request.addHeader("Content-Type", "application/json");
            String version = "";
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                JSONObject response = new JSONObject(handler.handleResponse(http_response));
                version = response.get("version").toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                request = null;
            }
            return version;
        }

        private void openDB(){
            sql_handler = new SqlHandler(CustomerLoadingActivity.this);
        }

        private Bitmap getBitmapFromURL(String imageUrl)
        {
            try
            {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
                connection.disconnect();
                return bitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
        


    }

    class ApiSessionSuccess extends AsyncTask<Void, Void, Boolean> {
        HttpClient httpClient = new DefaultHttpClient();
        private String promotion_id, offer, name, address, shop_id, time, session_id;
        private int discount, person;
        private float rating;
        private boolean block = false;
        @Override
        protected Boolean doInBackground(Void... voids) {
            //NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("user_id", getUserId()));
            params.add(new BasicNameValuePair("verbose", "1"));
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_sessions?"+ URLEncodedUtils.format(params, "utf-8"));
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
                        block = false;
                        return block;
                    }else{
                        JSONObject object = session_array.getJSONObject(1);
                        this.promotion_id = object.getString("promotion_id");
                        this.discount = object.getInt("promotion_name");
                        this.address = object.getString("shop_address");
                        this.name = object.getString("shop_name");
                        this.offer = object.getString("promotion_description");
                        this.shop_id = object.getString("shop_id");
                        this.person = object.getInt("number");
                        this.time = object.getString("due_time");
                        this.session_id = object.getString("session_id");
                        block = true;
                        request = new HttpGet("http://"+getString(R.string.server_domain)+"/api/shop_comments?shop_id="+shop_id);
                        request.addHeader("Content-Type", "application/json");
                        JSONArray responseShopRating = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(request)));
                        String status = responseShopRating.getJSONObject(0).getString("status_code");
                        if (status.equals("0"))
                            this.rating = Float.parseFloat(responseShopRating.getJSONObject(0).getString("average_score"))/2;
                        else
                            this.rating = 0;
                        block = true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return block;
        }

        @Override
        protected void onPostExecute(Boolean s) {
            if(s){
                //block => goto reservation activity
                Intent intent = new Intent(CustomerLoadingActivity.this, CustomerReservationActivity.class);
                intent.putExtra("promotion_id", this.promotion_id);
                intent.putExtra("discount", this.discount);
                intent.putExtra("offer", this.offer);
                intent.putExtra("persons", this.person);
                intent.putExtra("shop_name", this.name);
                intent.putExtra("rating", Float.toString(this.rating));
                intent.putExtra("shop_location", this.address);
                intent.putExtra("shop_id", this.shop_id);
                intent.putExtra("time", this.time);
                intent.putExtra("session_id", this.session_id);
                intent.putExtra("block", true);
                intent.putExtra("blocktype", "session");
                startActivity(intent);
                CustomerLoadingActivity.this.finish();
            } else {

                SharedPreferences guide = CustomerLoadingActivity.this.getSharedPreferences("GUIDE", MODE_PRIVATE);
                boolean viewed_guide = guide.getBoolean("viewed_guide", false);

                if (!viewed_guide) {
                    Intent intent = new Intent(CustomerLoadingActivity.this, CustomerGuideActivity.class);
                    startActivity(intent);
                    guide.edit().putBoolean("viewed_guide", true).apply();
                }
                else {
                    Intent intent = new Intent(CustomerLoadingActivity.this, CustomerMainActivity.class);
                    startActivity(intent);
                    CustomerLoadingActivity.this.finish();
                }

            }
        }
    }

    class ApiRequestSuccess extends AsyncTask<Void, String, String>{
        HttpClient httpClient = new DefaultHttpClient();
        String request_id = null;
        String promotion_id, offer, address, name, shop_id;
        int discount, person;
        long time;
        float rating;
        String state = "nothing";

        @Override
        protected String doInBackground(Void... voids) {
            NameValuePair param = new BasicNameValuePair("user_id", getUserId());
            HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_requests?"+param.toString()+"&verbose=1");
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
                        return "nothing";
                    }else{
                        //request still exists
                        for (int i = 1; i <= request_size; i++) {
                            String due_time = request_array.getJSONObject(i).getString("due_time");
                            DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss yyyy", Locale.ENGLISH);
                            Date date = dateFormat.parse(due_time);
                            long time = date.getTime() - Calendar.getInstance().getTimeInMillis();
                            String request_id = request_array.getJSONObject(i).getString("request_id");
                            if(time > 0) {
                                state = "waiting";
                                JSONObject object = request_array.getJSONObject(i);
                                this.promotion_id = object.getString("promotion_id");
                                this.discount = object.getInt("promotion_name");
                                this.address = object.getString("shop_address");
                                this.name = object.getString("shop_name");
                                this.offer = object.getString("promotion_description");
                                this.shop_id = object.getString("shop_id");
                                this.person = object.getInt("number");
                                this.time = time;
                                this.request_id = request_id;
                                request = new HttpGet("http://"+getString(R.string.server_domain)+"/api/shop_comments?shop_id="+shop_id);
                                request.addHeader("Content-Type", "application/json");
                                JSONArray responseShopRating = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(request)));
                                String status = responseShopRating.getJSONObject(0).getString("status_code");
                                if (status.equals("0"))
                                    this.rating = Float.parseFloat(responseShopRating.getJSONObject(0).getString("average_score"))/2;
                                else
                                    this.rating = 0;
                                return state;
                            }else{
                                publishProgress(request_id);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return state;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            new ApiCancel("request").execute(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
                case "waiting":
                    Intent intent = new Intent(CustomerLoadingActivity.this, CustomerReservationActivity.class);
                    intent.putExtra("promotion_id", this.promotion_id);
                    intent.putExtra("discount", this.discount);
                    intent.putExtra("offer", this.offer);
                    intent.putExtra("persons", this.person);
                    intent.putExtra("shop_name", this.name);
                    intent.putExtra("rating", Float.toString(this.rating));
                    intent.putExtra("shop_location", this.address);
                    intent.putExtra("shop_id", this.shop_id);
                    intent.putExtra("time", this.time);
                    intent.putExtra("request_id", this.request_id);
                    intent.putExtra("block", true);
                    intent.putExtra("blocktype", "request");
                    startActivity(intent);
                    CustomerLoadingActivity.this.finish();
                    break;
                default:
                    new ApiSessionSuccess().execute();
                    break;
            }

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

    private String getUserId(){
        SharedPreferences preferences = getSharedPreferences("USER", MODE_PRIVATE);
        return preferences.getString("userID", "");
    }

    private void setVersion(String version){
        SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
        preferences.edit().putString("version", version).commit();
    }

    private String getCurrentVersion() {
        SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
        return preferences.getString("version", "0");
    }
}
