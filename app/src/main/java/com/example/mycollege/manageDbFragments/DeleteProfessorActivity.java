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
import com.example.mycollege.databinding.ActivityDeleteProfessorBinding;
import com.example.mycollege.databinding.FragmentDeleteProfessorBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteProfessorActivity extends AppCompatActivity {

    private ActivityDeleteProfessorBinding binding;

    private ManagePreferences preferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteProfessorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle profData = getIntent().getBundleExtra("profData");
        if (profData != null && !profData.isEmpty()) {
            binding.profEmailTxt.setText(profData.getString("profEmail"));
            binding.profNameTxt.setText(profData.getString("profName"));

            binding.profDesigTxt.setText(profData.getString("profDesig"));
            binding.profBranchTxt.setText(profData.getString("profBranch"));
            binding.profDeptTxt.setText(profData.getString("profDept"));
            binding.profSemTxt.setText(profData.getString("profSem"));
        }

        setListeners(collegeName, profData);
    }

    private void setListeners(String collegeName, Bundle profData) {
        binding.submitBtn.setOnClickListener(view -> {
            try {
                String email = profData.getString("profEmail");

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    setErrorTxt(error.getMessage());
                                } else {
                                    for (DocumentSnapshot docSnap : value) {
                                        String collegeID = docSnap.getId();
                                        deleteProfessor(collegeID, email);
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


    private void deleteProfessor(String collegeID, String email) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_FACULTY_COL)
                    .document(email)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Professor details deleted", Toast.LENGTH_SHORT).show();
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

        startActivity(new Intent(DeleteProfessorActivity.this, ManageDbAdminActivity.class));
        finish();
    }
}