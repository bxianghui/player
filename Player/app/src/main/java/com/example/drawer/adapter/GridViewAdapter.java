package com.example.drawer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drawer.R;
import com.example.drawer.entity.Radio;
import com.example.drawer.ui.utils.RoundTransform;

import java.util.List;


public class GridViewAdapter extends BaseAdapter {
    private List<Radio> mRadioList;

    public GridViewAdapter(List<Radio> radioList) {
        mRadioList = radioList;
    }

    @Override
    public int getCount() {
        return mRadioList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRadioList.get(position);
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
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_view_item, parent, false);
            holder.coverImg=convertView.findViewById(R.id.imageView2);
            holder.radioTitle=convertView.findViewById(R.id.radio_title);
            holder.playCount=convertView.findViewById(R.id.play_count_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Radio radio=mRadioList.get(position);
        RequestOptions options = new RequestOptions().centerCrop() .transform(new RoundTransform(parent.getContext(),15));
        Glide.with(convertView).load(radio.getCoverUrl()).apply(options).into(holder.coverImg);
        holder.radioTitle.setText(radio.getName());
        holder.playCount.setText(radio.getPalyCount());
        return convertView;
    }

    private class ViewHolder {
        ImageView coverImg;
        TextView playCount;
        TextView radioTitle;
    }
}
