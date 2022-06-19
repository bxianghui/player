package com.example.drawer.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.drawer.R;
import com.example.drawer.entity.Music;

public class MusicInfoActivity extends BaseActivity {

    private TextView title;
    private TextView artist;
    private TextView album;
    private TextView size;
    private TextView path;
    private Music mMusic;
    public static void start(Context context, Music music) {
        Intent intent=new Intent(context,MusicInfoActivity.class);
        intent.putExtra("music",music);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null)
        {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initView();
        setText();
    }

    private void setText() {
        mMusic=(Music)getIntent().getSerializableExtra("music");
        title.setText(mMusic.getTitle());
        artist.setText(mMusic.getArtist());
        album.setText(mMusic.getAlbum());
        size.setText(String.valueOf(mMusic.getFileSize()));
        path.setText(mMusic.getPath());
    }

    private void initView() {
        title =findViewById(R.id.label_music_info_title);
        artist =findViewById(R.id.label_music_info_artist);
        album =findViewById(R.id.label_music_info_album);
        size =findViewById(R.id.label_music_info_file_size);
        path =findViewById(R.id.label_music_info_file_path);
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
