package com.example.newsapp;

public class MyNews {

    private String title;
    private String pubDate;
    private String sectionId;
    private String imageUrl;
    private String id;
    private String url;
    private String timeAgo;

    public String getTimeAgo() {
        return timeAgo;
    }

    public MyNews(String title, String pubDate, String sectionId, String imageUrl, String id, String url, String timeAgo) {
        this.title = title;
        this.pubDate = pubDate;
        this.sectionId = sectionId;
        this.imageUrl = imageUrl;
        this.id = id;
        this.url = url;
        this.timeAgo = timeAgo;
    }

    public String getTitle() {
        return title;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "MyNews{" +
                "title='" + title + '\'' +
                ", pubDate='" + pubDate + '\'' +
                ", sectionId='" + sectionId + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", timeAgo='" + timeAgo + '\'' +
                '}';
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
