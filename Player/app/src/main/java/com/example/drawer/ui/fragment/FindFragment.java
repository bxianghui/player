package com.example.drawer.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drawer.R;
import com.example.drawer.activity.ArtistsActivity;
import com.example.drawer.activity.CommendActivity;
import com.example.drawer.activity.OnLineSongListActivity;
import com.example.drawer.activity.SongListActivity;
import com.example.drawer.adapter.GridViewAdapter;
import com.example.drawer.entity.Radio;
import com.example.drawer.ui.utils.RoundTransform;
import com.example.drawer.widget.MyGridView;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class FindFragment extends Fragment implements View.OnClickListener{

    private View mView;
    private Banner mBanner;
    private List<String> mTitleList;
    private List<String> mImages;
    private ImageView mSongList;
    private ImageView mRadio;
    private ImageView mCommend;
    private MyGridView mGridView;
    private GridViewAdapter mGridViewAdapter;
    private List<Radio> mRadios =new ArrayList<>();
    private Intent mIntent;
    private View mArtist;

    public static FindFragment newInstance() {
        return new FindFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.f_fragment, container, false);
        initImageData();
        initViews();
        settingBanner();
        setListenner();
        settingGridView();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(getContext(), OnLineSongListActivity.class);
                intent.putExtra("songlist",mRadios.get(position));
                startActivity(intent);
            }
        });
        return  mView;
    }

    private void settingGridView() {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/personalized?limit=6").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("result");
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject result=new JSONObject(jsonArray.getString(i));
                        String raidoid=result.getString("id");
                        String raidoname=result.getString("name");
                        String raidourl=result.getString("picUrl");
                        String detital=result.getString("copywriter");
                        String playCount=result.getString("playCount");
                        Radio radio=new Radio();
                        radio.setId(raidoid);
                        radio.setName(raidoname);
                        radio.setCoverUrl(raidourl);
                        radio.setDetital(detital);
                        radio.setPalyCount(playCount);
                        mRadios.add(radio);
                    }
                    mGridViewAdapter=new GridViewAdapter(mRadios);
                    mGridView.setAdapter(mGridViewAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setListenner() {
        mSongList.setOnClickListener(this);
        mCommend.setOnClickListener(this);
        mRadio.setOnClickListener(this);
        mArtist.setOnClickListener(this);
    }

    private void settingBanner() {
        //设置内置样式，共有六种可以点入方法内逐一体验使用。
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        //设置图片加载器
        mBanner.setImageLoader(new MyLoader());
        //设置图片网址或地址的集合
        mBanner.setImages(mImages);
        //设置的动画效果，内含多种特效，可点入方法内查找后内逐一体验
        mBanner.setBannerAnimation(Transformer.Default);
        //设置图的标题集合
        mBanner.setBannerTitles(mTitleList);
        //设置间隔时间
        mBanner.setDelayTime(3000);
        //设置手动影响 默认是手指触摸 轮播图不能翻页
        mBanner.setViewPagerIsScroll(true);
        //设置mBanner，小点点，左中右。
        mBanner.setIndicatorGravity(BannerConfig.RIGHT);
        //自动滚动
        mBanner.isAutoPlay(true);
        mBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mBanner.start();
    }

    private void initViews() {
        mBanner = mView.findViewById(R.id.banner);
        mSongList =mView.findViewById(R.id.songlist);
        mRadio =mView.findViewById(R.id.radio_image);
        mCommend =mView.findViewById(R.id.commend);
        mGridView =mView.findViewById(R.id.gridView);
        mArtist =mView.findViewById(R.id.artist);
    }

    private void initImageData() {
        mTitleList = new ArrayList<>();
        mImages =new ArrayList<>();
        mTitleList.add("1");
        mTitleList.add("2");
        mTitleList.add("3");
        mTitleList.add("4");
        mImages.add("https://p1.music.126.net/1GmOKIS-0D_jkZgk3kCrkA==/109951164938358545.jpg?imageView&quality=89");
        mImages.add("https://p1.music.126.net/OMiOXlsVb5rM965-P6MuYw==/109951164938387283.jpg?imageView&quality=89");
        mImages.add("https://p1.music.126.net/ZMG9hgPd03U9SbSRuSKzTg==/109951164940800673.jpg?imageView&quality=89");
        mImages.add("https://p1.music.126.net/t45l42EE9r5JsW7DsvmWBA==/109951164938347685.jpg?imageView&quality=89");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.songlist:
                mIntent = new Intent(getContext(), SongListActivity.class);
                startActivity(mIntent);
                break;
            case R.id.radio_image:
                break;
            case R.id.commend:
                mIntent = new Intent(getContext(), CommendActivity.class);
                startActivity(mIntent);
                break;
            case R.id.artist:
                mIntent = new Intent(getContext(), ArtistsActivity.class);
                startActivity(mIntent);
                break;
        }
    }

    private class MyLoader extends ImageLoader {
        @Override
        public void displayImage(Context context, Object path, ImageView imageView) {
            RequestOptions options = new RequestOptions().centerCrop() .transform(new RoundTransform(context,30));
            Glide.with(context).load((String)path).apply(options).into(imageView);
        }
    }
}
