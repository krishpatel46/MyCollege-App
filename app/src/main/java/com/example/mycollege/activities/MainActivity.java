package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.example.mycollege.R;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityMainBinding;
import com.example.mycollege.splashScreenFragments.Fragment1;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        boolean stuHasLoggedIn = checkLogin(Constant.KEY_STUDENT_PREFERENCE_NAME);
        boolean adminHasLoggedIn = checkLogin(Constant.KEY_ADMIN_PREFERENCE_NAME);
        boolean facultyHasLoggedIn = checkLogin(Constant.KEY_PROFESSOR_PREFERENCE_NAME);

        if(stuHasLoggedIn) {
            startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
            finish();
        }

        else if(adminHasLoggedIn) {
            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            finish();
        }

        else if(facultyHasLoggedIn) {
            startActivity(new Intent(MainActivity.this, ProfessorDashboardActivity.class));
            finish();
        }

        else {
            replaceFragment(new Fragment1());
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_view, fragment);

        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean checkLogin(String preferenceName) {
        ManagePreferences preferences = new ManagePreferences(getApplicationContext(), preferenceName);

        return preferences.getBoolean(Constant.KEY_IS_LOGGED_IN);
    }
}