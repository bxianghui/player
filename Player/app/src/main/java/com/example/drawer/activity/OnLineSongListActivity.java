package com.example.drawer.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.example.drawer.entity.Radio;
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

public class OnLineSongListActivity extends BaseActivity {

    private ImageView mRadioCover;
    private TextView mRadioName;
    private TextView mRadioDetital;
    private ListView mSonglistListView;
    private Radio mRadio;
    private List<Music> mMusicList=new ArrayList<>();
    private int mode;
    private MediaPlayer mMediaPlayer;
    private int currentIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_songlist);
        setTitle("歌单");
        mMediaPlayer=MainActivity.getmMediaPlayer();
        if(getIntent()!=null)
        {
            Intent intent=getIntent();
            mRadio=(Radio) intent.getSerializableExtra("songlist");
        }
        backPress();
        initView();
        setData();
        mode= PlayMode.getMode();
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
        RequestOptions requestOptions=new RequestOptions();
        requestOptions.placeholder(R.drawable.default_cover);
        requestOptions.fallback(R.drawable.default_cover);
        Glide.with(this)
                .load(mRadio.getCoverUrl())
                .apply(requestOptions)
                .into(mRadioCover);
        mRadioName.setText(mRadio.getName());
        mRadioDetital.setText(mRadio.getDetital());
        getMusic();
    }

    private void getMusic() {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/playlist/detail?id="+mRadio.getId())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject palyList=jsonObject.getJSONObject("playlist");
                    JSONArray tracks=palyList.getJSONArray("tracks");
                    for (int i = 0; i <tracks.length() ; i++) {
                        JSONObject track=new JSONObject(tracks.getString(i));
                        String musicid=track.getString("id");
                        String musicname=track.getString("name");
                        JSONArray trackArray1 =track.getJSONArray("ar");
                        JSONObject tracka1=new JSONObject(trackArray1.getString(0));
                        String arid=tracka1.getString("id");
                        String arname=tracka1.getString("name");
                        JSONObject al=track.getJSONObject("al");
                        String picurl=al.getString("picUrl");
                        Music music=new Music();
                        music.setTitle(musicname);
                        music.setArtist(arname);
                        music.setPath("https://music.163.com/song/media/outer/url?id="+musicid+".mp3");
                        music.setCoverPath(picurl);
                        mMusicList.add(music);
                    }
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
