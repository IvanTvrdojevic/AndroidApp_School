package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Vector;

public class ArchiveActivity extends AppCompatActivity {

    Button btnList, btnLogout;
    Vector<String> titles = new Vector<>();
    Vector<String> descriptions = new Vector<>();
    Vector<String> dateTimes = new Vector<>();
    Vector<String> priorities = new Vector<>();
    Vector<Integer> dones = new Vector<>();
    Vector<Integer> ids = new Vector<>();
    String user;
    int numOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        user = getIntent().getStringExtra("user");

        btnList = findViewById(R.id.btnList);
        btnLogout = findViewById(R.id.btnLogout);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArchiveActivity.this, ListActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ArchiveActivity.this, LoginActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        fillListFromDB();
    }

    private void getListFromDB(){
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        Cursor cursor = database.rawQuery("select id, title, description, dateTime, priority, done from " + user + " where done = true", null);

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

    private TextView generateTextView(String tvText){
        TextView tv = new TextView(this);
        tv.setPadding(16, 0, 16, 0);
        tv.setText(tvText);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        params1.gravity = Gravity.CENTER_VERTICAL;
        tv.setLayoutParams(params1);
        return tv;
    }

    private void fillListFromDB() {
        getListFromDB();
        System.out.println(titles);
        LinearLayout containerLayout = findViewById(R.id.taskContainer);

        LinearLayout newLayout = new LinearLayout(this);
        newLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        for (int i = 0; i < numOfTasks; i++) {
            LinearLayout inlineLayout = new LinearLayout(this);
            inlineLayout.setOrientation(LinearLayout.HORIZONTAL);
            inlineLayout.setBackgroundResource(R.drawable.layout_outline);
            inlineLayout.setPadding(16, 16, 16, 16);

            TextView tvTitle = generateTextView(titles.get(i));
            inlineLayout.addView(tvTitle);

            TextView tvDescription = generateTextView(dateTimes.get(i));
            inlineLayout.addView(tvDescription);

            Button button = new Button(this);
            LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            buttonParams.gravity = Gravity.CENTER_VERTICAL;
            button.setLayoutParams(buttonParams);
            button.setBackgroundResource(R.drawable.button_background); // Set the background
            button.setText("+");
            button.setTextSize(16);
            inlineLayout.addView(button);

            newLayout.addView(inlineLayout, layoutparams);

            final int index = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ArchiveActivity.this, TaskArchiveActivity.class);
                    intent.putExtra("index", index);
                    intent.putExtra("user", user);
                    intent.putExtra("id", ids.get(index));
                    startActivity(intent);
                }
            });
        }
        containerLayout.addView(newLayout);
    }
}