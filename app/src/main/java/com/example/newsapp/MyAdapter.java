package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MyAdapter";
    public static final String BOOKMARKS = "Bookmarks";
    
//    private ArrayList<String> titles;
//    private ArrayList<String> images;
    private ArrayList<MyNews> myNewsArrayList;
    private ArrayList<MyNews> myBookmarkList;

    private String city;
    private String state;
    private String temp;
    private String summary;

    private boolean showWeather;
    private boolean showBookmarks;

    private Context mContext;

    private static final int FIRST_LAYOUT = 0;
    private static final int SECOND_LAYOUT = 1;
    private static final int THIRD_LAYOUT = 2;

    public class MyViewHolder extends  RecyclerView.ViewHolder {
        private TextView title;
        private CardView cardView;
        private ImageView imageView;
        private ImageView bookmarkIcon;
//        public ImageView onBookmark;
        private TextView section;
        private TextView timeAgo;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            cardView = itemView.findViewById(R.id.parent_news_card);
            imageView = itemView.findViewById(R.id.image_card);
            bookmarkIcon = itemView.findViewById(R.id.bookmark_icon);
//            onBookmark = itemView.findViewById(R.id.bookmark_on_icon);
            section = itemView.findViewById(R.id.section);
            timeAgo = itemView.findViewById(R.id.time);
        }
    }

    public class BookmarkViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private CardView cardView;
        private ImageView imageView;
        private ImageView bookmarkIcon;
        private TextView section;
        private TextView timeAgo;

        public BookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookmark_tv);
            cardView = itemView.findViewById(R.id.parent_bookmark_card);
            imageView = itemView.findViewById(R.id.bookmark_iv);
            bookmarkIcon = itemView.findViewById(R.id.bookmark_bicon);
            section = itemView.findViewById(R.id.bookmark_section);
            timeAgo = itemView.findViewById(R.id.bookmark_time);
        }
    }

    public class WeatherViewHolder extends RecyclerView.ViewHolder {
        public TextView city;
        public TextView state;
        public TextView temp;
        public TextView summary;
        public ImageView imageView;

        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            this.city = itemView.findViewById(R.id.city);
            this.state = itemView.findViewById(R.id.state);
            this.temp = itemView.findViewById(R.id.temperature);
            this.summary = itemView.findViewById(R.id.summary);
            this.imageView = itemView.findViewById(R.id.summary_image);
        }
    }

    public MyAdapter(
//            ArrayList<String> t, ArrayList<String> i,
                     ArrayList<MyNews> myNews,
                     ArrayList<MyNews> myBookmarkList,
                     String c, String s, String tem, String sum,
                     boolean showWeather,
                     boolean showBookmarks,
                     Context contx){
//        titles = t;
//        images = i;
        this.myNewsArrayList = myNews;
        this.myBookmarkList = myBookmarkList;

        city = c;
        state = s;
        temp = tem;
        summary = sum;

        this.showWeather = showWeather;
        this.showBookmarks = showBookmarks;

        mContext = contx;
    }




    @Override
    public int getItemViewType(int position) {
        if (position == 0 && showWeather)
            return FIRST_LAYOUT;
        else if (showBookmarks)
            return THIRD_LAYOUT;
        else
            return  SECOND_LAYOUT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateVH called");
        View view;
        RecyclerView.ViewHolder vh;

        if (viewType==FIRST_LAYOUT){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_weather, parent, false);
            vh = new WeatherViewHolder(view);
        }
        else if (viewType == SECOND_LAYOUT ) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news, parent, false);
            vh = new MyViewHolder(view);
        }
        else if (viewType == THIRD_LAYOUT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_bookmark, parent, false);
            vh = new BookmarkViewHolder(view);
        }
        else  {
            vh = null;
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, final int position) {

        if ( vh.getItemViewType() == FIRST_LAYOUT ){
            WeatherViewHolder holder = (WeatherViewHolder) vh;
//            Log.d(TAG, "onBind called.. ");
            holder.city.setText(city);
            holder.state.setText(state);
            holder.temp.setText(temp);
            holder.summary.setText(summary);

            switch (summary){
                case "Clouds":{
                    holder.imageView.setImageResource(R.drawable.cloudy);
                    break;
                }
                case "Clear":{
                    holder.imageView.setImageResource(R.drawable.clear);
                    break;
                }
                case "Snow":{
                    holder.imageView.setImageResource(R.drawable.snowy);
                    break;
                }
                case "Rain":
                case "Drizzle":{
                    holder.imageView.setImageResource(R.drawable.rainy);
                    break;
                }
                case "Thunderstorm":{
                    holder.imageView.setImageResource(R.drawable.thunder);
                    break;
                }
                default:{
                    holder.imageView.setImageResource(R.drawable.sunny);
                    break;
                }

            }
        }
        else if ( vh.getItemViewType() == THIRD_LAYOUT ) {
            BookmarkViewHolder holder = (BookmarkViewHolder) vh;
            final int arr_idx = showWeather ? position - 1 : position;

            Glide.with(mContext).load(myBookmarkList.get(arr_idx).getImageUrl()).into(holder.imageView);
            holder.title.setText(myBookmarkList.get(arr_idx).getTitle());
            holder.section.setText(myBookmarkList.get(arr_idx).getSectionId());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = simpleDateFormat.parse(myBookmarkList.get(arr_idx).getPubDate().substring(0, 10));
//                Log.d(TAG, new SimpleDateFormat("dd MMM").format(date));
                holder.timeAgo.setText(new SimpleDateFormat("dd MMM").format(date));

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Article.class);
                    intent.putExtra("article",
                            new Gson().toJson(myBookmarkList.get(arr_idx)) );
                    intent.putExtra("goto", mContext.getClass().getSimpleName());
                    mContext.startActivity(intent);
                }
            });

            holder.bookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    SharedPreferences sharedPreferences = mContext.getSharedPreferences(BOOKMARKS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.remove(myBookmarkList.get(arr_idx).getId());
                    editor.commit();

                    Toast.makeText(mContext, "\""+ myBookmarkList.get(arr_idx).getTitle() +"\" was removed to Bookmarks", Toast.LENGTH_SHORT).show();

                    myBookmarkList.remove(arr_idx);
                    notifyItemRemoved(arr_idx);
                    notifyItemRangeChanged(arr_idx, myBookmarkList.size());


                    if (sharedPreferences.getAll().size() == 0) {
                        BookmarkFragment.changeText(sharedPreferences);
                    }
                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Log.d(TAG, "Long pressed");
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog);

                    ImageView imgBookmark = dialog.findViewById(R.id.dialog_img);

                    Glide.with(mContext)
                            .load(myBookmarkList.get(arr_idx).getImageUrl())
                            .into(imgBookmark);

                    TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                    dialogTitle.setText(myBookmarkList.get(arr_idx).getTitle());

                    final ImageView dialogBookmark = dialog.findViewById(R.id.dialog_bookmark);
                    dialogBookmark.setImageResource(R.drawable.ic_bookmark_black_18dp);

                    dialogBookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            SharedPreferences sharedPreferences = mContext.getSharedPreferences(BOOKMARKS, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.remove(myBookmarkList.get(arr_idx).getId());

                            editor.commit();

                            Toast.makeText(mContext, "\""+ myBookmarkList.get(arr_idx).getTitle() +"\" was removed to Bookmarks", Toast.LENGTH_SHORT).show();

                            myBookmarkList.remove(arr_idx);
                            notifyItemRemoved(arr_idx);
                            notifyItemRangeChanged(arr_idx, myBookmarkList.size());

                            if (sharedPreferences.getAll().size() == 0) {
                                BookmarkFragment.changeText(sharedPreferences);
                            }

                            dialog.dismiss();
                        }
                    });

                    ImageView dialogTwitter = dialog.findViewById(R.id.dialog_twitter);
                    dialogTwitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String shareURL = "https://twitter.com/intent/tweet?hashtags=CSCI571NewsSearch&text=Check out this Link: "+myBookmarkList.get(arr_idx).getUrl();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
                            mContext.startActivity(browserIntent);
                        }
                    });


                    dialog.show();
                    return true;
                }
            });




        }
        else{
            final MyViewHolder holder = (MyViewHolder) vh;
            final int arr_idx = showWeather ? position - 1 : position;

            final SharedPreferences sharedPreferences = mContext.getSharedPreferences(BOOKMARKS, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            Glide.with(mContext)
                    .load(myNewsArrayList.get(arr_idx).getImageUrl())
                    .into(holder.imageView);

            holder.title.setText(myNewsArrayList.get(arr_idx).getTitle());
            holder.section.setText(myNewsArrayList.get(arr_idx).getSectionId());
            holder.timeAgo.setText(myNewsArrayList.get(arr_idx).getTimeAgo());

            holder.bookmarkIcon.setImageResource(
                    sharedPreferences.contains(myNewsArrayList.get(arr_idx).getId()) ?
                    R.drawable.ic_bookmark_black_18dp :
                            R.drawable.ic_bookmark_border_black_18dp
                    );


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, Article.class);
                    intent.putExtra("article",
                            new Gson().toJson(myNewsArrayList.get(arr_idx)) );

                    intent.putExtra("goto", mContext.getClass().getSimpleName());
                    Log.d(TAG, mContext.getClass().getSimpleName());
                    mContext.startActivity(intent);

                }
            });

            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog);

                    ImageView imgBookmark = dialog.findViewById(R.id.dialog_img);
                    Glide.with(mContext)
                            .load(myNewsArrayList.get(arr_idx).getImageUrl())
                            .into(imgBookmark);

                    TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                    dialogTitle.setText(myNewsArrayList.get(arr_idx).getTitle());

                    final ImageView dialogBookmark = dialog.findViewById(R.id.dialog_bookmark);
                    dialogBookmark.setImageResource(
                            sharedPreferences.contains(myNewsArrayList.get(arr_idx).getId()) ?
                                    R.drawable.ic_bookmark_black_18dp :
                                    R.drawable.ic_bookmark_border_black_18dp
                    );

                    dialogBookmark.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if( dialogBookmark.getDrawable().getConstantState() == ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_bookmark_border_black_18dp, null).getConstantState() ) {
                                dialogBookmark.setImageResource( R.drawable.ic_bookmark_black_18dp );
                                holder.bookmarkIcon.setImageResource(  R.drawable.ic_bookmark_black_18dp );

                                String json = new Gson().toJson(myNewsArrayList.get(arr_idx));
                                editor.putString(myNewsArrayList.get(arr_idx).getId(), json);

                                editor.commit();

                                Toast.makeText(mContext, "\""+ myNewsArrayList.get(arr_idx).getTitle() +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                dialogBookmark.setImageResource( R.drawable.ic_bookmark_border_black_18dp );
                                holder.bookmarkIcon.setImageResource(  R.drawable.ic_bookmark_border_black_18dp );

                                editor.remove(myNewsArrayList.get(arr_idx).getId());

                                editor.commit();

                                Toast.makeText(mContext, "\""+ myNewsArrayList.get(arr_idx).getTitle() +"\" was removed to Bookmarks", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

                    ImageView dialogTwitter = dialog.findViewById(R.id.dialog_twitter);
                    dialogTwitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String shareURL = "https://twitter.com/intent/tweet?hashtags=CSCI571NewsSearch&text=Check out this Link: "+myNewsArrayList.get(arr_idx).getUrl();
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shareURL));
                            mContext.startActivity(browserIntent);
                        }
                    });

                    dialog.show();
                    return true;
                }
            });

            holder.bookmarkIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.bookmarkIcon.getDrawable().getConstantState() == ResourcesCompat.getDrawable(mContext.getResources(), R.drawable.ic_bookmark_border_black_18dp, null ).getConstantState() ) {
                        holder.bookmarkIcon.setImageResource(  R.drawable.ic_bookmark_black_18dp );



                        String json = new Gson().toJson(myNewsArrayList.get(arr_idx));
                        editor.putString(myNewsArrayList.get(arr_idx).getId(), json);

                        editor.commit();

                        Toast.makeText(mContext, "\""+ myNewsArrayList.get(arr_idx).getTitle() +"\" was added to Bookmarks", Toast.LENGTH_SHORT).show();

                    }
                    else{
                        holder.bookmarkIcon.setImageResource(  R.drawable.ic_bookmark_border_black_18dp );
                        editor.remove(myNewsArrayList.get(arr_idx).getId());

                        editor.commit();

                        Toast.makeText(mContext, "\""+ myNewsArrayList.get(arr_idx).getTitle() +"\" was removed to Bookmarks", Toast.LENGTH_SHORT).show();

                    }

//                        holder.onBookmark.setVisibility(View.VISIBLE);
//                        holder.bookmarkIcon.setVisibility(View.INVISIBLE);
//
//                        Toast.makeText(mContext, "Add bookmark "+ arr_idx, Toast.LENGTH_SHORT).show();

                }
            });


//            holder.onBookmark.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    holder.onBookmark.setVisibility(View.INVISIBLE);
//                    holder.bookmarkIcon.setVisibility(View.VISIBLE);
//
//                    Toast.makeText(mContext, "Remove bookmark "+ arr_idx, Toast.LENGTH_SHORT).show();
//                }
//            });

        }

    }

    @Override
    public int getItemCount() {
        if (showWeather)
            return myNewsArrayList.size() + 1;
        else if (showBookmarks)
            return myBookmarkList.size();
        else
            return  myNewsArrayList.size();
    }



}
