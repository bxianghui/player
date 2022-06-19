package com.example.drawer.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.ActionMenuItemView;

import com.example.drawer.R;
import com.example.drawer.adapter.SearchAdapter;
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

public class SearchActivity extends BaseActivity {
    private EditText mEditText;
    private ImageView mSearch;
    private TextView mTextView;
    private ListView searchList;
    private List<String> searchContent;
    private MediaPlayer mMediaPlayer;
    private int currentIndex;
    private int mode;
    private List<Music> list=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mMediaPlayer=MainActivity.getmMediaPlayer();
        mode= PlayMode.getMode();
        initView();
        setListenner();
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=searchContent.get(position);
                String[] strings = s.split(":");
                if(strings[0].equals("song"))
                {
                    Music music=new Music();
                    music.setTitle(strings[2]);
                    music.setArtist(strings[3]);
                    music.setCoverPath(strings[4]);
                    music.setPath("https://music.163.com/song/media/outer/url?id="+strings[1]);
                    list.add(music);
                    if(mMediaPlayer==null)
                    {
                        mMediaPlayer=new MediaPlayer();
                    }
                    currentIndex =0;
                    play(0);
                    Intent intent =new Intent();
                    intent.putExtra("index",position);
                    intent.putExtra("mode",mode);
                    intent.putExtra("music",(Serializable) list);
                    intent.setAction("music_list");
                    sendBroadcast(intent);
                }
                else if(strings[0].equals("artist"))
                {
                    Intent intent=new Intent(SearchActivity.this,ArtistActivity.class);
                    intent.putExtra("artist",searchContent.get(position));
                    startActivity(intent);
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
                    if(currentIndex>list.size()-1)
                    {
                        //如果播放到最后，重新播放
                        currentIndex=0;
                    }
                    play(currentIndex);
                }else if(mode ==2)
                {
                    currentIndex=new Random().nextInt(list.size());
                    play(currentIndex);
                }
                PlayMode.setPosition(currentIndex);
            }
        });
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(list.get(position).getPath());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PlayMode.setPosition(position);
        Intent intent = new Intent();
        intent.putExtra("index", position);
        intent.putExtra("mode",mode);
        intent.putExtra("music", (Serializable)list);
        intent.setAction("music_list");
        sendBroadcast(intent);
    }

    private void setListenner() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
       mSearch.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               searchContent=new ArrayList<>();
               CharSequence s=mEditText.getText();
               final String str=s.toString();
               OkHttpUtils.get().url("http://musicapi.leanapp.cn/search/suggest?keywords="+str).build().execute(
                       new StringCallback() {
                   @Override
                   public void onError(Call call, Exception e, int id) {

                   }

                   @Override
                   public void onResponse(String response, int id) {
                       try {
                           JSONObject jsonObject = new JSONObject(response);
                           JSONObject result =jsonObject.getJSONObject("result");
                           JSONArray ar=result.getJSONArray("artists");
                           JSONArray song=null;
                           if(result.has("songs"))
                           {
                              song=result.getJSONArray("songs");
                               for (int i = 0; i <song.length() ; i++) {
                                   JSONObject object=new JSONObject(song.getString(i));
                                   String songid=object.getString("id");
                                   String songname=object.getString("name");
                                   JSONArray artists=null;
                                   String arname="";
                                   String img1v1Url="";
                                   artists = object.getJSONArray("artists");
                                   JSONObject jsonObject1 = new JSONObject(artists.getString(0));
                                   arname=jsonObject1.getString("name");
                                   img1v1Url=jsonObject1.getString("img1v1Url");
                                   
                                   searchContent.add("song:"+songid+":"+songname+":"+arname+":"+img1v1Url);
                               }
                           }
                           for (int i = 0; i <ar.length() ; i++) {
                                JSONObject object=new JSONObject(ar.getString(i));
                                String arid=object.getString("id");
                                String aranme=object.getString("name");
                                String pcurl="";
                                if(object.has("pcUrl"))
                                {
                                    pcurl=object.getString("pcUrl");
                                }
                                searchContent.add("artist:"+arid+":"+aranme+":"+pcurl);
                           }
                           if(searchContent==null)
                           {
                               mTextView.setVisibility(View.VISIBLE);
                               mTextView.setText("未找到相关结果");
                           }
                           else{
                               SearchAdapter mSearchAdapter = new SearchAdapter(searchContent);
                               mSearchAdapter.refresh(searchContent);
                               searchList.setAdapter(mSearchAdapter);
                               mSearchAdapter.notifyDataSetChanged();
                           }

                       } catch (JSONException e) {
                           e.printStackTrace();
                       }

                   }
               });
           }
       });
    }

    private void initView() {
        mEditText=findViewById(R.id.editText);
        mSearch=findViewById(R.id.search_music);
        mTextView=findViewById(R.id.failsearch);
        searchList=findViewById(R.id.lv_search_music_list);
    }

}
