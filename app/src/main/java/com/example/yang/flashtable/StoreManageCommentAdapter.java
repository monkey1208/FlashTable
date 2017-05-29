package com.example.yang.flashtable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import static com.example.yang.flashtable.R.id.store_manage_comment_iv_avatar;


public class StoreManageCommentAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Context context;
    private List<StoreCommentInfo> comments;

    private TextView tv_user, tv_content;
    private RatingBar rb_rating;
    private ImageView iv_avatar;

    public StoreManageCommentAdapter(Context _context, List<StoreCommentInfo> _comments) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        comments = _comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int index) {
        return comments.get(index);
    }

    @Override
    public long getItemId(int position) {
        return comments.indexOf(getItem(position));
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.store_manage_comment_row, parent, false);
            tv_user = (TextView) convertView.findViewById(R.id.store_manage_comment_tv_user);
            tv_content = (TextView) convertView.findViewById(R.id.store_manage_comment_tv_content);
            rb_rating = (RatingBar) convertView.findViewById(R.id.store_manage_comment_rb_rating);
            iv_avatar = (ImageView) convertView.findViewById(store_manage_comment_iv_avatar);
            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        StoreCommentInfo comment = comments.get(position);
        if (comment.user != null) {
            tv_user.setText(comment.user);
            tv_user.setBackgroundResource(android.R.color.transparent);
        }
        rb_rating.setRating(comment.rating);
        rb_rating.setIsIndicator(true);

        if (comment.content.equals("")) {
            ((ViewGroup) tv_content.getParent()).removeView(tv_content);
        } else {
            comment.content = comment.content.replace("%0D%0A", "\n");
            tv_content.setText(comment.content);
        }

        if (!comment.user_pic_url.equals("")){
            Log.e("comment", comment.user_pic_url);
            new ImageDownloader(iv_avatar).execute(comment.user_pic_url);
        } else{
            Log.e("comment", "default image");
            iv_avatar.setImageResource(R.drawable.default_avatar);
        }
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
