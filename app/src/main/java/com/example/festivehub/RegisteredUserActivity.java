package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RegisteredUserActivity extends AppCompatActivity {
    Spinner spinnerevents;
    RecyclerView recyclerviewregisteredusers;
    TextView txttotalregistered, txtnoregisteredusers, txtbackregistered;
    FirebaseFirestore db;
    ArrayList<String> eventNames = new ArrayList<>();
    ArrayList<String> eventIds = new ArrayList<>();
    ArrayList<RegisteredUser> userList = new ArrayList<>();
    RegisteredUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_user);

        db = FirebaseFirestore.getInstance();

        spinnerevents = findViewById(R.id.spinnerevents);
        recyclerviewregisteredusers = findViewById(R.id.rvregisteredusers);

        txttotalregistered = findViewById(R.id.txttotalregistered);
        txtnoregisteredusers = findViewById(R.id.txtnoregisterusers);
        txtbackregistered = findViewById(R.id.txtregisteredback);

        recyclerviewregisteredusers.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new RegisteredUserAdapter(userList);
        recyclerviewregisteredusers.setAdapter(adapter);

        //back button
        txtbackregistered.setOnClickListener(v -> finish());
        loadEvents();
    }

    // Load all events from Firestore
    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    eventNames.clear();
                    eventIds.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String eventId = document.getId();
                        String eventName = document.getString("title");

                        if (eventName != null && !eventName.isEmpty()) {
                            eventIds.add(eventId);
                            eventNames.add(eventName);
                        }
                    }
                    if (eventNames.isEmpty()) {
                        Toast.makeText(this, "No events found.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    ArrayAdapter<String> spinnerAdapter =
                            new ArrayAdapter<>(this, R.layout.item_spinner_selected, eventNames);

                    spinnerAdapter.setDropDownViewResource(
                            R.layout.item_spinner_event
                    );
                    spinnerevents.setAdapter(spinnerAdapter);
                    spinnerevents.setOnItemSelectedListener(
                            new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    loadRegisteredUsers(eventIds.get(position));
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                }
                            }
                    );
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load events: "
                        + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Load registered users for selected event
    private void loadRegisteredUsers(String eventId) {
        userList.clear();
        adapter.notifyDataSetChanged();

        txttotalregistered.setText("Total Registered Users: 0");
        txtnoregisteredusers.setVisibility(View.GONE);

        db.collection("EventRegistrations")
                .whereEqualTo("eventId", eventId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    txttotalregistered.setText("Total Registered Users: " + querySnapshot.size());
                    if (querySnapshot.isEmpty()) {
                        txtnoregisteredusers.setVisibility(View.VISIBLE);
                        return;
                    }
                    txtnoregisteredusers.setVisibility(View.GONE);

                    for (DocumentSnapshot registration : querySnapshot.getDocuments()) {
                        String userId = registration.getString("userId");

                        Long tickets = registration.getLong("ticketCount");
                        int ticketCount = tickets != null ? tickets.intValue() : 1;

                        String status = registration.getString("status");
                        if (status == null) {
                            status = "Registered";
                        }
                        String registeredAt = "N/A";
                        Timestamp timestamp = registration.getTimestamp("registeredAt");
                        if (timestamp != null) {
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                            registeredAt = sdf.format(date);
                        }
                        if (userId == null || userId.isEmpty()) {
                            continue;
                        }
                        String finalStatus = status;
                        String finalRegisteredAt = registeredAt;
                        int finalTicketCount = ticketCount;
                        // Get student details from Users collection
                        db.collection("Users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDocument -> {
                                    String name = "Student details unavailable";
                                    String email = "N/A";
                                    String mobile = "N/A";

                                    if (userDocument.exists()) {
                                        String studentName = userDocument.getString("name");
                                        String studentEmail = userDocument.getString("email");
                                        String studentMobile = userDocument.getString("mobile");
                                        if (studentName != null) {
                                            name = studentName;
                                        }
                                        if (studentEmail != null) {
                                            email = studentEmail;
                                        }
                                        if (studentMobile != null) {
                                            mobile = studentMobile;
                                        }
                                    }
                                    RegisteredUser user = new RegisteredUser(
                                            name,
                                            email,
                                            mobile,
                                            finalTicketCount,
                                            finalStatus,
                                            finalRegisteredAt
                                    );
                                    userList.add(user);
                                    adapter.notifyDataSetChanged();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(RegisteredUserActivity.this, "Unable to load student details.",
                                                Toast.LENGTH_SHORT).show()
                                );
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load registrations: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}