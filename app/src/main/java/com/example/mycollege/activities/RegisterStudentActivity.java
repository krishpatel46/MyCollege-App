package com.example.mycollege.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.databinding.ActivityRegisterStudentBinding;
import com.example.mycollege.objects.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterStudentActivity extends AppCompatActivity {

    private ActivityRegisterStudentBinding binding;
    TextView errorTxt;

    Spinner collegeSpinner, branchSpinner, courseSpinner, semSpinner;
    ArrayAdapter<String> branchAdapter, courseAdapter, semAdapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    DocumentReference docRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        errorTxt = binding.errorTxt;

        //  [set spinner values]
        setSpinners();

        //  [set button click listeners]
        setListeners();
    }


    //  --------------------------------------
    //  [method to set button click listeners]
    //  --------------------------------------
    private void setListeners() {

        /* submit button logic */
        binding.submitBtn.setOnClickListener(view -> {
            try {
                if(isRegisterDetailsFilled()) {
                    Student studentData = setStudentData();

                    colRef.whereEqualTo("Name", studentData.getStuCollege())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for(DocumentSnapshot docSnap: value) {
                                        String collegeID = docSnap.getId();
                                        docRef = colRef.document(collegeID);

                                        insertStudentDetails(studentData, docRef);
                                    }
                                }
                            });

                }

            } catch(NullPointerException npe) {
                setErrorTxt("*Please select your college details");
            }
        });


        /* cancel button logic */
        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(RegisterStudentActivity.this, AskUserRoleActivity.class));
            finish();
        });
    }


    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        errorTxt.setVisibility(View.VISIBLE);
        errorTxt.setText(message);
    }


    //  ...method to verify input details...
    private Boolean isRegisterDetailsFilled() {
        if(binding.stuNameTxt.getText().toString().isEmpty()) {
            setErrorTxt("*Please enter a name");

            return false;
        }

        else if(binding.stuEnrollNoTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please enter a number");

            return false;
        }

        else if (! Patterns.EMAIL_ADDRESS.matcher(binding.stuEmailTxt.getText().toString().trim()).matches()) {
            setErrorTxt("*Please enter an email id");

            return false;
        }

        else if(binding.stuPasswordTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please enter a password");

            return false;
        }

        else if (binding.collegeSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your college");

            return false;
        }

        else if (binding.branchSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your branch");

            return false;
        }

        else if (binding.courseSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your course");

            return false;
        }

        else if (binding.semesterSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your semester");

            return false;
        }

        else {
            return true;
        }
    }


    //  ...method to set student data into a java object...
    private Student setStudentData() {
        String stu_name = binding.stuNameTxt.getText().toString();
        String stu_enrollNo = binding.stuEnrollNoTxt.getText().toString().trim();
        String stu_email = binding.stuEmailTxt.getText().toString().trim();
        String stu_pass = binding.stuPasswordTxt.getText().toString().trim();
        String stu_college = binding.collegeSpinner.getSelectedItem().toString();
        String stu_branch = binding.branchSpinner.getSelectedItem().toString();
        String stu_course = binding.courseSpinner.getSelectedItem().toString();
        String stu_sem = binding.semesterSpinner.getSelectedItem().toString();


        /* store data in a java object */
        return new Student(stu_name, stu_enrollNo, stu_email, stu_pass, stu_college, stu_branch, stu_course, stu_sem);
    }


    //  ...method to insert student data values in database...
    private void insertStudentDetails(Student studentData, DocumentReference docRef) {

        String stuEnrollNo = studentData.getStuEnrollNo();

        Map<String, Object> studentDataMap = new HashMap<>();
        studentDataMap.put("StuName", studentData.getStuName());
        studentDataMap.put("StuEnrollNo", stuEnrollNo);
        studentDataMap.put("StuEmail", studentData.getStuEmail());
        studentDataMap.put("StuPassword", studentData.getStuPassword());
        studentDataMap.put("StuBranch", studentData.getStuBranch());
        studentDataMap.put("StuCourse", studentData.getStuCourse());
        studentDataMap.put("StuSem", studentData.getStuSem());


        //save student data for login and further use
//        preferences.putBoolean(Constant.KEY_IS_LOGGED_IN, true);
//        preferences.putString(Constant.KEY_STU_NAME, studentData.getStuName());
//        preferences.putString(Constant.KEY_STU_ENROLL_NO, studentData.getStuEnrollNo());
//        preferences.putString(Constant.KEY_STU_EMAIL, studentData.getStuEmail());
//        preferences.putString(Constant.KEY_STU_PASSWORD, studentData.getStuPassword());
//        preferences.putString(Constant.KEY_STU_BRANCH, studentData.getStuBranch());
//        preferences.putString(Constant.KEY_STU_COURSE, studentData.getStuCourse());
//        preferences.putString(Constant.KEY_STU_SEMESTER, studentData.getStuSem());
//
//        Bundle subjectNames = new Bundle();


        docRef.collection(Constant.KEY_DB_STUDENT_COL).document(stuEnrollNo)
                .set(studentData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterStudentActivity.this, "Your details are successfully registered", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterStudentActivity.this, StudentLoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterStudentActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                });
    }


    //  ----------------------------------
    //  [method to set values in spinners]
    //  ----------------------------------
    private void setSpinners() {
        collegeSpinner = binding.collegeSpinner;
        branchSpinner = binding.branchSpinner;
        courseSpinner = binding.courseSpinner;
        semSpinner = binding.semesterSpinner;


        Log.d("col ref path", colRef.getPath());
        //fill college spinner
        binding.collegeSpinner.setAdapter(getCollegeName(colRef));


        Log.d("col ref path", colRef.getPath());
        //fill branch spinner
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String collegeName = collegeSpinner.getSelectedItem().toString();

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener((value, error) -> {
                            if(error != null) {
                                setErrorTxt(error.toString());
                            }

                            else {
                                for(QueryDocumentSnapshot docSnap: value) {
                                    String collegeID = docSnap.getId();
                                    binding.branchSpinner.setAdapter(getBranch(colRef, collegeID));
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        Log.d("col ref path", colRef.getPath());
        //fill course spinner
        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String collegeName = collegeSpinner.getSelectedItem().toString();
                String branchName = branchSpinner.getSelectedItem().toString();

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener((value, error) -> {
                            if(error != null) {
                                setErrorTxt(error.toString());
                            }

                            else {
                                for(QueryDocumentSnapshot docSnap: value) {
                                    String collegeID = docSnap.getId();
                                    binding.courseSpinner.setAdapter(getCourseName(colRef, collegeID, branchName));
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        Log.d("col ref path", colRef.getPath());
        //fill semester spinner
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String collegeName = collegeSpinner.getSelectedItem().toString();
                String branchName = branchSpinner.getSelectedItem().toString();
                String courseName = courseSpinner.getSelectedItem().toString();

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener((value, error) -> {
                            if(error != null) {
                                setErrorTxt(error.toString());
                            }

                            else {
                                for(QueryDocumentSnapshot docSnap: value) {
                                    String collegeID = docSnap.getId();
                                    binding.semesterSpinner.setAdapter(getSemNumber(colRef, collegeID, branchName, courseName));
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }



    //collegeSpinner - fetch college names
    private ArrayAdapter<String> getCollegeName(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.get()
                .addOnSuccessListener(value -> {

                    arrayList.clear();

                    for(DocumentSnapshot docSnap: value) {
                        String collegeName = docSnap.getString("Name");
                        arrayList.add(collegeName);
                    }

                    arrayAdapter.notifyDataSetChanged();
                });

        return arrayAdapter;
    }


    //branchSpinner - get specified college collection's branch details
    private ArrayAdapter<String> getBranch(CollectionReference colRef_fun, String collegeID) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(collegeID).collection(Constant.KEY_DB_BRANCH_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot docSnap : queryDocumentSnapshots) {

                            //fetch branch names here
                            String branch_name = docSnap.getString(Constant.KEY_BRANCH_NAME);
                            arrayList.add(branch_name);
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }
                });

//        binding.branchSpinner.setAdapter(arrayAdapter);
        return arrayAdapter;
    }


    //courseSpinner - fetch course names
    private ArrayAdapter<String> getCourseName(CollectionReference colRef_fun, String collegeID, String branchName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrayList.clear();

                        for(QueryDocumentSnapshot docSnap: queryDocumentSnapshots) {

                            //get course names
                            String course_name = docSnap.getString(Constant.KEY_COURSE_NAME);
                            arrayList.add(course_name);
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }
                });

//        binding.courseSpinner.setAdapter(arrayAdapter);
        return arrayAdapter;
    }



    //semSpinner - fetch sem numbers
    private ArrayAdapter<String> getSemNumber(CollectionReference colRef_fun, String collegeID, String branchName, String courseName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterStudentActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


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
                            }
                            catch (NumberFormatException nfe) {
                                setErrorTxt("*Please select a course");
                            }
                        }
                    });

//        binding.semesterSpinner.setAdapter(arrayAdapter);
        return arrayAdapter;
    }
}