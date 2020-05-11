package com.example.newsapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    LocationManager locationManager;
    TextView textViewCity, textViewTemp, textViewState, textViewSummary;
    ImageView imageViewSummary;
    RequestQueue mQue;
    boolean isRecyclerViewLoaded ;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    LinearLayout spinner;

//    private Double latitude;
//    private Double longitude;

    String city = "";
    String state = "";
    String temp = "";
    String summary = "";

//    private ArrayList<String> mNames = new ArrayList<>();
//    private ArrayList<String> mImageUrls = new ArrayList<>();

    private ArrayList<MyNews> myNewsArrayList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;


    private static String API_URL_WEATHER = Data.WEATHER_API_URL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "oncreateview called");
        isRecyclerViewLoaded = false;
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "On resume!!!");
    }

    @Override
    public void onStart() {
        Log.d(TAG, "on start called");
        super.onStart();

//        textViewCity = getActivity().findViewById(R.id.city);
//        textViewState = getActivity().findViewById(R.id.state);
//        textViewTemp = getActivity().findViewById(R.id.temperature);
//        textViewSummary= getActivity().findViewById(R.id.summary);
//        imageViewSummary = getActivity().findViewById(R.id.summary_image);

//        progressBar = getActivity().findViewById(R.id.progress_circular);
//        progressBar.setVisibility(View.VISIBLE);

        spinner = getActivity().findViewById(R.id.loading_spinner);
        if (myNewsArrayList.size() == 0){
            spinner.setVisibility(View.VISIBLE);
        }

        swipeRefreshLayout = getActivity().findViewById(R.id.pull_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeApiCall();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        makeApiCall();
    }

    public void makeApiCall(){
        Log.d(TAG, "MakeAPI called");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mQue = Volley.newRequestQueue(getContext());



        try {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if( location != null){
                Log.d("cs571", "Last known "+location.getLongitude()+" "+location.getLatitude());
                geoLoc(location.getLatitude(), location.getLongitude());
            }
            else {
                Log.d("cs571", "null location");
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000, 10,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Double longitude = location.getLongitude();
                            Double latitude = location.getLatitude();

                            geoLoc(latitude, longitude);

                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            try{
                                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                Log.d("cs571", "[Listener..] gps is off");
                            }
                            catch (NullPointerException npe){
                                npe.printStackTrace();
                            }

                        }
                    }
            );
//            }
        }
        catch (SecurityException e){
            Log.d("cs571", e.getMessage());

        }
//        geoLoc(latitude, longitude);
    }

    private void geoLoc(Double latitude, Double longitude){
        try {
            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            Log.d("cs571", "[Listener..]"+longitude+" "+latitude+" "+ city +", "+state);
            getWeatherJson();

        }
        catch (IOException ioe){
            Log.d("cs571", "io exception "+ioe.getMessage());
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    private void getWeatherJson(){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, API_URL_WEATHER+city, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if (myNewsArrayList.size() == 0){
                                spinner.setVisibility(View.VISIBLE);
                            }

                            Double t = response.getJSONObject("main").getDouble("temp") ;
                            String inn = String.valueOf(  Math.round(t) );
                            String s = response.getJSONArray("weather").getJSONObject(0).getString("main");
                            Log.d("cs571", t+", "+s);
                            temp = inn + " °C";
                            summary = s;


//                            switch (summary){
//                                case "Clouds":{
//                                    imageViewSummary.setImageResource(R.drawable.cloudy);
//                                    break;
//                                }
//                                case "Clear":{
//                                    imageViewSummary.setImageResource(R.drawable.clear);
//                                    break;
//                                }
//                                case "Snow":{
//                                    imageViewSummary.setImageResource(R.drawable.snowy);
//                                    break;
//                                }
//                                case "Rain":
//                                case "Drizzle":{
//                                    imageViewSummary.setImageResource(R.drawable.rainy);
//                                    break;
//                                }
//                                case "Thunderstorm":{
//                                    imageViewSummary.setImageResource(R.drawable.thunder);
//                                    break;
//                                }
//                                default:{
//                                    imageViewSummary.setImageResource(R.drawable.sunny);
//                                    break;
//                                }
//
//                            }

//                            textViewCity.setText(city);
//                            textViewTemp.setText(temp);
//                            textViewState.setText(state);
//                            textViewSummary.setText(summary);

//                            if (! isRecyclerViewLoaded){
//                                newsApiReq();
//                                isRecyclerViewLoaded = true;
////                                progressBar.setVisibility(View.GONE);
//
//                            }

                            newsApiReq();

//                            new Handler().postDelayed(
//                            new Runnable() {
//                                @Override
//                                public void run() {
//                                    linearLayout.setVisibility(View.GONE);
//                                }
//                            }, 900);


                        }
                        catch (JSONException je){
                            Log.d("cs571", je.getMessage());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("cs571", "err.. "+error.toString());
                    }
                }
        );


        mQue.add(req);

    }

    private void newsApiReq(){
        Log.d(TAG, "Making request for news...");


        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, MainActivity.BACKEND_ENDPOINT+"?section=x",
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
            recyclerView = getActivity().findViewById(R.id.recycler_view);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                    DividerItemDecoration.VERTICAL
            ));

            MyAdapter adapter = new MyAdapter(
                    myNewsArrayList,
                    null,
                    city, state, temp, summary,
                    true,
                    false,
                    getContext());



            recyclerView.setAdapter(adapter);

            spinner.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        catch (NullPointerException npe){
            npe.printStackTrace();
        }

    }



}
