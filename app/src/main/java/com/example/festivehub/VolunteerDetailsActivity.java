package com.example.festivehub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class VolunteerDetailsActivity extends AppCompatActivity {
    TextView txtName, txtEmail, txtMobile, txtCollege, txtCreatedAt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_details);

        txtName = findViewById(R.id.txtDetailVolunteerName);
        txtEmail = findViewById(R.id.txtDetailVolunteerEmail);
        txtMobile = findViewById(R.id.txtDetailVolunteerMobile);
        txtCollege = findViewById(R.id.txtDetailVolunteerCollege);
        txtCreatedAt = findViewById(R.id.txtDetailVolunteerCreatedAt);

        String name = getIntent().getStringExtra("volunteerName");
        String email = getIntent().getStringExtra("volunteerEmail");
        String mobile = getIntent().getStringExtra("volunteerMobile");
        String college = getIntent().getStringExtra("volunteerCollege");

        long createdAtMillis = getIntent().getLongExtra("volunteerCreatedAt", -1L);
        txtName.setText(name != null ? name : "Not available");
        txtEmail.setText(email != null ? email : "Not available");
        txtMobile.setText(mobile != null ? mobile : "Not available");
        txtCollege.setText(college != null ? college : "Not available");

        if (createdAtMillis != -1L) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            txtCreatedAt.setText(sdf.format(new Date(createdAtMillis)));
        } else {
            txtCreatedAt.setText("Not available");
        }
    }
}
