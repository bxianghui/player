package com.example.drawer.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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

public class CommendActivity extends BaseActivity {
    private ListView lvOnlineMusic;
    private List<Music> mMusicList = new ArrayList<>();
    private MediaPlayer mMediaPlayer;
    private int currentIndex;
    private int mode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_music);
        setTitle("最新音乐推荐");
        backPress();
        initView();
        mMediaPlayer=MainActivity.getmMediaPlayer();
        mode= PlayMode.getMode();
        getMsuicList(lvOnlineMusic);
        lvOnlineMusic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
    private void getMsuicList(final ListView lvOnline) {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/personalized/newsong").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
            @Override
            public void onResponse(String response, int id) {
                Log.i(OnlineMusicActivity.class.getSimpleName(),response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    JSONArray tracks=jsonObject.getJSONArray("result");
                    for (int i = 0; i <tracks.length() ; i++) {
                        JSONObject result=new JSONObject(tracks.getString(i));
                        String musicid=result.getString("id");
                        String musicname=result.getString("name");
                        String picurl=result.getString("picUrl");
                        JSONObject song = result.getJSONObject("song");
                        JSONArray trackArray1 =song.getJSONArray("artists");
                        JSONObject tracka1=new JSONObject(trackArray1.getString(0));
                        String arid=tracka1.getString("id");
                        String arname=tracka1.getString("name");
                        Music music=new Music();
                        music.setTitle(musicname);
                        music.setArtist(arname);
                        music.setPath("https://music.163.com/song/media/outer/url?id="+musicid+".mp3");
                        music.setCoverPath(picurl);
                        mMusicList.add(music);
                    }
                    OnlineMusicAdapter onlineMusicAdapter=new OnlineMusicAdapter(mMusicList);
                    lvOnline.setAdapter(onlineMusicAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private void initView() {
        lvOnlineMusic=findViewById(R.id.lv_online_music_list);
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
