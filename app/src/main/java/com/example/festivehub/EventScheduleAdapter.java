package com.example.festivehub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class EventScheduleAdapter extends RecyclerView.Adapter<EventScheduleAdapter.ViewHolder> {
    ArrayList<EventSchedule> scheduleList;
    public EventScheduleAdapter(ArrayList<EventSchedule> scheduleList) {
        this.scheduleList = scheduleList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_schedule, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventSchedule schedule = scheduleList.get(position);

        holder.txtnumber.setText(String.valueOf(position + 1));
        holder.txttitle.setText(schedule.getTitle());

        holder.txttime.setText(
                schedule.getStartTime() + " - " + schedule.getEndTime()
        );
        holder.txtdescription.setText(schedule.getDescription());
    }
    @Override
    public int getItemCount() {
        return scheduleList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtnumber, txttitle, txttime, txtdescription;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtnumber = itemView.findViewById(R.id.txtschedulenumber);
            txttitle = itemView.findViewById(R.id.txtscheduletitle);
            txttime = itemView.findViewById(R.id.txtscheduletime);
            txtdescription = itemView.findViewById(R.id.txtscheduledescription);
        }
    }
}