package com.example.drawer.ui.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.drawer.R;
import com.example.drawer.activity.OnlineMusicActivity;
import com.example.drawer.adapter.SheetAdapter;
import com.example.drawer.entity.SheetInfo;
import com.example.drawer.receiver.MusicListReceiver;

import java.util.ArrayList;
import java.util.List;

public class RecommendFragment extends Fragment {
    private ListView lvPlaylist;

    private List<SheetInfo> mSongLists;
    private SheetAdapter mAdapter;
    private MusicListReceiver mMusicListReceiver;


    public static RecommendFragment newInstance() {
        return new RecommendFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.s_fragment, container, false);
        lvPlaylist=view.findViewById(R.id.lv_sheet);
        mSongLists=new ArrayList<>();
        if (mSongLists.isEmpty()) {
            String[] titles = getResources().getStringArray(R.array.online_music_list_title);
            String[] types = getResources().getStringArray(R.array.online_music_list_type);
            for (int i = 0; i < titles.length; i++) {
                SheetInfo info = new SheetInfo();
                info.setTitle(titles[i]);
                info.setType(types[i]);
                mSongLists.add(info);
            }
        }
        mAdapter = new SheetAdapter(mSongLists);
        lvPlaylist.setAdapter(mAdapter);
        lvPlaylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SheetInfo sheetInfo = mSongLists.get(position);
                Intent intent = new Intent(getContext(), OnlineMusicActivity.class);
                mMusicListReceiver = new MusicListReceiver();
                int mode= mMusicListReceiver.getMode();
                intent.putExtra("music_list_type", sheetInfo);
                intent.putExtra("mode",mode);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the ViewModel

    }

}
