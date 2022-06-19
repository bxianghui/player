package com.example.drawer.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;


public class MyViewPagerAdapter extends FragmentStatePagerAdapter {
    List<String> titlelist;
    List<Fragment> mFragments;

    public MyViewPagerAdapter(@NonNull FragmentManager fm, List<Fragment> views, List<String> titles) {
        super(fm);
        mFragments = views;
        titlelist = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlelist.get(position);
    }
}
