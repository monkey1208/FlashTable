package com.example.yang.flashtable.customer;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yang.flashtable.DialogBuilder;
import com.example.yang.flashtable.R;
import com.example.yang.flashtable.customer.adapter.CustomerCommentContentAdapter;
import com.example.yang.flashtable.customer.database.SqlHandler;
import com.example.yang.flashtable.customer.infos.CustomerCommentContentInfo;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Yang on 2017/7/5.
 */

public class CustomerCommentTabFragment extends Fragment {
    private int page_index;
    boolean has_comment;
    ListView lv_comments;
    View view;
    CustomerCommentContentAdapter comment_adapter;
    ArrayList<CustomerCommentContentInfo> comments;
    DialogBuilder dialog_builder;
    TextView tv_nothing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.customer_comment_tab_fragment, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        page_index = FragmentPagerItem.getPosition(getArguments());
        initView();
        initData();

        new APIComments().execute(getUserId());
    }

    private void initView(){
        lv_comments = (ListView)view.findViewById(R.id.customer_comment_tab_lv);
        dialog_builder = new DialogBuilder(getContext());
        tv_nothing = (TextView) view.findViewById(R.id.customer_comment_tv_nothing);
    }

    private void initData(){
        if(page_index == 1)
            has_comment = true;
        else
            has_comment = false;
        comments = new ArrayList<>();
        comment_adapter = new CustomerCommentContentAdapter(getContext(), comments, has_comment);
    }

    private void updateComments(){
        comment_adapter.notifyDataSetChanged();
        lv_comments.setAdapter(comment_adapter);

        if (comments.size() > 0) tv_nothing.setVisibility(View.INVISIBLE);
        else {
            tv_nothing.setVisibility(View.VISIBLE);
            if (has_comment) tv_nothing.setText("目前沒有已評論的餐廳");
            else tv_nothing.setText("目前沒有未評論的餐廳");
        }
    }

    private String getUserId() {
        SharedPreferences user = getActivity().getSharedPreferences("USER", MODE_PRIVATE);
        return user.getString("userID", "");
    }

    class APIComments extends AsyncTask<String, Void, Void> {
        private ProgressDialog progress_dialog = new ProgressDialog(getContext());
        private String status = null;
        private SqlHandler sqlHandler;
        @Override
        protected void onPreExecute() {
            progress_dialog.setMessage( getResources().getString(R.string.login_wait) );
            progress_dialog.show();
            sqlHandler = new SqlHandler(getContext());
        }
        @Override
        protected Void doInBackground(String... params) {
            HttpClient httpClient = new DefaultHttpClient();
            try {
                HttpGet request = new HttpGet(getString(R.string.server_domain)+"api/user_records?user_id=" + params[0]
                        + "&verbose=1");
                request.addHeader("Content-Type", "application/json");
                JSONArray responseJSON = new JSONArray( new BasicResponseHandler().handleResponse( httpClient.execute(request) ) );

                status = responseJSON.getJSONObject(0).getString("status_code");
                if( status.equals("0") ) {
                    int size = responseJSON.getJSONObject(0).getInt("size");
                    for(int i = 1 ; i <= size ; i++) {
                        JSONObject jsonObject1 = responseJSON.getJSONObject(i);
                        boolean is_success = jsonObject1.getBoolean("is_succ");
                        boolean has_comment = jsonObject1.getBoolean("is_used");
                        if(is_success){
                            if(has_comment){
                                if(page_index == 0)
                                    continue;
                            }else{
                                if(page_index == 1)
                                    continue;
                            }
                        }else{
                            continue;
                        }
                        String record_id = jsonObject1.getString("record_id");
                        String time = jsonObject1.getString("created_at");
                        String address = jsonObject1.getString("shop_address");
                        String shop_id = jsonObject1.getString("shop_id");
                        String shop_name = jsonObject1.getString("shop_name");
                        CustomerCommentContentInfo comment = new CustomerCommentContentInfo(shop_name, shop_id, address, time, record_id);
                        comment.setImg(sqlHandler.getDetail(Integer.parseInt(shop_id)).getImage());
                        comments.add( comment );
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
            if( status == null  || !status.equals("0") )    dialog_builder.dialogEvent("資料載入失敗，請重試", "normal", null);
            else {
                Collections.reverse(comments);
                updateComments();
            }
            progress_dialog.dismiss();
        }
    }

}
