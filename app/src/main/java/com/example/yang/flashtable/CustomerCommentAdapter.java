package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

/**
 * Created by CS on 2017/3/24.
 */

public class CustomerCommentAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    List<CustomerCommentInfo> comments;

    TextView tv_user, tv_shop, tv_content;
    RatingBar rb_rating;
    ImageView iv_avatar;

    public CustomerCommentAdapter(Context _context, List<CustomerCommentInfo> _comments) {
        inflater = LayoutInflater.from(_context);
        context = _context;
        comments = _comments;
    }

    @Override
    public int getCount() { return comments.size(); }

    @Override
    public Object getItem(int index) { return comments.get(index); }

    @Override
    public long getItemId(int position) { return comments.indexOf(getItem(position)); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.customer_comment_item, parent, false);
            tv_shop = (TextView) convertView.findViewById(R.id.customer_comment_tv_shop);
            tv_user = (TextView) convertView.findViewById(R.id.customer_comment_tv_user);
            tv_content = (TextView) convertView.findViewById(R.id.customer_comment_tv_content);
            rb_rating = (RatingBar) convertView.findViewById(R.id.customer_comment_rb_rating);
            iv_avatar = (ImageView) convertView.findViewById(R.id.customer_comment_iv_avatar);
            setView(position);
        }
        return convertView;
    }

    private void setView(int position) {
        CustomerCommentInfo comment = comments.get(position);
        if (comment.shop != null) {
            tv_shop.setText(comment.shop);
            tv_shop.setBackgroundResource(android.R.color.transparent);
        }
        if (comment.user != null) {
            tv_user.setText(comment.user);
            tv_user.setBackgroundResource(android.R.color.transparent);
        }
        rb_rating.setRating(comment.rating);
        rb_rating.setIsIndicator(true);

        if (comment.content.equals("")) {
            ((ViewGroup) tv_content.getParent()).removeView(tv_content);
        } else {
            tv_content.setText(comment.content);
        }
    }
}
