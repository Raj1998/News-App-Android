package com.example.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class SearchActivity extends AppCompatActivity {

    private static final String TAG = "SearchActivity";
    private ArrayList<MyNews> myNewsArrayList = new ArrayList<>();
    private RequestQueue mQue;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout spinner;
    private String query;
    private TextView no_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setElevation(0);
        mQue = Volley.newRequestQueue(this);
        swipeRefreshLayout = findViewById(R.id.search_pull_refresh);
        spinner = findViewById(R.id.search_loading_spinner);
        no_search = findViewById(R.id.search_empty);


        Intent intent = getIntent();
//        query =



        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.EXTRA_QUERY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ( intent.hasExtra(MainActivity.EXTRA_QUERY) ){
            query = intent.getStringExtra(MainActivity.EXTRA_QUERY);
            editor.putString(MainActivity.EXTRA_QUERY, query);
            editor.commit();

            Log.d(TAG, "...."+query);
        }
        else {

            query = sharedPreferences.getString(MainActivity.EXTRA_QUERY, "");
            Log.d(TAG, sharedPreferences.contains(MainActivity.EXTRA_QUERY) + ">>>>");
        }



        getSupportActionBar().setTitle("Search Results for "+query);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                no_search.setVisibility(View.GONE);
                newsApiReq(query);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        newsApiReq(query);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
        }

    }


    private void newsApiReq(String query){

        if (myNewsArrayList.size() == 0){
            spinner.setVisibility(View.VISIBLE);
        }
        Log.d(TAG, "Making request for news...");


        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, MainActivity.BACKEND_ENDPOINT+"/search?q="+query,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {

//                            String msg = response.getString("msg");
                            JSONArray arr = response.getJSONArray("news");
//                            if ( msg == "success")

                            myNewsArrayList = new ArrayList<>();

                            for ( int i = 0; i < arr.length(); i++){
//                                mImageUrls.add(arr.getJSONObject(i).getString("img"));
//                                mNames.add(arr.getJSONObject(i).getString("name"));


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
//                                Log.d(TAG, myNews.toString());
                            }
                            initRecyclerView();
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

//        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest();
//        jsonArrayRequest.add

        mQue.add(req1);

    }

    private void initRecyclerView(){

        Log.d(TAG, "initRecyclerView: init RecyclerView.");
        try {
            RecyclerView recyclerView = findViewById(R.id.search_rv);

            MyAdapter adapter = new MyAdapter(
                    myNewsArrayList,
                    null,
                    "", "", "", "",
                    false,
                    false,
                    this);
            recyclerView.setAdapter(adapter);

            spinner.setVisibility(View.GONE);

            if (myNewsArrayList.size() == 0) no_search.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }

    }

}
