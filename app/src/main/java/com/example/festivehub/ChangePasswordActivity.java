package com.example.festivehub;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText edtcurrentpassword, edtnewpassword, edtconfirmpassword;
    Button btnchangedpassword;
    TextView txtback;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        edtcurrentpassword = findViewById(R.id.edtcurrentpassword);
        edtnewpassword = findViewById(R.id.edtnewpassword);
        edtconfirmpassword = findViewById(R.id.edtconfirmpassword);

        btnchangedpassword = findViewById(R.id.btnchangepassword);
        txtback = findViewById(R.id.txtback);

        txtback.setOnClickListener(v -> {
            finish();
        });

        btnchangedpassword.setOnClickListener(view -> {
            String currentPassword = edtcurrentpassword.getText().toString().trim();
            String newpassword = edtnewpassword.getText().toString().trim();
            String confirmpassword = edtconfirmpassword.getText().toString().trim();

            if (currentPassword.isEmpty()){
                edtcurrentpassword.setError("Enter Current Password");
                edtcurrentpassword.requestFocus();
                return;
            }
            if (newpassword.isEmpty()){
                edtnewpassword.setError("Enter New Password");
                edtnewpassword.requestFocus();
                return;
            }
            if (confirmpassword.isEmpty()){
                edtconfirmpassword.setError("Confirm New Password");
                edtconfirmpassword.requestFocus();
                return;
            }
            if (newpassword.length()<6){
                edtnewpassword.setError("Password must be atleast 6 characters long");
                edtnewpassword.requestFocus();
                return;
            }
            if (newpassword.equals(currentPassword)){
                edtnewpassword.setError("New Password must be different");
                edtnewpassword.requestFocus();
                return;
            }
            if (!newpassword.equals(confirmpassword)){
                edtconfirmpassword.setError("Password do not match");
                edtconfirmpassword.requestFocus();
                return;
            }
            if (user == null || user.getEmail() == null){
                Toast.makeText(ChangePasswordActivity.this, "User not logges in", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user.updatePassword(newpassword).addOnCompleteListener(updateTask->{
                        if (updateTask.isSuccessful()){
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            Toast.makeText(
                                    ChangePasswordActivity.this, "Failed to update password: "
                                            + updateTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(ChangePasswordActivity.this, "Current Password is incorrect", Toast.LENGTH_SHORT).show();
                }
            });
        });

    }
}