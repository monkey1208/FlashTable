package com.example.yang.flashtable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        new ApiUpdate().execute();
    }
    public void setProgress(String input){
        progress_tv.setText(input);
    }
    private class ApiUpdate extends AsyncTask<String, String, String>{
        HttpClient httpClient = new DefaultHttpClient();
        TextView progress_tv;


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
                getServerShop();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                List<CustomerRestaurantInfo> update_list = requestUpdate(current_version);
                openDB();
                insertDB(update_list);
                System.gc();
                return server_version;
            }
        }
        @Override
        protected void onPostExecute(String version) {
            super.onPostExecute(version);
            //save the newest version.
            if(version != null){
                //setVersion(version);
            }
            Intent intent = new Intent(CustomerLoadingActivity.this, CustomerMainActivity.class);
            startActivity(intent);
            CustomerLoadingActivity.this.finish();
        }
        private List<CustomerRestaurantInfo> requestUpdate(String version){
            //API
            List<CustomerRestaurantInfo> list = new ArrayList<>();
            return list;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            setProgress(values[0]);
        }

        private void getServerShop(){
            HttpGet request = new HttpGet("https://flash-table.herokuapp.com/api/flash_shops");
            request.addHeader("Content-Type", "application/json");
            try {
                HttpResponse http_response = httpClient.execute(request);
                ResponseHandler<String> handler = new BasicResponseHandler();
                String shop_list_json = handler.handleResponse(http_response);
                JSONArray jsonArray = new JSONArray(shop_list_json);
                int size = Integer.valueOf(jsonArray.getJSONObject(0).get("size").toString());
                for(int i = 1; i<=size; i++){
                    String id = jsonArray.getJSONObject(i).get("shop_id").toString();
                    request = null;

                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("shop_id", id));
                    String paramsString = URLEncodedUtils.format(params, "UTF-8");
                    request = new HttpGet("https://flash-table.herokuapp.com/api/shop_info"+"?"+paramsString);
                    http_response = httpClient.execute(request);
                    String shop_json = handler.handleResponse(http_response);
                    Log.d(getLocalClassName(), shop_json);
                    JSONObject shop_object = new JSONObject(shop_json);
                    String status = shop_object.get("status_code").toString();

                    if(status.equals("0")) {
                        String picture_url = shop_object.get("picture_url").toString();
                        Bitmap image = getBitmapFromURL(picture_url);
                        if(image != null){

                        }
                        String name = shop_object.get("name").toString();
                        String intro = shop_object.get("intro").toString();
                        String phone = shop_object.get("phone_number").toString();
                        String email = shop_object.get("email").toString();
                        String location = shop_object.get("location").toString();
                        String []tmp = location.split(",");
                        String lat = tmp[0];
                        String lng = tmp[1];
                        LatLng latlng = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));

                        //CustomerRestaurantInfo info = new CustomerRestaurantInfo();
                        //Log.d(getLocalClassName(), "name=" + name + " intro=" + intro);
                        //Log.d(getLocalClassName(), handler.handleResponse(http_response));
                        if(!image.isRecycled()){
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

        private String checkServerVersion(){
            HttpGet request = new HttpGet("https://flash-table.herokuapp.com/api/check_version");
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
        private void insertDB(List<CustomerRestaurantInfo> infoList){
            Bitmap image = BitmapFactory.decodeResource(CustomerLoadingActivity.this.getResources(), R.drawable.ic_gift);
            for(int i = 0; i < infoList.size(); i++){
                System.out.println(infoList.get(i).id);
                infoList.get(i).turnBitmap2ByteArray(image);
            }
            sql_handler.insertList(infoList);
            if(!image.isRecycled()){
                image.recycle();
            }
        }
        private void insertDB(CustomerRestaurantInfo info){
            sql_handler.insert(info);
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
                options.inSampleSize = 4;
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
        

        private void setVersion(String version){
            SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
            preferences.edit().putString("version", version).commit();
        }
        private String getCurrentVersion() {
            SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
            return preferences.getString("version", "0");
        }
    }
}
