package com.example.festivehub;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddEditNotificationActivity extends AppCompatActivity {
    TextView txtNotificationFormTitle;
    EditText edtNotificationTitle, edtNotificationMessage;
    AppCompatButton btnSaveNotification;
    FirebaseFirestore db;
    String notificationId;
    boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_notification);

        db = FirebaseFirestore.getInstance();

        txtNotificationFormTitle = findViewById(R.id.txtFormTitleNotification);
        edtNotificationTitle = findViewById(R.id.edtNofiticationtitle);
        edtNotificationMessage = findViewById(R.id.edtmsgnotification);
        btnSaveNotification = findViewById(R.id.btnsavenotification);

        notificationId = getIntent().getStringExtra("notificationId");

        if (notificationId != null && !notificationId.isEmpty()) {
            isEdit = true;
            txtNotificationFormTitle.setText("Edit Notification");
            btnSaveNotification.setText("Update Notification");

            edtNotificationTitle.setText(getIntent().getStringExtra("notificationTitle"));
            edtNotificationMessage.setText(getIntent().getStringExtra("notificationMessage"));
        }
        btnSaveNotification.setOnClickListener(v -> saveNotification());
    }

    private void saveNotification() {
        String title = edtNotificationTitle.getText().toString().trim();
        String message = edtNotificationMessage.getText().toString().trim();

        if (title.isEmpty()) {
            edtNotificationTitle.setError("Enter notification title");
            edtNotificationTitle.requestFocus();
            return;
        }

        if (message.isEmpty()) {
            edtNotificationMessage.setError("Enter notification message");
            edtNotificationMessage.requestFocus();
            return;
        }
        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("message", message);
        if (isEdit) {
            notification.put("updatedAt", FieldValue.serverTimestamp());

            db.collection("Notifications").document(notificationId).update(notification)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Notification updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());

        } else {
            notification.put("createdAt", FieldValue.serverTimestamp());
            notification.put("updatedAt", FieldValue.serverTimestamp());

            db.collection("Notifications").add(notification)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Notification added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to add notification: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
