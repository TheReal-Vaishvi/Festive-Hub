package com.example.festivehub;

public class EventRegistration {
    String userId;
    String eventId;
    String eventTitle;
    String eventDate;
    String status;
    int ticketCount;

    public EventRegistration(){
        // Default constructor required for firestore
    }

    public EventRegistration(String userId,String eventId, String eventTitle,String eventDate, String status, int ticketCount){
        this.userId = userId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.status = status;
        this.ticketCount = ticketCount;
    }

    public String getUserId(){
        return userId;
    }
    public String getEventId(){
        return eventId;
    }
    public String getEventTitle(){
        return eventTitle;
    }
    public String getEventDate(){
        return eventDate;
    }
    public String getStatus(){
        return status;
    }
    public int getTicketCount(){
        return ticketCount;
    }
    public void setTicketCount(int ticketCount) {
        this.ticketCount = ticketCount;
    }
}
