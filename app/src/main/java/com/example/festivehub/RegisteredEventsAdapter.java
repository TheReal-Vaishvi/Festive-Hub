package com.example.festivehub;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RegisteredEventsAdapter extends RecyclerView.Adapter<RegisteredEventsAdapter.EventViewHolder> {

    Context context;
    ArrayList<EventRegistration> eventList;
    TextView txtregisteredcount;

    public RegisteredEventsAdapter(Context context, ArrayList<EventRegistration> eventList,
                                   TextView txtregisteredcount) {
        this.context = context;
        this.eventList = eventList;
        this.txtregisteredcount = txtregisteredcount;
    }

    @NonNull
    @Override
    public RegisteredEventsAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registered_event,parent,false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegisteredEventsAdapter.EventViewHolder holder, int position) {
        EventRegistration event = eventList.get(position);

        holder.txteventcardtitle.setText(event.getEventTitle());
        holder.txteventcarddate.setText(event.getEventDate());
        holder.txteventcardstatus.setText(event.getStatus());

        int tickets = event.getTicketCount();
        if (tickets <= 0) {
            tickets = 1;
        }
        holder.txtticketcount.setText("Tickets: " + tickets);

        holder.btneventedit.setOnClickListener(view -> {
            showEditDialog(event);
        });

        holder.btneventcancle.setOnClickListener(view -> {
            showCancelDialog(event);
        });
    }

    //cancel registration button
    private void showCancelDialog(EventRegistration event) {
        new AlertDialog.Builder(context).setTitle("Cancle Registration?").setMessage("Are you sure you want to cancel your registration for "
        +event.getEventTitle() + "?").setNegativeButton("No",(dialog,which)->{dialog.dismiss();
        })
                .setPositiveButton("Yes",(dialog,which)->{
                    deleteregistration(event);
                })
                .show();
    }

    //delete fun method
    private void deleteregistration(EventRegistration event) {
            String documentId = event.getUserId() + "_" + event.getEventId();

            FirebaseFirestore.getInstance()
                    .collection("EventRegistrations")
                    .document(documentId)
                    .delete()
                    .addOnSuccessListener(unused -> {
                        int position = eventList.indexOf(event);
                        if (position != -1) {
                            eventList.remove(position);

                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, eventList.size());

                            txtregisteredcount.setText(
                                    "Your Registrations (" + eventList.size() + ")"
                            );
                        }
                        Toast.makeText(context, "Registration cancelled successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Cancellation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
    }

    private void showEditDialog(EventRegistration event) {
        NumberPicker numberPicker = new NumberPicker(eventList.isEmpty() ? null : context);

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(5);

        int currentTickets = event.getTicketCount();

        if (currentTickets < 1 || currentTickets > 5) {
            currentTickets = 1;
        }

        numberPicker.setValue(currentTickets);

        new AlertDialog.Builder(context)
                .setTitle("Edit Registration")
                .setMessage("Select number of tickets")
                .setView(numberPicker)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Save Changes", (dialog, which) -> {

                    int selectedTickets = numberPicker.getValue();

                    updateTicketCount(event, selectedTickets);
                })
                .show();
    }

    private void updateTicketCount(EventRegistration event, int selectedTickets) {
        String documentId = event.getUserId() + "_" + event.getEventId();

        FirebaseFirestore.getInstance()
                .collection("EventRegistrations")
                .document(documentId)
                .update("ticketCount", selectedTickets)
                .addOnSuccessListener(unused -> {

                    event.setTicketCount(selectedTickets);

                    notifyDataSetChanged();

                    Toast.makeText(context, "Ticket quantity updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
    public class EventViewHolder extends RecyclerView.ViewHolder {

        TextView txteventcardtitle, txteventcarddate, txteventcardstatus,txtticketcount;
        Button btneventedit, btneventcancle;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            txteventcardtitle = itemView.findViewById(R.id.txteventcardtitle);
            txteventcarddate = itemView.findViewById(R.id.txteventcarddate);
            txteventcardstatus = itemView.findViewById(R.id.txteventcardstatus);
            txtticketcount = itemView.findViewById(R.id.txtticketcount);

            btneventcancle = itemView.findViewById(R.id.btneventcancel);
            btneventedit = itemView.findViewById(R.id.btneventedit);
        }
    }
}
