package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ManageEventsActivity extends AppCompatActivity {

    RecyclerView recyclerviewevents;
    Button btnaddevent;
    FirebaseFirestore db;
    ArrayList<Event> eventList;
    AdminEventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        db = FirebaseFirestore.getInstance();

        btnaddevent = findViewById(R.id.btnaddevent);
        recyclerviewevents = findViewById(R.id.recyclerviewevents);
        recyclerviewevents.setLayoutManager(new LinearLayoutManager(this));

        eventList = new ArrayList<>();
        adapter = new AdminEventsAdapter(eventList);
        recyclerviewevents.setAdapter(adapter);
        loadEvents();

        btnaddevent.setOnClickListener(view -> {
           Intent intent = new Intent(ManageEventsActivity.this,AddEditEventActivity.class);
           startActivity(intent);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }
    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();

                        String title = document.getString("title");
                        String category = document.getString("category");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String location = document.getString("location");
                        String description = document.getString("description");

                        Event event = new Event(id, 0, title, category, date, time, location, description);
                        //for image read
                        event.imageUrl = document.getString("imageUrl");
                        eventList.add(event);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageEventsActivity.this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
