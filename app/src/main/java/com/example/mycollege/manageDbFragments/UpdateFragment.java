package com.example.mycollege.manageDbFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.FragmentUpdateBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateFragment extends Fragment {

    private FragmentUpdateBinding binding;
    private ManagePreferences preferences;

    Spinner branchSpinner, courseSpinner;
    ArrayAdapter<String> branchAdapter, courseAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentUpdateBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);


        //  ...set spinners...
        branchSpinner = binding.branchSpinner;
        courseSpinner = binding.courseSpinner;


        //CollectionReference colRef_local = db.collection(Constant.KEY_DB_BRANCH_COL);

        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);
//        colRef = colRef.document(collegeName).collection(Constant.KEY_DB_BRANCH_COL);
//
//
//        branchAdapter = setBranchSpinner(colRef);
//        branchSpinner.setAdapter(branchAdapter);
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

                                colRef = colRef.document(collegeID).collection(Constant.KEY_DB_BRANCH_COL);
                                branchAdapter = setBranchSpinner(colRef);
                                branchSpinner.setAdapter(branchAdapter);
                            }
                        }
                    }
                });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String branchName = branchSpinner.getSelectedItem().toString();

                colRef.whereEqualTo(Constant.KEY_BRANCH_NAME, branchName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (!isAdded()) {
                                    // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                                    return;
                                }
                                else {
                                    assert value != null;
                                    for(DocumentSnapshot docSnap:value) {
                                        String branchColID = docSnap.getId();
                                        setCourseSpinner(colRef, branchColID);
                                    }
//                                    courseAdapter.notifyDataSetChanged();
//                                    binding.courseSpinner.setAdapter(courseAdapter);
                                }
                            }
                        });
                //adapter = setCourseSpinner(colRef_local, branchName);
                //binding.courseSpinner.setAdapter(courseAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //  ...set listeners...
        setListeners();

        return binding.getRoot();
    }


    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }

    private Boolean isBranchDetailsFilled() {
        if(binding.branchSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please select a branch");

            return false;
        }

        else if(binding.courseSpinner.getSelectedItem().toString().isEmpty()) {
            setErrorTxt("*Please enter a course");

            return false;
        }

        else if(binding.courseDurationTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please maintain the default value\nOr type appropriate duration (years)");

            return false;
        }

        else if (binding.semesterTxt.getText().toString().trim().isEmpty()) {
            setErrorTxt("*Please maintain the default value\nOr type total semesters of course");

            return false;
        }

        else {
            return true;
        }
    }

    private ArrayAdapter<String> setBranchSpinner(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!isAdded()) {
                    // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                    return;
                }
                else {
                    arrayList.clear();

                    assert value != null;
                    for(DocumentSnapshot docSnap:value) {
                        String branchName = docSnap.getString(Constant.KEY_BRANCH_NAME);
                        arrayList.add(branchName);
                    }

                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        return arrayAdapter;
    }

    private void getBranchCol(CollectionReference colRef_fun, String branchName) {
        colRef_fun.whereEqualTo(Constant.KEY_BRANCH_NAME, branchName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            assert value != null;
                            for(DocumentSnapshot docSnap:value) {
                                String branchColID = docSnap.getId();
                                setCourseSpinner(colRef_fun, branchColID);
                            }
                        }
                    }
                });
    }

    private void setCourseSpinner(CollectionReference colRef_fun, String branchColID) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(branchColID)
                .collection(Constant.KEY_DB_COURSE_COL)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (!isAdded()) {
                            // Fragment is no longer attached to the activity, do nothing or handle accordingly.
                            return;
                        }
                        else {
                            arrayList.clear();

                            assert value != null;
                            for(DocumentSnapshot docSnap:value) {
                                String courseName = docSnap.getString(Constant.KEY_COURSE_NAME);
                                String courseDuration = docSnap.getString(Constant.KEY_COURSE_DURATION);
                                String numOfSem = String.valueOf(docSnap.get(Constant.KEY_SEM_NUM));

                                binding.courseDurationTxt.setText(courseDuration);
                                binding.semesterTxt.setText(numOfSem);
                                arrayList.add(courseName);
                            }

                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

        binding.courseSpinner.setAdapter(arrayAdapter);
    }

    private void setListeners() {
        binding.submitBtn.setOnClickListener(view -> {

            if(isBranchDetailsFilled()) {
                String branchName = binding.branchSpinner.getSelectedItem().toString();
                String courseName = binding.courseSpinner.getSelectedItem().toString();
                String courseDuration = binding.courseDurationTxt.getText().toString();
                String semesterNum = binding.semesterTxt.getText().toString();

                String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);


                //  ...update branch details...
                docRef = colRef.document(collegeName)
                        .collection(Constant.KEY_DB_BRANCH_COL)
                        .document(branchName);


                //  ...insert course details...
                Map<String, String> branchDataMap = new HashMap<>();
                branchDataMap.put(Constant.KEY_COURSE_NAME, courseName);
                branchDataMap.put(Constant.KEY_COURSE_DURATION, courseDuration);
                branchDataMap.put(Constant.KEY_SEM_NUM, semesterNum);

                docRef.collection(Constant.KEY_DB_COURSE_COL).document(courseName)
                        .set(branchDataMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getActivity(), "Branch detail updated", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                setErrorTxt("*Fill all the details");
            }
        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }
}