package com.example.projekt;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class FormActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button dateTimePicker, btnDone;
    private Spinner spinner;
    private String selectedOption, dateTime, user;
    private TextView tvDateTime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        dateTimePicker = findViewById(R.id.dateTimePicker);
        spinner = findViewById(R.id.spinner);
        tvDateTime = findViewById(R.id.tvDateTime);
        btnDone = findViewById(R.id.btnDone);

        user = getIntent().getStringExtra("user");

        // Set up the options for the spinner
        String[] options = {"High priority", "Medium priority", "Low priority"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Handle the selection from the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedOption = options[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        dateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTaskInDB();
            }
        });
    }

    // Function to show the date-time picker
    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH);
        int year = currentDate.get(Calendar.YEAR);
        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        int minute = currentDate.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(FormActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        dateTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute;
                        tvDateTime.setText("Selected Date-Time: " + dateTime);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveTaskInDB(){
        if(etTitle.getText().toString().isEmpty()){
            Toast.makeText(this, "Tittle is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        ContentValues values = new ContentValues();
        values.put("title", etTitle.getText().toString());
        values.put("description", etDescription.getText().toString());
        values.put("dateTime", dateTime);
        values.put("priority", selectedOption);
        values.put("done", false);
        long newRowId = database.insert(user, null, values);
        if(newRowId != -1){
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(FormActivity.this, ListActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
