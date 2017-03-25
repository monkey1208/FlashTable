package com.example.yang.flashtable;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CustomerLoadingActivity extends AppCompatActivity {
    private SqlHandler sql_handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_loading_layout);
        new ApiUpdate().execute();
    }
    private class ApiUpdate extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            //check data version

            //if need update, do update, or else return
            String current_version = getCurrentVersion();
            Log.d(getLocalClassName(), "current version = "+current_version);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<CustomerRestaurantInfo> update_list = requestUpdate(current_version);
            openDB();
            insertDB(update_list);
            System.gc();
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //save the newest version.
            Intent intent = new Intent(CustomerLoadingActivity.this, CustomerMainActivity.class);
            startActivity(intent);
            CustomerLoadingActivity.this.finish();
        }
        private List<CustomerRestaurantInfo> requestUpdate(String version){
            //API
            List<CustomerRestaurantInfo> list = new ArrayList<>();
            return list;
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
