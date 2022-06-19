package com.example.drawer.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.drawer.R;
import com.example.drawer.entity.SheetInfo;
import com.example.drawer.ui.utils.binding.Bind;
import com.example.drawer.ui.utils.binding.ViewBinder;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * 歌单列表适配器
 */
public class SheetAdapter extends BaseAdapter {
    private static final int TYPE_PROFILE = 0;
    private static final int TYPE_MUSIC_LIST = 1;
    private Context mContext;
    private List<SheetInfo> mData;

    public SheetAdapter(List<SheetInfo> data) {
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
    public boolean isEnabled(int position) {
        return getItemViewType(position) == TYPE_MUSIC_LIST;
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getType().equals("#")) {
            return TYPE_PROFILE;
        } else {
            return TYPE_MUSIC_LIST;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        mContext = parent.getContext();
        ViewHolderProfile holderProfile;
        ViewHolderMusicList holderMusicList;
        SheetInfo sheetInfo = mData.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case TYPE_PROFILE:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_sheet_profile, parent, false);
                    holderProfile = new ViewHolderProfile(convertView);
                    convertView.setTag(holderProfile);
                } else {
                    holderProfile = (ViewHolderProfile) convertView.getTag();
                }
                holderProfile.tvProfile.setText(sheetInfo.getTitle());
                break;
            case TYPE_MUSIC_LIST:
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.view_holder_sheet, parent, false);
                    holderMusicList = new ViewHolderMusicList(convertView);
                    convertView.setTag(holderMusicList);
                } else {
                    holderMusicList = (ViewHolderMusicList) convertView.getTag();
                }
                getMusicListInfo(sheetInfo, holderMusicList);
                holderMusicList.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
                break;
        }
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    private void getMusicListInfo(final SheetInfo sheetInfo, final ViewHolderMusicList holderMusicList) {
        final List<String> list;
        if (sheetInfo.getCoverUrl() == null) {
            list=new ArrayList<>();
            holderMusicList.tvMusic1.setTag(sheetInfo.getTitle());
            holderMusicList.ivCover.setImageResource(R.drawable.default_cover);
            holderMusicList.tvMusic1.setText("1.加载中…");
            holderMusicList.tvMusic2.setText("2.加载中…");
            holderMusicList.tvMusic3.setText("3.加载中…");
            OkHttpUtils.get().url("https://musicapi.leanapp.cn/top/list?idx="+sheetInfo.getType()).build().execute(
                    new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                JSONObject palyList=jsonObject.getJSONObject("playlist");
                                String coverImage=palyList.getString("coverImgUrl");
                                JSONArray tracks=palyList.getJSONArray("tracks");
                                for (int i = 0; i <3 ; i++) {
                                    JSONObject track=new JSONObject(tracks.getString(i));
//                                String musicid=track.getString("id");
                                    String name=track.getString("name");
                                    JSONArray trackArray1 =track.getJSONArray("ar");
                                    JSONObject tracka1=new JSONObject(trackArray1.getString(0));
//                                String arid=tracka1.getString("id");
                                    String arname=tracka1.getString("name");
                                    JSONObject al=track.getJSONObject("al");
//                                String alid=al.getString("id");
//                                String picurl=al.getString("picUrl");
                                    list.add(name);
                                    list.add(arname);
                                }
//                                JSONObject tracka1=new JSONObject(trackArray1.getString(0));
////                                String arid=tracka1.getString("id");
//                                String arname=tracka1.getString("name");
//                                JSONObject al=track.getJSONObject("al");
////                                String alid=al.getString("id");
////                                String picurl=al.getString("picUrl");
//                                list.add(name);
//                                list.add(arname);
                                list.add(coverImage);
                                parse(list,sheetInfo);
                                setData(sheetInfo, holderMusicList);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onAfter(int id) {
                            super.onAfter(id);
                        }
                    });
        } else {
            holderMusicList.tvMusic1.setTag(null);
            setData(sheetInfo, holderMusicList);
        }
    }

    private void parse(List<String> response, SheetInfo sheetInfo) {

        sheetInfo.setCoverUrl(response.get(6));
            sheetInfo.setMusic1(mContext.getString(R.string.song_list_item_title_1,
                    response.get(0), response.get(1)));
            sheetInfo.setMusic2(mContext.getString(R.string.song_list_item_title_2,
                    response.get(2), response.get(3)));
            sheetInfo.setMusic3(mContext.getString(R.string.song_list_item_title_3,
                    response.get(4), response.get(5)));

    }

    private void setData(SheetInfo sheetInfo, ViewHolderMusicList holderMusicList) {
        holderMusicList.tvMusic1.setText(sheetInfo.getMusic1());
        holderMusicList.tvMusic2.setText(sheetInfo.getMusic2());
        holderMusicList.tvMusic3.setText(sheetInfo.getMusic3());
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.default_cover);
        requestOptions.fallback(R.drawable.default_cover);
        Glide.with(mContext)
                .load(sheetInfo.getCoverUrl())
                .apply(requestOptions)
                .into(holderMusicList.ivCover);
    }

    private static class ViewHolderProfile {
        @Bind(R.id.tv_profile)
        private TextView tvProfile;

        public ViewHolderProfile(View view) {
            ViewBinder.bind(this, view);
        }
    }

    private static class ViewHolderMusicList {
        @Bind(R.id.iv_cover)
        private ImageView ivCover;
        @Bind(R.id.tv_music_1)
        private TextView tvMusic1;
        @Bind(R.id.tv_music_2)
        private TextView tvMusic2;
        @Bind(R.id.tv_music_3)
        private TextView tvMusic3;
        @Bind(R.id.v_divider)
        private View vDivider;

        public ViewHolderMusicList(View view) {
            ViewBinder.bind(this, view);
        }
    }
}
