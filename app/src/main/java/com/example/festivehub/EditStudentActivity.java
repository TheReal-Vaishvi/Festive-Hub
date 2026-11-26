package com.example.festivehub;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditStudentActivity extends AppCompatActivity {
    EditText edtStudentName, edtStudentMobile;
    AppCompatButton btnUpdateStudent;
    FirebaseFirestore db;
    String studentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_student);

        db = FirebaseFirestore.getInstance();

        edtStudentName = findViewById(R.id.edtStudentName);
        edtStudentMobile = findViewById(R.id.edtStudentMobile);
        btnUpdateStudent = findViewById(R.id.btnUpdateStudent);

        // Receive existing student details
        studentId = getIntent().getStringExtra("studentId");

        String name = getIntent().getStringExtra("studentName");
        String mobile = getIntent().getStringExtra("studentMobile");

        edtStudentName.setText(name);
        edtStudentMobile.setText(mobile);

        btnUpdateStudent.setOnClickListener(v -> updateStudent());
    }

    private void updateStudent() {

        String name = edtStudentName.getText().toString().trim();
        String mobile = edtStudentMobile.getText().toString().trim();

        if (name.isEmpty()) {
            edtStudentName.setError("Enter student name");
            return;
        }

        if (mobile.isEmpty()) {
            edtStudentMobile.setError("Enter mobile number");
            return;
        }

        if (!mobile.matches("[0-9]{10}")) {
            edtStudentMobile.setError("Enter a valid 10-digit mobile number");
            return;
        }

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Student ID not found",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("mobile", mobile);

        btnUpdateStudent.setEnabled(false);

        db.collection("Users")
                .document(studentId)
                .update(updates)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Student details updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnUpdateStudent.setEnabled(true);

                    Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
