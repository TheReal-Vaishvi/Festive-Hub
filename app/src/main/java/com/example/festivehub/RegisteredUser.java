package com.example.festivehub;
public class RegisteredUser {
    String name, email, mobile, status, registeredAt;
    int ticketCount;
    public RegisteredUser(String name, String email, String mobile, int ticketCount, String status, String registeredAt) {
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.ticketCount = ticketCount;
        this.status = status;
        this.registeredAt = registeredAt;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getMobile() {
        return mobile;
    }
    public int getTicketCount() {
        return ticketCount;
    }
    public String getStatus() {
        return status;
    }
    public String getRegisteredAt() {
        return registeredAt;
    }
}