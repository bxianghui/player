package com.example.drawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.drawer.R;
import com.example.drawer.adapter.GridViewAdapter;
import com.example.drawer.entity.Radio;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class SongListActivity extends BaseActivity {

    private GridView mGridView;
    private List<Radio> mRadioList=new ArrayList<>();
    private GridViewAdapter mGridViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("歌单广场");
        setContentView(R.layout.allsonglist);
        backPress();
        initView();
        setData();
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(SongListActivity.this, OnLineSongListActivity.class);
                intent.putExtra("songlist",mRadioList.get(position));
                startActivity(intent);
            }
        });
    }

    private void setData() {
        settingGridView();
    }
    private void settingGridView() {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/top/playlist?limit=50&order=hot").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray=jsonObject.getJSONArray("playlists");
                    for (int i = 0; i <jsonArray.length() ; i++) {
                        JSONObject result=new JSONObject(jsonArray.getString(i));
                        String raidoid=result.getString("id");
                        String raidoname=result.getString("name");
                        String raidourl=result.getString("coverImgUrl");
                        String detital=result.getString("description");
                        String playCount=result.getString("playCount");
                        Radio radio=new Radio();
                        radio.setId(raidoid);
                        radio.setName(raidoname);
                        radio.setCoverUrl(raidourl);
                        radio.setDetital(detital);
                        radio.setPalyCount(playCount);
                        mRadioList.add(radio);
                    }
                    mGridViewAdapter=new GridViewAdapter(mRadioList);
                    mGridView.setAdapter(mGridViewAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void initView() {
        mGridView = findViewById(R.id.alllist);
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
