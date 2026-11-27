package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {
    Button btnpchangepassword, btnmyregisteredevents, btnlogout;
    TextView txtbackprofile;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();

        btnpchangepassword = findViewById(R.id.btnpchangepassword);
        btnmyregisteredevents = findViewById(R.id.btnmyregisteredevents);
        btnlogout = findViewById(R.id.btnlogout);
        txtbackprofile = findViewById(R.id.txtbackprofile);

        txtbackprofile.setOnClickListener(v -> {
            finish();
        });

        btnpchangepassword.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        btnmyregisteredevents.setOnClickListener(view -> {
            Intent intent = new Intent(ProfileActivity.this, RegisteredEventsActivity.class);
            startActivity(intent);
        });

        // Logout button
        btnlogout.setOnClickListener(v -> showLogoutDialog());
    }

    // Confirmation dialog before logout
    private void showLogoutDialog() {

        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Logout", (dialog, which) -> {
                    // Sign out from Firebase
                    auth.signOut();
                    // Navigate to Login screen
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
