package com.example.mycollege.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityAddAssignmentBinding;
import com.example.mycollege.objects.Assignment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddAssignmentActivity extends AppCompatActivity {

    private ActivityAddAssignmentBinding binding;
    private ManagePreferences preferences;
    Assignment assignment;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // set subject spinner
        setSubjectSpinner();

        // click listener
        binding.submitBtn.setOnClickListener(view -> {
            if(isDetailsFilled()) {
                preferences = new ManagePreferences(this, Constant.KEY_PROFESSOR_PREFERENCE_NAME);
                String semester = preferences.getString(Constant.KEY_PROF_SEMESTER);

                String heading = binding.assignmentHeadingTxt.getText().toString();
                String description = binding.assignmentDescriptionTxt.getText().toString();
                String link = binding.assignmentLinkTxt.getText().toString().trim();
                String subject = binding.subjectSpinner.getSelectedItem().toString();

                assignment = new Assignment(heading, description, link, subject, semester);

                String college = preferences.getString(Constant.KEY_PROF_COLLEGE);
                String email = preferences.getString(Constant.KEY_PROF_EMAIL);

                colRef.whereEqualTo("Name", college)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentSnapshot documentSnapshot : value) {
                                    String collegeID = documentSnapshot.getId();
                                    addAssignment(assignment, collegeID, email);
                                }
                            }
                        });
            }
        });

        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(AddAssignmentActivity.this, ProfessorNoteSenseiActivity.class));
            finish();
        });
    }

    private void addAssignment(Assignment assignment, String collegeID, String email) {
        colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                .document(email).collection("Assignment")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        Log.d("path", colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                                .document(email).collection("Assignment").getPath());

                        try {
                            for(DocumentSnapshot docSnap: value) {
                                if (docSnap.getId().equals(assignment.getHeading())) {
                                    Toast.makeText(AddAssignmentActivity.this, "Provide a unique assignment heading", Toast.LENGTH_LONG).show();
                                    return;
                                }
//                                else {
//                                    throw new NullPointerException();
//                                }
                            }

                            // insert assignment code
                            Log.d("path - unique heading", colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                                    .document(email).collection("Assignment").getPath());

                            colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                                    .document(email).collection("Assignment")
                                    .document(assignment.getHeading())
                                    .set(assignment)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(AddAssignmentActivity.this, "Assignment added!", Toast.LENGTH_SHORT).show();

                                            startActivity(new Intent(AddAssignmentActivity.this, ProfessorNoteSenseiActivity.class));
                                            finish();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(AddAssignmentActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                        catch(NullPointerException npe) {
                            //nothing
                        }
                    }
                });

        // old location of insert assignment code
    }

    private void setSubjectSpinner() {
        ArrayList<String> subjectList = new ArrayList<>();
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(AddAssignmentActivity.this, android.R.layout.simple_spinner_dropdown_item, subjectList);

        subjectList.add("AJP");
        subjectList.add("NMA");
        subjectList.add("MCAD");
        subjectList.add("PPUD");

        subjectAdapter.notifyDataSetChanged();

        binding.subjectSpinner.setAdapter(subjectAdapter);
    }

    //  [method to verify input details]
    private Boolean isDetailsFilled() {

        if (binding.assignmentHeadingTxt.getText().toString().isEmpty()) {
            setErrorTxt("*Assignment name/header required");

            return false;
        }

        else if(binding.assignmentDescriptionTxt.getText().toString().isEmpty()) {
            setErrorTxt("*Description required");

            return false;
        }

        else if (binding.assignmentLinkTxt.getText().toString().isEmpty()) {
            setErrorTxt("*Submission link required");

            return false;
        }

        else {
            return true;
        }
    }

    //  [method to set error textView]
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddAssignmentActivity.this, ProfessorNoteSenseiActivity.class));
        finish();
    }
}