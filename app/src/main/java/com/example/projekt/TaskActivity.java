package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

public class TaskActivity extends AppCompatActivity {
    private String user, dateTime, currentTime;
    private int index, id;
    private TextView tvTitle, tvDescription, tvDateTime, tvPriority;
    private Button btnBack, btnTaskDone, btnDateTime;
    Vector<String> titles = new Vector<>();
    Vector<String> descriptions = new Vector<>();
    Vector<String> dateTimes = new Vector<>();
    Vector<String> priorities = new Vector<>();
    Vector<Integer> dones = new Vector<>();
    Vector<Integer> ids = new Vector<>();
    int numOfTasks;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvPriority = findViewById(R.id.tvPriority);
        btnBack = findViewById(R.id.btnBack);
        btnTaskDone = findViewById(R.id.btnTaskDone);
        btnDateTime = findViewById(R.id.btnDateTime);

        user = getIntent().getStringExtra("user");
        index = getIntent().getIntExtra("index", 0);
        id = getIntent().getIntExtra("id", 0);

        getListFromDB();
        setTexts();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskActivity.this, ListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        btnDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateTimePicker();
            }
        });

        btnTaskDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markTaskAsDoneInDB();
                Intent intent = new Intent(TaskActivity.this, ListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void showDateTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        int month = currentDate.get(Calendar.MONTH);
        int year = currentDate.get(Calendar.YEAR);
        int hour = currentDate.get(Calendar.HOUR_OF_DAY);
        int minute = currentDate.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                        dateTime = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth + " " + hourOfDay + ":" + minute;
                        tvDateTime.setText(dateTime);
                        changeDateTime();
                        System.out.println("datetime" + dateTime);
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void getListFromDB(){
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select id, title, description, dateTime, priority, done from " + user + " where done = false", null);
        System.out.println(cursor.getColumnNames());

        while (cursor.moveToNext()){
            numOfTasks += 1;
            ids.add(cursor.getInt(0));
            titles.add(cursor.getString(1));
            descriptions.add(cursor.getString(2));
            dateTimes.add(cursor.getString(3));
            priorities.add(cursor.getString(4));
            dones.add(cursor.getInt(5));
        }
        cursor.close();
        database.close();
    }

    private void setTexts(){
        tvTitle.setText(titles.get(index));
        tvDescription.setText(descriptions.get(index));
        tvDateTime.setText(dateTimes.get(index));
        tvPriority.setText(priorities.get(index));
    }

    private void markTaskAsDoneInDB(){
        currentTime = Calendar.getInstance(TimeZone.getDefault()).getTime().toString().substring(4,16);
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        database.execSQL("update " + user + " set done = 1, dateTimeDone = " + "'" + currentTime + "'" + " where id = " + Integer.toString(id));
        database.close();
    }

    private void changeDateTime(){
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        database.execSQL("update " + user + " set dateTime = " + "'" + dateTime + "'" + " where id = " + Integer.toString(id));
    }
}