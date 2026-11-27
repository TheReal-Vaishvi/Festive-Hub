package com.example.festivehub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class EventAssignmentAdapter extends RecyclerView.Adapter<EventAssignmentAdapter.ViewHolder> {
    ArrayList<EventAssignment> assignmentList;
    public EventAssignmentAdapter(ArrayList<EventAssignment> assignmentList) {
        this.assignmentList = assignmentList;
    }
    //it will displays the assigned event cards on the volunteer side.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_assignment, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventAssignment assignment = assignmentList.get(position);

        holder.txttitle.setText(assignment.getEventTitle());
        holder.txtdate.setText("Date: " + assignment.getEventDate());
        holder.txttime.setText("Time: " + assignment.getEventTime());
        holder.txtlocation.setText("Location: " + assignment.getEventLocation());
        holder.txtstatus.setText("Status: " + assignment.getStatus());
    }
    @Override
    public int getItemCount() {
        return assignmentList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txttitle, txtdate, txttime, txtlocation, txtstatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txttitle = itemView.findViewById(R.id.txtTitleassignment);
            txtdate = itemView.findViewById(R.id.txtdateassignment);
            txttime = itemView.findViewById(R.id.txtTimeassignment);
            txtlocation = itemView.findViewById(R.id.txtlocationassignment);
            txtstatus = itemView.findViewById(R.id.txtstatusassignment);
        }
    }
}