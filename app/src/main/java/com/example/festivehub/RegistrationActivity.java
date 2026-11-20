package com.example.festivehub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText edtname, edtemailaddress, edtnumber, edtpassword, edtconfirmpassword;
    Button btnregister;
    TextView txtlogin;

    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtname = findViewById(R.id.edtname);
        edtemailaddress = findViewById(R.id.edtemail);
        edtnumber = findViewById(R.id.edtnumber);
        edtpassword = findViewById(R.id.edtpassword);
        edtconfirmpassword = findViewById(R.id.edtconfirmpassword);

        btnregister = findViewById(R.id.btnregister);
        txtlogin = findViewById(R.id.txtlogin);

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtname.getText().toString().trim();
                String email = edtemailaddress.getText().toString().trim();
                String number = edtnumber.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();
                String confirmpassword = edtconfirmpassword.getText().toString().trim();

                if (name.isEmpty()) {
                    edtname.setError("Enter your name");
                }
                else if (email.isEmpty()) {
                    edtemailaddress.setError("Enter your email");
                }
                else if (number.isEmpty()) {
                    edtnumber.setError("Enter your number");
                }
                else if (number.length() != 10) {
                    edtnumber.setError("Enter a valid 10 digit number");
                }
                else if (password.isEmpty()) {
                    edtpassword.setError("Enter your password");
                }
                else if (confirmpassword.isEmpty()) {
                    edtconfirmpassword.setError("Confirm your password");
                }
                else if (password.length() < 6) {
                    edtpassword.setError("Password must contain at least 6 characters");
                }
                else if (!password.equals(confirmpassword)) {
                    edtconfirmpassword.setError("Passwords do not match");
                }
                else {
                    registerUser(name, email, number, password);
                }
            }
        });

        //login
        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);

                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser(String name, String email, String number, String password) {

        btnregister.setEnabled(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        String uid = auth.getCurrentUser().getUid();

                        Map<String, Object> user = new HashMap<>();

                        user.put("name", name);
                        user.put("email", email);
                        user.put("mobile", number);
                        user.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("Users")
                                .document(uid)
                                .set(user)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(RegistrationActivity.this,
                                            "Registration successful!",
                                            Toast.LENGTH_SHORT).show();

                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    btnregister.setEnabled(true);

                                    Toast.makeText(RegistrationActivity.this,
                                            "Profile saving failed: " + e.getMessage(),
                                            Toast.LENGTH_LONG).show();
                                });

                    } else {

                        btnregister.setEnabled(true);

                        Toast.makeText(RegistrationActivity.this,
                                "Registration failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }
}