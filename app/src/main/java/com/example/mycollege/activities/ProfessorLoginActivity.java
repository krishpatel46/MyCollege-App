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
import com.example.mycollege.databinding.ActivityProfessorLoginBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ProfessorLoginActivity extends AppCompatActivity {

    private ActivityProfessorLoginBinding binding;

    private ManagePreferences preferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    DocumentReference docRef;

    TextView errorTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessorLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_PROFESSOR_PREFERENCE_NAME);

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
                    String profEmail = binding.profEmailTxt.getText().toString().trim();
                    String profPass = binding.profPasswordTxt.getText().toString().trim();
                    String profCollege = binding.collegeSpinner.getSelectedItem().toString().trim();


                    colRef.whereEqualTo("Name", profCollege)
                            .addSnapshotListener((value, error) -> {
                                if(error != null) {
                                    setErrorTxt(error.toString());
                                }

                                else {
                                    setErrorTxt("in else part");
                                    setErrorTxt("value - " + value.toString());

                                    for(QueryDocumentSnapshot docSnap: value) {
                                        docRef = colRef.document(docSnap.getId());

                                        stuLogin(docRef, profCollege, profEmail, profPass);
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
            startActivity(new Intent(ProfessorLoginActivity.this, AskUserRoleActivity.class));
            finish();
        });


        /* create button button logic */
        binding.createNewAccountLink.setOnClickListener(view -> {
            startActivity(new Intent(ProfessorLoginActivity.this, RegisterProfessorActivity.class));
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
        if (! Patterns.EMAIL_ADDRESS.matcher(binding.profEmailTxt.getText().toString().trim()).matches()) {
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

        else {
            return true;
        }
    }


    //   [collegeSpinner - fetch college names]
    private void setCollegeSpinner(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProfessorLoginActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


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


    private void stuLogin(DocumentReference docRef_fun, String profCollege, String profEmail, String profPass) {
        docRef_fun.collection(Constant.KEY_DB_FACULTY_COL)
                .document(profEmail)
                .addSnapshotListener((value, error) -> {
                    if(error != null) {
                        setErrorTxt(error.toString());
                    }

                    else {
//                        setErrorTxt("in else part");
//                        setErrorTxt("value - " + value.toString());

//                        setErrorTxt("in for loop");
                        String pass = value.getString(Constant.KEY_PROF_PASSWORD);
//                        setErrorTxt("password - " + pass);

                        try {
                            if (pass.equals(profPass)) {

                                preferences.putBoolean(Constant.KEY_IS_LOGGED_IN, true);
                                preferences.putString(Constant.KEY_PROF_NAME, value.getString(Constant.KEY_PROF_NAME));
                                preferences.putString(Constant.KEY_PROF_EMAIL, value.getString(Constant.KEY_PROF_EMAIL));
                                preferences.putString(Constant.KEY_PROF_COLLEGE, profCollege);
                                preferences.putString(Constant.KEY_PROF_BRANCH, value.getString(Constant.KEY_PROF_BRANCH));
                                preferences.putString(Constant.KEY_PROF_DEPARTMENT, value.getString(Constant.KEY_PROF_DEPARTMENT));
                                preferences.putString(Constant.KEY_PROF_DESIGNATION, value.getString(Constant.KEY_PROF_DESIGNATION));
                                preferences.putString(Constant.KEY_PROF_SEMESTER, value.getString(Constant.KEY_PROF_SEMESTER));


                                startActivity(new Intent(ProfessorLoginActivity.this, ProfessorDashboardActivity.class));
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