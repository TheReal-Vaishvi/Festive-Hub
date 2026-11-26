package com.example.festivehub;

public class Student {
    String id, name, email, mobile;
    Object createdAt;
//this model use to store each student's Firestore details in an organized way.
// The id will store the student's Firestore document ID...
    public Student(String id, String name, String email, String mobile, Object createdAt) {

        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.createdAt = createdAt;
    }
}
