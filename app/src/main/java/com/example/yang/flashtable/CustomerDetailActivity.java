package com.example.yang.flashtable;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.example.yang.flashtable.customer.database.SqlHandler;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CS on 2017/3/23.
 */

public class CustomerDetailActivity extends AppCompatActivity {

    SharedPreferences user;
    String userID;

    DialogBuilder dialog_builder;

    ViewFlipper vf_flipper;
    ListView lv_reservations;
    CustomerDetailAdapter reservation_adapter;
    List<CustomerDetailInfo> reservations;
    int current_index;
    Button bt_comment;
    private int current_record;

    // Elements in show
    TextView tv_record_success, tv_record_arrival_time, tv_record_shop,
            tv_gift, tv_description, tv_location, tv_category;
    ImageView iv_record_credit;
    RatingBar rb_record_rating;

    String time, success, fail, persons, discount_off;
    String no_discount, no_gift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_detail_activity);

        initView();
        initData();
    }

    private void initView() {
        setupActionBar();
        setTitle(getResources().getString(R.string.customer_detail_title));
        dialog_builder = new DialogBuilder(this);

        vf_flipper = (ViewFlipper) findViewById(R.id.customer_detail_vf_flipper);
        lv_reservations = (ListView) findViewById(R.id.customer_detail_lv_details);
        bt_comment = (Button) findViewById(R.id.customer_detail_bt_comment);

        // Elements in show
        tv_record_success = (TextView) findViewById(R.id.customer_detail_tv_record_success);
        tv_record_arrival_time = (TextView) findViewById(R.id.customer_detail_tv_record_arrival_time);
        tv_record_shop = (TextView) findViewById(R.id.customer_detail_tv_record_shop);
        tv_gift = (TextView) findViewById(R.id.customer_detail_tv_gift);
        tv_description = (TextView) findViewById(R.id.customer_detail_tv_description);
        tv_location = (TextView) findViewById(R.id.customer_detail_tv_show_location);
        tv_category = (TextView) findViewById(R.id.customer_detail_tv_show_category);
        iv_record_credit = (ImageView) findViewById(R.id.customer_detail_iv_record_credit);
        rb_record_rating = (RatingBar) findViewById(R.id.customer_detail_rb_record_rating);
    }

    private void initData() {
        getUserInfo();

        // Set reservation ListView
        reservations = new ArrayList<>();
        new APIRecords().execute(userID);
        reservation_adapter = new CustomerDetailAdapter(this, reservations);
        lv_reservations.setAdapter(reservation_adapter);
        lv_reservations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View view, int i, long l) {
                showRecord(i);
                current_record = i;
            }
        });
        bt_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAvailable();
            }
        });
        rb_record_rating.setIsIndicator(true);

        time = getResources().getString(R.string.customer_detail_record_arrival_time);
        success = getResources().getString(R.string.customer_detail_success);
        fail = getResources().getString(R.string.customer_detail_fail);
        persons = getResources().getString(R.string.customer_detail_persons);
        discount_off = getResources().getString(R.string.discount);

        no_discount = getResources().getString(R.string.customer_detail_record_discount);
        no_gift = getResources().getString(R.string.customer_detail_record_gift);
    }

    public void checkAvailable(){
        if(reservations.get(current_index).success)
            new ApiComment().execute(reservations.get(current_index).record_id);
        else
            commentDenied(true);
    }

    public void commentAvailable(){
        Intent intent = new Intent(getApplicationContext(), CustomerRatingActivity.class);
        intent.putExtra("shop", reservations.get(current_record).shop);
        intent.putExtra("shop_location", reservations.get(current_record).location);
        intent.putExtra("shop_id", Integer.toString(reservations.get(current_record).shop_id));
        intent.putExtra("record_id", reservations.get(current_record).record_id);
        startActivity(intent);
    }

    public void commentDenied(boolean reservation_fail){
        if(reservation_fail)
            dialog_builder.dialogEvent(getString(R.string.customer_comments_error_fail_reservation), "normal", null);
        else
            dialog_builder.dialogEvent(getString(R.string.customer_comments_error_already_comment), "normal", null);
    }

    public void updateReservations() {
        reservation_adapter.notifyDataSetChanged();
        lv_reservations.setAdapter(reservation_adapter);
    }

    private void getUserInfo() {
        user = this.getSharedPreferences("USER", MODE_PRIVATE);
        userID = user.getString("userID", "");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (vf_flipper.getDisplayedChild() == 0) {
                finish();
            }
            else if (vf_flipper.getDisplayedChild() == 1) {
                showList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void showList() {
        vf_flipper.setDisplayedChild(0);
        setTitle(getResources().getString(R.string.customer_detail_title));
    }

    private void showRecord(int position) {
        // TODO: Store information can be read from phone

        vf_flipper.setDisplayedChild(1);
        setTitle(getResources().getString(R.string.customer_detail_record_title));

        CustomerDetailInfo record = reservations.get(position);
        current_index = position;
        if (record.success) {
            tv_record_success.setText(success + Integer.toString(record.persons) + persons);
            tv_record_success.setTextColor(getResources().getColor(R.color.textColorOrange));
            iv_record_credit.setImageResource(R.drawable.customer_detail_credit_plus);
        } else {
            tv_record_success.setText(fail + Integer.toString(record.persons) + persons);
            tv_record_success.setTextColor(getResources().getColor(R.color.textColorRed));
            iv_record_credit.setImageResource(R.drawable.customer_detail_credit_minus);
        }
        tv_record_arrival_time.setText(time + record.time);
        tv_record_shop.setText(record.shop);
        rb_record_rating.setRating(record.rating);
        tv_description.setText(record.shop_info);
        tv_location.setText(record.location);
        tv_category.setText(record.category);

        if (!record.gift.equals("")) {
            tv_gift.setText(record.gift);
        } else {
            tv_gift.setText(no_gift);
        }
    }

    class APIRecords extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerDetailActivity.this);
        private String status = null;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.setCanceledOnTouchOutside(false);
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            SqlHandler sqlHandler = new SqlHandler(CustomerDetailActivity.this);
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_records?user_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {

                        // TODO: Show information that has already been received (or at least the UI).
                        int record_id = responseJSON.getJSONObject(i).getInt("record_id");
                        HttpGet requestInfo = new HttpGet( getString(R.string.server_domain)+"api/record_info?record_id=" + responseJSON.getJSONObject(i).getString("record_id") );
                        requestInfo.addHeader("Content-Type", "application/json");
                        JSONObject responseInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestInfo) ) );
                        status =  responseInfo.getString("status_code");
                        if( !status.equals("0") )   break;
                        String promotion_id = responseInfo.getString("promotion_id"), is_succ = responseInfo.getString("is_succ"), persons = responseInfo.getString("number"), created_at = responseInfo.getString("created_at"), user_id = responseInfo.getString("user_id"), shop_id = responseInfo.getString("shop_id");

                        HttpGet requestPromotion = new HttpGet(getString(R.string.server_domain)+"api/promotion_info?promotion_id=" + promotion_id);
                        requestPromotion.addHeader("Content-Type", "application/json");
                        JSONObject responsePromotion = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestPromotion) ) );
                        status = responsePromotion.getString("status_code");
                        if( !status.equals("0") )   break;
                        String promotion_discount = responsePromotion.getString("name");
                        String promotion_gift = responsePromotion.getString("description");

                        CustomerRestaurantInfo info = sqlHandler.getDetail(Integer.valueOf(shop_id));
                        String shop_name = info.name;
                        String shop_address = info.address;
                        String shop_intro = info.intro;
                        String shop_category = info.category;

                        HttpGet requestShopRating = new HttpGet(getString(R.string.server_domain)+"api/shop_comments?shop_id=" + shop_id);
                        requestShopRating.addHeader("Content-Type", "application/json");
                        JSONArray responseShopRating = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(requestShopRating) ) );
                        status = responseShopRating.getJSONObject(0).getString("status_code");
                        if( !status.equals("0") )   break;
                        String shop_rating = responseShopRating.getJSONObject(0).getString("average_score");

                        reservations.add(new CustomerDetailInfo(shop_name, shop_address, Float.parseFloat(shop_rating) / 2,
                                created_at, Integer.parseInt(promotion_discount), promotion_gift, is_succ.equals("true"), Integer.parseInt(persons)
                                , shop_intro, shop_category, Integer.parseInt(shop_id), record_id, info.getImage()));
                    }
                }
            } catch (Exception e) {
                status = null;
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else    updateReservations();
            progress_dialog.dismiss();
        }
    }

    class ApiComment extends AsyncTask<Integer, Void, Boolean> {
        private ProgressDialog progress_dialog = new ProgressDialog(CustomerDetailActivity.this);
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage(getString(R.string.login_wait));
            progress_dialog.setCanceledOnTouchOutside(false);
            progress_dialog.show();
        }

        @Override
        protected Boolean doInBackground(Integer... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/record_info?record_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");

                JSONObject responseJSON = new JSONObject(
                        new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                String status = responseJSON.getString("status_code");
                if( status.equals("0") ) {
                    return responseJSON.
                            getString("is_used").equals("true");
                }
            } catch (Exception e) {
                Log.d("GetCode", "Request exception:" + e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean already) {
            progress_dialog.dismiss();
            if(already){
                commentDenied(false);
            }else {
                commentAvailable();
            }

        }
    }

}
