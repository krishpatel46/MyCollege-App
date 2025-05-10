package com.example.mycollege.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.example.mycollege.adapters.AssignmentAdapter;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.adapters.OnAssignmentSelectListener;
import com.example.mycollege.databinding.ActivityStudentNoteSensaiBinding;
import com.example.mycollege.objects.Assignment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class StudentNoteSensaiActivity extends AppCompatActivity implements OnAssignmentSelectListener {

    private ActivityStudentNoteSensaiBinding binding;
    private ManagePreferences preferences;

    AssignmentAdapter assignmentAdapter;
    ArrayList<Assignment> assignmentArrayList;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentNoteSensaiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(this, Constant.KEY_STUDENT_PREFERENCE_NAME);
        String college = preferences.getString(Constant.KEY_STU_COLLEGE);

        assignmentArrayList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(StudentNoteSensaiActivity.this, assignmentArrayList, this);

        binding.assignmentList.setLayoutManager(new LinearLayoutManager(this));
        binding.assignmentList.setHasFixedSize(true);

        colRef.whereEqualTo("Name", college)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot documentSnapshot: value) {
                            String collegeID = documentSnapshot.getId();
                            fetchProf(collegeID);
                        }
                    }
                });
    }

    private void fetchProf(String collegeID) {
        colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                .whereEqualTo(Constant.KEY_PROF_DESIGNATION, "Class Coordinator - CC")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot documentSnapshot: value) {
                            String email = documentSnapshot.getString(Constant.KEY_PROF_EMAIL);
                            fetchData(assignmentArrayList, assignmentAdapter, collegeID, email);
                        }
                    }
                });
    }

    private void fetchData(ArrayList<Assignment> assignmentArrayList, AssignmentAdapter assignmentAdapter, String collegeID, String email) {
        colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                .document(email).collection("Assignment")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot docSnap: queryDocumentSnapshots) {
                            assignmentArrayList.add(docSnap.toObject(Assignment.class));
                        }
                        assignmentAdapter.notifyDataSetChanged();
                    }
                });

        binding.assignmentList.setAdapter(assignmentAdapter);
    }

    @Override
    public void onAssignmentSelected(Assignment assignment) {
        Intent intent = new Intent(StudentNoteSensaiActivity.this, AssignmentInfoActivity.class);
        intent.putExtra("Link", assignment.getSubmissionLink());
        intent.putExtra("Subject", assignment.getSubject());
        intent.putExtra("Semester", assignment.getSemester());
        intent.putExtra("Heading", assignment.getHeading());
        intent.putExtra("Description", assignment.getDescription());

        startActivity(intent);
    }
}