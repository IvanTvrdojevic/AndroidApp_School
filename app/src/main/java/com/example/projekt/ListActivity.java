package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ListActivity extends AppCompatActivity {
    List<TaskData> resultObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Button btnList = findViewById(R.id.btnList);
        Button btnArchive = findViewById(R.id.btnArchive);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnAddTask = findViewById(R.id.btnAddTask);

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerView(false);
            }
        });

        btnArchive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecyclerView(true);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginActivity();
            }
        });

        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToTaskAddActivity();
            }
        });

        String saved = getIntent().getStringExtra("saved");
        if (saved != null)
            Toast.makeText(this, "Task saved", Toast.LENGTH_SHORT).show();

        getListFromDB();
    }

    private void getListFromDB(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore.getInstance().collection(currentUser.getEmail())
            .get()
            .addOnCompleteListener(this, doc -> {
                if (doc.isSuccessful()) {
                    resultObjects = doc.getResult().toObjects(TaskData.class);
                    updateRecyclerView(false);
                }
                else
                    Toast.makeText(this, "Getting tasks from Firebase failed!", Toast.LENGTH_SHORT).show();
            });
    }

    private void updateRecyclerView(boolean archiveOnly) {
        List<TaskData> archived = resultObjects.stream()
                .filter(p -> !Objects.equals(p.done, "")).collect(Collectors.toList());

        List<TaskData> active = resultObjects.stream()
                .filter(p -> Objects.equals(p.done, "")).collect(Collectors.toList());
            
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvTasks);
        TaskAdapter adapter = new TaskAdapter(archiveOnly ? archived : active);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new TaskAdapter.OnClickListener() {
            @Override
            public void onClick(int position, TaskData task) {
                Intent intent = new Intent(ListActivity.this, TaskActivity.class);
                intent.putExtra("id", task.id);
                intent.putExtra("title", task.title);
                intent.putExtra("description", task.description);
                intent.putExtra("deadline", task.deadline);
                intent.putExtra("image", task.image);
                intent.putExtra("priority", task.priority);
                intent.putExtra("done", task.done);
                startActivity(intent);
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(ListActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void goToTaskAddActivity() {
        Intent intent = new Intent(ListActivity.this, TaskAddActivity.class);
        startActivity(intent);
    }
}