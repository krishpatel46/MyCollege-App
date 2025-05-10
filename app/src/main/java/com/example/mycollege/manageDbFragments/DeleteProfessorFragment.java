package com.example.mycollege.manageDbFragments;

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
import com.example.mycollege.databinding.FragmentDeleteProfessorBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteProfessorFragment extends Fragment {

    private FragmentDeleteProfessorBinding binding;

    private ManagePreferences preferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDeleteProfessorBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle profData = getArguments();
        if (profData != null && !profData.isEmpty()) {
            binding.profEmailTxt.setText(profData.getString("profEmail"));
            binding.profNameTxt.setText(profData.getString("profName"));

            binding.profDesigTxt.setText(profData.getString("profDesig"));
            binding.profBranchTxt.setText(profData.getString("profBranch"));
            binding.profDeptTxt.setText(profData.getString("profDept"));
            binding.profSemTxt.setText(profData.getString("profSem"));
        }

        setListeners(collegeName, profData);

        return binding.getRoot();
    }


    private void setListeners(String collegeName, Bundle profData) {
        binding.submitBtn.setOnClickListener(view -> {
            try {
                String email = profData.getString("profEmail");

                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    setErrorTxt(error.getMessage());
                                } else {
                                    for (DocumentSnapshot docSnap : value) {
                                        String collegeID = docSnap.getId();
                                        deleteProfessor(collegeID, email);
                                    }
                                }
                            }
                        });
            }
            catch (NullPointerException npe) {
                Toast.makeText(getActivity(), "Oops, details not available", Toast.LENGTH_LONG).show();
                removeFragment(DeleteProfessorFragment.this);
            }
        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }


    private void deleteProfessor(String collegeID, String email) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_FACULTY_COL)
                    .document(email)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(), "Professor details deleted", Toast.LENGTH_SHORT).show();
                            removeFragment(DeleteProfessorFragment.this);
                        }
                    });
        }
        catch (NullPointerException npe) {
            Toast.makeText(getContext(), "Oops, details not available", Toast.LENGTH_LONG).show();
            removeFragment(DeleteProfessorFragment.this);
        }
    }

    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);

//        fragmentTransaction.addToBackStack("SearchStu");
        fragmentTransaction.commit();
    }
}