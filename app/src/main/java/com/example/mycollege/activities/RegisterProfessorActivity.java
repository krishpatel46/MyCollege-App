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
import com.example.mycollege.databinding.ActivityRegisterProfessorBinding;
import com.example.mycollege.objects.Professor;
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

public class RegisterProfessorActivity extends AppCompatActivity {

    private final String PROFESSOR = "Professor";
    private final String CC = "Class Coordinator - CC";
    private final String HOD = "Head of Department - HOD";

    private ActivityRegisterProfessorBinding binding;

    TextView errorTxt;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterProfessorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.selectSemesterHeader.setVisibility(View.INVISIBLE);
        binding.semesterSpinner.setVisibility(View.INVISIBLE);

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
                    Professor profData = setProfData();

                    colRef.whereEqualTo("Name", profData.getProfCollege())
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    for(DocumentSnapshot docSnap: value) {
                                        String collegeID = docSnap.getId();
                                        docRef = colRef.document(collegeID);

                                        insertProfDetails(profData, docRef);
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
            startActivity(new Intent(RegisterProfessorActivity.this, AskUserRoleActivity.class));
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
        if(binding.profNameTxt.getText().toString().isEmpty()) {
            setErrorTxt("*Please enter a name");

            return false;
        }

        else if (! Patterns.EMAIL_ADDRESS.matcher(binding.profEmailTxt.getText().toString().trim()).matches()) {
            setErrorTxt("*Please enter an email id");

            return false;
        }

        else if(binding.profPasswordTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please enter a password");

            return false;
        }

        else if (binding.collegeSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your college");

            return false;
        }

        else if (binding.designationSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your designation");

            return false;
        }

        else if (binding.branchSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your branch");

            return false;
        }

        else if (binding.departmentSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select your department");

            return false;
        }

//        else if (binding.branchSpinner.isEnabled() && binding.semesterSpinner.getSelectedItem().toString().isEmpty()) {
//            setErrorTxt("*Please select your semester");
//
//            return false;
//        }

        else {
            return true;
        }
    }

    //  ...method to set student data into a java object...
    private Professor setProfData() {
        String prof_name = binding.profNameTxt.getText().toString();
        String prof_email = binding.profEmailTxt.getText().toString().trim();
        String prof_pass = binding.profPasswordTxt.getText().toString().trim();
        String prof_college = binding.collegeSpinner.getSelectedItem().toString();
        String prof_designation = binding.designationSpinner.getSelectedItem().toString();
        String prof_dept = binding.departmentSpinner.getSelectedItem().toString();
        String prof_branch = binding.branchSpinner.getSelectedItem().toString();

        if(binding.semesterSpinner.getVisibility() == View.VISIBLE) {
            String prof_sem = binding.semesterSpinner.getSelectedItem().toString();

            /* store data in a java object */
            return new Professor(prof_name, prof_email, prof_pass, prof_college, prof_designation, prof_dept, prof_branch, prof_sem);
        }
        else {
            /* store data in a java object */
            return new Professor(prof_name, prof_email, prof_pass, prof_college, prof_designation, prof_dept, prof_branch, "0");
        }
    }


    //  ...method to insert student data values in database...
    private void insertProfDetails(Professor profData, DocumentReference docRef) {

        String profEmail = profData.getProfEmail();
        docRef.collection(Constant.KEY_DB_FACULTY_COL).document(profEmail)
                .set(profData)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterProfessorActivity.this, "Your details are successfully registered", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(RegisterProfessorActivity.this, AskUserRoleActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterProfessorActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                });
    }


    //  ----------------------------------
    //  [method to set values in spinners]
    //  ----------------------------------
    private void setSpinners() {
        Spinner collegeSpinner = binding.collegeSpinner;
        Spinner branchSpinner = binding.branchSpinner;
        Spinner designationSpinner = binding.designationSpinner;
        Spinner departmentSpinner = binding.departmentSpinner;
        Spinner semSpinner = binding.semesterSpinner;


        //fill college spinner
        collegeSpinner.setAdapter(getCollegeName(colRef));


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
                                    docRef = colRef.document(docSnap.getId());

                                    getBranch(docRef, collegeName);
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        //fill dept. spinner
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
                                    docRef = colRef.document(docSnap.getId());

                                    getDeptName(docRef, collegeName, branchName);
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        //fill designation spinner
        ArrayList<String> desigList = new ArrayList<String>() {
            {
                add(PROFESSOR);
                add(CC);
                add(HOD);
            }
        };
        designationSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, desigList));


        //fill semester spinner
        designationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String collegeName = collegeSpinner.getSelectedItem().toString();
                    String branchName = branchSpinner.getSelectedItem().toString();
                    String deptName = departmentSpinner.getSelectedItem().toString();

                    String designation = designationSpinner.getSelectedItem().toString();

                    if (designation.equals(CC) && !deptName.equals("General")) {
                        binding.selectSemesterHeader.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);

                        colRef.whereEqualTo("Name", collegeName)
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        setErrorTxt(error.toString());
                                    } else {
                                        for (QueryDocumentSnapshot docSnap : value) {
                                            docRef = colRef.document(docSnap.getId());

                                            getSemNumber(docRef, collegeName, branchName, deptName);
                                        }
                                    }
                                });
                    } else if (designation.equals(CC)) {
                        binding.selectSemesterHeader.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);

                        ArrayList<String> semList = new ArrayList<String>() {
                            {
                                add("8");
                                add("7");
                                add("6");
                                add("5");
                                add("4");
                                add("3");
                                add("2");
                                add("1");
                            }
                        };
                        semSpinner.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, semList));
                    } else {
                        binding.selectSemesterHeader.setVisibility(View.INVISIBLE);
                        semSpinner.setVisibility(View.INVISIBLE);
                    }
                }
                catch (NullPointerException npe) {
                    setErrorTxt("*Please fill all the details");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }



    //collegeSpinner - fetch college names
    private ArrayAdapter<String> getCollegeName(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterProfessorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);

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
    private void getBranch(DocumentReference docRef_fun, String collegeName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterProfessorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        docRef_fun.collection(Constant.KEY_DB_BRANCH_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot docSnap : queryDocumentSnapshots) {

                            //fetch branch names here
                            String branch_name = docSnap.getId();
                            arrayList.add(branch_name);
                        }

                        arrayAdapter.notifyDataSetChanged();
                    }
                });


        binding.branchSpinner.setAdapter(arrayAdapter);
    }


    //courseSpinner - fetch course names
    private void getDeptName(DocumentReference docRef_fun, String collegeName, String branchName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterProfessorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        docRef_fun.collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        arrayList.clear();

                        for(QueryDocumentSnapshot docSnap: queryDocumentSnapshots) {

                            //get dept. names
                            String dept_name = docSnap.getString(Constant.KEY_COURSE_NAME);
                            arrayList.add(dept_name);
                        }

                        arrayList.add("General");
                        arrayAdapter.notifyDataSetChanged();
                    }
                });

        binding.departmentSpinner.setAdapter(arrayAdapter);
    }



    //semSpinner - fetch sem numbers
    private void getSemNumber(DocumentReference docRef_fun, String collegeName, String branchName, String courseName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(RegisterProfessorActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        docRef_fun.collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL).document(courseName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        arrayList.clear();

                        try {
                            Log.d("path", docRef_fun.getPath());
                            int semNo = documentSnapshot.getLong("numOfSem").intValue();

                            for (int sem = semNo; sem > 0; sem--) {
                                arrayList.add(String.valueOf(sem));
                            }

                            arrayAdapter.notifyDataSetChanged();

                        } catch (NullPointerException npe) {
                            setErrorTxt("*Please select appropriate dept.");
                        }
                        catch (NumberFormatException nfe) {
                            setErrorTxt("*Please select a dept.");
                        }
                    }
                });

        binding.semesterSpinner.setAdapter(arrayAdapter);
    }
}