package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SignInActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnSignIn;
    private TextView tvGoToSignUp;

    private static final String ACCESS_KEY = "AKIA3FRRIP4VGHFMEDP6";
    private static final String SECRET_KEY = "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";
    private static final String BUCKET_NAME = "f-sep";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = findViewById(R.id.emailSignIn);
        password = findViewById(R.id.passwordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        btnSignIn.setOnClickListener(v -> {
            String userEmail = email.getText().toString();
            String userPassword = password.getText().toString();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(SignInActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                if (authenticateUser(userEmail, userPassword)) {
                    Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                    // Redirect to home activity (You can create a HomeActivity)
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
    }

    private boolean authenticateUser(String email, String password) {
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
                    .withRegion(Regions.US_EAST_1)
                    .build();

            String key = "users/" + email + ".txt";
            if (s3Client.doesObjectExist(BUCKET_NAME, key)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        s3Client.getObject(BUCKET_NAME, key).getObjectContent()
                ));
                String storedPassword = reader.readLine().split(": ")[1].trim();
                return storedPassword.equals(password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
