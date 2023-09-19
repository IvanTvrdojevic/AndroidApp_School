package com.example.projekt;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.UUID;

public class TaskAddActivity extends AppCompatActivity {

    private EditText etTitle, etDescription;
    private Button dateTimePicker, btnDone, btnPickImage;
    private Spinner spinner;
    private String selectedOption, dateTime;
    private TextView tvDateTime;
    private ImageView ivImage;

    int SELECT_PICTURE = 200;
    private Uri selectedImage = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        // Initialize views
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        dateTimePicker = findViewById(R.id.dateTimePicker);
        spinner = findViewById(R.id.spinner);
        tvDateTime = findViewById(R.id.tvDateTime);
        ivImage = findViewById(R.id.ivImage);
        btnDone = findViewById(R.id.btnDone);
        btnPickImage = findViewById(R.id.btnPick);

        Glide.with(this)
                .load(selectedImage) // Uri of the picture
                .into(ivImage);

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

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
            }
        });
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImage = data.getData();
                if (selectedImage != null) {
                    Glide.with(this)
                            .load(selectedImage)
                            .into(ivImage);
                }
            }
        }
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(TaskAddActivity.this, new TimePickerDialog.OnTimeSetListener() {
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

        TaskData task = new TaskData();
        task.id = UUID.randomUUID().toString();
        task.title = etTitle.getText().toString();
        task.description = etDescription.getText().toString();
        task.deadline = dateTime;
        task.image = selectedImage.toString();
        task.priority = selectedOption;
        task.done = "";

        saveTaskInFirebase(task);
    }

    private void saveTaskInFirebase(TaskData taskData) {
        String imageId = UUID.randomUUID().toString();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference ref = FirebaseStorage.getInstance().getReference().child(imageId);

        ref
            .putFile(Uri.parse(taskData.image))
            .addOnCompleteListener(this, doc -> {
                if (doc.isSuccessful()) {
                    ref.getDownloadUrl().addOnCompleteListener(this, doc2 -> {
                        if (doc2.isSuccessful()) {
                            taskData.image = doc2.getResult().toString();
                            FirebaseFirestore.getInstance().collection(currentUser.getEmail())
                                .add(taskData)
                                .addOnCompleteListener(this, doc3 -> {
                                    if (doc3.isSuccessful()) {
                                        Intent intent = new Intent(TaskAddActivity.this, ListActivity.class);
                                        intent.putExtra("saved", "saved");
                                        startActivity(intent);
                                    }
                                    else Toast.makeText(this, "Adding task to Firebase failed!", Toast.LENGTH_SHORT).show();
                                });
                        } else Toast.makeText(this, "Retrieving image URI failed!", Toast.LENGTH_SHORT).show();
                    });
                } else Toast.makeText(this, "Retrieving image URL failed!", Toast.LENGTH_SHORT).show();
            });
    }
}
