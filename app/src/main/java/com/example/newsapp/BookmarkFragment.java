package com.example.newsapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarkFragment extends Fragment {
    private static final String TAG = "BookmarkFragment";
    private static TextView noBookmarks;
    private ArrayList<MyNews> newsArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_bookmark, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        noBookmarks = getActivity().findViewById(R.id.bookmark_empty);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MyAdapter.BOOKMARKS, Context.MODE_PRIVATE);
        Map<String, ?> all_items = sharedPreferences.getAll();

        if (all_items.size() == 0) {
            noBookmarks.setVisibility(View.VISIBLE);
        }

        newsArrayList = new ArrayList<>();
        for(Map.Entry<String, ?> item : all_items.entrySet()){
            MyNews myNews = new Gson().fromJson(item.getValue().toString(), MyNews.class);
            newsArrayList.add(myNews);
        }

        changeText(sharedPreferences);

        RecyclerView recyclerView = getActivity().findViewById(R.id.bookmark_rv);
        MyAdapter adapter = new MyAdapter(
                null,
                newsArrayList,
                "", "", "", "",
                false,
                true,
                getContext()
        );
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL
        ));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


    }

    public static void changeText(SharedPreferences sp) {
//        SharedPreferences sharedPreferences = act.getSharedPreferences(MyAdapter.BOOKMARKS, Context.MODE_PRIVATE);
        if (sp.getAll().size() == 0) {
//                        mContext.getResources(). .setVisibility(View.VISIBLE);
            noBookmarks.setVisibility(View.VISIBLE);
        }
    }

}
