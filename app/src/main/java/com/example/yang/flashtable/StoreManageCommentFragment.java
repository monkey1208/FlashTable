package com.example.yang.flashtable;

import android.support.v4.app.ListFragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StoreManageCommentFragment extends ListFragment {
    private String shopID;

    SharedPreferences user;
    String userID;
    DialogBuilder dialog_builder;

    StoreManageCommentAdapter comment_adapter;
    List<CustomerCommentInfo> comments;

    public StoreManageCommentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.store_manage_comment_fragment, container, false);
        setTitleBar(view);

        dialog_builder = new DialogBuilder(getActivity());
        shopID = StoreMainActivity.storeInfo.id;

        getComments();
        return view;
    }

    private void setTitleBar(View view){
        Toolbar bar = (Toolbar)view.findViewById(R.id.store_manage_comment_tb_toolbar);
        bar.setPadding(0,getStatusBarHeight(),0,0);
        Drawable dr = getResources().getDrawable(R.drawable.ic_back);
        Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
        bar.setNavigationIcon(d);
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE);
            }
        });
    }

    private void getComments() {
        comments = new ArrayList<>();
        comment_adapter = new StoreManageCommentAdapter(getActivity(), comments);
        setListAdapter(comment_adapter);
        new APIShopComments().execute(shopID);
    }

    public void updateComments() {
        comment_adapter.notifyDataSetChanged();
    }


    class APIShopComments extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(getContext());
        private String status = null;
        @Override
        protected void onPreExecute() {
            progress_dialog.setCanceledOnTouchOutside(false);
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
        }
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet("https://flash-table.herokuapp.com/api/shop_comments?shop_id=" + params[0]);
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {
                        HttpGet requestInfo = new HttpGet( "https://flash-table.herokuapp.com/api/comment_info?comment_id=" + responseJSON.getJSONObject(i).getString("comment_id") );
                        requestInfo.addHeader("Content-Type", "application/json");
                        JSONObject responseInfo = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestInfo) ) );
                        status =  responseInfo.getString("status_code");

                        if( !status.equals("0") )   break;
                        String body = responseInfo.getString("body"), score = responseInfo.getString("score"), user_id = responseInfo.getString("user_id"), shop_id = responseInfo.getString("shop_id");

                        // TODO: Show information that has already been received (or at least the UI).
                        HttpGet requestUser = new HttpGet("https://flash-table.herokuapp.com/api/user_info?user_id=" + user_id);
                        requestUser.addHeader("Content-Type", "application/json");
                        JSONObject responseUser = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestUser) ) );
                        status = responseUser.getString("status_code");
                        if( !status.equals("0") )   break;
                        String userAccount = responseUser.getString("account");

                        HttpGet requestShop = new HttpGet("https://flash-table.herokuapp.com/api/shop_info?shop_id=" + shop_id);
                        requestShop.addHeader("Content-Type", "application/json");
                        JSONObject responseShop = new JSONObject( new BasicResponseHandler().handleResponse( httpClient.execute(requestShop) ) );
                        status = responseShop.getString("status_code");

                        if( !status.equals("0") )   break;
                        String shopName = responseShop.getString("name");
                        comments.add( new CustomerCommentInfo( userAccount, shopName, body, Float.parseFloat(score) / 2, Integer.parseInt(user_id), Integer.parseInt(shop_id) ) );
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
            if( status == null  || !status.equals("0") )
                dialog_builder.dialogEvent(getResources().getString(R.string.login_error_connection), "normal", null);
            else    updateComments();
            progress_dialog.dismiss();
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

}
