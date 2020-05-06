package com.example.newsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TechnologyFragment extends Fragment {

    private static final String TAG = "technologyFrag";

    private ArrayList<MyNews> myNewsArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout spinner;
    RequestQueue mQue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_technology, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "onstart called");
        mQue = Volley.newRequestQueue(getContext());
        spinner = getActivity().findViewById(R.id.technology_loading_spinner);
        if (myNewsArrayList.size() == 0){
            spinner.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = getActivity().findViewById(R.id.technology_pull_refresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        loadData();
    }


    public void loadData() {
        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, MainActivity.BACKEND_ENDPOINT+"?section=technology",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            JSONArray arr = response.getJSONArray("news");
                            myNewsArrayList = new ArrayList<>();
                            for ( int i = 0; i < arr.length(); i++){
                                MyNews myNews = new MyNews(
                                        arr.getJSONObject(i).getString("title"),
                                        arr.getJSONObject(i).getString("pubDate"),
                                        arr.getJSONObject(i).getString("section"),
                                        arr.getJSONObject(i).getString("image_url"),
                                        arr.getJSONObject(i).getString("id"),
                                        arr.getJSONObject(i).getString("url"),
                                        arr.getJSONObject(i).getString("time_ago")
                                );

                                myNewsArrayList.add(myNews);

                            }
//                            initRecyclerView();


                            try {
                                RecyclerView recyclerView = getActivity().findViewById(R.id.rv_technology);
                                MyAdapter adapter = new MyAdapter(
                                        myNewsArrayList,
                                        null,
                                        "", "", "", "",
                                        false,
                                        false,
                                        getContext()
                                );

                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                            }
                            catch (NullPointerException e){
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );


        mQue.add(req1);
    }


}
