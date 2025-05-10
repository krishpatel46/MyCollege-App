package com.example.mycollege.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

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
import com.example.mycollege.databinding.ActivityProfessorDashboardBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;

public class ProfessorDashboardActivity extends AppCompatActivity {

    private ActivityProfessorDashboardBinding binding;
    private ManagePreferences preferences;

    AlertDialog.Builder dialogBox;
    String encodedImageValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfessorDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_PROFESSOR_PREFERENCE_NAME);
        String profName = preferences.getString(Constant.KEY_PROF_NAME);
        String profDesignation = preferences.getString(Constant.KEY_PROF_DESIGNATION);
        String profEmail = preferences.getString(Constant.KEY_PROF_EMAIL);
        String profBranch = preferences.getString(Constant.KEY_PROF_BRANCH);
        String profDept = preferences.getString(Constant.KEY_PROF_DEPARTMENT);
        String semester = preferences.getString(Constant.KEY_PROF_SEMESTER);


        // set student details
        binding.profNameTxt.setText(profName);
        binding.profEmailTxt.setText(profEmail);
        binding.profDesignationTxt.setText(profDesignation);
        binding.profBranchTxt.setText(profBranch);
        binding.profDeptTxt.setText(profDept);
        binding.profSemTxt.setText(semester);


        // set listeners
        setListeners();
    }


    //  [method to set listeners]
    private void setListeners() {

        //  ...logout button logic...
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogBox = new AlertDialog.Builder(ProfessorDashboardActivity.this);

                dialogBox.setIcon(AppCompatResources.getDrawable(ProfessorDashboardActivity.this, R.drawable.image_logout));
                dialogBox.setTitle(" LogOut from your account");
                dialogBox.setMessage("Are you sure you want to logout?");
                dialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ProfessorDashboardActivity.this, "Logout cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.clear();

                        startActivity(new Intent(ProfessorDashboardActivity.this, AskUserRoleActivity.class));
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


        //  ...attendanceBaabu button logic...
        binding.attendanceBaabuBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ProfessorDashboardActivity.this, ProfessorAttendanceBaabuActivity.class);
            intent.putExtra("CollegeName", preferences.getString(Constant.KEY_PROF_COLLEGE));

            startActivity(intent);
        });


        //  ...notesensai button logic...
        binding.noteSensaiBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ProfessorDashboardActivity.this, ProfessorNoteSenseiActivity.class);
            startActivity(intent);
        });

        binding.doubtForumBtn.setOnClickListener(view -> {
            Toast.makeText(this, "This service will be available in future", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        dialogBox = new AlertDialog.Builder(this);

        dialogBox.setTitle(" Exit MyCollege");
        dialogBox.setIcon(R.drawable.image_exit);
        dialogBox.setMessage("Are you sure?");
        dialogBox.setCancelable(false);

        dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        dialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(ProfessorDashboardActivity.this, "Exit cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = dialogBox.create();
        alertDialog.show();
    }
}