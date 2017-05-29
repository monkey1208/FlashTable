package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StoreManageRecordAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ReservationInfo> reservation_list = new ArrayList<>();
    private Context c;


    public StoreManageRecordAdapter(Context c, List<ReservationInfo> reservation_list) {
        this.c = c;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.reservation_list = reservation_list;
    }

    @Override
    public int getCount() {
        return reservation_list.size();
    }

    @Override
    public Object getItem(int position) {
        return reservation_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return reservation_list.indexOf((getItem(position)));
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        ReservationInfo info = reservation_list.get(position);
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.store_manage_record_row, parent, false);
            holder.tv_name = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_date);
            holder.tv_state = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_state);
            holder.tv_point = (TextView) convertView.findViewById(R.id.store_manage_record_row_tv_point);
            holder.iv_avatar = (ImageView) convertView.findViewById(R.id.store_manage_record_row_iv_icon);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.tv_name.setText(info.name);
        holder.tv_point.setText("（信譽" + reservation_list.get(position).point + "）");
        holder.tv_date.setText(info.record_time);
        if (info.is_succ.equals("true")) {
            holder.tv_state.setTextColor(Color.parseColor("#6DBD61"));
            holder.tv_state.setText("預約 ( " + info.number + " ) 人已到達");
        } else {
            holder.tv_state.setTextColor(Color.parseColor("#E41E1B"));
            holder.tv_state.setText("店家已取消預約 ( " + info.number + " ) 人桌位");
        }
        if (!info.get_Image_Url().equals("")) {
            new ImageDownloader(holder.iv_avatar).execute(info.get_Image_Url());
        }else{
            holder.iv_avatar.setImageResource(R.drawable.default_avatar);
        }

        return  convertView;

    }

    private class ViewHolder {
        TextView tv_name;
        TextView tv_date;
        TextView tv_state;
        TextView tv_point;
        ImageView iv_avatar;
    }


    private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        boolean connect_error = false;

        private ImageDownloader(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                connect_error = true;
                Log.e("Error", e.getMessage());
            }
            return mIcon;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
