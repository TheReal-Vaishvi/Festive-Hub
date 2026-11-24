package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EventDetailActivity extends AppCompatActivity {

    ImageView imgeventdetail;
    TextView txtbackevent, txteventtitle, txteventcategory, txteventdate, txteventtime, txteventlocation;
    TextView txtdescription;
    Button btnregisterevent;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String eventId, title, date;

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
        btnregisterevent.setText("Checking...");
        btnregisterevent.setEnabled(false);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        eventId = intent.getStringExtra("eventId");
        title = intent.getStringExtra("eventTitle");
        String category = intent.getStringExtra("eventCategory");
        date = intent.getStringExtra("eventDate");
        String time = intent.getStringExtra("eventTime");
        String location = intent.getStringExtra("eventLocation");
        String description = intent.getStringExtra("eventDescription");

        int image = intent.getIntExtra(
                "eventImage", R.drawable.grand_festival);

        // Display event information
        txteventtitle.setText(title);
        txteventcategory.setText(category);
        txteventdate.setText(date);
        txteventtime.setText(time);
        txteventlocation.setText(location);
        txtdescription.setText(description);

        imgeventdetail.setImageResource(image);

        // Back button
        txtbackevent.setOnClickListener(v -> {
            finish();
        });

        // Register button
        btnregisterevent.setOnClickListener(v -> {
            registerForEvent();
        });
        checkRegistrationStatus();
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

        // Check user already registered
        db.collection("EventRegistrations").document(registrationId)
                .get().addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {
                        btnregisterevent.setText("Registered");
                        btnregisterevent.setEnabled(false);

                        Toast.makeText(this, "You have already registered for this event!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Map<String, Object> registration = new HashMap<>();

                    registration.put("userId", userId);
                    registration.put("eventId", eventId);
                    registration.put("eventTitle", title);
                    registration.put("eventDate", date);
                    registration.put("status", "Registered");
                    registration.put("registeredAt", FieldValue.serverTimestamp());

                    // Save registration in Firestore
                    db.collection("EventRegistrations").document(registrationId).set(registration)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                                btnregisterevent.setText("Registered");
                                btnregisterevent.setEnabled(false);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Unable to check registration: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    //if user already register then btn change to registered
    private void checkRegistrationStatus(){
        if (auth.getCurrentUser() == null || eventId == null){
            return;
        }
        String userId = auth.getCurrentUser().getUid();
        String registrationId = userId + "_" + eventId;

        db.collection("EventRegistrations").document(registrationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()){
                        btnregisterevent.setText("Registered");
                        btnregisterevent.setEnabled(false);
                    }
                    else {
                        btnregisterevent.setText("Register Now");
                        btnregisterevent.setEnabled(true);
                    }
                }).addOnFailureListener(e -> {
                    btnregisterevent.setText("Register Now");
                    btnregisterevent.setEnabled(true);
                    Toast.makeText(this, "Unable to check registration status", Toast.LENGTH_SHORT).show();
                });
    }
}
