package com.example.festivehub;

public class EventBanner {
    int image;
    String imageUrl;
    String title;
    String subtitle;

    public EventBanner(int image,String title,String subtitle){
        this.image = image;
        this.imageUrl = "";
        this.title = title;
        this.subtitle = subtitle;
    }
    public EventBanner(int image, String imageUrl, String title, String subtitle){
        this.image = image;
        this.imageUrl = imageUrl;
        this.title = title;
        this.subtitle = subtitle;
    }
}
