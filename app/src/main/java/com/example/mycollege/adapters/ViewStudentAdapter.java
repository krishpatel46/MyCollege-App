package com.example.mycollege.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycollege.R;
import com.example.mycollege.objects.Student;

import java.util.ArrayList;

public class ViewStudentAdapter extends RecyclerView.Adapter<ViewStudentAdapter.ViewStudentViewHolder> {

    Context context;
    ArrayList<Student> studentArrayList;


    public ViewStudentAdapter(Context context, ArrayList<Student> studentArrayList) {
        this.context = context;
        this.studentArrayList = studentArrayList;
    }

    @NonNull
    @Override
    public ViewStudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false);
        return new ViewStudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewStudentViewHolder holder, int position) {
        Student student = studentArrayList.get(position);

        holder.stuNameTxt.setText(student.getStuName());
        holder.stuEnrollNoTxt.setText(student.getStuEnrollNo());
        holder.stuSemTxt.setText(student.getStuSem());
        holder.stuBranchTxt.setText(student.getStuBranch());
        holder.stuCourseTxt.setText(student.getStuCourse());
    }

    @Override
    public int getItemCount() {
        return studentArrayList.size();
    }

    public static class ViewStudentViewHolder extends RecyclerView.ViewHolder {

        TextView stuNameTxt, stuEnrollNoTxt, stuSemTxt, stuBranchTxt, stuCourseTxt;
//        CardView stuViewCard;

        public ViewStudentViewHolder(@NonNull View itemView) {
            super(itemView);

            stuNameTxt = itemView.findViewById(R.id.stuNameTxt);
            stuEnrollNoTxt = itemView.findViewById(R.id.stuEnrollTxt);
            stuSemTxt = itemView.findViewById(R.id.stuSemNumTxt);
            stuBranchTxt = itemView.findViewById(R.id.stuBranchTxt);
            stuCourseTxt = itemView.findViewById(R.id.stuCourseTxt);
        }
    }
}
