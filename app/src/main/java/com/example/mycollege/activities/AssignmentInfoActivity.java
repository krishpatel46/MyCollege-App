package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.mycollege.databinding.ActivityAssignmentInfoBinding;

public class AssignmentInfoActivity extends AppCompatActivity {

    private ActivityAssignmentInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignmentInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();

        binding.headingTxt2.setText(intent.getStringExtra("Heading"));
        binding.descriptionTxt2.setText(intent.getStringExtra("Description"));
        binding.subjectTxt2.setText(intent.getStringExtra("Subject"));
        binding.semTxt2.setText(intent.getStringExtra("Semester"));
        binding.submitAssignmentBtn.setOnClickListener(view -> {
            Uri submissionLink = Uri.parse(intent.getStringExtra("Link"));

            Intent newIntent = new Intent(Intent.ACTION_VIEW, submissionLink);
            startActivity(newIntent);
        });
    }
}