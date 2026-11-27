package com.example.festivehub;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssignVolunteerActivity extends AppCompatActivity {
    Spinner spinnerassignevent, spinnervolunteer;
    AppCompatButton btnassignvolunteer;
    TextView txtbackassign;
    FirebaseFirestore db;
    FirebaseAuth auth;
    ArrayList<String> eventNames = new ArrayList<>();
    ArrayList<String> eventIds = new ArrayList<>();
    ArrayList<String> volunteerNames = new ArrayList<>();
    ArrayList<String> volunteerEmails = new ArrayList<>();
    ArrayList<String> volunteerIds = new ArrayList<>();
    String selectedEventId;
    String selectedVolunteerEmail;
    String selectedVolunteerName;
    String selectedEventTitle;
    String selectedEventDate;
    String selectedEventTime;
    String selectedEventLocation;
    boolean eventsLoaded = false;
    boolean volunteersLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_volunteer);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        spinnerassignevent = findViewById(R.id.spinnerassignevent);
        spinnervolunteer = findViewById(R.id.spinnervolunteer);
        btnassignvolunteer = findViewById(R.id.btnvolunteerassign);
        txtbackassign = findViewById(R.id.txtbackassign);

        txtbackassign.setOnClickListener(v -> finish());

        btnassignvolunteer.setEnabled(false);

        loadEvents();
        loadVolunteers();

        spinnerassignevent.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view,
                            int position, long id) {

                        if (position >= 0 && position < eventIds.size()) {
                            selectedEventId = eventIds.get(position);
                            selectedEventTitle = eventNames.get(position);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedEventId = null;
                    }
                }
        );
        spinnervolunteer.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent, View view,
                            int position, long id) {
                        if (position >= 0 && position < volunteerEmails.size()) {
                            selectedVolunteerEmail = volunteerEmails.get(position);
                            selectedVolunteerName = volunteerNames.get(position);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedVolunteerEmail = null;
                    }
                }
        );
        btnassignvolunteer.setOnClickListener(v -> assignVolunteer());
    }
    private void loadEvents() {
        db.collection("Events")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    eventNames.clear();
                    eventIds.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String title = document.getString("title");

                        if (title != null && !title.trim().isEmpty()) {

                            eventIds.add(document.getId());
                            eventNames.add(title);
                        }
                    }
                    if (eventNames.isEmpty()) {
                        Toast.makeText(this, "No events found.", Toast.LENGTH_SHORT).show();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            eventNames
                    );

                    adapter.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item
                    );

                    spinnerassignevent.setAdapter(adapter);

                    if (!eventIds.isEmpty()) {
                        selectedEventId = eventIds.get(0);
                        selectedEventTitle = eventNames.get(0);
                    }
                    eventsLoaded = true;
                    updateAssignButton();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unable to load events: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void loadVolunteers() {
        db.collection("Volunteers")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    volunteerNames.clear();
                    volunteerEmails.clear();
                    volunteerIds.clear();

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String name = document.getString("name");
                        String email = document.getString("email");

                        if (name != null && email != null
                                && !email.trim().isEmpty()) {

                            volunteerIds.add(document.getId());
                            volunteerNames.add(name);
                            volunteerEmails.add(email.trim());
                        }
                    }
                    ArrayList<String> displayNames = new ArrayList<>();

                    for (int i = 0; i < volunteerNames.size(); i++) {
                        displayNames.add(
                                volunteerNames.get(i) + " (" +
                                        volunteerEmails.get(i) + ")"
                        );
                    }

                    if (displayNames.isEmpty()) {
                        Toast.makeText(this, "No volunteers found.", Toast.LENGTH_SHORT).show();
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this, android.R.layout.simple_spinner_item, displayNames);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnervolunteer.setAdapter(adapter);

                    if (!volunteerEmails.isEmpty()) {
                        selectedVolunteerEmail = volunteerEmails.get(0);
                        selectedVolunteerName = volunteerNames.get(0);
                    }
                    volunteersLoaded = true;
                    updateAssignButton();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Unable to load volunteers: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private void updateAssignButton() {
        btnassignvolunteer.setEnabled(
                eventsLoaded
                        && volunteersLoaded
                        && !eventIds.isEmpty()
                        && !volunteerEmails.isEmpty()
        );
    }
    private void assignVolunteer() {
        if (selectedEventId == null || selectedVolunteerEmail == null) {
            Toast.makeText(this, "Please select an event and volunteer.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnassignvolunteer.setEnabled(false);

        // Read the selected event details from Firestore
        db.collection("Events")
                .document(selectedEventId)
                .get()
                .addOnSuccessListener(eventDocument -> {

                    if (!eventDocument.exists()) {
                        btnassignvolunteer.setEnabled(true);
                        Toast.makeText(this, "Selected event not found.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedEventTitle = eventDocument.getString("title");
                    selectedEventDate = eventDocument.getString("date");
                    selectedEventTime = eventDocument.getString("time");
                    selectedEventLocation = eventDocument.getString("location");
                    saveAssignment();
                })
                .addOnFailureListener(e -> {
                    btnassignvolunteer.setEnabled(true);
                    Toast.makeText(this, "Unable to get event details: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void saveAssignment() {
        String encodedEmail = Base64.encodeToString(
                selectedVolunteerEmail.toLowerCase().getBytes(StandardCharsets.UTF_8),
                Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING
        );

        String assignmentId = selectedEventId + "_" + encodedEmail;

        Map<String, Object> assignment = new HashMap<>();

        assignment.put("eventId", selectedEventId);
        assignment.put("eventTitle", selectedEventTitle);
        assignment.put("eventDate", selectedEventDate != null ? selectedEventDate : "");
        assignment.put("eventTime", selectedEventTime != null ? selectedEventTime : "");
        assignment.put("eventLocation", selectedEventLocation != null ? selectedEventLocation : "");
        assignment.put("volunteerEmail", selectedVolunteerEmail.toLowerCase());
        assignment.put("volunteerName", selectedVolunteerName);
        assignment.put("status", "Assigned");
        assignment.put("assignedAt", FieldValue.serverTimestamp());

        db.collection("EventAssignments")
                .document(assignmentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        btnassignvolunteer.setEnabled(true);
                        Toast.makeText(this, "This volunteer is already assigned to this event.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        db.collection("EventAssignments")
                                .document(assignmentId)
                                .set(assignment)
                                .addOnSuccessListener(unused -> {
                                    btnassignvolunteer.setEnabled(true);
                                    Toast.makeText(this, "Volunteer assigned successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    btnassignvolunteer.setEnabled(true);
                                    Toast.makeText(this, "Assignment failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    btnassignvolunteer.setEnabled(true);
                    Toast.makeText(this, "Unable to check assignment: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}