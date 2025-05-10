package com.example.mycollege.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityAttendancePreviewBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Timer;
import java.util.TimerTask;

public class AttendancePreviewActivity extends AppCompatActivity {

    private Timer activityTimer;
    private static final long DISPLAY_LENGTH = 30000;
    private ActivityAttendancePreviewBinding binding;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    DocumentReference docRef;
    private ManagePreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendancePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        //  [set enrollment number and lecture (subject) name]
        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_STUDENT_PREFERENCE_NAME);
        String stuEnrollNo = preferences.getString(Constant.KEY_STU_ENROLL_NO);

        Intent i = getIntent();
        String subjectName = i.getStringExtra("qrText");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.subjectNamePreview.setText(subjectName);
                binding.stuEnrollNoPreview.setText(stuEnrollNo);
            }
        });


        //  [set submit button listener]
        binding.submitBtn.setOnClickListener(view -> {
            /* -------------- fetch current attendance -------------- */

            String collegeName = preferences.getString(Constant.KEY_STU_COLLEGE);

            colRef.whereEqualTo("Name", collegeName)
                    .addSnapshotListener((value, error) -> {

                        for(QueryDocumentSnapshot docSnap: value) {
                            String docID = docSnap.getId();

                            docRef = colRef.document(docID).collection(Constant.KEY_DB_STUDENT_COL).document(stuEnrollNo)
                                    .collection(Constant.KEY_DB_ATTENDANCE_COL).document(subjectName);

                            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot != null) {
                                        Log.d("value", documentSnapshot.toString());

                                        int presentValue = documentSnapshot.getLong("Present").intValue();
                                        String nm = documentSnapshot.getString("SubjectName");

                                        Log.d("subject name", nm + "null string");
                                        Log.d("present value", String.valueOf(presentValue));
                                        Log.d("doc path", docRef.getPath());

                                        fillAttendance(presentValue, docRef);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("TAG", "Error getting document: ", e);
                                }
                            });
                        }
                    });

        });

        //  < old location of above code block >


        activityTimer = new Timer();
        activityTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, DISPLAY_LENGTH);
    }

    private void fillAttendance(int presentValue, DocumentReference docRef_fun) {
        /* -------------- fill attendance -------------- */

            int finalPresentValue = presentValue + 1;

            Log.d("final present value", String.valueOf(finalPresentValue));

            docRef_fun.update("Present", finalPresentValue)
                    .addOnSuccessListener(unused -> {
                        binding.checkView.setVisibility(View.VISIBLE);

                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {

                                //because one cannot run a ui element in another thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AttendancePreviewActivity.this, "Attendance filled!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AttendancePreviewActivity.this, StudentDashboardActivity.class));
                                        finish();
                                    }
                                });

                            }
                        }, 3000);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(AttendancePreviewActivity.this, "Oops! Something went wrong.", Toast.LENGTH_LONG).show();
                    });
            /* -------------- attendance filled -------------- */

    }
}