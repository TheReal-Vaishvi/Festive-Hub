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

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private Context context;
    private ArrayList<Student> studentList;
    private FirebaseFirestore db;

    private Runnable onStudentDeleted;

    public void setOnStudentDeletedListener(Runnable listener) {
        onStudentDeleted = listener;
    }
    public StudentAdapter(Context context, ArrayList<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
        db = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

        Student student = studentList.get(position);

        holder.txtstudentname.setText(student.name);
        holder.txtstudentemail.setText(student.email);
        holder.txtstudentphone.setText(student.mobile);

        // View Student Details
        holder.btnviewstudent.setOnClickListener(v -> {

            Intent intent = new Intent(context, StudentDetailsActivity.class);

            intent.putExtra("studentId", student.id);
            intent.putExtra("studentName", student.name);
            intent.putExtra("studentEmail", student.email);
            intent.putExtra("studentMobile", student.mobile);

            intent.putExtra("studentCreatedAt",
                    student.createdAt != null
                            ? ((com.google.firebase.Timestamp) student.createdAt)
                            .toDate().getTime()
                            : -1L);

            context.startActivity(intent);
        });

        // Edit Student Details
        holder.btneditstudent.setOnClickListener(v -> {

            Intent intent = new Intent(context, EditStudentActivity.class);

            intent.putExtra("studentId", student.id);
            intent.putExtra("studentName", student.name);
            intent.putExtra("studentMobile", student.mobile);

            context.startActivity(intent);
        });

        // Delete Student
        holder.btndeletestudent.setOnClickListener(v -> {

            new AlertDialog.Builder(context).setTitle("Remove Student").setMessage("Are you sure you want to remove "
                            + student.name + " from the student list?").setPositiveButton("Remove", (dialog, which) ->
                    {
                        deleteStudent(student, holder);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });
    }
    private void deleteStudent(Student student, StudentViewHolder holder) {

        holder.btndeletestudent.setEnabled(false);

        db.collection("Users")
                .document(student.id)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(context, "Student removed successfully", Toast.LENGTH_SHORT).show();

                    // Remove from the currently displayed list
                    int position = holder.getBindingAdapterPosition();

                    if (onStudentDeleted != null) {
                        onStudentDeleted.run();
                    }
                })
                .addOnFailureListener(e -> {

                    holder.btndeletestudent.setEnabled(true);

                    Toast.makeText(context,
                            "Failed to remove student: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    //refresh the already list student with the search result
    public void filterList(ArrayList<Student> filteredList) {
        studentList = filteredList;
        notifyDataSetChanged();
    }

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView txtstudentname, txtstudentemail, txtstudentphone;
        AppCompatButton btnviewstudent, btneditstudent, btndeletestudent;
        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            txtstudentname = itemView.findViewById(R.id.txtstudentname);
            txtstudentemail = itemView.findViewById(R.id.txtstudentemail);
            txtstudentphone = itemView.findViewById(R.id.txtstudentmobile);

            btnviewstudent = itemView.findViewById(R.id.btnviewstudent);
            btneditstudent = itemView.findViewById(R.id.btneditstudent);
            btndeletestudent = itemView.findViewById(R.id.btndeletestudent);
        }
    }
}
