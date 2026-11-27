package com.example.festivehub;

import com.google.firebase.Timestamp;
public class EventAssignment {
    String id, eventId, eventTitle, eventDate, eventTime, eventLocation, volunteerEmail, volunteerName, status;
    Timestamp assignedAt;

    public EventAssignment() {
    }
    public EventAssignment(String id, String eventId, String eventTitle, String eventDate, String eventTime, String eventLocation, String volunteerEmail,
                           String volunteerName, String status, Timestamp assignedAt) {
        this.id = id;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.volunteerEmail = volunteerEmail;
        this.volunteerName = volunteerName;
        this.status = status;
        this.assignedAt = assignedAt;
    }
    public String getId() {
        return id;
    }
    public String getEventId() {
        return eventId;
    }
    public String getEventTitle() {
        return eventTitle;
    }
    public String getEventDate() {
        return eventDate;
    }
    public String getEventTime() {
        return eventTime;
    }
    public String getEventLocation() {
        return eventLocation;
    }
    public String getVolunteerEmail() {
        return volunteerEmail;
    }
    public String getVolunteerName() {
        return volunteerName;
    }
    public String getStatus() {
        return status;
    }
    public Timestamp getAssignedAt() {
        return assignedAt;
    }
}