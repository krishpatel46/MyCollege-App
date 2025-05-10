package com.example.mycollege.manageDbFragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
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
import com.example.mycollege.databinding.FragmentUpdateProfessorBinding;
import com.example.mycollege.objects.Professor;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UpdateProfessorFragment extends Fragment {

    private final String PROFESSOR = "Professor";
    private final String CC = "Class Coordinator - CC";
    private final String HOD = "Head of Department - HOD";

    private FragmentUpdateProfessorBinding binding;

    Spinner designationSpinner, branchSpinner, deptSpinner, semSpinner;
    ArrayAdapter<String> designationAdapter, branchAdapter, deptAdapter, semAdapter;

    private ManagePreferences preferences;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUpdateProfessorBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        Bundle profData = getArguments();
        if (profData != null && !profData.isEmpty()) {
            binding.profEmailTxt.setText("Email: " + profData.getString("profEmail"));
            binding.profNameTxt.setText(profData.getString("profName"));

            binding.profDesignationTxt.setText("Old designation: " + profData.getString("profDesig"));
            binding.profBranchTxt.setText("Old Branch: " + profData.getString("profBranch"));
            binding.profDeptTxt.setText("Old Dept.: " + profData.getString("profDept"));
            binding.profSemTxt.setText("Old Semester: " + profData.getString("profSem"));
        }


        //  ...set spinners...
        branchSpinner = binding.branchSpinner;
        deptSpinner = binding.deptSpinner;
        designationSpinner = binding.designationSpinner;
        semSpinner = binding.semesterSpinner;


        ArrayList<String> designationList = new ArrayList<String>(){
            {
                add(PROFESSOR);
                add(CC);
                add(HOD);
            }
        };
        designationAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, designationList);
        designationSpinner.setAdapter(designationAdapter);


        colRef.whereEqualTo("Name", collegeName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            for(DocumentSnapshot docSnap: value) {
                                String collegeID = docSnap.getId();
                                branchSpinner.setAdapter(setBranchSpinner(colRef, collegeID));
                            }
                        }
                    }
                });

        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String branchName = branchSpinner.getSelectedItem().toString();

                Log.d("col path", colRef.getPath());
                colRef.whereEqualTo("Name", collegeName)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(error != null) {
                                    setErrorTxt(error.getMessage());
                                }
                                else {
                                    for(DocumentSnapshot docSnap: value) {
                                        String collegeID = docSnap.getId();
                                        deptSpinner.setAdapter(setDeptSpinner(colRef, collegeID, branchName));
                                    }
                                }
                            }
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //fill semester spinner
        designationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String branchName = branchSpinner.getSelectedItem().toString();
                    String deptName = deptSpinner.getSelectedItem().toString();

                    String designation = designationSpinner.getSelectedItem().toString();

                    if (designation.equals(CC) && !deptName.equals("General")) {
                        binding.selectSemesterHeader.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        binding.profSemTxt.setVisibility(View.VISIBLE);

                        colRef.whereEqualTo("Name", collegeName)
                                .addSnapshotListener((value, error) -> {
                                    if (error != null) {
                                        setErrorTxt(error.toString());
                                    }
                                    else {
                                        for (QueryDocumentSnapshot docSnap : value) {
                                            String collegeID = docSnap.getId();
                                            semAdapter = getSemNumber(colRef, collegeID, branchName, deptName);
                                            semSpinner.setAdapter(semAdapter);
                                        }
                                    }
                                });

                    } else if (designation.equals(CC)) {
                        binding.selectSemesterHeader.setVisibility(View.VISIBLE);
                        semSpinner.setVisibility(View.VISIBLE);
                        binding.profSemTxt.setVisibility(View.VISIBLE);

                        ArrayList<String> semList = new ArrayList<String>() {
                            {
                                add("8");
                                add("7");
                                add("6");
                                add("5");
                                add("4");
                                add("3");
                                add("2");
                                add("1");
                            }
                        };
                        semSpinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, semList));
                    } else {
                        binding.selectSemesterHeader.setVisibility(View.INVISIBLE);
                        semSpinner.setVisibility(View.INVISIBLE);
                        binding.profSemTxt.setVisibility(View.INVISIBLE);
                    }
                }
                catch (NullPointerException npe) {
                    setErrorTxt("*Please fill all the details");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        //  ...set listeners...
        setListeners(collegeName, profData);

        return binding.getRoot();
    }

    private ArrayAdapter<String> setBranchSpinner(CollectionReference colRef_fun, String collegeID) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
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

    private ArrayAdapter<String> setDeptSpinner(CollectionReference colRef_fun, String collegeID, String branchName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            setErrorTxt(error.getMessage());
                        }
                        else {
                            arrayList.clear();

                            assert value != null;
                            for(DocumentSnapshot docSnap:value) {
                                String courseName = docSnap.getString(Constant.KEY_COURSE_NAME);
                                arrayList.add(courseName);
                            }

                            Log.d("course list", arrayList.toString());
                            arrayList.add("General");
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

//        binding.courseSpinner.setAdapter(arrayAdapter);
        return arrayAdapter;
    }


    private ArrayAdapter<String> getSemNumber(CollectionReference colRef_fun, String collegeID, String branchName, String deptName) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);


        colRef_fun.document(collegeID)
                .collection(Constant.KEY_DB_BRANCH_COL).document(branchName)
                .collection(Constant.KEY_DB_COURSE_COL).document(deptName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        arrayList.clear();

                        try {
                            int semNo = documentSnapshot.getLong(Constant.KEY_SEM_NUM).intValue();

                            for (int sem = semNo; sem > 0; sem--) {
                                arrayList.add(String.valueOf(sem));
                            }

                            arrayAdapter.notifyDataSetChanged();

                        } catch (NullPointerException npe) {
                            setErrorTxt("*Please select appropriate course");
                        } catch (NumberFormatException nfe) {
                            setErrorTxt("*Please select a course");
                        }
                    }
                });

        return arrayAdapter;
    }

    //  ...method to set student data into a java object...
    private Professor setProfData(Bundle profData, String prof_college) {
        String prof_name = binding.profNameTxt.getText().toString();
        String prof_email = profData.getString("profEmail");
        String prof_pass = profData.getString("profPass");
        String prof_designation = binding.designationSpinner.getSelectedItem().toString();
        String prof_dept = binding.deptSpinner.getSelectedItem().toString();
        String prof_branch = binding.branchSpinner.getSelectedItem().toString();

        if(binding.semesterSpinner.getVisibility() == View.VISIBLE) {
            String prof_sem = binding.semesterSpinner.getSelectedItem().toString();

            /* store data in a java object */
            return new Professor(prof_name, prof_email, prof_pass, prof_college, prof_designation, prof_dept, prof_branch, prof_sem);
        }
        else {
            /* store data in a java object */
            return new Professor(prof_name, prof_email, prof_pass, prof_college, prof_designation, prof_dept, prof_branch, "0");
        }
    }

    private void setListeners(String collegeName, Bundle profData) {
        binding.submitBtn.setOnClickListener(view -> {
            Professor profDetails = setProfData(profData, collegeName);

            colRef.whereEqualTo("Name", collegeName)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                setErrorTxt(error.getMessage());
                            } else {
                                for (DocumentSnapshot docSnap : value) {
                                    String collegeID = docSnap.getId();
                                    updateProfessor(collegeID, profDetails.getProfEmail(), profDetails);
                                }
                            }
                        }
                    });
        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }

    private void updateProfessor(String collegeID, String email, Professor profDetails) {
        try {
            colRef.document(collegeID)
                    .collection(Constant.KEY_DB_FACULTY_COL)
                    .document(email)
                    .update(Constant.KEY_PROF_NAME, profDetails.getProfName(),
                            Constant.KEY_PROF_DESIGNATION, profDetails.getProfDesignation(),
                            Constant.KEY_STU_BRANCH, profDetails.getProfBranch(),
                            Constant.KEY_PROF_DEPARTMENT, profDetails.getProfDepartment(),
                            Constant.KEY_STU_SEMESTER, profDetails.getProfSem())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(requireContext(), "Professor details updated", Toast.LENGTH_SHORT).show();
                            removeFragment(UpdateProfessorFragment.this);
                        }
                    });
        }
        catch (NullPointerException npe) {
            Toast.makeText(requireContext(), "Oops, details not available", Toast.LENGTH_LONG).show();
            removeFragment(UpdateProfessorFragment.this);
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