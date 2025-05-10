package com.example.mycollege.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycollege.R;
import com.example.mycollege.objects.Student;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    Context context;
    ArrayList<Student> studentArrayList;
    OnStudentSelectListener studentSelectListener;

    public StudentAdapter(Context context, ArrayList<Student> studentArrayList, OnStudentSelectListener studentSelectListener) {
        this.context = context;
        this.studentArrayList = studentArrayList;
        this.studentSelectListener = studentSelectListener;
    }

    @NonNull
    @Override
    public StudentAdapter.StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.student_attendance_list_item, parent, false);
        return new StudentViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentViewHolder holder, int position) {

        Student student = studentArrayList.get(position);

        holder.stuName.setText(student.getStuName());
        holder.stuEnrollNo.setText(student.getStuEnrollNo());
        holder.stuSem.setText(student.getStuSem());

        holder.stuCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentSelectListener.onStudentSelected(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentArrayList.size();
    }


    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView stuName, stuEnrollNo, stuSem;
        CardView stuCard;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);

            stuName = itemView.findViewById(R.id.studentNameTxt);
            stuEnrollNo = itemView.findViewById(R.id.studentEnrollTxt);
            stuSem = itemView.findViewById(R.id.studentSemNumTxt);

            stuCard = itemView.findViewById(R.id.studentCard);
        }
    }
}