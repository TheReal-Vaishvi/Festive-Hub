package com.example.festivehub;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ManageStudentActivity extends AppCompatActivity {
    EditText edtsearchstudent;
    RecyclerView rvstudents;
    FirebaseFirestore db;
    ArrayList<Student> studentList;
    StudentAdapter adapter;
    ArrayList<Student> allStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_student);

        db = FirebaseFirestore.getInstance();
        edtsearchstudent = findViewById(R.id.edtSearchstudent);
        rvstudents = findViewById(R.id.rvstudents);
        rvstudents.setLayoutManager(
                new LinearLayoutManager(this)
        );

        studentList = new ArrayList<>();
        adapter = new StudentAdapter(this, studentList);
        rvstudents.setAdapter(adapter);
        //load screen after deleting the user
        adapter.setOnStudentDeletedListener(this::loadStudents);

        allStudents = new ArrayList<>();

        edtsearchstudent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudents(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadStudents();
    }
    private void filterStudents(String searchText) {
        ArrayList<Student> filteredList = new ArrayList<>();

        for (Student student : allStudents) {

            if ((student.name != null &&
                    student.name.toLowerCase().contains(searchText.toLowerCase())) ||

                    (student.email != null &&
                            student.email.toLowerCase().contains(searchText.toLowerCase()))) {

                filteredList.add(student);
            }
        }
        adapter.filterList(filteredList);
    }

    private void loadStudents() {
        db.collection("Users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    studentList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String id = document.getId();

                        String name = document.getString("name");
                        String email = document.getString("email");
                        String mobile = document.getString("mobile");
                        Object createdAt = document.getTimestamp("createdAt");
                        Student student = new Student(
                                id,
                                name,
                                email,
                                mobile,
                                createdAt
                        );
                        studentList.add(student);
                    }
                    allStudents.clear();
                    allStudents.addAll(studentList);

                    adapter.filterList(new ArrayList<>(allStudents));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ManageStudentActivity.this, "Failed to load students: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
