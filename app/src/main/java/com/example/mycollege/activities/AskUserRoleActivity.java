package com.example.mycollege.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mycollege.R;
import com.example.mycollege.databinding.ActivityAskUserRoleBinding;

public class AskUserRoleActivity extends AppCompatActivity {

    private ActivityAskUserRoleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAskUserRoleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    private void setListeners() {

        //when clicked on register button...
        binding.registerBtn.setOnClickListener(view -> {
            startActivity(new Intent(AskUserRoleActivity.this, RegisterCollegeActivity.class));
            finish();
        });

        //when clicked on admin login button...
        binding.adminLoginBtn.setOnClickListener(view -> {
            startActivity(new Intent(AskUserRoleActivity.this, AdminLoginActivity.class));
            finish();
        });

        //when clicked on student login button...
        binding.studentBtn.setOnClickListener(view -> {
            startActivity(new Intent(AskUserRoleActivity.this, StudentLoginActivity.class));
            finish();
        });

        //when clicked on professor login button...
        binding.profBtn.setOnClickListener(view -> {
            startActivity(new Intent(AskUserRoleActivity.this, ProfessorLoginActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(" Exit MyCollege");
        alert.setIcon(R.drawable.image_exit);
        alert.setMessage("Are you sure?");
        alert.setCancelable(false);

        alert.setPositiveButton("Yes", (dialogInterface, i) -> finish());

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(AskUserRoleActivity.this, "Exit cancelled", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }
}