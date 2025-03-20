package com.example.f_sep;

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

    private EditText email, password;
    private Button btnSignUp;
    private TextView tvGoToSignIn;

//    private static final String ACCESS_KEY = "AKIA3FRRIP4VGHFMEDP6";
//    private static final String SECRET_KEY = "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";
//    private static final String BUCKET_NAME = "f-sep";

    private static final String ACCESS_KEY = "AKIAQ3EGT44632GVYX4W";
    private static final String SECRET_KEY = "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";
    private static final String BUCKET_NAME = "f-sep-app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        System.setProperty("com.amazonaws.sdk.disableJMX", "true");

        email = findViewById(R.id.emailSignUp);
        password = findViewById(R.id.passwordSignUp);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToSignIn = findViewById(R.id.signInRedirect);

        btnSignUp.setOnClickListener(v -> {
            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (userEmail.isEmpty() || userPassword.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                uploadUserDataAsync(userEmail, userPassword);
            }
        });

        tvGoToSignIn.setOnClickListener(v -> startActivity(new Intent(SignUpActivity.this, SignInActivity.class)));
    }

    private void uploadUserDataAsync(String email, String password) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {

                // Initialize static credentials
                BasicAWSCredentials awsCredentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

                // Initialize the Amazon S3 client using the static credentials
                AmazonS3 s3Client = new AmazonS3Client(awsCredentials);
                s3Client.setRegion(Region.getRegion(Regions.US_EAST_1)); // Change to your region

//                AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//                        .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY)))
//                        .withRegion(Regions.US_EAST_1)
//                        .build();

                String data = "Email: " + email + "\nPassword: " + password;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8));
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(data.length());

                s3Client.putObject(BUCKET_NAME, "users/" + email + ".txt", inputStream, metadata);

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
