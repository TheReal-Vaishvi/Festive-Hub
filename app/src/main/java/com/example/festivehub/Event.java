package com.example.festivehub;

public class Event {
    int image;
    String id, title, category, date, time, location, description, imageUrl;

    public Event(String id, int image, String title, String category, String date,
                 String time, String location, String description){

        this.id = id;
        this.image = image;
        this.title = title;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
        this.imageUrl = "";
    }
}
