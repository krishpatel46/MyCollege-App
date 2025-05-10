package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.mycollege.R;
import com.example.mycollege.databinding.ActivityManageDbAdminBinding;
import com.example.mycollege.manageDbFragments.DeleteFragment;
import com.example.mycollege.manageDbFragments.InsertFragment;
import com.example.mycollege.manageDbFragments.SearchProfessorFragment;
import com.example.mycollege.manageDbFragments.SearchStudentFragment;
import com.example.mycollege.manageDbFragments.UpdateFragment;
import com.example.mycollege.manageDbFragments.ViewProfessorFragment;
import com.example.mycollege.manageDbFragments.ViewStudentFragment;

public class ManageDbAdminActivity extends AppCompatActivity {

    private ActivityManageDbAdminBinding binding;

    Bundle cardInfo;
    private final String DELETE = "delete";
    private final String EDIT = "edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageDbAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    private void setListeners() {
        binding.insertCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment(new InsertFragment());
            }
        });


        binding.deleteCard.setOnClickListener(new View.OnClickListener() {
            SearchStudentFragment searchStudentFragment;
            SearchProfessorFragment searchProfessorFragment;

            @Override
            public void onClick(View view) {
                PopupMenu manageMenu = new PopupMenu(ManageDbAdminActivity.this, view);
                manageMenu.getMenuInflater().inflate(R.menu.menu, manageMenu.getMenu());
                manageMenu.show();

                cardInfo = new Bundle();
                cardInfo.putString("menuID", DELETE);

                manageMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.branchDetails:
                                replaceFragment(new DeleteFragment());
                                return true;

                            case R.id.stuDetails:
                                searchStudentFragment = new SearchStudentFragment();
                                searchStudentFragment.setArguments(cardInfo);
                                replaceFragment(searchStudentFragment);
                                return true;

                            case R.id.professorDetails:
                                searchProfessorFragment = new SearchProfessorFragment();
                                searchProfessorFragment.setArguments(cardInfo);
                                replaceFragment(searchProfessorFragment);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        });


        binding.updateCard.setOnClickListener(new View.OnClickListener() {
            SearchStudentFragment searchStudentFragment;
            SearchProfessorFragment searchProfessorFragment;

            @Override
            public void onClick(View view) {
                PopupMenu manageMenu = new PopupMenu(ManageDbAdminActivity.this, view);
                manageMenu.getMenuInflater().inflate(R.menu.menu, manageMenu.getMenu());
                manageMenu.show();

                cardInfo = new Bundle();
                cardInfo.putString("menuID", EDIT);

                manageMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.branchDetails:
                                replaceFragment(new UpdateFragment());
                                return true;

                            case R.id.stuDetails:
                                searchStudentFragment = new SearchStudentFragment();
                                searchStudentFragment.setArguments(cardInfo);
                                replaceFragment(searchStudentFragment);
                                return true;

                            case R.id.professorDetails:
                                searchProfessorFragment = new SearchProfessorFragment();
                                searchProfessorFragment.setArguments(cardInfo);
                                replaceFragment(searchProfessorFragment);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        });

        binding.readCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu manageMenu = new PopupMenu(ManageDbAdminActivity.this, view);
                manageMenu.getMenuInflater().inflate(R.menu.menu, manageMenu.getMenu());
                manageMenu.show();

                manageMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.branchDetails:
                                Toast.makeText(ManageDbAdminActivity.this, "Sorry! Service not available", Toast.LENGTH_SHORT).show();
                                return true;

                            case R.id.stuDetails:
                                replaceFragment(new ViewStudentFragment());
                                return true;

                            case R.id.professorDetails:
                                replaceFragment(new ViewProfessorFragment());
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        });
        //........... all buttons added!!
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(!fragment.isAdded()) {
            fragmentTransaction.replace(binding.commonFragmentView.getId(), fragment);
            fragmentTransaction.addToBackStack(null);

            fragmentTransaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ManageDbAdminActivity.this, AdminDashboardActivity.class));
        finish();
    }
}