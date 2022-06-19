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
import com.example.drawer.entity.Music;
import com.example.drawer.ui.utils.binding.Bind;
import com.example.drawer.ui.utils.binding.ViewBinder;

import java.util.List;


/**
 * 在线音乐列表适配器
 */
public class OnlineMusicAdapter extends BaseAdapter {
    private List<Music> mData;

    public OnlineMusicAdapter(List<Music> data) {
        this.mData = data;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Music onlineMusic = mData.get(position);
//        RequestOptions requestOptions=new RequestOptions();
//        requestOptions.placeholder(R.drawable.default_cover);
//        requestOptions.fallback(R.drawable.default_cover);
//        Glide.with(parent.getContext())
//                .load(onlineMusic.getCoverPath())
//                .apply(requestOptions)
//                .into(holder.ivCover);
        holder.tvTitle.setText(onlineMusic.getTitle());
        holder.tvArtist.setText(onlineMusic.getArtist());
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    private static class ViewHolder {
//        @Bind(R.id.iv_cover)
//        private ImageView ivCover;
        @Bind(R.id.tv_title)
        private TextView tvTitle;
        @Bind(R.id.tv_artist)
        private TextView tvArtist;
        @Bind(R.id.v_divider)
        private View vDivider;

        public ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
