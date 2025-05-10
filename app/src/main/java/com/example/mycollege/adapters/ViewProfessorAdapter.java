package com.example.mycollege.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycollege.R;
import com.example.mycollege.objects.Professor;
import com.example.mycollege.objects.Student;

import java.util.ArrayList;

public class ViewProfessorAdapter extends RecyclerView.Adapter<ViewProfessorAdapter.ViewProfessorViewHolder> {
    Context context;
    ArrayList<Professor> professorArrayList;

    private final String PROFESSOR = "Professor";
    private final String CC = "Class Coordinator - CC";
    private final String HOD = "Head of Department - HOD";


    public ViewProfessorAdapter(Context context, ArrayList<Professor> professorArrayList) {
        this.context = context;
        this.professorArrayList = professorArrayList;
    }

    @NonNull
    @Override
    public ViewProfessorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.professor_list_item, parent, false);
        return new ViewProfessorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewProfessorAdapter.ViewProfessorViewHolder holder, int position) {
        Professor professor = professorArrayList.get(position);

        holder.profNameTxt.setText(professor.getProfName());
        holder.profDesignationTxt.setText(professor.getProfDesignation());
        holder.profBranchTxt.setText(professor.getProfBranch());
        holder.profDeptTxt.setText(professor.getProfDepartment());

        if(professor.getProfDesignation().equals(CC))
            holder.profSemTxt.setText(professor.getProfSem());
        else
            holder.profSemTxt.setText("0");
    }

    @Override
    public int getItemCount() {
        return professorArrayList.size();
    }

    public static class ViewProfessorViewHolder extends RecyclerView.ViewHolder {

        TextView profNameTxt, profDesignationTxt, profSemTxt, profBranchTxt, profDeptTxt;
//        CardView stuViewCard;

        public ViewProfessorViewHolder(@NonNull View itemView) {
            super(itemView);

            profNameTxt = itemView.findViewById(R.id.profName);
            profDesignationTxt = itemView.findViewById(R.id.profDesignation);
            profBranchTxt = itemView.findViewById(R.id.profBranch);
            profDeptTxt = itemView.findViewById(R.id.profDept);
            profSemTxt = itemView.findViewById(R.id.profSem);
        }
    }
}
