package com.example.mycollege.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mycollege.adapters.AttendanceAdapter;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.databinding.ActivityStudentAttendanceOverlookBinding;
import com.example.mycollege.objects.Attendance;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentAttendanceOverlookActivity extends AppCompatActivity {

    private ActivityStudentAttendanceOverlookBinding binding;

    AttendanceAdapter attendanceAdapter;
    ArrayList<Attendance> attendanceArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentAttendanceOverlookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.studentAttendanceList.setHasFixedSize(true);
        binding.studentAttendanceList.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        String stuCollege = intent.getStringExtra("StuCollege");
        String stuEnrollNo = intent.getStringExtra("EnrollNo");

        attendanceArrayList = new ArrayList<Attendance>();
        attendanceAdapter = new AttendanceAdapter(StudentAttendanceOverlookActivity.this, attendanceArrayList);

        colRef.whereEqualTo("Name", stuCollege)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot docSnap: value) {
                            String collegeID = docSnap.getId();

                            colRef.document(collegeID).collection(Constant.KEY_DB_STUDENT_COL)
                                    .document(stuEnrollNo).collection(Constant.KEY_DB_ATTENDANCE_COL)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            Log.d("path", colRef.document(collegeID).collection(Constant.KEY_DB_STUDENT_COL)
                                                    .document(stuEnrollNo).collection(Constant.KEY_DB_ATTENDANCE_COL).getPath());

                                            for(DocumentSnapshot docSnap: queryDocumentSnapshots) {
                                                attendanceArrayList.add(docSnap.toObject(Attendance.class));
                                                Log.d("array list", attendanceArrayList.toString());
                                            }

                                            attendanceAdapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });

        binding.studentAttendanceList.setAdapter(attendanceAdapter);
    }
}