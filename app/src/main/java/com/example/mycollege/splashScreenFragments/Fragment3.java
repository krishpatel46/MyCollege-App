package com.example.mycollege.splashScreenFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mycollege.activities.AskUserRoleActivity;
import com.example.mycollege.databinding.Fragment3Binding;

public class Fragment3 extends Fragment {

    //View view;
    private Fragment3Binding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = Fragment3Binding.inflate(inflater, container, false);

        setListeners();

        //view = inflater.inflate(R.layout.fragment_3, container, false);
        return binding.getRoot();
    }

    private void setListeners() {
        binding.forwardBtn.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), AskUserRoleActivity.class));
            requireActivity().finish();
        });

        /*
        binding.skipTxt.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), AskUserRoleActivity.class));
            requireActivity().finish();
        });
        */
    }

    /*
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_view, fragment);

        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    */
}