package com.example.festivehub;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditVolunteerActivity extends AppCompatActivity {
    EditText edtName, edtEmail, edtMobile, edtCollege;
    TextView txtTitle;
    AppCompatButton btnSave;
    FirebaseFirestore db;
    String volunteerId;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_volunteer);

        db = FirebaseFirestore.getInstance();

        txtTitle = findViewById(R.id.txtVolunteerFormTitle);

        edtName = findViewById(R.id.edtVolunteerName);
        edtEmail = findViewById(R.id.edtVolunteerEmail);
        edtMobile = findViewById(R.id.edtVolunteerMobile);
        edtCollege = findViewById(R.id.edtVolunteerCollege);

        btnSave = findViewById(R.id.btnSaveVolunteer);

        volunteerId = getIntent().getStringExtra("volunteerId");

        if (volunteerId != null && !volunteerId.isEmpty()) {

            isEdit = true;

            txtTitle.setText("Edit Volunteer");
            btnSave.setText("Update Volunteer");

            edtName.setText(getIntent().getStringExtra("volunteerName"));
            edtEmail.setText(getIntent().getStringExtra("volunteerEmail"));
            edtMobile.setText(getIntent().getStringExtra("volunteerMobile"));
            edtCollege.setText(getIntent().getStringExtra("volunteerCollege"));
        }
        btnSave.setOnClickListener(v -> saveVolunteer());
    }
    private void saveVolunteer() {

        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String college = edtCollege.getText().toString().trim();
        if (name.isEmpty()) {
            edtName.setError("Enter volunteer name");
            return;
        }
        if (email.isEmpty()) {
            edtEmail.setError("Enter email");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Enter a valid email");
            return;
        }
        if (mobile.isEmpty()) {
            edtMobile.setError("Enter mobile number");
            return;
        }
        if (!mobile.matches("[0-9]{10}")) {
            edtMobile.setError("Enter a valid 10-digit mobile number");
            return;
        }
        if (college.isEmpty()) {
            edtCollege.setError("Enter college name");
            return;
        }
        Map<String, Object> volunteer = new HashMap<>();

        // Save each value using its correct Firestore field name.
        volunteer.put("name", name);
        volunteer.put("email", email);
        volunteer.put("mobile", mobile);
        volunteer.put("college", college);

        btnSave.setEnabled(false);

        if (isEdit) {
            db.collection("Volunteers")
                    .document(volunteerId)
                    .update(volunteer)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Volunteer updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });

        } else {
            volunteer.put("createdAt", FieldValue.serverTimestamp());

            db.collection("Volunteers")
                    .add(volunteer)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Volunteer added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        btnSave.setEnabled(true);
                        Toast.makeText(this, "Failed to add volunteer: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}
