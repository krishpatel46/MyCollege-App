package com.example.mycollege.manageDbFragments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityUpdateStudentBinding;
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

public class UpdateStudentActivity extends AppCompatActivity {

    private ActivityUpdateStudentBinding binding;
    Spinner branchSpinner, courseSpinner;
    ArrayAdapter<String> branchAdapter, courseAdapter;

    private ManagePreferences preferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(UpdateStudentActivity.this, Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle stuData = getIntent().getBundleExtra("stuData");
        if (stuData != null && !stuData.isEmpty()) {
            binding.stuEnrollNoTxt.setText("Enrolment No.: " + stuData.getString("stuEnrollNo"));
            binding.stuNameTxt.setText(stuData.getString("stuName"));
            binding.stuEmailTxt.setText(stuData.getString("stuEmail"));

            binding.stuBranchTxt.setText("Old Branch: " + stuData.getString("stuBranch"));
            binding.stuCourseTxt.setText("Old Course: " + stuData.getString("stuCourse"));
            binding.stuSemTxt.setText("Old Semester: " + stuData.getString("stuSem"));
        }


        //  ...set spinners...
        branchSpinner = binding.branchSpinner;
        courseSpinner = binding.courseSpinner;

        colRef.whereEqualTo("Name", collegeName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            for(DocumentSnapshot docSnap: value) {
                                String collegeID = docSnap.getId();
                                binding.branchSpinner.setAdapter(setBranchSpinner(colRef, collegeID));
                            }
                        }
                    }
                });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String branchName = branchSpinner.getSelectedItem().toString();

                Log.d("col path", colRef.getPath());
                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null) {
                                    setErrorTxt(error.getMessage());
                                }
                                else {
                                    for(DocumentSnapshot docSnap: value) {
                                        String collegeID = docSnap.getId();
                                        binding.courseSpinner.setAdapter(setCourseSpinner(colRef, collegeID, branchName));
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String branchName = branchSpinner.getSelectedItem().toString();
                String courseName = courseSpinner.getSelectedItem().toString();

                Log.d("col path 2", colRef.getPath());
                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null) {
                                    setErrorTxt(error.getMessage());
                                }
                                else {
                                    for(DocumentSnapshot docSnap: value) {
                                        String collegeID = docSnap.getId();
                                        binding.semesterSpinner.setAdapter(getSemNumber(colRef, collegeID, branchName, courseName));
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //  ...set listeners...
        setListeners(collegeName, stuData);
    }

    private ArrayAdapter<String> setBranchSpinner(CollectionReference colRef_fun, String collegeID) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            arrayList.clear();

                            assert value != null;
                            for(DocumentSnapshot docSnap:value) {
                                String branchName = docSnap.getString(Constant.KEY_BRANCH_NAME);
                                arrayList.add(branchName);
                            }

                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

        return arrayAdapter;
    }

    private ArrayAdapter<String> setCourseSpinner(CollectionReference colRef_fun, String collegeID, String branchName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            arrayList.clear();

                            assert value != null;
                            for(DocumentSnapshot docSnap:value) {
                                String courseName = docSnap.getString(Constant.KEY_COURSE_NAME);
                                arrayList.add(courseName);
                            }

                            Log.d("course list", arrayList.toString());
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

//        binding.courseSpinner.setAdapter(arrayAdapter);
        return arrayAdapter;
    }


    private ArrayAdapter<String> getSemNumber(CollectionReference colRef_fun, String collegeID, String branchName, String courseName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(UpdateStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL).document(courseName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        arrayList.clear();

                        try {
                            int semNo = documentSnapshot.getLong(Constant.KEY_SEM_NUM).intValue();

                            for (int sem = semNo; sem > 0; sem--) {
                                arrayList.add(String.valueOf(sem));
                            }

                            arrayAdapter.notifyDataSetChanged();

                        } catch (NullPointerException npe) {
                            setErrorTxt("*Please select appropriate course");
                        } catch (NumberFormatException nfe) {
                            setErrorTxt("*Please select a course");
                        }
                    }
                });

        return arrayAdapter;
    }

    private void setListeners(String collegeName, Bundle stuData) {
        binding.submitBtn.setOnClickListener(view -> {
            String name = binding.stuNameTxt.getText().toString();
            String enrolNo = stuData.getString("stuEnrollNo");
            String email = binding.stuEmailTxt.getText().toString().trim();
            String branch = binding.branchSpinner.getSelectedItem().toString();
            String course = binding.courseSpinner.getSelectedItem().toString();
            String sem = binding.semesterSpinner.getSelectedItem().toString();
            String pass = stuData.getString("stuPass");

            Student studentData = new Student(name, enrolNo, email, pass, collegeName, branch, course, sem);

            colRef.whereEqualTo("Name", collegeName)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                setErrorTxt(error.getMessage());
                            } else {
                                for (DocumentSnapshot docSnap : value) {
                                    String collegeID = docSnap.getId();
                                    updateStudent(collegeID, enrolNo, studentData);
                                }
                            }
                        }
                    });
        });


        binding.cancelBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void updateStudent(String collegeID, String enrolNo, Student studentData) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_STUDENT_COL)
                    .document(enrolNo)
                    .set(studentData)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Student details updated", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    });
        }
        catch (NullPointerException npe) {
            Toast.makeText(getApplicationContext(), "Oops, details not available", Toast.LENGTH_LONG).show();
            onBackPressed();
        }
    }


    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(UpdateStudentActivity.this, ManageDbAdminActivity.class));
        finish();
    }
}