package com.example.mycollege.manageDbFragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.adapters.ViewStudentAdapter;
import com.example.mycollege.databinding.FragmentViewStudentBinding;
import com.example.mycollege.objects.Student;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ViewStudentFragment extends Fragment {

    private FragmentViewStudentBinding binding;

    private ManagePreferences preferences;

    ViewStudentAdapter viewStudentAdapter;
    ArrayList<Student> studentArrayList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentViewStudentBinding.inflate(inflater, container, false);

        binding.viewStudentList.setHasFixedSize(true);
        binding.viewStudentList.setLayoutManager(new LinearLayoutManager(getActivity()));

        studentArrayList = new ArrayList<>();
        viewStudentAdapter = new ViewStudentAdapter(requireActivity(), studentArrayList);

        preferences = new ManagePreferences(requireActivity(), Constant.KEY_ADMIN_PREFERENCE_NAME);
        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);

        //get college document
        colRef.whereEqualTo("Name", collegeName)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value!=null) {
                            for(DocumentSnapshot docSnap: value) {
                                String collegeID = docSnap.getId();
                                fetchData(collegeID);
                            }
                        }
                    }
                });

        binding.viewStudentList.setAdapter(viewStudentAdapter);

        return binding.getRoot();
    }

    private void fetchData(String collegeID) {
        colRef.document(collegeID).collection(Constant.KEY_DB_STUDENT_COL)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        try {
                            for(DocumentSnapshot docSnap: queryDocumentSnapshots) {
                                studentArrayList.add(docSnap.toObject(Student.class));
                            }
                            viewStudentAdapter.notifyDataSetChanged();
                        }
                        catch (NullPointerException npe) {
                            Log.d("n p e", npe.getMessage());
                        }
                    }
                });
    }
}