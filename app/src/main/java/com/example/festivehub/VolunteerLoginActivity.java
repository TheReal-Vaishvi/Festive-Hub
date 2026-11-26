package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class VolunteerLoginActivity extends AppCompatActivity {
    EditText edtEmail, edtPassword;
    AppCompatButton btnLogin;
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_login);

        edtEmail = findViewById(R.id.edtvolunteeremail);
        edtPassword = findViewById(R.id.edtvolunteerpassword);
        btnLogin = findViewById(R.id.btnvolunteerlogin);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        btnLogin.setOnClickListener(v -> loginVolunteer());
    }

    private void loginVolunteer() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (email.isEmpty()) {
            edtEmail.setError("Enter email");
            edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            edtPassword.setError("Enter password");
            edtPassword.requestFocus();
            return;
        }
        btnLogin.setEnabled(false);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String loginEmail = auth.getCurrentUser().getEmail();
                        db.collection("Volunteers").whereEqualTo("email", loginEmail).limit(1).get()
                                .addOnSuccessListener(querySnapshot -> {

                                    if (!querySnapshot.isEmpty()) {
                                        Toast.makeText(VolunteerLoginActivity.this, "Welcome Volunteer!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(VolunteerLoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        auth.signOut();
                                        Toast.makeText(VolunteerLoginActivity.this, "You are not registered as a volunteer.",
                                                Toast.LENGTH_LONG).show();
                                        btnLogin.setEnabled(true);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    auth.signOut();
                                    btnLogin.setEnabled(true);
                                    Toast.makeText(VolunteerLoginActivity.this, "Unable to verify volunteer: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });

                    } else {
                        btnLogin.setEnabled(true);
                        Toast.makeText(VolunteerLoginActivity.this, "Invalid email or password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
