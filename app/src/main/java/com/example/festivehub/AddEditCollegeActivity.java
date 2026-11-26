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

public class AddEditCollegeActivity extends AppCompatActivity {
    TextView txtCollegeFormTitle;
    EditText edtCollegeName, edtCollegeCity, edtCollegeState, edtCollegeAddress;
    AppCompatButton btnSaveCollege;
    FirebaseFirestore db;
    String collegeId;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_college);

        db = FirebaseFirestore.getInstance();

        txtCollegeFormTitle = findViewById(R.id.txtclgFormTitle);

        edtCollegeName = findViewById(R.id.edtclgName);
        edtCollegeCity = findViewById(R.id.edtclgCity);
        edtCollegeState = findViewById(R.id.edtclgState);
        edtCollegeAddress = findViewById(R.id.edtclgAddress);

        btnSaveCollege = findViewById(R.id.btnSaveCollege);

        collegeId = getIntent().getStringExtra("collegeId");

        if (collegeId != null && !collegeId.isEmpty()) {

            isEdit = true;

            txtCollegeFormTitle.setText("Edit College");
            btnSaveCollege.setText("Update College");

            edtCollegeName.setText(
                    getIntent().getStringExtra("collegeName"));

            edtCollegeCity.setText(
                    getIntent().getStringExtra("collegeCity"));

            edtCollegeState.setText(
                    getIntent().getStringExtra("collegeState"));

            edtCollegeAddress.setText(
                    getIntent().getStringExtra("collegeAddress"));
        }
        btnSaveCollege.setOnClickListener(v -> saveCollege());
    }

    private void saveCollege() {
        String collegeName = edtCollegeName.getText().toString().trim();
        String city = edtCollegeCity.getText().toString().trim();
        String state = edtCollegeState.getText().toString().trim();
        String address = edtCollegeAddress.getText().toString().trim();

        if (collegeName.isEmpty()) {
            edtCollegeName.setError("Enter college name");
            edtCollegeName.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            edtCollegeCity.setError("Enter city");
            edtCollegeCity.requestFocus();
            return;
        }

        if (state.isEmpty()) {
            edtCollegeState.setError("Enter state");
            edtCollegeState.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtCollegeAddress.setError("Enter college address");
            edtCollegeAddress.requestFocus();
            return;
        }

        Map<String, Object> college = new HashMap<>();

        college.put("collegeName", collegeName);
        college.put("city", city);
        college.put("state", state);
        college.put("address", address);

        if (isEdit) {
            db.collection("Colleges")
                    .document(collegeId)
                    .update(college)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "College updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            college.put("createdAt", FieldValue.serverTimestamp());

            db.collection("Colleges")
                    .add(college)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "College added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add college: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
