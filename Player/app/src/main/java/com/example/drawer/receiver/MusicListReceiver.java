package com.example.drawer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.drawer.R;
import com.example.drawer.activity.MainActivity;
import com.example.drawer.entity.Music;

import java.util.ArrayList;


public class MusicListReceiver extends BroadcastReceiver {
    private ArrayList<Music> mMusicList;
    private int mPosition;
    private int mode;
    private int flag;
    private MediaPlayer mMediaPlayer=MainActivity.getmMediaPlayer();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null)
        {
            if(TextUtils.equals(intent.getAction(),"music_list"))
            {
                mPosition =intent.getIntExtra("index",-1);
                mMusicList=(ArrayList<Music>)intent.getSerializableExtra("music");
                mode=intent.getIntExtra("mode",-1);
                flag=intent.getIntExtra("is_playing",0);
                if(mMusicList!=null&& mPosition !=-1)
                {
                    MainActivity.getTvPlayBarTitle().setText(mMusicList.get(mPosition).getTitle());
                    MainActivity.getTvPlayBarArtist().setText(mMusicList.get(mPosition).getArtist());
                    MainActivity.getIvPlayBarPlay().setImageResource(R.drawable.ic_play_bar_btn_pause);
                    if(mMusicList.get(mPosition).getCoverPath()!=null&&mMusicList.get(mPosition).getCoverPath()!="")
                    {
                        Glide.with(context).load(mMusicList.get(mPosition).getCoverPath()).into(MainActivity.getPlayCover());
                    }
                    else
                    {
                        Glide.with(context).load(R.drawable.default_cover).into(MainActivity.getPlayCover());
                    }
                }
                if(flag!=0)
                {
                    if (flag==1)
                    {
                        MainActivity.getIvPlayBarPlay().setImageResource(R.drawable.ic_play_bar_btn_play);
                    }
                    else if(flag==2)
                    {
                        MainActivity.getIvPlayBarPlay().setImageResource(R.drawable.ic_play_bar_btn_pause);
                    }
                }

            }
        }
    }
    public ArrayList<Music> getMusicList() {
        return mMusicList;
    }

    public int getMode() {
        return mode;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        this.mPosition = position;
    }

}
