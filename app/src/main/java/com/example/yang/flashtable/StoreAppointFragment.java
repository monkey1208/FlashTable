package com.example.yang.flashtable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class StoreAppointFragment extends ListFragment {
    private List<RecordInfo> list = new ArrayList<>();
    private StoreAppointAdapter adapter;
    private String DateFormat = "EEE MMM dd HH:mm:ss yyyy";
    private List<Integer> updateList = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private Timer timer = new Timer();
    private Fragment userInfoFragment;
    private FragmentManager fragmentManager;

    public void startUpdate(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        },0,3000);
    }

    public  StoreAppointFragment () {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentManager = getFragmentManager();
        final View v = inflater.inflate(R.layout.store_appoint_fragment, container, false);
        List<RecordInfo> newList = new ArrayList<>();
        for(int i=0;i<list.size();i++)
            if(System.currentTimeMillis()-list.get(i).due_time < 85500000)
                newList.add(list.get(i));
        list.clear();
        list = newList;
        adapter = new StoreAppointAdapter(getContext(), list,getString(R.string.server_domain));
        setListAdapter(adapter);
        Toolbar bar = (Toolbar)v.findViewById(R.id.shop_toolbar);

        bar.setPadding(0,getStatusBarHeight(), 0,0);
        bar.inflateMenu(R.menu.shop_reservation_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.change_prev_fragment(FragmentController.APPOINT);
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_RECORD);
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        refresh();
        Log.d("AppointInit","done");
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Bundle bundle = new Bundle();
        bundle.putString("session_id",Integer.toString(list.get(position).id));
        bundle.putString("name",list.get(position).name);
        bundle.putString("number",Integer.toString(list.get(position).number));
        bundle.putInt("promotion_id",list.get(position).promotion_id);
        bundle.putInt("point", list.get(position).point);
        bundle.putLong("due_time",list.get(position).due_time);
        bundle.putString("picture_url", list.get(position).get_Image_Url());
        Log.e("picture", list.get(position).get_Image_Url());
        StoreMainActivity.fragmentController.sendBundle(bundle,FragmentController.CONFIRM);
    }


    public RecordInfo getItem(int position){
        if(list.size()>position)
            return list.get(position);
        else
            return null;
    }
    public int addItem(RecordInfo info){
        list.add(info);
        adapter.notifyDataSetChanged();
        return 0;
    }
    public int removeItem(List<Integer> delete){
        Collections.reverse(delete);
        for(int i=0;i<delete.size();i++)
            list.remove(delete.get(i).intValue());
        int size = list.size();
        if(size>9)
            size = 10;
        StoreMainActivity.appointUpdateNumber(size);
        adapter.notifyDataSetChanged();
        return 0;
    }
    public int getSize(){
        return list.size();
    }
    public void deleteSession(String session_id){
        for(int i=0;i<list.size();i++){
            if(Integer.toString(list.get(i).id).equals(session_id)) {
                list.remove(i);
                adapter.notifyDataSetChanged();
                break;
            }
        }
        return;
    }
    public void refresh(){
        new API_Refresh().execute(StoreMainActivity.storeInfo.id);
        return;
    }
    public class API_Refresh extends AsyncTask<String,Void,Void>{

        List<RecordInfo> infos = new ArrayList<>();
        private int total;
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet(getString(R.string.server_domain)+"api/shop_sessions?shop_id="+params[0]+"&verbose=1");
                JSONArray sessions = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                total = sessions.length()-1;
                for(int i=1;i<sessions.length();i++){
                    JSONObject session = sessions.getJSONObject(i);
                    int id = session.getInt("session_id");
                    String name = session.getString("user_account");
                    int number = session.getInt("number");
                    int promotion_id = session.getInt("promotion_id");
                    int point = session.getInt("user_point");
                    String url = session.getString("user_picture_url");
                    Date due_time = stringToDate(session.getString("due_time"),DateFormat);
                    if(due_time != null)
                        Log.d("Session",Long.toString(due_time.getTime()));
                    RecordInfo info = new RecordInfo(id,name,point, number,due_time.getTime(),promotion_id,url);
                    if(System.currentTimeMillis() - info.due_time < 85500000)
                        infos.add(info);
                    else
                        total--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            final List<Thread> threadList = new ArrayList<>();
            for(int i=0;i<infos.size();i++) {

                final RecordInfo info = infos.get(i);
                Bitmap image = null;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap mIcon = null;
                        try {
                            InputStream in = new java.net.URL(info.get_Image_Url()).openStream();
                            mIcon = BitmapFactory.decodeStream(in);
                        } catch (Exception e) {
                            Log.e("Error", e.getMessage());
                        }
                        info.picture = mIcon;
                        Log.e("picture", info.name + " get picture!");
                    }
                });
                threadList.add(t);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<threadList.size();i++)
                        threadList.get(i).start();
                    for(int i=0;i<threadList.size();i++) {
                        try {
                            threadList.get(i).join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    list.clear();
                    for(int i=0;i<infos.size();i++)
                        list.add(infos.get(i));
                    Log.d("Session","Refresh "+Integer.toString(list.size()));
                }
            }).start();
            if(total>9)
                total=10;
            StoreMainActivity.appointUpdateNumber(total);
        }
    }
    private Date stringToDate(String aDate, String aFormat) {
        if(aDate==null) return null;
        Log.d("Session",aDate);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat, Locale.ENGLISH);
        Date stringDate = null;
        try {
            stringDate = simpledateformat.parse(aDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("Session","ParseError");
        }
        return stringDate;
    }
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
