package com.example.yang.flashtable;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StoreManageCommentFragment extends ListFragment {
    private String shopID;

    SharedPreferences user;
    DialogBuilder dialog_builder;

    StoreManageCommentAdapter comment_adapter;
    List<StoreCommentInfo> comments;

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
        Drawable dr = getResources().getDrawable(R.drawable.ic_back_white);
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
        private String status = null;
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"/api/shop_comments?shop_id=" + params[0] +"&verbose=1");
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );
                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = Integer.parseInt( responseJSON.getJSONObject(0).getString("size") );
                    for(int i = 1 ; i <= size ; i++) {
                        JSONObject user_comment = responseJSON.getJSONObject(i);
                        String userAccount = user_comment.getString("user_account");
                        String body = user_comment.getString("body");
                        String score = user_comment.getString("score");
                        String user_picture_url = user_comment.getString("user_picture_url");
                        String comment_time = user_comment.getString("created_at");

                        DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.ENGLISH);
                        Date date =  df.parse(comment_time);
                        df = new SimpleDateFormat("yyyy/MM/dd  hh:mm a", Locale.ENGLISH);
                        comment_time = df.format(date).replace("PM","pm").replace("AM", "am");
                        comments.add( new StoreCommentInfo(userAccount, body, Float.parseFloat(score) / 2, user_picture_url, comment_time) );
                    }
                }
            } catch (Exception e) {
                status = null;
                Log.d("GetCode", "Request exception:"+ e.getMessage());
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void _params) {
            if( status == null  || !status.equals("0") )
                new AlertDialogController(getString(R.string.server_domain)).warningConfirmDialog(getContext(), "提醒", "資料載入失敗，請重試");
            else    updateComments();
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
