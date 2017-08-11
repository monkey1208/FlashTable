package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.yang.flashtable.R.id.store_manage_comment_iv_avatar;


public class StoreManageCommentAdapter extends BaseAdapter{
    private LayoutInflater inflater;
    private Context context;
    private List<StoreCommentInfo> comments;

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
        final ViewHolder holder;
        if(convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.store_manage_comment_row, parent, false);
            holder.tv_user = (TextView) convertView.findViewById(R.id.store_manage_comment_tv_user);
            holder.tv_content = (TextView) convertView.findViewById(R.id.store_manage_comment_tv_content);
            holder.tv_time = (TextView) convertView.findViewById(R.id.store_manage_comment_tv_time);
            holder.rb_rating = (RatingBar) convertView.findViewById(R.id.store_manage_comment_rb_rating);
            holder.iv_avatar = (ImageView) convertView.findViewById(store_manage_comment_iv_avatar);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        setView(position, holder);
        return convertView;
    }

    private void setView(int position, ViewHolder holder) {
        StoreCommentInfo comment = comments.get(position);
        if (comment.user != null) {
            holder.tv_user.setText(comment.user);
            holder.tv_user.setBackgroundResource(android.R.color.transparent);
        }
        holder.rb_rating.setRating(comment.rating);
        holder.rb_rating.setIsIndicator(true);
        if(comment.created_time != null && !comment.created_time.equals("")){
            holder.tv_time.setText(comment.created_time);
        }else{
            holder.tv_time.setText("");
        }

        if (comment.content == null || comment.content.equals("")) {
            holder.tv_content.setVisibility(View.GONE);
        } else {
            comment.content = comment.content.replace("%0D%0A", "\n");
            holder.tv_content.setText(comment.content);
        }

        if (!comment.user_pic_url.equals("")){
            Picasso.with(context).load(comment.user_pic_url).into(holder.iv_avatar);
        } else{
            holder.iv_avatar.setImageResource(R.drawable.default_avatar);
        }
    }

    private class ViewHolder{
        TextView tv_user;
        TextView tv_content;
        TextView tv_time;
        RatingBar rb_rating;
        ImageView iv_avatar;
    }



}
