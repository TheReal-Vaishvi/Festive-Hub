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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageCollegeActivity extends AppCompatActivity {

    EditText edtSearchCollege;
    AppCompatButton btnAddCollege;
    RecyclerView rvColleges;
    FirebaseFirestore db;
    ArrayList<College> allColleges = new ArrayList<>();
    ArrayList<College> filteredColleges = new ArrayList<>();
    CollegeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_college);

        db = FirebaseFirestore.getInstance();

        edtSearchCollege = findViewById(R.id.edtSearchclg);
        btnAddCollege = findViewById(R.id.btnAddclg);
        rvColleges = findViewById(R.id.rvColleges);

        rvColleges.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CollegeAdapter(this, filteredColleges, this::loadColleges);

        rvColleges.setAdapter(adapter);

        btnAddCollege.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCollegeActivity.this, AddEditCollegeActivity.class);
            startActivity(intent);
        });
        edtSearchCollege.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterColleges(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadColleges();
    }
    private void loadColleges() {
        db.collection("Colleges")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    allColleges.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                        String id = document.getId();
                        String collegeName = document.getString("collegeName");

                        String city = document.getString("city");
                        String state = document.getString("state");
                        String address = document.getString("address");

                        College college = new College(
                                id,
                                collegeName != null ? collegeName : "",
                                city != null ? city : "",
                                state != null ? state : "",
                                address != null ? address : ""
                        );
                        allColleges.add(college);
                    }
                    filterColleges(edtSearchCollege.getText().toString());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to load colleges: "
                        + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
    private void filterColleges(String query) {
        filteredColleges.clear();
        String searchText = query.toLowerCase().trim();
        for (College college : allColleges) {
            if (college.collegeName.toLowerCase().contains(searchText) || college.city.toLowerCase().contains(searchText)) {
                filteredColleges.add(college);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
