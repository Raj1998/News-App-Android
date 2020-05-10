package com.example.newsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.widget.SearchView;
//import android.widget.SearchView;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String BACKEND_ENDPOINT = Data.BACKEND_BASE_URL;
//    public static final String BACKEND_ENDPOINT = "http://hw8nodeu2ti6d82zh87ejp.us-east-1.elasticbeanstalk.com/api/mobile";
//    public static final String BACKEND_ENDPOINT = "http://192.168.1.2:8081/api/mobile";

    public static final String EXTRA_QUERY = "query";
    public static final int REQUEST_LOCATION = 79;
    private int mCurrFragId;
    RequestQueue mQue;

    private static final int TRIGGER_AUTO_COMPLETE = 1300;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    private AutoSuggestAdapter autoSuggestAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQue = Volley.newRequestQueue(this);

        BottomNavigationView btm_nav_view = findViewById(R.id.bottom_nav);
        btm_nav_view.setOnNavigationItemSelectedListener(btm_nav_listener);



        getSupportActionBar().setElevation(0);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("cs571", "not granted");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
        else{
            mCurrFragId = R.id.nav_home;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }

        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION:{
                if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED ) {
                    Log.d("cs571", "location permission GRANTED");

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            new HomeFragment()).commit();
                }
                else {
                    Log.d("cs571", "location permission DENIED");
                }
            }
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener btm_nav_listener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    int itemId = item.getItemId();
//                    Log.d(TAG, itemId + " " + mCurrFragId);

                    if (mCurrFragId != itemId) {
                        switch (itemId){
                            case R.id.nav_home:
                                selectedFragment = new HomeFragment();
                                break;
                            case R.id.nav_headlines:
                                selectedFragment = new HeadlinesFragment();
                                break;
                            case R.id.nav_trending:
                                selectedFragment = new TrendingFragment();
                                break;
                            case R.id.nav_bookmark:
                                selectedFragment = new BookmarkFragment();
                                break;
                        }
                        mCurrFragId = itemId;

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.m_menu, menu);


//        SearchManager searchManager =
//                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search_icon).getActionView();

//        searchView.setQueryHint("Search Query hint");
        searchView.setIconified(false);
//        searchView.setIconifiedByDefault(false);

        searchView.onActionViewExpanded();


        final androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);

        searchAutoComplete.setThreshold(3);
        searchAutoComplete.setAdapter(autoSuggestAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchAutoComplete.setText(parent.getItemAtPosition(position)+"");
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        makeApiCall(searchAutoComplete.getText().toString());
                    }
                }
                return false;
            }
        });

//        dataArr.add("app");
//        dataArr.add("bap");
//        final androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchAutoComplete.setThreshold(0);
//
//        final ArrayList<String> dataArr = new ArrayList<>();
//        dataArr.add("app");
//        dataArr.add("bap");
//        searchAutoComplete.setDropDownAnchor(R.id.search_icon);
//        ArrayAdapter<ArrayList> adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line , dataArr);
//        searchAutoComplete.setAdapter(adapter);
//
//
//        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                searchAutoComplete.setText(parent.getItemAtPosition(position)+"");
//            }
//        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("CS571", "Send to search activity !!!");

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra(EXTRA_QUERY, query);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
//                final androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//                searchAutoComplete.setThreshold(3);
//                searchAutoComplete.setDropDownAnchor(R.id.search_icon);
//
////                final ArrayList<String> dataArr = new ArrayList<>();
////                dataArr.add("app");
////                dataArr.add("bap");
////                ArrayAdapter<ArrayList> adapter =  new ArrayAdapter(getBaseContext(), android.R.layout.simple_dropdown_item_1line , dataArr);
//
//
//
//
//
//
//
//
//                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
//                        "https://rajpatel.cognitiveservices.azure.com/bing/v7.0/suggestions?q=" + newText,
//                        null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray sugg = response.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
//
////                            final ArrayList<String> dataArr = new ArrayList<>();
////                            String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
////                            searchAutoComplete.setDropDownAnchor(R.id.search_icon);
//
//                            String dataArr[] = new String[sugg.length()];
//                            for(int i = 0; i < sugg.length(); i++){
////                                dataArr.add(sugg.getJSONObject(i).getString("displayText"));
//                                dataArr[i] = sugg.getJSONObject(i).getString("displayText");
//                            }
//
//
//                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, dataArr);
//                            searchAutoComplete.setAdapter(adapter);
//                            adapter.notifyDataSetChanged();
//
//                            Log.d(TAG, "After : "+dataArr.toString());
////                            ArrayAdapter<ArrayList> adapter = new ArrayAdapter(getBaseContext(), android.R.layout.activity_list_item , dataArr);
////                            dataArr.add("app");
////                            dataArr.add("bap");
////                            searchAutoComplete.setAdapter(adapter);
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (NullPointerException npe) {
//                            npe.printStackTrace();
//                        }
//
//
//                    }
//                },
//                        new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//
//                            }
//                        }
//
//                ) {
//                    @Override
//                    public Map<String, String> getHeaders() throws AuthFailureError {
//                        Map<String, String> headers = new HashMap<>();
//                        headers.put("Ocp-Apim-Subscription-Key", "ac024cec74564fddaf6810bf13a1702a");
//                        return headers;
//                    }
//                };
//                mQue.add(request);

                return false;
            }
        });




        return super.onCreateOptionsMenu(menu);
//        return true;
    }


    private void makeApiCall(String text) {
        Log.d(TAG, "making api call");
        ApiCall.make(this, text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //parsing logic, please change it as per your requirement
                List<String> stringList = new ArrayList<>();
                try {
                    JSONObject responseObject = new JSONObject(response);
                    JSONArray array = responseObject.getJSONArray("suggestionGroups").getJSONObject(0).getJSONArray("searchSuggestions");
                    int upto5 = array.length() > 5 ? 5 : array.length();
//                    JSONArray array = responseObject.getJSONArray("results");
                    for (int i = 0; i < upto5; i++) {
//                        JSONObject row = array.getJSONObject(i);
//                        stringList.add(row.getString("trackName"));
                        stringList.add(array.getJSONObject(i).getString("displayText"));
                    }
                    Log.d(TAG, stringList.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //IMPORTANT: set data here and notify
                autoSuggestAdapter.setData(stringList);
                autoSuggestAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "exiting... ");
//        ActivityManager am =
//        finish();
        this.finish();
    }


}
