package com.example.drawer.ui.fragment;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.drawer.R;
import com.example.drawer.activity.MainActivity;
import com.example.drawer.entity.Music;
import com.example.drawer.ui.utils.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PlayingListFragment extends Fragment implements View.OnClickListener{
    private Handler handler=new Handler();
    private View mView;
    private List<Music> mMusicList;
    private int mPosition;
    private ListView mListView;
    private ImageView leave;
    private int mode;
    private MediaPlayer mMediaPlayer;
    private int flag;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.playing_music_list,container,false);
        mMediaPlayer= MainActivity.getmMediaPlayer();
        Bundle bundle=this.getArguments();
        if(bundle!=null)
        {
            mMusicList =(List<Music>)bundle.getSerializable("MusicList");
            mPosition =bundle.getInt("Position");
            mode=bundle.getInt("mode");
        }
        mListView =mView.findViewById(R.id.playing_music_listview);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                play(position);
//            }
//        });
        leave =mView.findViewById(R.id.leave);
        leave.setOnClickListener(this);
        return mView;
    }
        private void play(int position) {
            mPosition=position;
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mode ==0)
                    {
                        //单曲继续播放
                        play(mPosition);
                    }else if(mode==1)
                    {
                        //顺序播放
                        mPosition++;
                        if(mPosition>mMusicList.size()-1)
                        {
                            //如果播放到最后，重新播放
                            mPosition=0;
                        }
                        play(mPosition);
                    }else if(mode ==2)
                    {
                        play(new Random().nextInt(mMusicList.size()));
                    }
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
            Intent intent =new Intent();
            intent.putExtra("mode",mode);
            intent.putExtra("index",mPosition);
            intent.putExtra("music",(Serializable)mMusicList);
            intent.setAction("music_list");
            getActivity().sendBroadcast(intent);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.leave:
                onBackPressed();
                break;
        }
    }
    private void onBackPressed() {
//        if(mMediaPlayer.isPlaying()) flag =2;
//        else flag=1;
//        Intent intent =new Intent();
//        intent.setAction("music_list");
//        intent.putExtra("index", mPosition);
//        intent.putExtra("is_playing", flag);
//        intent.putExtra("music",(Serializable)mMusicList);
//        intent.putExtra("mode",mode);
//        getActivity().sendBroadcast(intent);
        getActivity().onBackPressed();
        leave.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                leave.setEnabled(true);
            }
        },300);
    }

}
