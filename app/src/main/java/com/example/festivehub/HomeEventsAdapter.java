package com.example.festivehub;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeEventsAdapter extends RecyclerView.Adapter<HomeEventsAdapter.EventViewHolder> {

    Context context;
    ArrayList<Event> eventList;

    public HomeEventsAdapter(Context context, ArrayList<Event> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_home_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {

        Event event = eventList.get(position);

        holder.txthomeeventtitle.setText(event.title);
        holder.txthomeeventcategory.setText(event.category);
        holder.txthomeeventdate.setText(event.date + " | " + event.location);

        if (event.image != 0) {
            holder.imgevent.setImageResource(event.image);
        } else {
            holder.imgevent.setImageResource(android.R.color.darker_gray);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);

            intent.putExtra("eventId", event.id);
            intent.putExtra("eventTitle", event.title);
            intent.putExtra("eventCategory", event.category);
            intent.putExtra("eventDate", event.date);
            intent.putExtra("eventTime", event.time);
            intent.putExtra("eventLocation", event.location);
            intent.putExtra("eventDescription", event.description);
            intent.putExtra("eventImage", event.image);

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        ImageView imgevent;
        TextView txthomeeventtitle, txthomeeventcategory, txthomeeventdate;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            imgevent = itemView.findViewById(R.id.imgevent);
            txthomeeventtitle = itemView.findViewById(R.id.txthomeeventtitle);
            txthomeeventcategory = itemView.findViewById(R.id.txthomeeventcategory);
            txthomeeventdate = itemView.findViewById(R.id.txthomeeventdate);
        }
    }
}