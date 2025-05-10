package com.example.mycollege.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.mycollege.adapters.StudentAdapter;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.adapters.OnStudentSelectListener;
import com.example.mycollege.databinding.ActivityProfessorAttendanceBaabuBinding;
import com.example.mycollege.objects.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ProfessorAttendanceBaabuActivity extends AppCompatActivity implements OnStudentSelectListener {

    private ActivityProfessorAttendanceBaabuBinding binding;

    StudentAdapter studentAdapter;
    ArrayList<Student> studentArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessorAttendanceBaabuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.studentList.setHasFixedSize(true);
        binding.studentList.setLayoutManager(new LinearLayoutManager(this));

        studentArrayList = new ArrayList<Student>();
        studentAdapter = new StudentAdapter(ProfessorAttendanceBaabuActivity.this, studentArrayList, this);


        Intent intent = getIntent();
        String collegeName = intent.getStringExtra("CollegeName");

        colRef.whereEqualTo("Name", collegeName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        for(DocumentSnapshot docSnap: value) {
                            String collegeID = docSnap.getId();

                            fetchData(colRef.document(collegeID));
                        }
                    }
                });

        binding.studentList.setAdapter(studentAdapter);
    }

    private void fetchData(DocumentReference docRef_fun) {
        docRef_fun.collection(Constant.KEY_DB_STUDENT_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            Log.d("path", docRef_fun.collection(Constant.KEY_DB_STUDENT_COL).getPath());

                            for (DocumentSnapshot docSnap : queryDocumentSnapshots) {
                                studentArrayList.add(docSnap.toObject(Student.class));
                                Log.d("array list", studentArrayList.toString());
                            }

                            studentAdapter.notifyDataSetChanged();
                        }
                        catch (NullPointerException npe) {
                            Log.d("n p e", npe.getMessage());
                        }
                    }
                });
    }

    @Override
    public void onStudentSelected(Student student) {
        Intent intent = new Intent(ProfessorAttendanceBaabuActivity.this, StudentAttendanceOverlookActivity.class);
        intent.putExtra("StuCollege", student.getStuCollege());
        intent.putExtra("EnrollNo", student.getStuEnrollNo());

        startActivity(intent);
    }
}