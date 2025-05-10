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

import com.example.mycollege.R;
import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.FragmentSearchProfessorBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchProfessorFragment extends Fragment {

    private FragmentSearchProfessorBinding binding;

    private ManagePreferences preferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    private final String DELETE = "delete";
    private final String EDIT = "edit";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSearchProfessorBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        setListeners();

        return binding.getRoot();
    }

    private void setListeners() {
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        binding.submitBtn.setOnClickListener(view -> {

            String email = binding.profEmailTxt.getText().toString().trim();
            colRef.whereEqualTo("Name", collegeName)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (!isAdded()) {
                                // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                                return;
                            }
                            else {
                                for(DocumentSnapshot docSnap: value) {
                                    String collegeID = docSnap.getId();
                                    searchStudent(collegeID, email);
                                }

                                //branchAdapter.notifyDataSetChanged();
                            }
                        }
                    });
        });

        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }

    private void searchStudent(String collegeID, String email) {
        Bundle cardInfo = getArguments();

        colRef.document(collegeID).collection(Constant.KEY_DB_FACULTY_COL)
                .whereEqualTo(Constant.KEY_PROF_EMAIL, email)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    UpdateProfessorFragment updateProfessorFragment;
                    DeleteProfessorFragment deleteProfessorFragment;

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!isAdded()) {
                            // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                            return;
                        }
                        else {
                            Bundle profData = new Bundle();

                            if(value!=null) {
                                for (DocumentSnapshot docSnap : value) {
                                    String profName = docSnap.getString(Constant.KEY_PROF_NAME);
                                    String profEmail = docSnap.getString(Constant.KEY_PROF_EMAIL);
                                    String profDesig = docSnap.getString(Constant.KEY_PROF_DESIGNATION);
                                    String profBranch = docSnap.getString(Constant.KEY_PROF_BRANCH);
                                    String profDept = docSnap.getString(Constant.KEY_PROF_DEPARTMENT);
                                    String profSem = docSnap.getString(Constant.KEY_PROF_SEMESTER);
                                    String profPass = docSnap.getString(Constant.KEY_PROF_PASSWORD);


                                    //send student data to next fragment using bundle
                                    profData.putString("profEmail", profEmail);
                                    profData.putString("profName", profName);
                                    profData.putString("profDesig", profDesig);
                                    profData.putString("profBranch", profBranch);
                                    profData.putString("profDept", profDept);
                                    profData.putString("profSem", profSem);
                                    profData.putString("profPass", profPass);
                                }

                                if (cardInfo != null && cardInfo.getString("menuID").equals(EDIT)) {
                                    Intent intent = new Intent(requireActivity(), UpdateProfessorActivity.class);
                                    intent.putExtra("profData", profData);

                                    startActivity(intent);
                                    removeFragment(SearchProfessorFragment.this);
                                    requireActivity().finish();
                                }
                                else if (cardInfo != null && cardInfo.getString("menuID").equals(DELETE)) {
                                    Intent intent = new Intent(requireActivity(), DeleteProfessorActivity.class);
                                    intent.putExtra("profData", profData);

                                    startActivity(intent);
                                    removeFragment(SearchProfessorFragment.this);
                                    requireActivity().finish();
                                }
                            }
                            else {
                                binding.errorTxt.setText("Oops, details not available");
                            }
                        }
                    }
                });
    }

    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if(fragment.isAdded()) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragment);
            fragmentTransaction.detach(fragment);

            fragmentTransaction.commit();

            fragmentManager.popBackStack();
        }
    }
}