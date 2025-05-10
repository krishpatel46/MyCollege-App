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
import com.example.mycollege.databinding.ActivityAdminDashboardBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Base64;

public class AdminDashboardActivity extends AppCompatActivity {

    private ManagePreferences preferences;
    private ActivityAdminDashboardBinding binding;

    AlertDialog.Builder dialogBox;
    String encodedImageValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferences = new ManagePreferences(getApplicationContext(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String adminName = preferences.getString(Constant.KEY_ADMIN_NAME);
        String adminEmail = preferences.getString(Constant.KEY_ADMIN_EMAIL);


        //set admin details
        binding.adminNameTxt.setText(adminName);
        binding.adminEmailTxt.setText(adminEmail);


        // set listeners
        setListeners();
    }

    //  [method to set listeners]
    private void setListeners() {

        //  ...logout button logic...
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//  ...logout button logic...
                binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialogBox = new AlertDialog.Builder(AdminDashboardActivity.this);

                        dialogBox.setIcon(AppCompatResources.getDrawable(AdminDashboardActivity.this, R.drawable.image_logout));
                        dialogBox.setTitle(" LogOut from your account");
                        dialogBox.setMessage("Are you sure you want to logout?");
                        dialogBox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(AdminDashboardActivity.this, "Logout cancelled", Toast.LENGTH_SHORT).show();
                            }
                        });
                        dialogBox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                preferences.clear();

                                startActivity(new Intent(AdminDashboardActivity.this, AskUserRoleActivity.class));
                                finish();
                            }
                        });

                        AlertDialog dialog = dialogBox.create();
                        dialog.show();
                    }
                });
            }
        });

        //  ...editProfileImage button logic...
        binding.editProfileImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            pickImage.launch(intent);
        });


        //  ...manage database button logic...
        binding.manageDbBtn.setOnClickListener(view -> {

            Intent intent = new Intent(AdminDashboardActivity.this, ManageDbAdminActivity.class);
            startActivity(intent);
            finish();
//            PopupMenu manageMenu = new PopupMenu(AdminDashboardActivity.this, view);
//            manageMenu.getMenuInflater().inflate(R.menu.menu, manageMenu.getMenu());
//            manageMenu.show();
//
//            manageMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//
//                    Intent intent;
//                    switch(menuItem.getItemId()) {
//                        case R.id.branchDetails:
//                            intent = new Intent(AdminDashboardActivity.this, ManageDbAdminActivity.class);
//                            startActivity(intent);
//
//                            return true;
//
//                        case R.id.stuDetails:
//                            intent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
//                            startActivity(intent);
//
//                            return true;
//
//                        case R.id.professorDetails:
//                            intent = new Intent(AdminDashboardActivity.this, AdminDashboardActivity.class);
//                            startActivity(intent);
//
//                            return true;
//
//                        default:
//                            return false;
//                    }
//                }
//            });
        });


        //  ...set QR-code button logic...
        binding.setQRBtn.setOnClickListener(view -> {

            Intent intent = new Intent(AdminDashboardActivity.this, GenerateQRCodeActivity.class);
            startActivity(intent);
            finish();
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
                Toast.makeText(AdminDashboardActivity.this, "Exit cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}