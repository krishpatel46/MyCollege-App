package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.example.mycollege.databinding.ActivityLoadingBinding;

import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {

    Timer timer;

    private ActivityLoadingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoadingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                startActivity(new Intent(LoadingActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }
}