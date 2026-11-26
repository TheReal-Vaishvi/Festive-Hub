package com.example.festivehub;
public class Volunteer {
    String id, name, email, mobile, college;
    Object createdAt;
    public Volunteer(String id, String name, String email, String mobile, String college, Object createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.college = college;
        this.createdAt = createdAt;
    }
}
