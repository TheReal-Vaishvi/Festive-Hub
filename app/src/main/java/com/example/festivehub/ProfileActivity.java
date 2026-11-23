package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {
    Button btnpchangepassword;
    TextView txtbackprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnpchangepassword = findViewById(R.id.btnpchangepassword);
        txtbackprofile = findViewById(R.id.txtbackprofile);

        txtbackprofile.setOnClickListener(v->{
            finish();
        });

        btnpchangepassword.setOnClickListener(v->{
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
    }
}