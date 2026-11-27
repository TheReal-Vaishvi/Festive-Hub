package com.example.festivehub;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;


public class AdminDashboardActivity extends AppCompatActivity {
    Button btnmanageevents, btnmanagestudents, btnmanagevolunteers, btnmanagecolleges, btnmanagenotifications, btnadminlogout;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        auth = FirebaseAuth.getInstance();

        //manage events
        btnmanageevents = findViewById(R.id.btnmanageevents);
        btnmanageevents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageEventsActivity.class);
            startActivity(intent);
        });

        //manage students
        btnmanagestudents = findViewById(R.id.btnmanagestudents);
        btnmanagestudents.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageStudentActivity.class);
            startActivity(intent);
        });
        //manage volunteers
        btnmanagevolunteers = findViewById(R.id.btnmanagevolunteers);
        btnmanagevolunteers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageVolunteerActivity.class);
            startActivity(intent);
        });
        //manage collage
        AppCompatButton btnManageColleges = findViewById(R.id.btnmanagecolleges);
        btnManageColleges.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageCollegeActivity.class);
            startActivity(intent);
        });
        //manage notification
        AppCompatButton btnManageNotifications = findViewById(R.id.btnmanagenotifications);
        btnManageNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboardActivity.this, ManageNotificationActivity.class);
            startActivity(intent);
        });

        // Logout
        btnadminlogout = findViewById(R.id.btnadminlogout);

        btnadminlogout.setOnClickListener(view -> {
            new AlertDialog.Builder(AdminDashboardActivity.this).setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("Logout", (dialog, which) -> {
                        auth.signOut();

                        Intent intent = new Intent(AdminDashboardActivity.this, MainActivity.class);

                        //clear previous activities
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .show();
        });
    }
}