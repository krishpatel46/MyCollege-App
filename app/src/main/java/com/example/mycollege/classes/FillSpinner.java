package com.example.mycollege.classes;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.mycollege.classes.Constant;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FillSpinner {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collegeDetails_colRef = db.collection(Constant.KEY_DB_ROOT_COL);
    CollectionReference branchDetails_colRef = db.collection(Constant.KEY_DB_BRANCH_COL);


    public static ArrayAdapter<String> setBranchSpinner(Activity activity, TextView errorTxt, CollectionReference colRef_fun) {
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, arrayList);

        colRef_fun.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    setErrorTxt(errorTxt, error.getMessage());
                }
                else {
                    arrayList.clear();

                    assert value != null;
                    for(DocumentSnapshot docSnap:value) {
                        String branchName = docSnap.getString(Constant.KEY_BRANCH_NAME);
                        arrayList.add(branchName);

                        Log.d("branch names: ", arrayList.toString());
                    }

                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

        return arrayAdapter;
    }


    //  ...method to set error textView...
    public static void setErrorTxt(TextView errorTxt, String message) {
        errorTxt.setVisibility(View.VISIBLE);
        errorTxt.setText(message);
    }
}
