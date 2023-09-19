package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class TaskActivity extends AppCompatActivity {
    private String taskId;
    private String taskImage;
    private String dateTime;
    private TextView tvTitle, tvDescription, tvDateTime, tvPriority;
    private Button btnBack, btnTaskDone, btnDateTime;
    private ImageView ivImage;

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
        ivImage = findViewById(R.id.ivImage);

        setTexts();

        taskImage = getIntent().getStringExtra("image");
        Glide.with(this).load(taskImage).into(ivImage);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToListActivity("");
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
                TaskData task = getTask();
                task.done = Calendar.getInstance(TimeZone.getDefault()).getTime().toString().substring(4,16);
                updateUserInFirebase(task, "done");
            }
        });
    }

    private void setTexts(){
        taskId = getIntent().getStringExtra("id");
        tvTitle.setText(getIntent().getStringExtra("title"));
        tvDescription.setText(getIntent().getStringExtra("description"));
        tvPriority.setText(getIntent().getStringExtra("priority"));

        String done = getIntent().getStringExtra("done");
        if (Objects.equals(done, "")) {
            btnDateTime.setVisibility(View.VISIBLE);
            btnTaskDone.setVisibility(View.VISIBLE);
            tvDateTime.setText(getIntent().getStringExtra("deadline"));
        } else {
            btnDateTime.setVisibility(View.GONE);
            btnTaskDone.setVisibility(View.GONE);
            tvDateTime.setText("TASK FINISHED ON: " + done);
        }
    }

    private TaskData getTask(){
        TaskData task = new TaskData();
        task.id = taskId;
        task.title = tvTitle.getText().toString();
        task.description = tvDescription.getText().toString();
        task.deadline = tvDateTime.getText().toString();
        task.priority = tvPriority.getText().toString();
        task.image = taskImage;
        task.done = "";

        return task;
    }

    private void updateUserInFirebase(TaskData task, String showUpdated){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        CollectionReference ref = FirebaseFirestore.getInstance().collection(currentUser.getEmail());
        ref
            .whereEqualTo("id", task.id)
            .get()
            .addOnCompleteListener(this, doc -> {
                List<DocumentSnapshot> resDocs = doc.getResult().getDocuments();
                if (doc.isSuccessful() && resDocs.size() == 1) {
                    String docId = resDocs.get(0).getId();
                    ref.document(docId)
                        .set(task)
                        .addOnCompleteListener(this, doc2 -> {
                            if (doc.isSuccessful()) {
                                if (Objects.equals(showUpdated, "done"))
                                    goToListActivity(showUpdated);
                                else
                                    Toast.makeText(this, "Deadline changed!", Toast.LENGTH_SHORT);
                            }
                            else
                                Toast.makeText(this, "Task update failed!", Toast.LENGTH_SHORT).show();
                        });
                }
                else
                    Toast.makeText(this, "Task for update not found!", Toast.LENGTH_SHORT).show();
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
                        TaskData task = getTask();
                        updateUserInFirebase(task, "deadline");
                    }
                }, hour, minute, false);
                timePickerDialog.show();
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void goToListActivity(String showUpdated) {
        Intent intent = new Intent(TaskActivity.this, ListActivity.class);
        if (!Objects.equals(showUpdated, ""))
            intent.putExtra("updated", showUpdated);
        startActivity(intent);
    }
}