package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {
    ImageView imgeventdetail;
    TextView txtbackevent, txteventtitle, txteventcategory, txteventdate, txteventtime, txteventlocation;
    TextView txtdescription;
    Button btnregisterevent, btnviewschedule;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String eventId, title, date, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        imgeventdetail = findViewById(R.id.imgeventdetail);
        txtbackevent = findViewById(R.id.txtbackevent);
        txteventtitle = findViewById(R.id.txteventtitle);
        txteventcategory = findViewById(R.id.txteventcategory);
        txteventdate = findViewById(R.id.txteventdate);
        txteventtime = findViewById(R.id.txteventtime);
        txteventlocation = findViewById(R.id.txteventlocation);
        txtdescription = findViewById(R.id.txteventdescription);

        btnregisterevent = findViewById(R.id.btnregisterevent);
        btnviewschedule = findViewById(R.id.btnviewschedule);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initially hide both buttons until user role is checked
        btnregisterevent.setVisibility(View.GONE);
        btnviewschedule.setVisibility(View.GONE);

        // Schedule button
        btnviewschedule.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailActivity.this, EventScheduleActivity.class);
            intent.putExtra("eventId", getIntent().getStringExtra("eventId"));
            intent.putExtra("eventTitle", getIntent().getStringExtra("eventTitle"));
            intent.putExtra("eventDate", getIntent().getStringExtra("eventDate"));

            startActivity(intent);
        });
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        title = intent.getStringExtra("eventTitle");
        date = intent.getStringExtra("eventDate");

        String category = intent.getStringExtra("eventCategory");
        String time = intent.getStringExtra("eventTime");
        String location = intent.getStringExtra("eventLocation");
        String description = intent.getStringExtra("eventDescription");

        int image = intent.getIntExtra("eventImage", R.drawable.grand_festival);
        imageUrl = intent.getStringExtra("eventImageUrl");

        // Display event information
        txteventtitle.setText(title);
        txteventcategory.setText(category);
        txteventdate.setText(date);
        txteventtime.setText(time);
        txteventlocation.setText(location);
        txtdescription.setText(description);

        // Display Cloudinary image nd local drawable
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(imgeventdetail);
        }
        else if (image != 0) {
            imgeventdetail.setImageResource(image);
        }
        else {
            imgeventdetail.setImageResource(android.R.drawable.ic_menu_gallery);
        }
        // Back button
        txtbackevent.setOnClickListener(v -> finish());

        // Register button
        btnregisterevent.setOnClickListener(v -> registerForEvent());
        // Check whether logged-in user is a student or volunteer
        checkUserRole();
    }
    // Check user role and show the appropriate button
    private void checkUserRole() {
        if (auth.getCurrentUser() == null) {
            return;
        }
        String email = auth.getCurrentUser().getEmail();
        if (email == null || email.trim().isEmpty()) {
            Toast.makeText(this, "Unable to identify user", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("Volunteers")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (isFinishing() || isDestroyed()) {
                        return;
                    }
                    if (!querySnapshot.isEmpty()) {
                        // Volunteer: Show schedule, hide registration
                        btnregisterevent.setVisibility(View.GONE);
                        btnviewschedule.setVisibility(View.VISIBLE);
                    }
                    else {
                        // Student: Show registration, hide schedule
                        btnregisterevent.setVisibility(View.VISIBLE);
                        btnviewschedule.setVisibility(View.GONE);
                        // Check existing registration
                        checkRegistrationStatus();
                    }
                })
                .addOnFailureListener(e -> {
                    btnregisterevent.setVisibility(View.GONE);
                    btnviewschedule.setVisibility(View.GONE);
                    Toast.makeText(this, "Unable to check user role: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    // Set button after successful registration or existing registration
    private void setRegisteredState() {
        btnregisterevent.setText("REGISTERED");
        btnregisterevent.setEnabled(false);
        btnregisterevent.setAlpha(0.7f);
    }
    // Set button when user has not registered
    private void setRegisterState() {
        btnregisterevent.setText("REGISTER NOW");
        btnregisterevent.setEnabled(true);
        btnregisterevent.setAlpha(1f);
    }
    // Check whether current user is already registered
    private void checkRegistrationStatus() {
        if (auth.getCurrentUser() == null) {
            setRegisterState();
            return;
        }
        if (eventId == null || eventId.isEmpty()) {
            btnregisterevent.setText("UNAVAILABLE");
            btnregisterevent.setEnabled(false);
            return;
        }
        btnregisterevent.setText("Checking...");
        btnregisterevent.setEnabled(false);

        String userId = auth.getCurrentUser().getUid();
        String registrationId = userId + "_" + eventId;

        db.collection("EventRegistrations")
                .document(registrationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        setRegisteredState();
                    }
                    else {
                        setRegisterState();
                    }
                })
                .addOnFailureListener(e -> {
                    setRegisterState();
                    Toast.makeText(this, "Unable to check registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    // Register user for event
    private void registerForEvent() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event information is missing", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = auth.getCurrentUser().getUid();
        String registrationId = userId + "_" + eventId;

        // Prevent multiple clicks while registering
        btnregisterevent.setText("Registering...");
        btnregisterevent.setEnabled(false);

        // Check whether registration already exists
        db.collection("EventRegistrations")
                .document(registrationId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        setRegisteredState();
                        Toast.makeText(this, "You have already registered for this event!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, Object> registration = new HashMap<>();

                    registration.put("userId", userId);
                    registration.put("eventId", eventId);
                    registration.put("eventTitle", title);
                    registration.put("eventDate", date);
                    registration.put("status", "Registered");
                    registration.put("ticketCount", 1);
                    registration.put("registeredAt", FieldValue.serverTimestamp());

                    // Save registration in Firestore
                    db.collection("EventRegistrations")
                            .document(registrationId)
                            .set(registration)
                            .addOnSuccessListener(unused -> {
                                // Update button immediately
                                setRegisteredState();
                                Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                setRegisterState();
                                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    setRegisterState();
                    Toast.makeText(this, "Unable to check registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
