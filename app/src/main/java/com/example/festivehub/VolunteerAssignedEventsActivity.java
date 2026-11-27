package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VolunteerAssignedEventsActivity extends AppCompatActivity {
    TextView txtbackassigned, txtassignedcount, txtnoassignedevents;
    RecyclerView recyclerviewassignedevents;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<EventAssignment> assignmentList = new ArrayList<>();
    EventAssignmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_assigned_events);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        txtbackassigned = findViewById(R.id.txtbackassigned);
        txtassignedcount = findViewById(R.id.txtassignedcount);
        txtnoassignedevents = findViewById(R.id.txtnoassignedevents);

        recyclerviewassignedevents = findViewById(R.id.recyclerviewassignedevents);

        recyclerviewassignedevents.setLayoutManager(
                new LinearLayoutManager(this)
        );
        adapter = new EventAssignmentAdapter(assignmentList);
        recyclerviewassignedevents.setAdapter(adapter);
        txtbackassigned.setOnClickListener(v -> finish());
        loadAssignedEvents();
    }
    private void loadAssignedEvents() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String volunteerEmail = currentUser.getEmail().toLowerCase();

        db.collection("EventAssignments")
                .whereEqualTo("volunteerEmail", volunteerEmail)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    assignmentList.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : querySnapshot.getDocuments()) {
                        EventAssignment assignment =
                                document.toObject(EventAssignment.class);
                        if (assignment != null) {
                            assignment.id = document.getId();
                            assignmentList.add(assignment);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    txtassignedcount.setText("Total Assigned Events: " + assignmentList.size());
                    if (assignmentList.isEmpty()) {
                        txtnoassignedevents.setVisibility(View.VISIBLE);
                        recyclerviewassignedevents.setVisibility(View.GONE);

                    } else {
                        txtnoassignedevents.setVisibility(View.GONE);
                        recyclerviewassignedevents.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unable to load assigned events: "
                                + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }
}