package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;

public class TaskArchiveActivity extends AppCompatActivity {

    private String user;
    private int index, id;
    private TextView tvTitle, tvDescription, tvDateTime, tvPriority, tvDateTimeDone;
    private Button btnBack, btnTaskDone;
    Vector<String> titles = new Vector<>();
    Vector<String> descriptions = new Vector<>();
    Vector<String> dateTimes = new Vector<>();
    Vector<String> dateTimesDone = new Vector<>();
    Vector<String> priorities = new Vector<>();
    Vector<Integer> dones = new Vector<>();
    Vector<Integer> ids = new Vector<>();
    int numOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_archive);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        tvPriority = findViewById(R.id.tvPriority);
        btnBack = findViewById(R.id.btnBack);
        tvDateTimeDone = findViewById(R.id.tvDateTimeDone);

        user = getIntent().getStringExtra("user");
        index = getIntent().getIntExtra("index", 0);
        id = getIntent().getIntExtra("id", 0);

        getListFromDB();
        setTexts();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TaskArchiveActivity.this, ArchiveActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
    }

    private void getListFromDB(){
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select id, title, description, dateTime, priority, done, dateTimeDone from " + user + " where done = true", null);
        System.out.println(cursor.getColumnNames());

        while (cursor.moveToNext()){
            numOfTasks += 1;
            ids.add(cursor.getInt(0));
            titles.add(cursor.getString(1));
            descriptions.add(cursor.getString(2));
            dateTimes.add(cursor.getString(3));
            priorities.add(cursor.getString(4));
            dones.add(cursor.getInt(5));
            dateTimesDone.add(cursor.getString(6));
        }
        cursor.close();
        database.close();
    }

    private void setTexts(){
        tvTitle.setText(titles.get(index));
        tvDescription.setText(descriptions.get(index));
        tvDateTime.setText(dateTimes.get(index));
        tvDateTimeDone.setText("Done at " + dateTimesDone.get(index));
        tvPriority.setText(priorities.get(index));
    }
}