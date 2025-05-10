package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycollege.databinding.ActivityRegisterCollegeBinding;
import com.example.mycollege.objects.College;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterCollegeActivity extends AppCompatActivity {

    private ActivityRegisterCollegeBinding binding;
    TextView errorTxt;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection("CollegeDetails");
    DocumentReference docRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterCollegeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {
        //submit logic
        binding.submitBtn.setOnClickListener(view -> {

            errorTxt = binding.errorTxt;

            String college_name = binding.collegeNameTxt.getText().toString();
            String admin_name = binding.adminNameTxt.getText().toString();
            String uni_name = binding.uniNameTxt.getText().toString();
            String reg_no = binding.collegeRegNumTxt.getText().toString().trim();
            String mail = binding.collegeEmailTxt.getText().toString().trim();
            String pass = binding.passwordTxt.getText().toString().trim();
            String confirm_pass = binding.confirmPasswordTxt.getText().toString().trim();

            int temp_regNo;
            try {
                temp_regNo = Integer.parseInt(reg_no);

                if(college_name.isEmpty() || uni_name.isEmpty() || reg_no.isEmpty() || mail.isEmpty()) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Please enter all the college details");
                }

                else if(!(temp_regNo >= 1 && temp_regNo <= 999)) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Please enter a valid reg. number!");
                }

                else if(!Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Please enter a valid email address!");
                }

                else if(pass.isEmpty()) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Password field is empty!");
                }

                else if(pass.length() <= 5) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Weak Password: Use atleast\n 6 characters or more");
                }

                else if(confirm_pass.isEmpty()) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Confirm-Password field is empty!");
                }

                else if(!pass.equals(confirm_pass)) {
                    errorTxt.setVisibility(View.VISIBLE);
                    errorTxt.setText("*Make sure both the passwords match");
                }

                else {
                    //FirestoreDB db = new FirestoreDB();

                    //store details in a java object
                    College collegeData = new College(college_name, admin_name, uni_name, reg_no, mail, pass);

                    insertCollegeDetails(mail, collegeData);
                }

            } catch (NumberFormatException nfe) {
                errorTxt.setVisibility(View.VISIBLE);
                errorTxt.setText("*Please enter a reg. number!");
                //errorTxt.setText(nfe.toString());
            }
        });


        //cancel logic
        binding.cancelBtn.setOnClickListener(view -> startActivity(new Intent(RegisterCollegeActivity.this, AskUserRoleActivity.class)));
    }

    private void insertCollegeDetails(String mail, College collegeData) {

        docRef = colRef.document(mail);

        Map<String, Object> collegeDataMap = new HashMap<>();
        collegeDataMap.put("Email", collegeData.getEmail());
        collegeDataMap.put("Name", collegeData.getName());
        collegeDataMap.put("Password", collegeData.getPassword());
        collegeDataMap.put("RegNo", collegeData.getRegNo());
        collegeDataMap.put("UniversityName", collegeData.getUniversityName());
        collegeDataMap.put("AdminName", collegeData.getAdminName());


        //insert details
        docRef.set(collegeDataMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(RegisterCollegeActivity.this, "Your college is registered!", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegisterCollegeActivity.this, AdminLoginActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterCollegeActivity.this, "Oops! Something went wrong.", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(RegisterCollegeActivity.this, AskUserRoleActivity.class));
        finish();
    }
}