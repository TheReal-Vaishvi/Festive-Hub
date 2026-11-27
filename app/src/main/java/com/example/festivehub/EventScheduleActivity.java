package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class EventScheduleActivity extends AppCompatActivity {
    TextView txtbackschedule, txteventschedulename, txtscheduleeventdate, txtnoschedule;
    RecyclerView recyclerviewschedule;
    FirebaseFirestore db;
    ArrayList<EventSchedule> scheduleList = new ArrayList<>();
    EventScheduleAdapter adapter;
    String eventId, eventTitle, eventDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_schedule);

        db = FirebaseFirestore.getInstance();

        txtbackschedule = findViewById(R.id.txtbackschedule);
        txteventschedulename = findViewById(R.id.txteventschedulename);
        txtscheduleeventdate = findViewById(R.id.txtscheduleeventdate);
        txtnoschedule = findViewById(R.id.txtnoschedule);

        recyclerviewschedule = findViewById(R.id.recyclerviewschedule);

        eventId = getIntent().getStringExtra("eventId");
        eventTitle = getIntent().getStringExtra("eventTitle");
        eventDate = getIntent().getStringExtra("eventDate");

        if (eventId == null || eventId.isEmpty()) {
            Toast.makeText(this, "Event not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        txteventschedulename.setText(eventTitle != null ? eventTitle : "Event Schedule");

        txtscheduleeventdate.setText(eventDate != null ? "Date: " + eventDate : "");

        recyclerviewschedule.setLayoutManager(new LinearLayoutManager(this));

        adapter = new EventScheduleAdapter(scheduleList);
        recyclerviewschedule.setAdapter(adapter);

        //back button
        txtbackschedule.setOnClickListener(v -> finish());
        loadSchedule();
    }

    private void loadSchedule() {
        db.collection("Events")
                .document(eventId)
                .collection("Schedules")
                .orderBy("order")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    scheduleList.clear();

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {
                        EventSchedule schedule = document.toObject(
                                EventSchedule.class
                        );

                        if (schedule != null) {
                            schedule.id = document.getId();
                            scheduleList.add(schedule);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    if (scheduleList.isEmpty()) {
                        txtnoschedule.setVisibility(View.VISIBLE);
                        recyclerviewschedule.setVisibility(View.GONE);
                    } else {
                        txtnoschedule.setVisibility(View.GONE);
                        recyclerviewschedule.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(EventScheduleActivity.this, "Unable to load schedule: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}