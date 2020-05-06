package com.example.newsapp;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragmentArrayList;
    private ArrayList<String> fragmentTitleList;


    public void addFragment(Fragment f, String t){
        fragmentArrayList.add(f);
        fragmentTitleList.add(t);
    }

    public PageAdapter(@NonNull FragmentManager fm) {
        super(fm);
        this.fragmentArrayList = new ArrayList<>();
        this.fragmentTitleList = new ArrayList<>();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleList.get(position);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentArrayList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentArrayList.size();
    }
}
