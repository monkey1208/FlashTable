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

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StoreAppointFragment extends ListFragment {
    public static List<ReservationInfo> appointList = new ArrayList<>();
    public static StoreAppointAdapter adapter;
    private List<Integer> updateList = new ArrayList<>();
    private List<Integer> deleteList = new ArrayList<>();
    private Timer timer;

    public  StoreAppointFragment () {
        // Required empty public constructor
    }
    public void startUpdate(){
        updateSession();
    }
    private void updateSession(){
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
               new APIgetSessions().execute(StoreMainActivity.storeInfo.id);
            }
        },0,10000);
    }
    public void stopTimer(){
        timer.cancel();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.store_appoint_fragment, container, false);
        //appointList = get_reservation_info();
        adapter = new StoreAppointAdapter(getContext(), appointList);
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
        return v;
    }

    private  List<ReservationInfo> get_reservation_info(){
        List<ReservationInfo> list = new ArrayList<>();
        ReservationInfo tmp = new ReservationInfo("Chen",10,System.currentTimeMillis()-1000);
        list.add(tmp);
        tmp = new ReservationInfo("Yi",100,System.currentTimeMillis()-2000);
        list.add(tmp);
        tmp = new ReservationInfo("Shan",1,System.currentTimeMillis()-3000);
        list.add(tmp);
        return list;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    private class APIgetSessions extends AsyncTask<String,Void,Void>{
        String result = "";
        @Override
        protected Void doInBackground(String... params) {
            try {
                updateList.clear();
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet get = new HttpGet("https://flash-table.herokuapp.com/api/shop_sessions?shop_id=" + params[0]);
                JSONArray jArray = new JSONArray(new BasicResponseHandler().handleResponse(httpClient.execute(get)));
                for(int i=1;i<jArray.length();i++){
                    int id = jArray.getJSONObject(i).getInt("session_id");
                    updateList.add(id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void _params) {
            deleteList.clear();
            Collections.sort(updateList, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o1-o2;
                }
            });
            result +="Server: ";
            for(int i=0;i<updateList.size();i++) {
                result += Integer.toString(updateList.get(i))+" ";
            }
            result+="\nLocal: ";
            for(int i=0;i<appointList.size();i++) {
                result += Integer.toString(appointList.get(i).id)+" ";
            }
            /*for(int i=0;i<appointList.size();i++){
                if(appointList.get(i).id!=-1) {
                    boolean stat = true;
                    for (int j = 0; j < updateList.size(); i++)
                        if(appointList.get(i).id==updateList.get(j))
                            stat = false;
                    if(stat)
                        deleteList.add(i);
                }
            }*/
            Collections.sort(updateList, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    return o2-o1;
                }
            });
            result+= "\nDelete: ";
            for(int i=0;i<deleteList.size();i++){
                result+=deleteList.get(i);
                appointList.remove(deleteList.get(i));
            }
            adapter.notifyDataSetChanged();
            Log.d("Session",result);
        }
    }
}
