package com.example.festivehub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    Context context;
    ArrayList<AppNotification> notificationList;
    Runnable onNotificationDeleted;

    public NotificationAdapter(Context context, ArrayList<AppNotification> notificationList, Runnable onNotificationDeleted) {
        this.context = context;
        this.notificationList = notificationList;
        this.onNotificationDeleted = onNotificationDeleted;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {

        AppNotification notification = notificationList.get(position);
        holder.txtNotificationTitle.setText(notification.title);
        holder.txtNotificationMessage.setText(notification.message);

        holder.btnViewNotification.setOnClickListener(v -> {

            new AlertDialog.Builder(context).setTitle(notification.title).setMessage(notification.message).setPositiveButton("OK", null)
                    .show();
        });

        holder.btnEditNotification.setOnClickListener(v -> {

            Intent intent = new Intent(context, AddEditNotificationActivity.class);
            intent.putExtra("notificationId", notification.id);
            intent.putExtra("notificationTitle", notification.title);
            intent.putExtra("notificationMessage", notification.message);

            context.startActivity(intent);
        });

        holder.btnDeleteNotification.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Notification")
                    .setMessage("Are you sure you want to delete this notification?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        FirebaseFirestore.getInstance()
                                .collection("Notifications")
                                .document(notification.id)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "Notification deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (onNotificationDeleted != null) {
                                        onNotificationDeleted.run();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    @Override
    public int getItemCount() {
        return notificationList.size();
    }
    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView txtNotificationTitle, txtNotificationMessage;
        AppCompatButton btnViewNotification, btnEditNotification, btnDeleteNotification;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNotificationTitle = itemView.findViewById(R.id.txtTitlenotifi);
            txtNotificationMessage = itemView.findViewById(R.id.txtmsgnotifi);
            btnViewNotification = itemView.findViewById(R.id.btnviewnotification);
            btnEditNotification = itemView.findViewById(R.id.btneditnotification);

            btnDeleteNotification = itemView.findViewById(R.id.btndeletenotification);
        }
    }
}
