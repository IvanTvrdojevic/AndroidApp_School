package com.example.projekt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister, btnGoToLogin;

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

        registerUserFirebase(username, password);
    }

    private void registerUserFirebase(String email, String password) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(RegisterActivity.this, ListActivity.class);
                    startActivity(intent);
                } else Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show();
            });
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
}
