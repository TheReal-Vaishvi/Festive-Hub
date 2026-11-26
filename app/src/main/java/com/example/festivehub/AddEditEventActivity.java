package com.example.festivehub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditEventActivity extends AppCompatActivity {
    EditText edteventtitle, edteventcategory, edteventdate;
    EditText edteventtime, edteventlocation, edteventdescription;
    Button btnsaveevent;
    FirebaseFirestore db;
    String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);
        // Keyboard and ScrollView handling
        ScrollView scrollView = findViewById(R.id.addEventsScrollView);
        ViewCompat.setOnApplyWindowInsetsListener(scrollView, (view, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            Insets systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int bottomPadding = Math.max(imeInsets.bottom, systemInsets.bottom);
            view.setPadding(
                    view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getPaddingRight(),
                    bottomPadding
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(scrollView);
        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize EditTexts
        edteventtitle = findViewById(R.id.edteventtitle);
        edteventcategory = findViewById(R.id.edteventcategory);
        edteventdate = findViewById(R.id.edteventdate);
        edteventtime = findViewById(R.id.edteventtime);
        edteventlocation = findViewById(R.id.edteventlocation);
        edteventdescription = findViewById(R.id.edteventdescription);

        btnsaveevent = findViewById(R.id.btnsaveevent);
        // Check whether this is Add or Edit mode
        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null && !eventId.isEmpty()) {
            // Edit mode
            loadEventDetails();
            btnsaveevent.setText("Update Event");

        } else {
            // Add mode
            btnsaveevent.setText("Save Event");
        }
        btnsaveevent.setOnClickListener(v -> saveEvent());
    }
    // Load existing event details
    private void loadEventDetails() {
        edteventtitle.setText(getIntent().getStringExtra("eventTitle"));
        edteventcategory.setText(getIntent().getStringExtra("eventCategory"));
        edteventdate.setText(getIntent().getStringExtra("eventDate"));
        edteventtime.setText(getIntent().getStringExtra("eventTime"));
        edteventlocation.setText(getIntent().getStringExtra("eventLocation"));
        edteventdescription.setText(getIntent().getStringExtra("eventDescription"));
    }
    // Save or update event
    private void saveEvent() {
        String title = edteventtitle.getText().toString().trim();
        String category = edteventcategory.getText().toString().trim();
        String date = edteventdate.getText().toString().trim();
        String time = edteventtime.getText().toString().trim();
        String location = edteventlocation.getText().toString().trim();
        String description = edteventdescription.getText().toString().trim();

        // Field validation
        if (title.isEmpty() || category.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }
        // Store event details
        Map<String, Object> event = new HashMap<>();
        event.put("title", title);
        event.put("category", category);
        event.put("date", date);
        event.put("time", time);
        event.put("location", location);
        event.put("description", description);

        btnsaveevent.setEnabled(false);

        if (eventId != null && !eventId.isEmpty()) {
            // Update existing event
            db.collection("Events")
                    .document(eventId)
                    .update(event)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Event updated successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnsaveevent.setEnabled(true);
                        Toast.makeText(this, "Failed to update event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // Add new event
            db.collection("Events")
                    .add(event)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnsaveevent.setEnabled(true);
                        Toast.makeText(this, "Failed to add event: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}
