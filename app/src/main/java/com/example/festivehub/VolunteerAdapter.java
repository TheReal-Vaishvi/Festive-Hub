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

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder> {
    private Context context;
    private ArrayList<Volunteer> volunteerList;
    private FirebaseFirestore db;
    private Runnable onVolunteerDeleted;
    public void setOnVolunteerDeletedListener(Runnable listener) {
        onVolunteerDeleted = listener;
    }
    public VolunteerAdapter(Context context, ArrayList<Volunteer> volunteerList) {
        this.context = context;
        this.volunteerList = volunteerList;
        db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public VolunteerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_volunteer, parent, false);
        return new VolunteerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunteerViewHolder holder, int position) {

        Volunteer volunteer = volunteerList.get(position);

        holder.txtName.setText(volunteer.name != null ? volunteer.name : "");
        holder.txtEmail.setText(volunteer.email != null ? volunteer.email : "");
        holder.txtMobile.setText(volunteer.mobile != null ? volunteer.mobile : "");

        // View Details
        holder.btnView.setOnClickListener(v -> {

            Intent intent = new Intent(context, VolunteerDetailsActivity.class);

            intent.putExtra("volunteerId", volunteer.id);
            intent.putExtra("volunteerName", volunteer.name);
            intent.putExtra("volunteerEmail", volunteer.email);
            intent.putExtra("volunteerMobile", volunteer.mobile);
            intent.putExtra("volunteerCollege", volunteer.college);

            long createdAtMillis = -1L;

            if (volunteer.createdAt instanceof Timestamp) {
                createdAtMillis = ((Timestamp) volunteer.createdAt).toDate().getTime();
            }
            intent.putExtra("volunteerCreatedAt", createdAtMillis);
            context.startActivity(intent);
        });
        // Edit Volunteer
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditVolunteerActivity.class);

            intent.putExtra("volunteerId", volunteer.id);
            intent.putExtra("volunteerName", volunteer.name);
            intent.putExtra("volunteerEmail", volunteer.email);
            intent.putExtra("volunteerMobile", volunteer.mobile);
            intent.putExtra("volunteerCollege", volunteer.college);

            context.startActivity(intent);
        });

        // Delete Volunteer
        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Volunteer")
                    .setMessage("Are you sure you want to delete " + volunteer.name + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        holder.btnDelete.setEnabled(false);
                        db.collection("Volunteers")
                                .document(volunteer.id)
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(context, "Volunteer deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (onVolunteerDeleted != null) {
                                        onVolunteerDeleted.run();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    holder.btnDelete.setEnabled(true);
                                    Toast.makeText(context, "Delete failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .setNegativeButton("Cancel", (dialog, which) ->
                            dialog.dismiss()).show();
        });
    }
    @Override
    public int getItemCount() {
        return volunteerList.size();
    }
    public void filterList(ArrayList<Volunteer> filteredList) {
        volunteerList = filteredList;
        notifyDataSetChanged();
    }
    public static class VolunteerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtEmail, txtMobile;
        AppCompatButton btnView, btnEdit, btnDelete;
        public VolunteerViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtVolunteername);
            txtEmail = itemView.findViewById(R.id.txtVolunteeremail);
            txtMobile = itemView.findViewById(R.id.txtVolunteermobile);

            btnView = itemView.findViewById(R.id.btnViewvolunteer);
            btnEdit = itemView.findViewById(R.id.btnEditvolunteer);
            btnDelete = itemView.findViewById(R.id.btnDeletevolunteer);
        }
    }
}
