package com.example.f_sep;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignUpActivity extends AppCompatActivity {

    private EditText email, password, name;
    private Button btnSignUp;
    private TextView tvGoToSignIn;

    private String accessKey;
    private String secretKey;
    private String bucketName;

    private String userName;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        System.setProperty("com.amazonaws.sdk.disableJMX", "true");

        accessKey = BuildConfig.ACCESS_KEY;
        bucketName = BuildConfig.BUCKET_NAME;
        secretKey = BuildConfig.SECRET_KEY;

        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.passwordSignUp);
        name = findViewById(R.id.userNameText);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToSignIn = findViewById(R.id.signInRedirect);

        btnSignUp.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();
            userName = name.getText().toString().trim();

            if (userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                uploadUserDataAsync(userEmail, userPassword, userName);
            }
        });

        tvGoToSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            intent.putExtra("UserName", userName);
            startActivity(intent);
        });
    }

    private void uploadUserDataAsync(String email, String password, String user) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                // Initialize static credentials
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

                // Initialize the Amazon S3 client using the static credentials
                AmazonS3 s3Client = new AmazonS3Client(awsCredentials);
                s3Client.setRegion(Region.getRegion(Regions.US_EAST_1)); // Change to your region


                String data = "Email: " + email + "\nPassword: " + password + "\nUsername: " + user;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(data.length());

                s3Client.putObject(bucketName, "users/" + email + ".txt", inputStream, metadata);

                // Run success message on UI thread
                runOnUiThread(() -> {
                    Toast.makeText(SignUpActivity.this, "Sign Up Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                    finish();
                });

            } catch (Exception e) {
                Log.e("SignUpDebug", "Error: " + e.getMessage(), e);
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(SignUpActivity.this, "Error signing up. Try again.", Toast.LENGTH_SHORT).show());
            }
        });
    }
}