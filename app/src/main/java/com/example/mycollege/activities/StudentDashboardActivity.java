package com.example.mycollege.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.mycollege.R;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityStudentDashboardBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Calendar;

public class StudentDashboardActivity extends AppCompatActivity {

    private ManagePreferences preferences;
    private ActivityStudentDashboardBinding binding;


    AlertDialog.Builder dialogBox;
    String encodedImageValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_STUDENT_PREFERENCE_NAME);
        String stuName = preferences.getString(Constant.KEY_STU_NAME);
        String stuEnrollNo = preferences.getString(Constant.KEY_STU_ENROLL_NO);
        String stuEmail = preferences.getString(Constant.KEY_STU_EMAIL);
        String stuBranch = preferences.getString(Constant.KEY_STU_BRANCH);
        String stuCourse = preferences.getString(Constant.KEY_STU_COURSE);
        String semester = preferences.getString(Constant.KEY_STU_SEMESTER);


        // set student details
        binding.stuNameTxt.setText(stuName);
        binding.stuEnrollNoTxt.setText(stuEnrollNo);
        binding.stuEmailTxt.setText(stuEmail);
        binding.stuBranchTxt.setText(stuBranch);
        binding.stuCourseTxt.setText(stuCourse);
        binding.stuSemTxt.setText(semester);


        // set listeners
        setListeners();
    }


    //  [method to set listeners]
    private void setListeners() {

        //  ...logout button logic...
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBox = new AlertDialog.Builder(StudentDashboardActivity.this);

                dialogBox.setIcon(AppCompatResources.getDrawable(StudentDashboardActivity.this, R.drawable.image_logout));
                dialogBox.setTitle(" LogOut from your account");
                dialogBox.setMessage("Are you sure you want to logout?");
                dialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(StudentDashboardActivity.this, "Logout cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.clear();

                        startActivity(new Intent(StudentDashboardActivity.this, AskUserRoleActivity.class));
                        finish();
                    }
                });

                AlertDialog dialog = dialogBox.create();
                dialog.show();
            }
        });

        //  ...editProfileImage button logic...
        binding.editProfileImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });


        /*** [logic to enable-disable attendance baabu button] ***/
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);

        //static implementation for putting up my concept
        int enableTargetHour = 11;
        int enableTargetMinute = 0;

        int disableTargetHour = 1;
        int disableTargetMinute = 59;

        if (currentHour == enableTargetHour && currentMinute >= enableTargetMinute && currentMinute <= disableTargetMinute) {
            binding.attendanceBaabuBtn.setEnabled(true);

            setABListener();
        }
        else if (currentHour != enableTargetHour && currentMinute >= disableTargetMinute) {

            setABListenerForTimeout();
            //binding.attendanceBaabuBtn.setEnabled(false);
        }


        //  ...notesensei button logic...
        binding.noteSensaiBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboardActivity.this, StudentNoteSensaiActivity.class);
            startActivity(intent);
        });

        binding.doubtForumBtn.setOnClickListener(view -> {
            Toast.makeText(this, "This service will be available in future", Toast.LENGTH_SHORT).show();
        });
    }


    private void setABListener() {
        //  ...attendanceBaabu button logic...
        binding.attendanceBaabuBtn.setOnClickListener(view -> {
            Intent intent = new Intent(StudentDashboardActivity.this, AttendanceBaabuActivity.class);
            startActivity(intent);
        });
    }

    private void setABListenerForTimeout() {
        binding.attendanceBaabuBtn.setOnClickListener(view -> {
            dialogBox = new AlertDialog.Builder(StudentDashboardActivity.this);

            dialogBox.setMessage("Attendance Timeout!");
            dialogBox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //do nothing
                }
            });
        });
    }


    //  [method to encode image]
    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = 150;
        //int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK) {
                    if(result.getData() != null) {
                        Uri imageUri = result.getData().getData();
//                        preferences.putString(Constant.KEY_STU_IMAGE, String.valueOf(imageUri));

                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            binding.profileImage.setImageBitmap(bitmap);

                            encodedImageValue = encodeImage(bitmap);

                        } catch(FileNotFoundException fnf) {
                            fnf.printStackTrace();
                        }
                    }
                }
            });


    //  [method to start new activity]
    private void startNewAct(Context fromActivityContext, Class<Activity> toActivityContext) {
        Intent intent = new Intent(fromActivityContext, toActivityContext);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(" Exit MyCollege");
        alert.setIcon(R.drawable.image_exit);
        alert.setMessage("Are you sure?");
        alert.setCancelable(false);

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(StudentDashboardActivity.this, "Exit cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}