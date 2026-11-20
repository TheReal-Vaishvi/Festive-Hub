package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.text.InputType;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    EditText edtemail, edtpassword;
    Button btnlogin;
    TextView txtsignup, txtforgotpassword;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        edtemail = findViewById(R.id.edtemail);
        edtpassword = findViewById(R.id.edtpassword);
        btnlogin = findViewById(R.id.btnlogin);
        txtsignup = findViewById(R.id.txtsignup);
        txtforgotpassword = findViewById(R.id.txtforgotPassword);

        txtforgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgotPassword();
            }
        });

        // Login
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtemail.getText().toString().trim();
                String password = edtpassword.getText().toString().trim();

                if (email.isEmpty()) {
                    edtemail.setError("Please enter your email");
                }
                else if (password.isEmpty()) {
                    edtpassword.setError("Please enter your password");
                }
                else {
                    btnlogin.setEnabled(false);

                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, task ->
                    {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Login Successfully!!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    btnlogin.setEnabled(true);
                                    Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });

        // Sign Up
        txtsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);

                startActivity(intent);
            }
        });

    }
    private void forgotPassword() {

        EditText resetEmail = new EditText(this);
        resetEmail.setHint("Enter your registered email");
        resetEmail.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        resetEmail.setSingleLine(true);
        resetEmail.setText(edtemail.getText().toString().trim());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Forgot Password?");
        builder.setMessage("Enter your email to receive a password reset link.");
        builder.setView(resetEmail);

        builder.setPositiveButton("Send", (dialog, which) -> {
            String email = resetEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
            }
            else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Password reset email sent!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(MainActivity.this, "Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();

    }
}