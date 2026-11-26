package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RegisteredEventsActivity extends AppCompatActivity {

    TextView txtbackregisteredevents, txtregisteredcount, txtnoregisteredevents;
    RecyclerView recyclerregisteredevents;
    FirebaseAuth auth;
    FirebaseFirestore db;
    ArrayList<EventRegistration> eventList;
    RegisteredEventsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_events);

        txtbackregisteredevents = findViewById(R.id.txtbackregisteredevents);
        txtregisteredcount = findViewById(R.id.txtregisteredcount);
        txtnoregisteredevents = findViewById(R.id.txtnoregisteredevents);
        recyclerregisteredevents = findViewById(R.id.recyclerregisteredevents);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        eventList = new ArrayList<>();
        adapter = new RegisteredEventsAdapter(this, eventList,txtregisteredcount);

        recyclerregisteredevents.setLayoutManager(new LinearLayoutManager(this));
        recyclerregisteredevents.setAdapter(adapter);

        txtbackregisteredevents.setOnClickListener(view -> finish());
        loadRegisteredEvents();
    }

    private void loadRegisteredEvents() {
        if (auth.getCurrentUser() == null){
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String userId = auth.getCurrentUser().getUid();

        db.collection("EventRegistrations").whereEqualTo("userId",userId)
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    eventList.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document :
                            queryDocumentSnapshots.getDocuments()) {

                        EventRegistration event = document.toObject(EventRegistration.class);

                        if (event != null) {
                            eventList.add(event);
                        }
                    }
                    adapter.notifyDataSetChanged();

                    int count = eventList.size();
                    txtregisteredcount.setText("Your Registrations (" + count + ")");

                    if (count == 0) {
                        txtnoregisteredevents.setVisibility(View.VISIBLE);
                        recyclerregisteredevents.setVisibility(View.GONE);
                    } else {
                        txtnoregisteredevents.setVisibility(View.GONE);
                        recyclerregisteredevents.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load registrations: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
