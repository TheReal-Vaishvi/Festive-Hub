package com.example.festivehub;

public class Event {
    int image;
    String title, category, date, time, location, description;

    public Event(int image, String title, String category, String date,
                 String time, String location, String description){
        this.image = image;
        this.title = title;
        this.category = category;
        this.date = date;
        this.time = time;
        this.location = location;
        this.description = description;
    }
}
