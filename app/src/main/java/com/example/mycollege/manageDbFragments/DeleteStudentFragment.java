package com.example.mycollege.manageDbFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.FragmentDeleteStudentBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteStudentFragment extends Fragment {

    private FragmentDeleteStudentBinding binding;
    private ManagePreferences preferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteStudentBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle stuData = getArguments();
        if (stuData != null && !stuData.isEmpty()) {
            binding.stuEnrollNoTxt.setText(stuData.getString("stuEnrollNo"));
            binding.stuNameTxt.setText(stuData.getString("stuName"));
            binding.stuEmailTxt.setText(stuData.getString("stuEmail"));

            binding.stuBranchTxt.setText(stuData.getString("stuBranch"));
            binding.stuCourseTxt.setText(stuData.getString("stuCourse"));
            binding.stuSemTxt.setText(stuData.getString("stuSem"));
        }

        setListeners(collegeName, stuData);

        return binding.getRoot();
    }


    private void setListeners(String collegeName, Bundle stuData) {
        binding.submitBtn.setOnClickListener(view -> {
            try {
                String enrolNo = stuData.getString("stuEnrollNo");

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    setErrorTxt(error.getMessage());
                                } else {
                                    for (DocumentSnapshot docSnap : value) {
                                        String collegeID = docSnap.getId();
                                        deleteStudent(collegeID, enrolNo);
                                    }
                                }
                            }
                        });
            }
            catch (NullPointerException npe) {
                Toast.makeText(getActivity(), "Oops, details not available", Toast.LENGTH_LONG).show();
                removeFragment(DeleteStudentFragment.this);
            }
        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }


    private void deleteStudent(String collegeID, String enrolNo) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_STUDENT_COL)
                    .document(enrolNo)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "Student details deleted", Toast.LENGTH_SHORT).show();
                            removeFragment(DeleteStudentFragment.this);
                        }
                    });
        }
        catch (NullPointerException npe) {
            Toast.makeText(requireContext(), "Oops, details not available", Toast.LENGTH_LONG).show();
            removeFragment(DeleteStudentFragment.this);
        }
    }

    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if(isAdded()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.detach(fragment);

            fragmentTransaction.commit();

            fragmentManager.popBackStack();
        }
    }
}