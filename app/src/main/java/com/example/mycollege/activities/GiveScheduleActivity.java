package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;

import com.example.mycollege.classes.Constant;
import com.example.mycollege.classes.ManagePreferences;
import com.example.mycollege.databinding.ActivityGiveScheduleBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;

public class GiveScheduleActivity extends AppCompatActivity {

    private ActivityGiveScheduleBinding binding;
    private ManagePreferences preferences;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference colRef = db.collection(Constant.KEY_DB_ROOT_COL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGiveScheduleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferences = new ManagePreferences(GiveScheduleActivity.this, Constant.KEY_PROFESSOR_PREFERENCE_NAME);

        String collegeName = preferences.getString(Constant.KEY_ADMIN_COLLEGE_NAME);
        colRef = colRef.document(collegeName).collection(Constant.KEY_DB_BRANCH_COL);

        setTimeDialog();
        setSpinners();

        binding.submitBtn.setOnClickListener(view -> {
            if(isDetailsFilled()) {
                String day = binding.selectDaySpinner.getSelectedItem().toString();
                String subject = binding.selectSubjectSpinner.getSelectedItem().toString();
                String startTimeStr = binding.startTime.toString().trim();
                String endTimeStr = binding.endTime.toString().trim();


            }
        });
    }

    private void setSpinners() {
        // fill day spinner
        ArrayList<String> dayList = new ArrayList<>();
        dayList.add("Monday");
        dayList.add("Tuesday");
        dayList.add("Wednesday");
        dayList.add("Thursday");
        dayList.add("Friday");
        dayList.add("Saturday");
        dayList.add("Sunday");

        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(GiveScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, dayList);
        binding.selectDaySpinner.setAdapter(dayAdapter);

        // fill subject spinner
        ArrayList<String> subjectList = new ArrayList<>();
        ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(GiveScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, subjectList);


    }

    private void setTimeDialog() {
        binding.startTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog1 = buildTimeDialog();

            timePickerDialog1.setTitle("Starting time");
            timePickerDialog1.show();
        });

        binding.endTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog2 = buildTimeDialog();

            timePickerDialog2.setTitle("Ending time");
            timePickerDialog2.show();
        });
    }

    private TimePickerDialog buildTimeDialog() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);

        return new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        binding.startTime.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
    }

    //  [method to verify input details]
    private Boolean isDetailsFilled() {

        if (binding.startTime.getText().toString().isEmpty()) {
            setErrorTxt("*Start time required");

            return false;
        }

        else if(binding.endTime.getText().toString().isEmpty()) {
            setErrorTxt("*End time required");

            return false;
        }

        else {
            return true;
        }
    }

    //  [method to set error textView]
    private void setErrorTxt(String message) {
        binding.errorTxt.setVisibility(View.VISIBLE);
        binding.errorTxt.setText(message);
    }
}