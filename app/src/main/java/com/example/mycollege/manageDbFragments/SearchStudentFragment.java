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
import com.example.mycollege.databinding.FragmentSearchStudentBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SearchStudentFragment extends Fragment {

    private FragmentSearchStudentBinding binding;

    private ManagePreferences preferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    private final String DELETE = "delete";
    private final String EDIT = "edit";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSearchStudentBinding.inflate(inflater, container, false);


        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        setListeners();

        return binding.getRoot();
    }

    private void setListeners() {
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        binding.submitBtn.setOnClickListener(view -> {

            String enrolNo = binding.stuEnrollNoTxt.getText().toString().trim();
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
                                    searchStudent(collegeID, enrolNo);
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

    private void searchStudent(String collegeID, String enrolNo) {
        Bundle cardInfo = getArguments();

        colRef.document(collegeID).collection(Constant.KEY_DB_STUDENT_COL)
                .whereEqualTo(Constant.KEY_STU_ENROLL_NO, enrolNo)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    UpdateStudentFragment updateStudentFragment;
//                    DeleteStudentFragment deleteStudentFragment;

                    UpdateStudentActivity updateStudentActivity;

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!isAdded()) {
                            // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                            return;
                        }
                        else {
                            Bundle stuData = new Bundle();

                            if(value!=null) {
                                for (DocumentSnapshot docSnap : value) {
                                    String stuName = docSnap.getString(Constant.KEY_STU_NAME);
                                    String stuEmail = docSnap.getString(Constant.KEY_STU_EMAIL);
                                    String stuBranch = docSnap.getString(Constant.KEY_STU_BRANCH);
                                    String stuCourse = docSnap.getString(Constant.KEY_STU_COURSE);
                                    String stuSem = docSnap.getString(Constant.KEY_STU_SEMESTER);
                                    String stuPass = docSnap.getString(Constant.KEY_STU_PASSWORD);


                                    //send student data to next fragment using bundle
                                    stuData.putString("stuEnrollNo", enrolNo);
                                    stuData.putString("stuName", stuName);
                                    stuData.putString("stuEmail", stuEmail);
                                    stuData.putString("stuBranch", stuBranch);
                                    stuData.putString("stuCourse", stuCourse);
                                    stuData.putString("stuSem", stuSem);
                                    stuData.putString("stuPass", stuPass);
                                }

                                if (cardInfo != null && cardInfo.getString("menuID").equals(EDIT)) {
                                    if(isAdded()) {
                                        Intent intent = new Intent(requireActivity(), UpdateStudentActivity.class);
                                        intent.putExtra("stuData", stuData);

                                        startActivity(intent);
                                        removeFragment(SearchStudentFragment.this);
                                        requireActivity().finish();
                                    }
                                }
                                else if (cardInfo != null && cardInfo.getString("menuID").equals(DELETE)) {
                                    if(isAdded()) {
                                        Intent intent = new Intent(requireActivity(), DeleteStudentActivity.class);
                                        intent.putExtra("stuData", stuData);

                                        startActivity(intent);
                                        removeFragment(SearchStudentFragment.this);
                                        requireActivity().finish();
                                    }
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