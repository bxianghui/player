package com.example.drawer.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.drawer.R;
import com.example.drawer.adapter.OnlineMusicAdapter;
import com.example.drawer.entity.Music;
import com.example.drawer.entity.PlayMode;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Call;

public class ArtistActivity extends BaseActivity {
    private ImageView mRadioCover;
    private TextView mRadioName;
    private TextView mRadioDetital;
    private ListView mSonglistListView;
    private String mString;
    private List<Music> mMusicList=new ArrayList<>();
    private int mode;
    private MediaPlayer mMediaPlayer;
    private int currentIndex;
    private String[] mStr;
    private String mArcover;
    private String mDes;
    private String mArname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("歌手详情");
        setContentView(R.layout.activity_online_songlist);
        backPress();
        mMediaPlayer=MainActivity.getmMediaPlayer();
        mode= PlayMode.getMode();
        Intent intent=getIntent();
        if(intent!=null)
        {
            mString=intent.getStringExtra("artist");
        }
        mStr = mString.split(":");
        initView();
        setData();
        mSonglistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(mMediaPlayer==null)
                {
                    mMediaPlayer=new MediaPlayer();
                }
                currentIndex =position;
                PlayMode.setPosition(currentIndex);
                play(position);
                Intent intent =new Intent();
                intent.putExtra("index",position);
                intent.putExtra("mode",mode);
                intent.putExtra("music",(Serializable) mMusicList);
                intent.setAction("music_list");
                sendBroadcast(intent);
            }
        });
    }
    private void play(int position) {

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mode ==0)
                {
                    //单曲继续播放
                    play(currentIndex);
                }else if(mode ==1)
                {
                    //顺序播放
                    currentIndex++;
                    if(currentIndex>mMusicList.size()-1)
                    {
                        //如果播放到最后，重新播放
                        currentIndex=0;
                    }
                    play(currentIndex);
                }else if(mode ==2)
                {
                    currentIndex=new Random().nextInt(mMusicList.size());
                    play(currentIndex);
                }
                PlayMode.setPosition(currentIndex);
            }
        });
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mMusicList.get(position).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayMode.setPosition(position);
        Intent intent = new Intent();
        intent.putExtra("index", position);
        intent.putExtra("mode",mode);
        intent.putExtra("music", (Serializable)mMusicList);
        intent.setAction("music_list");
        sendBroadcast(intent);
    }
    private void setData() {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/artists?id="+mStr[1]).build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject result=new JSONObject(response);
                    JSONObject artist=result.getJSONObject("artist");
                    mArname = artist.getString("name");
                    mDes = artist.getString("briefDesc");
                    mArcover = artist.getString("picUrl");

                    JSONArray songs=result.getJSONArray("hotSongs");
                    for (int i = 0; i <songs.length() ; i++) {
                        JSONObject object=new JSONObject(songs.getString(i));
                        String musicname =object.getString("name");
                        String musicId=object.getString("id");
                        JSONObject al=object.getJSONObject("al");
                        String cover =al.getString("picUrl");
                        Music music=new Music();
                        music.setTitle(musicname);
                        music.setArtist(mArname);
                        music.setPath("https://music.163.com/song/media/outer/url?id="+musicId+".mp3");
                        music.setCoverPath(cover);
                        mMusicList.add(music);
                    }
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.default_cover);
                    requestOptions.fallback(R.drawable.default_cover);
                    Glide.with(ArtistActivity.this)
                            .load(mArcover)
                            .apply(requestOptions)
                            .into(mRadioCover);
                    mRadioName.setText(mArname);
                    mRadioDetital.setText(mDes);
                    OnlineMusicAdapter onlineMusicAdapter=new OnlineMusicAdapter(mMusicList);
                    mSonglistListView.setAdapter(onlineMusicAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void initView() {
        mRadioCover = findViewById(R.id.radio_cover);
        mRadioName = findViewById(R.id.radio_name);
        mRadioDetital = findViewById(R.id.radio_detital);
        mSonglistListView = findViewById(R.id.songlist_listview);
    }

    private void backPress() {
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
