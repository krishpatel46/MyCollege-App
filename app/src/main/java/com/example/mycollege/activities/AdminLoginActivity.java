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
import com.example.mycollege.databinding.ActivityAdminLoginBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminLoginActivity extends AppCompatActivity {

    private ManagePreferences preferences;
    private ActivityAdminLoginBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    TextView errorTxt;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_ADMIN_PREFERENCE_NAME);

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
            if(isLoginDetailsFilled()) {
                try {
//                    String adminName = binding.adminNameTxt.getText().toString().trim();
                    String adminEmail = binding.collegeEmailTxt.getText().toString().trim();
                    String adminPass = binding.adminPasswordTxt.getText().toString().trim();
                    String collegeName = binding.collegeSpinner.getSelectedItem().toString().trim();

//                    setErrorTxt("All details filled... College name - " + stuCollege);

                    colRef.whereEqualTo("Name", collegeName)
                            .addSnapshotListener((value, error) -> {
//                                setErrorTxt("in snapshot listener");
                                if(error != null) {
                                    setErrorTxt(error.toString());
                                }

                                else {
                                    setErrorTxt("in else part");
                                    setErrorTxt("value - " + value.toString());


                                    for(DocumentSnapshot docSnap: value) {
                                        String pass = docSnap.getString("Password");
                                        setErrorTxt("password - " + pass);

                                        if (pass.equals(adminPass)) {

                                            preferences.putBoolean(Constant.KEY_IS_LOGGED_IN, true);
                                            preferences.putString(Constant.KEY_ADMIN_NAME, docSnap.getString("AdminName"));
                                            preferences.putString(Constant.KEY_ADMIN_EMAIL, docSnap.getString("Email"));
                                            preferences.putString(Constant.KEY_ADMIN_COLLEGE_NAME, docSnap.getString("Name"));

                                            startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                                            finish();
                                        } else {
                                            setErrorTxt("*Wrong Password: Please enter correct password");
                                        }
                                    }
                                }
                            });
                } catch(NullPointerException npe) {
                    setErrorTxt("*Please select your college");
                }

            }
        });


        /* cancel button logic */
        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(AdminLoginActivity.this, AskUserRoleActivity.class));
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

        if (! Patterns.EMAIL_ADDRESS.matcher(binding.collegeEmailTxt.getText().toString().trim()).matches()) {
            setErrorTxt("*Please enter an email id");

            return false;
        }

        else if(binding.adminPasswordTxt.getText().toString().trim().isEmpty()) {
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
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(AdminLoginActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList);


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
}