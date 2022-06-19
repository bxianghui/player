package com.example.drawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.example.drawer.R;
import com.example.drawer.adapter.ArtistAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;

public class ArtistsActivity extends BaseActivity{

    private ListView mListView;
    private List<String>mStrings=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artists);
        setTitle("热门歌手");
        mListView = findViewById(R.id.artists);
        getData();
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(ArtistsActivity.this,ArtistActivity.class);
                intent.putExtra("artist",mStrings.get(position));
                startActivity(intent);
            }
        });

    }

    private void getData() {
        OkHttpUtils.get().url("http://musicapi.leanapp.cn/top/artists?offset=0&limit=30").build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject result =new JSONObject(response);
                    JSONArray artist=result.getJSONArray("artists");
                    for (int i = 0; i <artist.length() ; i++) {
                        JSONObject object=new JSONObject(artist.getString(i));
                        String name=object.getString("name");
                        String artistid=object.getString("id");
                        String s=name+":"+artistid;
                        mStrings.add(s);
                    }
                    ArtistAdapter artistAdapter=new ArtistAdapter(mStrings);
                    mListView.setAdapter(artistAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
