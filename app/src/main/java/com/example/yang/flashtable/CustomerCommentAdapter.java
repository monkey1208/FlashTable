package com.example.yang.flashtable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by CS on 2017/3/24.
 */

public class CustomerCommentAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Context context;
    List<String> comments;

    public CustomerCommentAdapter(Context _context, List<String> _comments) {
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
        }
        return convertView;
    }
}
