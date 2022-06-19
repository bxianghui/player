package com.example.drawer.ui.fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.drawer.R;
import com.example.drawer.activity.MainActivity;
import com.example.drawer.adapter.PlayPagerAdapter;
import com.example.drawer.entity.Music;
import com.example.drawer.entity.PlayMode;

import com.example.drawer.lrcView.LyricView;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class PlayFragment extends BaseFragment implements View.OnClickListener,SeekBar.OnSeekBarChangeListener {
    private ImageView ivBack;
    private View mView;
    private TextView ivTitile;
    private TextView ivArtist;
    private TextView totalTime;
    private ImageView playMode;
    private List<Music> mMusicList;
    private int mPosition=-1;
    private ImageView ivPlay;
    private int mode;
    private ImageView playPre;
    private ImageView playNext;
    private MediaPlayer mMediaPlayer;
    private boolean playFlag;
    private int flag=0;
    private SeekBar mSeekBar;
    private TextView currentTime;
    private ViewPager mViewPager;
    private SeekBar sbVolume;
    private List<View> mViewList=new ArrayList<>();
    private AudioManager mAudioManager;
    private int index = 0;			//歌词检索值
    private LyricView mLrcView;
    private int INTERVAL=45;

    private TestHandler handler = new TestHandler(PlayFragment.this);

    private static String formatime(int lengrh) {
        Date date = new Date(lengrh);
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
        String total = sdf.format(date);
        return total;
    }

    public TextView getCurrentTime() {
        return currentTime;
    }

    public SeekBar getSeekBar() {
        return mSeekBar;
    }

    public LyricView getLrcView() {
        return mLrcView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_play,container,false);
        mMediaPlayer=MainActivity.getmMediaPlayer();

        initView();
        initViewPager();
        if(mMediaPlayer.isPlaying()==false)
        {
            mSeekBar.setProgress(0);
            currentTime.setText("00:00");
        }

        Bundle bundle=this.getArguments();
        if(bundle!=null)
        {
            mMusicList=(List<Music>)bundle.getSerializable("MusicList");
            mPosition =bundle.getInt("Position");
            mode =bundle.getInt("mode");
        }
//        searchLrc();
        setText();
       if(mMediaPlayer.isPlaying()==true)
        {
            mSeekBar.setProgress(mMediaPlayer.getCurrentPosition());
            currentTime.setText(formatime(mMediaPlayer.getCurrentPosition()));

        Thread thread;
        thread = new Thread(new MuiscThread());
        thread.start();
        setOnclick();
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)
                {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        }
        return mView;
    }



    class runable implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {

                try {
                    Thread.sleep(100);
                    if (mMediaPlayer.isPlaying()) {
                        mLrcView.setOffsetY(mLrcView.getOffsetY() - mLrcView.SpeedLrc());
                        mLrcView.SelectIndex(mMediaPlayer.getCurrentPosition());
                        mHandler.post(mUpdateResults);
                    }
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    Handler mHandler = new Handler();
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            mLrcView.invalidate(); // 更新视图
        }
    };


    private void setOnclick() {
        playMode.setOnClickListener(this);
        playPre.setOnClickListener(this);
        playNext.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
    }
    private void initViewPager()
    {
        View lrcView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_lrc, null);
        sbVolume=lrcView.findViewById(R.id.sb_volume);
        mLrcView=lrcView.findViewById(R.id.lrcShowView);
        initVolume();
        mViewList.add(lrcView);
        mViewPager.setAdapter(new PlayPagerAdapter(mViewList));
    }
    private void initVolume() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }


    private void setText() {
        if(mMusicList!=null&&mPosition!=-1)
        {
            ivTitile.setText(mMusicList.get(mPosition).getTitle());
            ivArtist.setText(mMusicList.get(mPosition).getArtist());
            totalTime.setText(formatime(mMediaPlayer.getDuration()));
            if(!mMediaPlayer.isPlaying())
            {
                ivPlay.setImageResource(R.drawable.ic_play_btn_play_pressed);
            }else
            {
                ivPlay.setImageResource(R.drawable.ic_play_btn_pause_pressed);
            }
            if(mode==0)
            {
                playMode.setImageResource(R.drawable.ic_play_btn_one_pressed);
            }
            else if(mode==1)
            {
                playMode.setImageResource(R.drawable.ic_play_btn_loop);
            }
            else
            {
                playMode.setImageResource(R.drawable.ic_play_btn_shuffle);
            }
            mSeekBar.setMax(mMediaPlayer.getDuration());
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();
    }

    private void initView() {
        ivBack=mView.findViewById(R.id.iv_back);
        ivTitile =mView.findViewById(R.id.tv_title);
        ivArtist =mView.findViewById(R.id.tv_artist);
        totalTime =mView.findViewById(R.id.tv_total_time);
        playMode=mView.findViewById(R.id.iv_mode);
        ivPlay =mView.findViewById(R.id.iv_play);
        playMode=mView.findViewById(R.id.iv_mode);
        playPre =mView.findViewById(R.id.iv_prev);
        playNext =mView.findViewById(R.id.iv_next);
        mSeekBar =mView.findViewById(R.id.sb_progress);
        currentTime =mView.findViewById(R.id.tv_current_time);
        mViewPager =mView.findViewById(R.id.vp_play_page);

    }

    @Override
    protected void setListener() {
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_play:
                if(mMusicList!=null&&mPosition!=-1)
                startOrStop();
                break;
            case R.id.iv_prev:
                if(mMusicList!=null&&mPosition!=-1)
                playPremusic();
                Intent intent = new Intent();
                intent.setAction("music_list");
                intent.putExtra("index", mPosition);
                if(mMediaPlayer.isPlaying())flag=2;
                else flag=1;
                intent.putExtra("is_playing",flag);
                intent.putExtra("music",(Serializable)mMusicList);
                intent.putExtra("mode",mode);
                getActivity().sendBroadcast(intent);
                break;
            case R.id.iv_next:
                if(mMusicList!=null&&mPosition!=-1)
                playNextmusic();
                Intent intent1 =new Intent();
                intent1.setAction("music_list");
                intent1.putExtra("index", mPosition);
                if(mMediaPlayer.isPlaying())flag=2;
                else flag=1;
                intent1.putExtra("is_playing",flag);
                intent1.putExtra("music",(Serializable)mMusicList);
                intent1.putExtra("mode",mode);
                getActivity().sendBroadcast(intent1);
                break;
            case R.id.iv_mode:
                if(mMusicList!=null&&mPosition!=-1)
                changeMode();
                break;
        }
    }
    private void play(int position) {
        mPosition=position;
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mSeekBar.setProgress(0);
                currentTime.setText("00:00");
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

                PlayMode.setPosition(mPosition);
            }
        });
        new Thread(new runable()).start();
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mMusicList.get(position).getPath());
//            searchLrc();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayMode.setPosition(mPosition);
        onChange();
        Intent intent =new Intent();
        intent.putExtra("mode",mode);
        intent.putExtra("index",mPosition);
        intent.putExtra("music",(Serializable)mMusicList);
        intent.setAction("music_list");
        getActivity().sendBroadcast(intent);
    }

    private void onChange() {
        ivTitile.setText(mMusicList.get(mPosition).getTitle());
        ivArtist.setText(mMusicList.get(mPosition).getArtist());
        totalTime.setText(formatime(mMediaPlayer.getDuration()));
        mSeekBar.setMax(mMediaPlayer.getDuration());
    }

    private void startOrStop() {
        if(mMediaPlayer.isPlaying())
        {
            ivPlay.setImageResource(R.drawable.ic_play_btn_play_pressed);
            mMediaPlayer.pause();
            PlayMode.setIsPlaying(false);
            playFlag=false;
            flag=1;
        }
        else
        {
            PlayMode.setIsPlaying(true);
            ivPlay.setImageResource(R.drawable.ic_play_btn_pause_pressed);
            if(playFlag)
            {
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mMusicList.get(mPosition).getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
//                    searchLrc();
                    mLrcView.setOffsetY(220 - mLrcView.SelectIndex(mMediaPlayer.getCurrentPosition())
                            * (mLrcView.getSIZEWORD() + INTERVAL-1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                mMediaPlayer.start();
                mLrcView.setOffsetY(220 - mLrcView.SelectIndex(mMediaPlayer.getCurrentPosition())
                        * (mLrcView.getSIZEWORD() + INTERVAL-1));
            }
            flag=2;
        }
        Intent intent = new Intent();
        intent.putExtra("index", mPosition);
        intent.putExtra("music",(Serializable) mMusicList);
        intent.putExtra("is_playing",flag);
        intent.putExtra("mode",mode);
        intent.setAction("music_list");
        getActivity().sendBroadcast(intent);
    }

    private void playNextmusic() {
        Log.i(PlayFragment.class.getSimpleName(), PlayMode.getPosition()+"");
        mSeekBar.setProgress(0);
        currentTime.setText("00:00");
        if(mode==0)
        {

            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        else if(mode==1)
        {
            mPosition++;
            if(mPosition>mMusicList.size()-1)
            {
                mPosition=0;
            }
            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        else
        {
            mPosition=new Random().nextInt(mMusicList.size());
            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        PlayMode.setPosition(mPosition);
    }

    private void playPremusic() {
        mSeekBar.setProgress(0);
        currentTime.setText("00:00");
        if(mode==0)
        {
            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        else if(mode==1)
        {

            mPosition--;
            if(mPosition<0)
            {
                mPosition=mMusicList.size()-1;
            }
            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        else
        {
            mPosition = new Random().nextInt(mMusicList.size());
            if (!mMediaPlayer.isPlaying()) {
                onChange();
            } else {
                play(mPosition);
            }
            playFlag = !playFlag;
        }
        PlayMode.setPosition(mPosition);
    }

    private void changeMode() {
        if(mode==0)
        {
            playMode.setImageResource(R.drawable.ic_play_btn_loop);
            mode=1;
            PlayMode.setMode(mode);
        }
        else if(mode==1)
        {
            playMode.setImageResource(R.drawable.ic_play_btn_shuffle);
            mode=2;
            PlayMode.setMode(mode);
        }
        else
        {
            playMode.setImageResource(R.drawable.ic_play_btn_one_pressed);
            mode=0;
            PlayMode.setMode(mode);
        }
        Intent intent =new Intent();
        intent.setAction("music_list");
        intent.putExtra("index", mPosition);
        intent.putExtra("is_playing",flag);
        intent.putExtra("music",(Serializable)mMusicList);
        intent.putExtra("mode",mode);
        getActivity().sendBroadcast(intent);
    }

    private void onBackPressed() {

        getActivity().onBackPressed();
        ivBack.setEnabled(false);
       handler.postDelayed(new Runnable() {
           @Override
           public void run() {
            ivBack.setEnabled(true);
           }
       },300);
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    private class MuiscThread implements Runnable{
        @Override
        public void run() {
            //判断音乐的状态，在不停止与不暂停的情况下向总线程发出信息
            while (mMediaPlayer != null) {
                try {
                    // 每1000毫秒更新一次位置
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //发出的信息
                handler.sendEmptyMessage(mMediaPlayer.getCurrentPosition());
            }

        }
        }
        public static class TestHandler extends Handler{
         final WeakReference<PlayFragment> mPlayFragmentWeakReference;
         TestHandler(PlayFragment fragment)
        {
            mPlayFragmentWeakReference = new WeakReference<>(fragment);
        }
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                PlayFragment playFragment=mPlayFragmentWeakReference.get();
                // 将SeekBar位置设置到当前播放位置
                playFragment.getSeekBar().setProgress(msg.what);
                playFragment.getLrcView().setOffsetY(220 -playFragment.getLrcView().SelectIndex(msg.what)
                        * (playFragment.getLrcView().getSIZEWORD() + 45-1));
                //获得音乐的当前播放时间
                playFragment.getCurrentTime().setText(formatime(msg.what));
            }
        }
}


