package com.example.yang.flashtable.customer.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.yang.flashtable.FlashCouponInfo;
import com.example.yang.flashtable.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
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
import java.util.List;

/**
 * Created by CS on 2017/5/22.
 */

public class CustomerFlashPointAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<FlashCouponInfo> coupons;

    private ImageView iv_1;
    private TextView tv_title_1;
    private TextView tv_points_1;

    public CustomerFlashPointAdapter(Context _context, List<FlashCouponInfo> _coupons) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        coupons = _coupons;
    }

    @Override
    public int getCount() { return coupons.size(); }

    @Override
    public Object getItem(int index) { return coupons.get(index); }

    @Override
    public long getItemId(int position) { return coupons.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_flash_point_item, parent, false);
            iv_1 = (ImageView) convertView.findViewById(R.id.customer_points_iv_1);
            tv_title_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_title_1);
            tv_points_1 = (TextView) convertView.findViewById(R.id.customer_points_tv_price_1);

            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        FlashCouponInfo coupon_1 = coupons.get(position);

        tv_title_1.setText(coupon_1.name);
        tv_points_1.setText(Integer.toString(coupon_1.flash_point));
        iv_1.setImageBitmap(coupon_1.picture_small);
    }

}
