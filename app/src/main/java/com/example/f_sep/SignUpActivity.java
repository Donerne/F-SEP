package com.example.f_sep;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {
    private EditText emailSignUp, passwordSignUp;
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailSignUp = findViewById(R.id.emailSignUp);
        passwordSignUp = findViewById(R.id.passwordSignUp);
        signUpBtn = findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(view -> signUp());
    }

    private void signUp() {
        String email = emailSignUp.getText().toString();
        String password = passwordSignUp.getText().toString();

        // Save user details in AWS S3
        uploadFileToS3("users/" + email + ".txt", "Email: " + email + "\nPassword: " + password);
    }
}