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
import com.example.mycollege.objects.Assignment;

import java.util.ArrayList;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {

    Context context;
    ArrayList<Assignment> assignmentArrayList;
    OnAssignmentSelectListener assignmentSelectListener;

    public AssignmentAdapter(Context context, ArrayList<Assignment> assignmentArrayList, OnAssignmentSelectListener assignmentSelectListener) {
        this.context = context;
        this.assignmentArrayList = assignmentArrayList;
        this.assignmentSelectListener = assignmentSelectListener;
    }

    @NonNull
    @Override
    public AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.assignment_list_item, parent, false);
        return new AssignmentAdapter.AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentViewHolder holder, int position) {
        Assignment assignment = assignmentArrayList.get(position);

        holder.heading.setText(assignment.getHeading());
        holder.description.setText(assignment.getDescription());
        holder.subject.setText(assignment.getSubject());
        holder.semester.setText(assignment.getSemester());

        holder.assignmentCard.setOnClickListener(view -> assignmentSelectListener.onAssignmentSelected(assignment));
    }

    @Override
    public int getItemCount() {
        return assignmentArrayList.size();
    }

    public static class AssignmentViewHolder extends RecyclerView.ViewHolder {

        TextView heading, description, subject, semester;
        CardView assignmentCard;

        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.headingTxt);
            description = itemView.findViewById(R.id.descriptionTxt);
            subject = itemView.findViewById(R.id.subjectTxt);
            semester = itemView.findViewById(R.id.semTxt);

            assignmentCard = itemView.findViewById(R.id.assignmentCardView);
        }
    }
}
