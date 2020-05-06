package com.example.newsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TrendingFragment extends Fragment {

    EditText editText;
    private static final String TAG = "TrendingFragment";
    private LineChart lineChart;
    RequestQueue mQue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trending, container, false);
//        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        makePostRequest("CoronaVirus");


        editText = getActivity().findViewById(R.id.et_trending);
        editText.setImeActionLabel("", KeyEvent.KEYCODE_ENTER);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "listnner");
                if (actionId == EditorInfo.IME_ACTION_SEND){
                    Log.d(TAG, "submitted");
//                    Toast.makeText(getContext(), "search for "+ editText.getText(), Toast.LENGTH_SHORT).show();

                    String q = editText.getText().toString();
                    makePostRequest(q);

                }
                return true;
            }
        });

    }



    public void makePostRequest(final String q){

        mQue = Volley.newRequestQueue(getContext());

        String url = MainActivity.BACKEND_ENDPOINT+"/search";
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("for_", "trending");
            jBody.put("query", q);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, url, jBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
//                            JSONArray respdata = null;

                            try {
                                JSONArray respdata = response.getJSONArray("points");


                                ArrayList<Entry> values = new ArrayList<>();
                                for(int i = 0; i < respdata.length(); i++){
                                    values.add(new Entry(i, respdata.getInt(i)));
                                }
//                                Log.d(TAG, values.toString());
                                makeChart(values, q);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            Log.d(TAG, respdata.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, error.toString());
                        }
                    }
            );
            mQue.add(req);


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }

    void makeChart(ArrayList<Entry> vals, String q){
        Log.d(TAG, "Making chart");

        lineChart = getActivity().findViewById(R.id.chart);
//        lineChart.setTouchEnabled(false);

        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawAxisLine(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);

        lineChart.setDrawBorders(false);

        Legend l = lineChart.getLegend();
        l.setTextSize(18f);
//        l.setTextColor(Color.parseColor("#5821ad"));
////        l.setEnabled(true);
//        l.setFormSize(18f);

        LegendEntry le1 = new LegendEntry();
        le1.label = "Trending data chart for "+q;
        le1.formColor = Color.parseColor("#5821ad");
        le1.formSize = 18f;


        l.setCustom(Arrays.asList(le1));


        LineDataSet set = new LineDataSet(vals, "Trending data chart for "+q);
        set.setColor(Color.parseColor("#5821ad"));
        set.setCircleColor(Color.parseColor("#5821ad"));
        set.setCircleHoleColor(Color.parseColor("#5821ad"));
        set.setValueTextSize(10f);



        LineData data = new LineData(set);
        lineChart.setData(data);
        lineChart.invalidate();

    }

}
