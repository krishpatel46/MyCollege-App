package com.example.mycollege.manageDbFragments;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityDeleteStudentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteStudentActivity extends AppCompatActivity {

    private ActivityDeleteStudentBinding binding;
    private ManagePreferences preferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteStudentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(DeleteStudentActivity.this, Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle stuData = getIntent().getBundleExtra("stuData");
        if (stuData != null && !stuData.isEmpty()) {
            binding.stuEnrollNoTxt.setText(stuData.getString("stuEnrollNo"));
            binding.stuNameTxt.setText(stuData.getString("stuName"));
            binding.stuEmailTxt.setText(stuData.getString("stuEmail"));

            binding.stuBranchTxt.setText(stuData.getString("stuBranch"));
            binding.stuCourseTxt.setText(stuData.getString("stuCourse"));
            binding.stuSemTxt.setText(stuData.getString("stuSem"));
        }

        setListeners(collegeName, stuData);
    }

    private void setListeners(String collegeName, Bundle stuData) {
        binding.submitBtn.setOnClickListener(view -> {
            try {
                String enrolNo = stuData.getString("stuEnrollNo");

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    setErrorTxt(error.getMessage());
                                } else {
                                    for (DocumentSnapshot docSnap : value) {
                                        String collegeID = docSnap.getId();
                                        deleteStudent(collegeID, enrolNo);
                                    }
                                }
                            }
                        });
            }
            catch (NullPointerException npe) {
                Toast.makeText(getApplicationContext(), "Oops, details not available", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });


        binding.cancelBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }


    private void deleteStudent(String collegeID, String enrolNo) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_STUDENT_COL)
                    .document(enrolNo)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Student details deleted", Toast.LENGTH_SHORT).show();
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

        startActivity(new Intent(DeleteStudentActivity.this, ManageDbAdminActivity.class));
        finish();
    }
}