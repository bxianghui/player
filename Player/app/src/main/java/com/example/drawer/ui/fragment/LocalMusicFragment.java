package com.example.drawer.ui.fragment;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.drawer.R;
import com.example.drawer.activity.MainActivity;
import com.example.drawer.activity.MusicInfoActivity;
import com.example.drawer.adapter.LocalMusicAdapter;
import com.example.drawer.entity.Music;
import com.example.drawer.entity.PlayMode;
import com.example.drawer.receiver.MusicListReceiver;
import com.example.drawer.ui.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class LocalMusicFragment extends Fragment{

    private View mView;
    ListView mListView;
    ArrayList<Music> mMusicList;
    private MediaPlayer mMediaPlayer;
    private LocalMusicAdapter mLocalMusicAdapter;
    private int currentIndex=-1;
    private int mode;

    public static LocalMusicFragment newInstance() {
        return new LocalMusicFragment();
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mMediaPlayer= MainActivity.getmMediaPlayer();
        mView = inflater.inflate(R.layout.activity_loacl_music_list, container, false);
        Log.i(LocalMusicFragment.class.getSimpleName(),""+mode);
        initView();
        setAdapter();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mode= PlayMode.getMode();
                Log.i(LocalMusicFragment.class.getSimpleName(), "onItemClick: "+mode);
               if(mMediaPlayer==null)
               {
                   mMediaPlayer=new MediaPlayer();
               }
               currentIndex=position;
               play(position);
                Intent intent =new Intent();
                intent.putExtra("index",position);
                intent.putExtra("mode",mode);
                intent.putExtra("music",mMusicList);
                intent.setAction("music_list");
                getActivity().sendBroadcast(intent);
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Music music=mMusicList.get(position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                dialog.setTitle(music.getTitle());
                dialog.setItems(R.array.local_music_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:// 设为铃声
                                requestSetRingtone(music);
                                break;
                            case 1:// 查看歌曲信息
                                MusicInfoActivity.start(getContext(), music);
                                break;
                            case 2:// 删除
                                deleteMusic(music);
                                break;
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
        return mView;
    }

    private void play(int position) {

            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if(mode==0)
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
        Intent intent = new Intent();
        intent.putExtra("index", position);
        intent.putExtra("mode",mode);
        intent.putExtra("music", mMusicList);
        intent.setAction("music_list");
        getActivity().sendBroadcast(intent);
    }



    private void setAdapter() {
        mMusicList=new ArrayList<>();
        mMusicList= Utils.getmusic(getActivity());
        mLocalMusicAdapter = new LocalMusicAdapter(getActivity(),mMusicList);
        mLocalMusicAdapter.notifyDataSetChanged();
        mListView.setAdapter(mLocalMusicAdapter);
    }

    private void initView() {
        mListView =mView.findViewById(R.id.local_music_listview);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel
    }

    private void deleteMusic(final Music music)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            File file = new File(music.getPath());
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (file.delete()) {
                    // 刷新媒体库
                    Intent intent =
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                    getContext().sendBroadcast(intent);
                    mMusicList= Utils.getmusic(getActivity());
                    mLocalMusicAdapter = new LocalMusicAdapter(getActivity(),mMusicList);
                    mLocalMusicAdapter.notifyDataSetChanged();
                    mListView.setAdapter(mLocalMusicAdapter);
                }
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }
    private void requestSetRingtone(Music music)
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M&&!Settings.System.canWrite(getContext()))
        {
            Toast.makeText(getContext(),"没有权限，无法设置铃声，请授予权限",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + getContext().getPackageName()));
            startActivityForResult(intent, 0);
        }
        else {
            setRingtone(music);
        }
    }
    //设置铃声
    private void setRingtone(Music music) {
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(music.getPath());
        // 查询音乐文件在媒体库是否存在
        Cursor cursor = getContext().getContentResolver()
                .query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { music.getPath() }, null);
        if (cursor == null) {
            return;
        }
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.IS_MUSIC, true);
            values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
            values.put(MediaStore.Audio.Media.IS_ALARM, false);
            values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
            values.put(MediaStore.Audio.Media.IS_PODCAST, false);

            getContext().getContentResolver()
                    .update(uri, values, MediaStore.MediaColumns.DATA + "=?", new String[] { music.getPath() });
            Uri newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_RINGTONE, newUri);
            Toast.makeText(getContext(),"设置铃声成功",Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }


}
