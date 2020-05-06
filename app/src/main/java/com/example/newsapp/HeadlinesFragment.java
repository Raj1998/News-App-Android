package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class HeadlinesFragment extends Fragment {
    private static final String TAG = "HeadlinesFragment";
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
//    private PageAdapter mPageAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_headline, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }





    @Override
    public void onStart() {
        Log.d(TAG, "onstarted");
        super.onStart();

//        mPageAdapter = new PageAdapter(getActivity().getSupportFragmentManager());

        if ( mViewPager == null ) {
            mViewPager =  getActivity().findViewById(R.id.view_pager);
            mTabLayout =  getActivity().findViewById(R.id.tab_layout);

            setPager(mViewPager);


            mTabLayout.setupWithViewPager(mViewPager);

        }

    }

    private void setPager(ViewPager mViewPager){
        PageAdapter adapter = new PageAdapter(getChildFragmentManager());
        adapter.addFragment(new WorldFragment(), "WORLD");
        adapter.addFragment(new BusinessFragment(), "BUSINESS");
        adapter.addFragment(new PoliticsFragment(), "POLITICS");
        adapter.addFragment(new SportsFragment(), "SPORTS");
        adapter.addFragment(new TechnologyFragment(), "TECHNOLOGY");
        adapter.addFragment(new ScienceFragment(), "SCIENCE");

        mViewPager.setAdapter(adapter);
    }

}
