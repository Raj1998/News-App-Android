package com.example.newsapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Article extends AppCompatActivity {
    private static final String TAG = "Article";
    private MyNews news;
    private String newsString;
    RequestQueue mQue;

    private ImageView imageView;
    private TextView title;
    private TextView date;
    private TextView section;
    private TextView desc;
    private TextView articleFull;

    private LinearLayout spinner;
    private CardView mainArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        getSupportActionBar().setElevation(0);

        imageView = findViewById(R.id.article_image);
        title = findViewById(R.id.article_title);
        date = findViewById(R.id.article_date);
        section = findViewById(R.id.article_section);
        desc = findViewById(R.id.article_desc);
        articleFull = findViewById(R.id.article_full);

        spinner = findViewById(R.id.loading_spinner );
        mainArticle = findViewById(R.id.main_news_card);

        mQue = Volley.newRequestQueue(this);

        newsString = getIntent().getStringExtra("article");
        news = new Gson().fromJson( getIntent().getStringExtra("article"), MyNews.class );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(news.getTitle());


        Log.d(TAG, getIntent().getStringExtra("goto"));
        makeReq();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                // todo: goto back activity from here
//
//                Intent intent = null;
//
//                String fromWhere = getIntent().getStringExtra("goto");
//
//
//                if (fromWhere.equals( MainActivity.class.getSimpleName() )){
//                    intent = new Intent(this, MainActivity.class);
//                }
//                else if (fromWhere.equals( SearchActivity.class.getSimpleName()) ) {
//                    intent = new Intent(this, SearchActivity.class);
//                }
//                startActivity(intent);
//                finish();
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.article_menu, menu);

        final MenuItem bookmarkBtn = menu.findItem(R.id.article_bookmark);
        MenuItem twitterBtn = menu.findItem(R.id.article_twitter);

        SharedPreferences sharedPreferences = getSharedPreferences(MyAdapter.BOOKMARKS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        bookmarkBtn.setIcon(
                sharedPreferences.contains(news.getId()) ?
                        R.drawable.ic_bookmark_black_18dp :
                        R.drawable.ic_bookmark_border_black_18dp
        );

        bookmarkBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (bookmarkBtn.getIcon().getConstantState() ==
                        ResourcesCompat.getDrawable(getResources(),
                                R.drawable.ic_bookmark_border_black_18dp,
                                getTheme() ).getConstantState() ) {
                    bookmarkBtn.setIcon(R.drawable.ic_bookmark_black_18dp);

                    editor.putString(news.getId(), newsString);
                    editor.commit();

                    Toast.makeText(Article.this, "\""+ news.getTitle() +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();
                }
                else {
                    bookmarkBtn.setIcon(R.drawable.ic_bookmark_border_black_18dp);

                    editor.remove(news.getId());
                    editor.commit();

                    Toast.makeText(Article.this, "\""+ news.getTitle() +"\" was removed to Bookmarks", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });


        twitterBtn.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                String shareURL = "https://twitter.com/intent/tweet?hashtags=CSCI571NewsSearch&text=Check out this Link: "+news.getUrl();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
                startActivity(browserIntent);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


    public void makeReq(){
        JsonObjectRequest req1 = new JsonObjectRequest(Request.Method.GET, MainActivity.BACKEND_ENDPOINT+"/article?id="+news.getId(),
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            spinner.setVisibility(View.VISIBLE);
                            final JSONObject obj = response.getJSONObject("news");

                            title.setText( obj.getString("title") );

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                Date fDate = simpleDateFormat.parse(obj.getString("pubDate") );
                                date.setText(new SimpleDateFormat("dd MMM yyyy").format(fDate));

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            section.setText( obj.getString("section") );


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                desc.setText(Html.fromHtml( obj.getString("desc"), Html.FROM_HTML_MODE_LEGACY ) );
                            }
                            else {
                                desc.setText( Html.fromHtml(obj.getString("desc")) );
                            }

                            articleFull.setText(R.string.full_article);
                            articleFull.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = null;
                                    try {
                                        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(obj.getString("url")));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    startActivity(browserIntent);
                                }
                            });




                            Glide.with(getApplicationContext()).load(obj.getString("image_url")).into(imageView);

                            spinner.setVisibility(View.GONE);
                            mainArticle.setVisibility(View.VISIBLE);

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

    @Override
    public void onBackPressed() {
        finish();
    }

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentI();
    }

    @Nullable
    @Override
    public Intent getParentActivityIntent() {
        return getParentI();
    }

    public Intent getParentI(){
        Intent intent = null;

        String fromWhere = getIntent().getStringExtra("goto");

        if (fromWhere == MainActivity.class.getSimpleName()){
            intent = new Intent(this, MainActivity.class);
        }
        else if (fromWhere == SearchActivity.class.getSimpleName()) {
            intent = new Intent(this, SearchActivity.class);
        }

        return intent;
    }
}
