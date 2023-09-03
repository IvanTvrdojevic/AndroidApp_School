package com.example.projekt;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnGoToLogin;
    private DBHelper dbhelper;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername        = findViewById(R.id.editTextUserame);
        etPassword        = findViewById(R.id.editTextPassword);
        etConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        btnRegister       = findViewById(R.id.btnRegister);
        btnGoToLogin      = findViewById(R.id.btnGoToLogin);

        dbhelper = new DBHelper(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();
        if(checkPassword(password, confirmPassword) == false) return;

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if(checkUserInDb(db, username) == true) return;

        ContentValues values = new ContentValues();
        values.put(DBHelper.columnUsername, username);
        values.put(DBHelper.columnPassword, password);
        long newRowId = db.insert(DBHelper.tableName, null, values);

        if (newRowId != -1) {
            Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
            intent.putExtra("user", username);
            createUserTable();
            startActivity(intent);
        } else {
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    private boolean checkPassword(String password, String confirmPassword){
        if(password.isEmpty() || confirmPassword.isEmpty()){
            Toast.makeText(this, "No password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(password.length() < 7){
            Toast.makeText(this, "Password must contain at least 7 characters", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean checkUserInDb(SQLiteDatabase db, String username){
        Cursor cursor = db.rawQuery("select * from users where username = ?", new String[] {username});
        if(cursor.getCount() > 0){
            Toast.makeText(this, "User exists", Toast.LENGTH_SHORT).show();
            return true;
        }
        else return false;
    }

  private void createUserTable(){
        SQLiteDatabase database = openOrCreateDatabase("UsersDatabase.db", Context.MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS "  + username + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, dateTime TEXT, priority TEXT, done INTEGER, dateTimeDone TEXT)");
  }
}
