package com.example.festivehub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ManageNotificationActivity extends AppCompatActivity {
    EditText edtSearchNotification;
    AppCompatButton btnAddNotification;
    RecyclerView rvNotifications;
    FirebaseFirestore db;
    ArrayList<AppNotification> allNotifications = new ArrayList<>();
    ArrayList<AppNotification> filteredNotifications = new ArrayList<>();
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_notification);

        db = FirebaseFirestore.getInstance();

        edtSearchNotification = findViewById(R.id.edtsearchnoti);
        btnAddNotification = findViewById(R.id.btnaddnoti);
        rvNotifications = findViewById(R.id.rvNotifications);

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        adapter = new NotificationAdapter(this, filteredNotifications, this::loadNotifications);
        rvNotifications.setAdapter(adapter);

        btnAddNotification.setOnClickListener(v -> {
            Intent intent = new Intent(ManageNotificationActivity.this, AddEditNotificationActivity.class);
            startActivity(intent);
        });

        edtSearchNotification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNotifications(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotifications();
    }
    private void loadNotifications() {
        db.collection("Notifications").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    allNotifications.clear();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        String id = document.getId();

                        String title = document.getString("title");
                        String message = document.getString("message");

                        AppNotification notification =
                                new AppNotification(
                                        id,
                                        title != null ? title : "",
                                        message != null ? message : ""
                                );
                        allNotifications.add(notification);
                    }
                    filterNotifications(edtSearchNotification.getText().toString());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load notifications: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void filterNotifications(String query) {
        filteredNotifications.clear();
        String searchText = query.toLowerCase().trim();

        for (AppNotification notification : allNotifications) {
            if (notification.title.toLowerCase().contains(searchText) || notification.message.toLowerCase().contains(searchText)) {
                filteredNotifications.add(notification);
            }
        }
        adapter.notifyDataSetChanged();
    }
}
