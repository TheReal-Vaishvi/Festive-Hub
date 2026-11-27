package com.example.festivehub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class RegisteredUserAdapter extends RecyclerView.Adapter<RegisteredUserAdapter.ViewHolder> {
    ArrayList<RegisteredUser> userList;
    public RegisteredUserAdapter(ArrayList<RegisteredUser> userList) {
        this.userList = userList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registered_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RegisteredUser user = userList.get(position);

        holder.txtName.setText(user.getName());
        holder.txtEmail.setText("Email: " + user.getEmail());
        holder.txtMobile.setText("Mobile: " + user.getMobile());
        holder.txtTickets.setText("Tickets Booked: " + user.getTicketCount());
        holder.txtStatus.setText("Status: " + user.getStatus());
        holder.txtDate.setText("Registered On: " + user.getRegisteredAt());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtMobile, txtTickets, txtStatus, txtDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtnameregistered);
            txtEmail = itemView.findViewById(R.id.txtemailregistered);
            txtMobile = itemView.findViewById(R.id.txtmobileregistered);
            txtTickets = itemView.findViewById(R.id.txtTicketregistered);
            txtStatus = itemView.findViewById(R.id.txtstatusregistered);
            txtDate = itemView.findViewById(R.id.txtdateregistered);
        }
    }
}