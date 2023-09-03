package com.example.projekt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnGoToRegister;
    private DBHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername      = findViewById(R.id.editTextUserame);
        etPassword      = findViewById(R.id.editTextPassword);
        btnLogin     = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        dbhelper = new DBHelper(this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        btnGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(){
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        SQLiteDatabase db = dbhelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from users where username = ? and password = ?", new String[] {username, password});
        if(cursor.getCount() > 0){
            Intent intent = new Intent(LoginActivity.this, ListActivity.class);
            intent.putExtra("user", username);
            startActivity(intent);
        }
        else Toast.makeText(this, "Incorrect login info", Toast.LENGTH_SHORT).show();
    }
}