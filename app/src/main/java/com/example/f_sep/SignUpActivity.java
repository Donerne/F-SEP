package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnSignUp;
    private TextView tvGoToSignIn;

    private static final String ACCESS_KEY = "AKIA3FRRIP4VGHFMEDP6";
    private static final String SECRET_KEY = "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";
    private static final String BUCKET_NAME = "f-sep";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.passwordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToSignIn = findViewById(R.id.signInRedirect);

        btnSignUp.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                uploadUserData(userEmail, userPassword);
                Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        tvGoToSignIn.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
    }

    private void uploadUserData(String email, String password) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                    .withRegion(Regions.US_EAST_1)
                    .build();

            String data = "Email: " + email + "\nPassword: " + password;
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
            s3Client.putObject(BUCKET_NAME, "users/" + email + ".txt", inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}