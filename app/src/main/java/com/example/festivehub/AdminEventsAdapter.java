package com.example.festivehub;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class AdminEventsAdapter extends RecyclerView.Adapter<AdminEventsAdapter.EventViewHolder> {
    private ArrayList<Event> eventList;
    private FirebaseFirestore db;
    public AdminEventsAdapter(ArrayList<Event> eventList) {
        this.eventList = eventList;
        db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new EventViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = eventList.get(position);

        holder.txteventtitle.setText(event.title);
        holder.txteventdate.setText(event.date);
        holder.txteventcategory.setText(event.category);

        // Edit Event
        holder.btneditevent.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), AddEditEventActivity.class);

            intent.putExtra("eventId", event.id);
            intent.putExtra("eventTitle", event.title);
            intent.putExtra("eventCategory", event.category);
            intent.putExtra("eventDate", event.date);
            intent.putExtra("eventTime", event.time);
            intent.putExtra("eventLocation", event.location);
            intent.putExtra("eventDescription", event.description);
            intent.putExtra("eventImageUrl",event.imageUrl);

            holder.itemView.getContext().startActivity(intent);
        });

        // Delete Event
        holder.btndeleteevent.setOnClickListener(v -> {

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Delete Event")
                    .setMessage("Are you sure you want to delete this event?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        db.collection("Events")
                                .document(event.id)
                                .delete()
                                .addOnSuccessListener(unused -> {int currentPosition = holder.getBindingAdapterPosition();
                                    if (currentPosition != RecyclerView.NO_POSITION) {
                                        eventList.remove(currentPosition);
                                        notifyItemRemoved(currentPosition);
                                        notifyItemRangeChanged(currentPosition, eventList.size());
                                    }
                                    Toast.makeText(holder.itemView.getContext(), "Event deleted successfully!", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(holder.itemView.getContext(), "Failed to delete event: "
                                            + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }
    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView txteventtitle, txteventdate, txteventcategory;
        Button btneditevent, btndeleteevent;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            txteventtitle = itemView.findViewById(R.id.txteventtitle);
            txteventdate = itemView.findViewById(R.id.txteventdate);
            txteventcategory = itemView.findViewById(R.id.txteventcategory);

            btneditevent = itemView.findViewById(R.id.btneditevent);
            btndeleteevent = itemView.findViewById(R.id.btndeleteevent);
        }
    }
}
