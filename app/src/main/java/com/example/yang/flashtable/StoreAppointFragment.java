package com.example.yang.flashtable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class StoreAppointFragment extends ListFragment {
    private List<ReservationInfo> list = new ArrayList<>();
    private StoreAppointAdapter adapter;
    private String DateFormat = "EEE MMM dd HH:mm:ss yyyy";
    private List<Integer> updateList = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private Timer timer;

    public  StoreAppointFragment () {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_appoint_fragment, container, false);
        adapter = new StoreAppointAdapter(getContext(), list);
        setListAdapter(adapter);
        Toolbar bar = (Toolbar)v.findViewById(R.id.shop_toolbar);
        bar.setPadding(0,getStatusBarHeight(), 0,0);
        bar.inflateMenu(R.menu.shop_reservation_menu);
        Toolbar.OnMenuItemClickListener onMenuItemClick = new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                StoreMainActivity.fragmentController.act(FragmentController.MANAGE_RECORD);
                return true;
            }
        };
        bar.setOnMenuItemClickListener(onMenuItemClick);
        refresh();
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Toast.makeText(getContext(),Integer.toString(position),Toast.LENGTH_LONG).show();
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public ReservationInfo getItem(int position){
        if(list.size()>position)
            return list.get(position);
        else
            return null;
    }
    public int addItem(ReservationInfo info){
        list.add(info);
        adapter.notifyDataSetChanged();
        return 0;
    }
    public int removeItem(List<Integer> delete){
        Collections.reverse(delete);
        for(int i=0;i<delete.size();i++)
            list.remove(delete.get(i).intValue());

        adapter.notifyDataSetChanged();
        return 0;
    }
    public int getSize(){
        return list.size();
    }
    public void deleteSession(String session_id){
        for(int i=0;i<list.size();i++){
            if(Integer.toString(list.get(i).id).equals(session_id)) {
                Log.d("Session",session_id);
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
        List<ReservationInfo> infos = new ArrayList<>();
        @Override
        protected Void doInBackground(String... params) {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_sessions?shop_id="+params[0]+"&verbose=1");
                JSONArray sessions = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                for(int i=1;i<sessions.length();i++){
                    JSONObject session = sessions.getJSONObject(i);
                    int id = session.getInt("session_id");
                    String name = session.getString("user_account");
                    int number = session.getInt("number");
                    Date due_time = stringToDate(session.getString("due_time"),DateFormat);
                    if(due_time != null)
                        Log.d("Session",Long.toString(due_time.getTime()));
                    ReservationInfo info = new ReservationInfo(id,name,number,due_time.getTime());
                    infos.add(info);
                }
                Log.d("Session","Refresh "+Integer.toString(sessions.length()));
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
            list.clear();
            for(int i=0;i<infos.size();i++)
                list.add(infos.get(i));
            Log.d("Session","Refresh "+Integer.toString(list.size()));
            adapter.notifyDataSetChanged();
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
}
