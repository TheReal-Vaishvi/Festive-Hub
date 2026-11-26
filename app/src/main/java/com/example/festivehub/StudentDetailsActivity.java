package com.example.festivehub;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudentDetailsActivity extends AppCompatActivity {

    TextView txtstudentid, txtstudentname,txtstudentemail, txtstudentmobile;
    TextView txtstudentcreatedat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_details);

        txtstudentid = findViewById(R.id.txtstudentid);
        txtstudentname = findViewById(R.id.txtstudentname);
        txtstudentemail = findViewById(R.id.txtstudentemail);
        txtstudentmobile = findViewById(R.id.txtstudentmobile);
        txtstudentcreatedat = findViewById(R.id.txtstudentcreatedat);

        // Receive student details from Intent
        String id = getIntent().getStringExtra("studentId");
        String name = getIntent().getStringExtra("studentName");
        String email = getIntent().getStringExtra("studentEmail");
        String mobile = getIntent().getStringExtra("studentMobile");

        long createdAtMillis = getIntent().getLongExtra(
                "studentCreatedAt", -1L
        );

        // Display student details
        txtstudentid.setText(id);
        txtstudentname.setText(name);
        txtstudentemail.setText(email);
        txtstudentmobile.setText(mobile);

        if (createdAtMillis != -1L) {
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
            );
            txtstudentcreatedat.setText(
                    sdf.format(new java.util.Date(createdAtMillis))
            );
        } else {
            txtstudentcreatedat.setText("Not available");
        }
    }
}
