package com.example.mycollege.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycollege.R;
import com.example.mycollege.objects.Attendance;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder> {

    Context context;
    ArrayList<Attendance> attendanceArrayList;

    public AttendanceAdapter(Context context, ArrayList<Attendance> attendanceArrayList) {
        this.context = context;
        this.attendanceArrayList = attendanceArrayList;
    }


    @NonNull
    @Override
    public AttendanceAdapter.AttendanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.student_attendance_overlook_list_item, parent, false);
        return new AttendanceViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceAdapter.AttendanceViewHolder holder, int position) {

        Attendance attendance = attendanceArrayList.get(position);

        holder.present.setText(String.valueOf(attendance.getPresent()));
        holder.absent.setText(String.valueOf(attendance.getAbsent()));
        holder.total.setText(String.valueOf(attendance.getTotal()));
        holder.subject.setText(attendance.getSubjectName());
    }

    @Override
    public int getItemCount() {
        return attendanceArrayList.size();
    }


    public static class AttendanceViewHolder extends RecyclerView.ViewHolder {

        TextView present, absent, total, subject;

        public AttendanceViewHolder(@NonNull View itemView) {
            super(itemView);

            present = itemView.findViewById(R.id.stuPresentNumTxt);
            absent = itemView.findViewById(R.id.stuAbsentNumTxt);
            total = itemView.findViewById(R.id.stuTotalNumTxt);
            subject = itemView.findViewById(R.id.subjectNameTxt);
        }
    }
}
