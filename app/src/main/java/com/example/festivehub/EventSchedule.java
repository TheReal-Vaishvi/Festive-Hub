package com.example.festivehub;
public class EventSchedule {
    String id, title, startTime, endTime, description;
    long order;
    public EventSchedule() {
    }
    public EventSchedule(String id, String title, String startTime, String endTime, String description, long order) {
        this.id = id;
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.order = order;
    }
    public String getTitle() {
        return title;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
    public String getDescription() {
        return description;
    }
    public long getOrder() {
        return order;
    }
}