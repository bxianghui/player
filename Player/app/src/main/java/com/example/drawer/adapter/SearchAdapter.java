package com.example.drawer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.drawer.R;

import java.util.List;

public class SearchAdapter extends BaseAdapter {
    private List<String> mData;

    public SearchAdapter(List<String> data) {
        mData = data;
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_music, parent, false);
            holder.tvTitle=convertView.findViewById(R.id.tv_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String s=mData.get(position);
        String []mStr = s.split(":");
        if(mStr[0].equals("song"))
        {
            String q=mStr[2]+"-"+mStr[3];
            holder.tvTitle.setText(q);
        }
        else if (mStr[0].equals("artist"))
        {
            String q=mStr[2];
            holder.tvTitle.setText(q);
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView tvTitle;
    }
    public void refresh(List<String> list)
    {
        mData=list;
        notifyDataSetChanged();
    }
}
