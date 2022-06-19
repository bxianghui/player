package com.example.drawer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drawer.R;
import com.example.drawer.entity.Music;

import java.util.List;

public class LocalMusicAdapter extends BaseAdapter {

    private Context mContext;
    private List<Music> mMusicList;
    private LayoutInflater mLayoutInflater;
    private OnMoreListenner mOnMoreListenner;
    public LocalMusicAdapter(Context context, List<Music> list) {
        mContext=context;
        mMusicList=list;
        mLayoutInflater=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position) {
        return mMusicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHodler viewHodler;
        if(convertView==null)
        {
            //返回一个视图(优化 只获得一次
            convertView = mLayoutInflater.inflate(R.layout.music_item,null);
            //获得控件
            viewHodler=new ViewHodler();
            viewHodler.mMusicNameTextView=convertView.findViewById(R.id.music_name);
            viewHodler.mMusicArtistTextView =convertView.findViewById(R.id.music_artist);
            convertView.setTag(viewHodler);
        }
        else
        {
            viewHodler = (ViewHodler) convertView.getTag();
        }
        viewHodler.mMusicNameTextView.setText(mMusicList.get(position).getTitle());
        viewHodler.mMusicArtistTextView.setText(String.valueOf(mMusicList.get(position).getArtist()));
        return convertView;

    }

    private class ViewHodler {
        TextView mMusicNameTextView;
        TextView mMusicArtistTextView;
    }

    public void refresh(List<Music> list)
    {
        mMusicList=list;
        notifyDataSetChanged();
    }
}
