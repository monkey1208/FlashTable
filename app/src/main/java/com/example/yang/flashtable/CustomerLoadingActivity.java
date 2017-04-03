package com.example.yang.flashtable;

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

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
        // SqlHandler.deleteDB(this);
        // setVersion("0");
        new ApiUpdate().execute();
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
            Intent intent = new Intent(CustomerLoadingActivity.this, CustomerMainActivity.class);
            startActivity(intent);
            CustomerLoadingActivity.this.finish();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            setProgress(values[0]);
        }

        private void getServerShop(){
            HttpGet request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/flash_shops");
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
                    request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/shop_info"+"?"+paramsString);
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
                        String location = shop_object.get("location").toString();
                        String tag = shop_object.get("tag").toString();
                        int consumption = Integer.valueOf(shop_object.get("consumption").toString());
                        String address = shop_object.get("address").toString();
                        String []tmp = location.split(",");
                        String lat = tmp[0];
                        String lng = tmp[1];
                        LatLng latlng = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
                        info = new CustomerRestaurantInfo(name, Integer.valueOf(id), consumption, tag, latlng);
                        info.detailInfo.setInfo(address, intro);
                        info.turnBitmap2ByteArray(image);
                        sql_handler.insert(info);

                        if(!image.isRecycled()){
                            image.recycle();
                        }
                    }
                    //publishProgress(Integer.toString(100*i/size)+"%");
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
            HttpGet request = new HttpGet("https://"+getString(R.string.server_domain)+"/api/check_version");
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
    private void setVersion(String version){
        SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
        preferences.edit().putString("version", version).commit();
    }
    private String getCurrentVersion() {
        SharedPreferences preferences = getSharedPreferences("VERSION", MODE_PRIVATE);
        return preferences.getString("version", "0");
    }
}
