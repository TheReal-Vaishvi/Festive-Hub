package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;

public class AdminLoginActivity extends AppCompatActivity {
    TextView txtbacktologin;
    EditText edtadminemail, edtadminpw;
    Button btnadminlogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        // Initialize Firebase Authentication and Firestore
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        edtadminemail = findViewById(R.id.edtadminemail);
        edtadminpw = findViewById(R.id.edtadminpw);
        btnadminlogin = findViewById(R.id.btnadminlogin);
        txtbacktologin = findViewById(R.id.txtbacktologin);

        txtbacktologin.setOnClickListener(view -> {
            finish();
        });

        btnadminlogin.setOnClickListener(view -> {
            adminLogin();
        });
    }

    private void adminLogin() {
        String email = edtadminemail.getText().toString().trim();
        String password = edtadminpw.getText().toString().trim();

        //validations
        if (email.isEmpty()){
            edtadminemail.setError("Email is required");
            edtadminemail.requestFocus();
            return;
        }
        if (password.isEmpty()){
            edtadminpw.setError("Password is required");
            edtadminpw.requestFocus();
            return;
        }
        if (password.length() < 6){
            edtadminpw.setError("Password must be at least 6 characters");
            edtadminpw.requestFocus();
            return;
        }
        btnadminlogin.setEnabled(false);

        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                String uid = auth.getCurrentUser().getUid();

                db.collection("Admins").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                    btnadminlogin.setEnabled(true);

                    if (documentSnapshot.exists() && "admin".equals(documentSnapshot.getString("role"))){
                        Toast.makeText(this, "Admin Login Successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(AdminLoginActivity.this,AdminDashboardActivity.class);

                        startActivity(intent);
                        finish();
                    }
                    else {
                        auth.signOut();
                        Toast.makeText(this, "You do not have admin permissions", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    auth.signOut();
                    btnadminlogin.setEnabled(true);
                    Toast.makeText(AdminLoginActivity.this, "Error checking admin access: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
            else
            {
                btnadminlogin.setEnabled(true);

                Toast.makeText(this, "Admin Login failed: " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}