package com.example.drawer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.example.drawer.R;

import java.util.List;

public class ArtistAdapter extends BaseAdapter {
    private List<String> mData;
    public ArtistAdapter(List<String> list) {
        mData=list;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder=new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
            holder.tvTitle=convertView.findViewById(R.id.artist_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String s=mData.get(position);
        String []mStr = s.split(":");
        holder.tvTitle.setText(mStr[0]);
        return convertView;
    }
    private class ViewHolder {
        private TextView tvTitle;
    }
}
