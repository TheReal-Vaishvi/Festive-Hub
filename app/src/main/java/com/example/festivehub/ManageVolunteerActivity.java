package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ManageVolunteerActivity extends AppCompatActivity {

    EditText edtSearchvolunteer;
    AppCompatButton btnAddvolunteer;
    RecyclerView rvVolunteers;
    FirebaseFirestore db;
    ArrayList<Volunteer> volunteerList;
    ArrayList<Volunteer> allVolunteers;
    VolunteerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_volunteer);

        db = FirebaseFirestore.getInstance();

        edtSearchvolunteer = findViewById(R.id.edtSearchvolunteer);
        btnAddvolunteer = findViewById(R.id.btnAddvolunteer);
        rvVolunteers = findViewById(R.id.rvVolunteers);

        rvVolunteers.setLayoutManager(new LinearLayoutManager(this));

        volunteerList = new ArrayList<>();
        allVolunteers = new ArrayList<>();
        adapter = new VolunteerAdapter(this, volunteerList);
        rvVolunteers.setAdapter(adapter);
        adapter.setOnVolunteerDeletedListener(this::loadVolunteers);

        btnAddvolunteer.setOnClickListener(v -> {
            Intent intent = new Intent(ManageVolunteerActivity.this, AddEditVolunteerActivity.class);
            startActivity(intent);
        });
        edtSearchvolunteer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVolunteers(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadVolunteers();
    }
    private void loadVolunteers() {

        db.collection("Volunteers")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    volunteerList.clear();

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String id = document.getId();
                        String name = document.getString("name");
                        String email = document.getString("email");
                        String mobile = document.getString("mobile");
                        String college = document.getString("college");

                        // Correct values in memory if name and email were saved swapped.
                        if (name != null && email != null
                                && name.contains("@")
                                && !email.contains("@")) {
                            String temp = name;
                            name = email;
                            email = temp;
                        }
                        Object createdAt = document.get("createdAt");
                        Volunteer volunteer = new Volunteer(
                                id,
                                name,
                                email,
                                mobile,
                                college,
                                createdAt
                        );
                        volunteerList.add(volunteer);
                    }

                    allVolunteers.clear();
                    allVolunteers.addAll(volunteerList);

                    filterVolunteers(edtSearchvolunteer.getText().toString());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load volunteers: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    private void filterVolunteers(String searchText) {
        ArrayList<Volunteer> filteredList = new ArrayList<>();
        for (Volunteer volunteer : allVolunteers) {
            if ((volunteer.name != null &&
                    volunteer.name.toLowerCase().contains(searchText.toLowerCase())) ||
                    (volunteer.email != null && volunteer.email.toLowerCase().contains(searchText.toLowerCase()))) {
                filteredList.add(volunteer);
            }
        }
        adapter.filterList(filteredList);
    }
}
