package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityStudentLoginBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class StudentLoginActivity extends AppCompatActivity {

    private ManagePreferences preferences;
    private ActivityStudentLoginBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    DocumentReference docRef;

    TextView errorTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_STUDENT_PREFERENCE_NAME);

        errorTxt = binding.errorTxt;
        setCollegeSpinner(colRef);

        //  [set click listeners]
        setListeners();
    }


    //  --------------------------------------
    //  [method to set button click listeners]
    //  --------------------------------------
    private void setListeners() {

        /* submit button logic */
        binding.submitBtn.setOnClickListener(view -> {
            try {
                if(isLoginDetailsFilled()) {
                    String stuEnrollNo = binding.stuEnrollNoTxt.getText().toString().trim();
                    String stuEmail = binding.stuEmailTxt.getText().toString().trim();
                    String stuPass = binding.stuPasswordTxt.getText().toString().trim();
                    String stuCollege = binding.collegeSpinner.getSelectedItem().toString().trim();


                    colRef.whereEqualTo("Name", stuCollege)
                            .addSnapshotListener((value, error) -> {
                                if(error != null) {
                                    setErrorTxt(error.toString());
                                }

                                else {
                                    setErrorTxt("in else part");
                                    setErrorTxt("value - " + value.toString());

                                    for(QueryDocumentSnapshot docSnap: value) {
                                        docRef = colRef.document(docSnap.getId());

                                        stuLogin(docRef, stuCollege, stuEnrollNo, stuPass);
                                    }
                                }
                            });
                }

            } catch(NullPointerException npe) {
                setErrorTxt("*Please select your college");
            }
        });


        /* cancel button logic */
        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(StudentLoginActivity.this, AskUserRoleActivity.class));
            finish();
        });


        /* create button button logic */
        binding.createNewAccountLink.setOnClickListener(view -> {
            startActivity(new Intent(StudentLoginActivity.this, RegisterStudentActivity.class));
            finish();
        });
    }


    //  [method to set error textView]
    private void setErrorTxt(String message) {
        errorTxt.setVisibility(View.VISIBLE);
        errorTxt.setText(message);
    }


    //  [method to verify input details]
    private Boolean isLoginDetailsFilled() {
        if(binding.stuEnrollNoTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please enter an enrollment number");

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

        else {
            return true;
        }
    }


    //   [collegeSpinner - fetch college names]
    private void setCollegeSpinner(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(StudentLoginActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.addSnapshotListener((value, error) -> {
            if(error != null) {
                setErrorTxt(error.toString());
            }

            else {
                arrayList.clear();

                assert value != null;
                for(DocumentSnapshot docSnap: value) {
                    String collegeName = docSnap.getString("Name");
                    arrayList.add(collegeName);
                }

                arrayAdapter.notifyDataSetChanged();
            }
        });

        binding.collegeSpinner.setAdapter(arrayAdapter);
    }


    private void stuLogin(DocumentReference docRef_fun, String stuCollege, String stuEnrollNo, String stuPass) {
        docRef_fun.collection(Constant.KEY_DB_STUDENT_COL)
                .document(stuEnrollNo)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        setErrorTxt(error.toString());
                    }

                    else {
//                        setErrorTxt("in else part");
//                        setErrorTxt("value - " + value.toString());

//                        setErrorTxt("in for loop");
                        String pass = value.getString(Constant.KEY_STU_PASSWORD);
//                        setErrorTxt("password - " + pass);

                        try {
                            if (pass.equals(stuPass)) {

                                preferences.putBoolean(Constant.KEY_IS_LOGGED_IN, true);
                                preferences.putString(Constant.KEY_STU_NAME, value.getString(Constant.KEY_STU_NAME));
                                preferences.putString(Constant.KEY_STU_ENROLL_NO, value.getString(Constant.KEY_STU_ENROLL_NO));
                                preferences.putString(Constant.KEY_STU_EMAIL, value.getString(Constant.KEY_STU_EMAIL));
                                preferences.putString(Constant.KEY_STU_COLLEGE, stuCollege);
                                preferences.putString(Constant.KEY_STU_BRANCH, value.getString(Constant.KEY_STU_BRANCH));
                                preferences.putString(Constant.KEY_STU_COURSE, value.getString(Constant.KEY_STU_COURSE));
                                preferences.putString(Constant.KEY_STU_SEMESTER, value.getString(Constant.KEY_STU_SEMESTER));


                                startActivity(new Intent(StudentLoginActivity.this, StudentDashboardActivity.class));
                                finish();
                            } else {
                                setErrorTxt("*Wrong Password: Please enter correct password");
                            }
                        }
                        catch (NullPointerException npe) {
                            setErrorTxt("*Wrong College: Please choose correct college");
                        }
                    }
                });
    }
}