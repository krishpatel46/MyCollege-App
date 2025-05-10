package com.example.mycollege.manageDbFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mycollege.R;
import com.example.mycollege.activities.ManageDbAdminActivity;
import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.FragmentDeleteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;

public class DeleteFragment extends Fragment {

    private FragmentDeleteBinding binding;
    private ManagePreferences preferences;

    Spinner branchSpinner, courseSpinner;
    ArrayAdapter<String> branchAdapter, courseAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    private DocumentReference docRef;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDeleteBinding.inflate(inflater, container, false);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);


        //  ...set spinners...
        branchSpinner = binding.branchSpinner;
        courseSpinner = binding.courseSpinner;


        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);
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

//                courseAdapter = setCourseSpinner(colRef, branchName);
//                binding.courseSpinner.setAdapter(courseAdapter);

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
        if(isAdded()) {
            setListeners();
        }
        return binding.getRoot();
    }


    //  ...method to set error textView...
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }


    private ArrayAdapter<String> setBranchSpinner(CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    private void setCourseSpinner(CollectionReference colRef_fun, String branchDocID) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.document(branchDocID)
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

                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                });

        binding.courseSpinner.setAdapter(arrayAdapter);
    }

    private void setListeners() {
        binding.deleteBtn.setOnClickListener(view -> {

            String branchName = binding.branchSpinner.getSelectedItem().toString();
            docRef = colRef.document(branchName);
            colRef = colRef.document(branchName).collection(Constant.KEY_DB_COURSE_COL);

            try {
                String courseName = binding.courseSpinner.getSelectedItem().toString();

                //   ...set pop up menu...
                PopupMenu manageMenu = new PopupMenu(getActivity(), view);
                manageMenu.getMenuInflater().inflate(R.menu.delete_menu, manageMenu.getMenu());

                Log.d("colRef", colRef.getPath());

                manageMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch(menuItem.getItemId()) {
                            case R.id.deleteBranch:
                                deleteBranch(colRef, docRef, courseName);

                                return true;

                            case R.id.deleteCourse:
                                //  ...delete course subjects...
                                colRef.document(courseName).collection("SubjectDetails")
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                                    colRef.document(courseName).collection("SubjectDetails")
                                                            .document(documentSnapshot.getId())
                                                            .delete();
                                                }

                                                Toast.makeText(getActivity(), "Selected course subjects deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                //  ...delete course details...
                                colRef.document(courseName)
                                        .delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(getActivity(), "Selected course details deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                return true;

                            default:
                                return false;
                        }
                    }
                });

                manageMenu.show();

            } catch (NullPointerException npe){
                docRef.delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("DocRef", docRef.getPath());
                                Toast.makeText(getActivity(), "Selected branch details deleted", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), ManageDbAdminActivity.class));
            requireActivity().finish();
        });
    }

    private void deleteBranch(CollectionReference colRef, DocumentReference docRef, String courseName) {
        //  ...delete course subjects...
        colRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();

                            if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                                WriteBatch batch = db.batch();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                                    batch.delete(documentSnapshot.getReference());

                                    colRef.document(documentSnapshot.getId()).collection("SubjectDetails")
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    for(DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                                                        colRef.document(courseName).collection("SubjectDetails")
                                                                .document(documentSnapshot.getId())
                                                                .delete();
                                                    }

                                                    Toast.makeText(getActivity(), "Selected course subjects deleted", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }

                                batch.commit()
                                        .addOnCompleteListener(batchTask -> {
                                            if (batchTask.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Selected course details deleted", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Handle the error
                                            }
                                        });
                            }
                        }
                    }
                });

        //  ...delete branch details...
        docRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("DocRef", docRef.getPath());
                        Toast.makeText(getActivity(), "Selected branch details deleted", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}