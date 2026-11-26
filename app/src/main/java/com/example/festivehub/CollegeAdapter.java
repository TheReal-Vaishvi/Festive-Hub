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

public class CollegeAdapter extends RecyclerView.Adapter<CollegeAdapter.CollegeViewHolder> {

    Context context;
    ArrayList<College> collegeList;
    Runnable onCollegeDeleted;

    public CollegeAdapter(Context context, ArrayList<College> collegeList,
                          Runnable onCollegeDeleted) {
        this.context = context;
        this.collegeList = collegeList;
        this.onCollegeDeleted = onCollegeDeleted;
    }

    @NonNull
    @Override
    public CollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_college, parent, false);
        return new CollegeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollegeViewHolder holder, int position) {

        College college = collegeList.get(position);

        holder.txtCollegeName.setText(college.collegeName);
        holder.txtCollegeCity.setText(college.city + ", " + college.state);

        holder.btnViewCollege.setOnClickListener(v -> {

            String details = "College Name: " + college.collegeName
                    + "\n\nCity: " + college.city
                    + "\n\nState: " + college.state
                    + "\n\nAddress: " + college.address;

            new AlertDialog.Builder(context)
                    .setTitle("College Details")
                    .setMessage(details)
                    .setPositiveButton("OK", null)
                    .show();
        });

        holder.btnEditCollege.setOnClickListener(v -> {

            Intent intent = new Intent(context, AddEditCollegeActivity.class);

            intent.putExtra("collegeId", college.id);
            intent.putExtra("collegeName", college.collegeName);
            intent.putExtra("collegeCity", college.city);
            intent.putExtra("collegeState", college.state);
            intent.putExtra("collegeAddress", college.address);

            context.startActivity(intent);
        });

        holder.btnDeleteCollege.setOnClickListener(v -> {

            new AlertDialog.Builder(context).setTitle("Delete College").setMessage("Are you sure you want to delete " + college.collegeName + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("Colleges")
                                .document(college.id)
                                .delete()
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(context, "College deleted successfully", Toast.LENGTH_SHORT).show();
                                    if (onCollegeDeleted != null) {
                                        onCollegeDeleted.run();
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
        return collegeList.size();
    }
    public static class CollegeViewHolder extends RecyclerView.ViewHolder {
        TextView txtCollegeName, txtCollegeCity;
        AppCompatButton btnViewCollege, btnEditCollege, btnDeleteCollege;
        public CollegeViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCollegeName = itemView.findViewById(R.id.txtCollegeName);
            txtCollegeCity = itemView.findViewById(R.id.txtCollegeCity);

            btnViewCollege = itemView.findViewById(R.id.btnViewCollege);
            btnEditCollege = itemView.findViewById(R.id.btnEditCollege);
            btnDeleteCollege = itemView.findViewById(R.id.btnDeleteCollege);
        }
    }
}
