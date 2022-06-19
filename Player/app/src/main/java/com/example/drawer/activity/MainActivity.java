package com.example.drawer.activity;
import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.drawer.adapter.MyViewPagerAdapter;
import com.example.drawer.R;
import com.example.drawer.application.MyApplication;
import com.example.drawer.entity.Music;
import com.example.drawer.entity.PlayMode;
import com.example.drawer.receiver.MusicListReceiver;
import com.example.drawer.service.QuitTimer;
import com.example.drawer.ui.fragment.FindFragment;
import com.example.drawer.ui.fragment.PlayFragment;
import com.example.drawer.ui.fragment.PlayingListFragment;
import com.example.drawer.ui.fragment.RecommendFragment;
import com.example.drawer.ui.fragment.LocalMusicFragment;
import com.example.drawer.utils.SystemUtils;
import com.example.drawer.utils.ToastUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends BaseActivity  implements View.OnClickListener,QuitTimer.OnTimerListener{

    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    private NavigationView mNavigationView;
    private Intent mIntent;
    private ViewPager mViewPager;
    private ArrayList<Fragment> mViews=new ArrayList<>();
    private ArrayList<String> titles=new ArrayList<>();
    private TabLayout mTablayout;
    private  static TextView tvPlayBarTitle;
    private static TextView tvPlayBarArtist;
    private  static ImageView ivPlayBarPlay;
    private ImageView ivPlayBarNext;
    private LinearLayout mLayout;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };
    private static String[] PERMISSIONS_LOCATION = {
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION" };
    private  boolean isPlayFragmentShow;
    private PlayFragment mPlayFragment;
    private  static List<Music> mMusicList=null;
    private int mPosition=-1;
    private static int mode=1;
    private static MediaPlayer mMediaPlayer;
    private MusicListReceiver mMusicListReceiver=new MusicListReceiver();
    private boolean playFlag=false;
    private PlayingListFragment mPlayingListFragment;
    private boolean isPlayingList;
    private RemoteViews mRemoteViews;
    private static ImageView playCover;
    private ImageView search;
    private MenuItem timerItem;
    private IntentFilter mIntentFilter;

    public static MediaPlayer getmMediaPlayer() {
        return mMediaPlayer;
    }
    public static TextView getTvPlayBarArtist() {
        return tvPlayBarArtist;
    }
    public static TextView getTvPlayBarTitle() {
        return tvPlayBarTitle;
    }
    public  static ImageView getIvPlayBarPlay() {
        return ivPlayBarPlay;
    }
    public static ImageView getPlayCover() {
        return playCover;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QuitTimer.get().init(this);
        QuitTimer.get().setOnTimerListener(this);
        ToastUtils.init(this);
        verifyStoragePermissions(MainActivity.this);
        SharedPreferences sharedPreferences=getSharedPreferences("music_play",MODE_PRIVATE);
        mode=sharedPreferences.getInt("mode",1);
        PlayMode.setMode(mode);
        initView();
        setDrawerSetting();
        setViewpagerSetting();
        setListener();
        registerReceiver();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();
    }

    private void registerReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("music_list");
        registerReceiver(mMusicListReceiver, mIntentFilter);



    }
    private void setViewpagerSetting() {
        mViews.add(new FindFragment());
        mViews.add(new RecommendFragment());
        mViews.add(new LocalMusicFragment());
        titles.add("发现");
        titles.add("推荐");
        titles.add("我的");
        mViewPager.setAdapter(new MyViewPagerAdapter
                (getSupportFragmentManager(),mViews,titles));
        mTablayout.setupWithViewPager(mViewPager);
    }


    private void initView() {
        search =findViewById(R.id.search);
        mToolbar = findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.nav_view);
        mViewPager = findViewById(R.id.viewpager_id);
        mTablayout = findViewById(R.id.tablayout_id);
        mLayout =findViewById(R.id.fl_play_bar);
        tvPlayBarTitle = findViewById(R.id.tv_play_bar_title);
        tvPlayBarArtist = findViewById(R.id.tv_play_bar_artist);
        ivPlayBarPlay = findViewById(R.id.iv_play_bar_play);
        ivPlayBarNext = findViewById(R.id.iv_play_bar_next);
        mMediaPlayer=new MediaPlayer();
        playCover =findViewById(R.id.iv_play_bar_cover);
    }
    private void setDrawerSetting() {
        setSupportActionBar(mToolbar);
        if(getSupportActionBar()!=null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, mDrawer, mToolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        toggle.syncState();
        mDrawer.addDrawerListener(toggle);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.nav_home:
                        mIntent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(mIntent);
                        break;
                    case R.id.nav_slideshow:
                       timerDialog();
                        break;
                    case R.id.nav_share:
                        MyApplication.getInstance().exit();
                        break;
                    case R.id.nav_tools:
                        mIntent =new Intent(MainActivity.this, ToolsActivity.class);
                        startActivity(mIntent);
                        break;
                }
                return false;
            }
        });
    }

    private void timerDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.menu_timer)
                .setItems(this.getResources().getStringArray(R.array.timer_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int[] times = getResources().getIntArray(R.array.timer_int);
                                startTimer(times[which]);
                            }
                        }
                ).show();
    }

    private void startTimer(int minute) {
        QuitTimer.get().start(minute * 60 * 1000);
        if (minute > 0) {
            ToastUtils.show(this.getString(R.string.timer_set, String.valueOf(minute)));
        } else {
            ToastUtils.show(R.string.timer_cancel);
        }
    }


    public static void verifyStoragePermissions(Activity activity) {

        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
            int permission1 = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.ACCESS_FINE_LOCATION");
            int location = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.ACCESS_COARSE_LOCATION");
            if (permission != PackageManager.PERMISSION_GRANTED || location != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_LOCATION,1);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setListener() {
        search.setOnClickListener(this);
        mLayout.setOnClickListener(this);
        ivPlayBarPlay.setOnClickListener(this);
        ivPlayBarNext.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        if(mMusicListReceiver.getMode()!=-1)
        {
            mode=mMusicListReceiver.getMode();
            PlayMode.setMode(mode);
        }
        mPosition=mMusicListReceiver.getPosition();
        PlayMode.setPosition(mPosition);
        mMusicList=mMusicListReceiver.getMusicList();
        PlayMode.setList(mMusicList);
        switch (v.getId())
        {
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
            case R.id.iv_play_bar_play:
                if(!TextUtils.equals(tvPlayBarTitle.getText(),"无音乐"))
                stopOrStartMediaplayer();
                break;
            case R.id.search:
                startSearch();
                break;
            case R.id.iv_play_bar_next:
                if(!TextUtils.equals(tvPlayBarTitle.getText(),"无音乐"))
                {

                    switch (mode)
                        {
                            case 0:
                                if (!mMediaPlayer.isPlaying()) {
                                    onChange();
                                } else {
                                    play(mPosition);
                                }
                                playFlag = !playFlag;
                                break;
                            case 1:
                                mPosition++;
                                if (mPosition > mMusicList.size() - 1) {
                                    mPosition = 0;
                                }
                                if (!mMediaPlayer.isPlaying()) {
                                    onChange();
                                } else {
                                    play(mPosition);
                                }
                                playFlag = !playFlag;
                                break;
                            case 2:

                                mPosition = new Random().nextInt(mMusicList.size());
                                if (!mMediaPlayer.isPlaying()) {
                                    onChange();
                                } else {
                                    play(mPosition);
                                }
                                playFlag = !playFlag;
                                break;

                    }
                    mMusicListReceiver.setPosition(mPosition);
                    PlayMode.setPosition(mPosition);
                }
                break;
        }
    }

    private void startSearch() {
        Intent intent=new Intent(MainActivity.this,SearchActivity.class);
        startActivity(intent);
    }

    private void onChange() {
        if(mMusicList.get(mPosition).getCoverPath()!=null&&mMusicList.get(mPosition).getCoverPath()!="")
        {
            Glide.with(this).load(mMusicList.get(mPosition).getCoverPath()).into(playCover);
        }
        tvPlayBarTitle.setText(mMusicList.get(mPosition).getTitle());
        tvPlayBarArtist.setText(mMusicList.get(mPosition).getArtist());
    }
    private void play(final int position) {
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(mode ==0)
                {
                    //单曲继续播放
                    play(mPosition);
                }else if(mode ==1)
                {
                    //顺序播放
                    mPosition++;
                    if(mPosition>mMusicList.size()-1)
                    {
                        //如果播放到最后，重新播放
                        mPosition=0;
                    }
                    play(mPosition);
                }else if(mode==2)
                {

                    mPosition=new Random().nextInt(mMusicList.size());
                    play(mPosition);
                }
                PlayMode.setPosition(mPosition);
            }
        });
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mMusicList.get(mPosition).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        onChange();
        PlayMode.setPosition(mPosition);
    }
    private void stopOrStartMediaplayer() {
        if(mMediaPlayer.isPlaying())
        {
            ivPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_play);
            mMediaPlayer.pause();
            PlayMode.setIsPlaying(false);
            playFlag=false;
        }
        else
        {
            PlayMode.setIsPlaying(true);
            ivPlayBarPlay.setImageResource(R.drawable.ic_play_bar_btn_pause);
            if(playFlag)
            {
                try {
                    mMediaPlayer.reset();
                    mMediaPlayer.setDataSource(mMusicList.get(mPosition).getPath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                mMediaPlayer.start();
            }

        }

    }
    private void showPlayingFragment() {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, R.anim.fragment_slide_down);
        mPlayFragment = new PlayFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("MusicList", (Serializable) mMusicList);
        bundle.putInt("Position", mPosition);
        bundle.putInt("mode",mode);
        mPlayFragment.setArguments(bundle);
        ft.replace(android.R.id.content, mPlayFragment);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }
    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.remove(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }
    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        mMediaPlayer.release();
        unregisterReceiver(mMusicListReceiver);
        super.onDestroy();
    }


    @Override
    public void onTimer(long remain) {
        if (timerItem == null) {
            timerItem = mNavigationView.getMenu().findItem(R.id.nav_slideshow);
        }
        String title = getString(R.string.menu_timer);
        timerItem.setTitle(remain == 0 ? title : SystemUtils.formatTime(title + "(mm:ss)", remain));
    }
}
