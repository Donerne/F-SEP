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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.regions.Regions;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class SignInActivity extends AppCompatActivity {

    private EditText email, password;
    private Button btnSignIn;
    private TextView tvGoToSignUp;

    private String accessKey;
    private String secretKey;
    private String bucketName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        System.setProperty("com.amazonaws.sdk.disableJMX", "true");



        accessKey = BuildConfig.ACCESS_KEY;
        bucketName = BuildConfig.BUCKET_NAME;
        secretKey = BuildConfig.SECRET_KEY;


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
                authenticateUserAsync(userEmail, userPassword);
            }
        });


        tvGoToSignUp.setOnClickListener(v -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
    }


    private boolean authenticateUser(String email, String password) {
        try {

            // Initialize static credentials
            BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

            // Initialize the Amazon S3 client using the static credentials
            AmazonS3 s3Client = new AmazonS3Client(awsCredentials);
            s3Client.setRegion(Region.getRegion(Regions.US_EAST_1)); // Change to your region



            String key = "users/" + email + ".txt";
            if (s3Client.doesObjectExist(bucketName, key)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                    s3Client.getObject(bucketName, key).getObjectContent()
            ));
            String storedPassword = reader.readLine();  // Read the first line (which you can discard)
            storedPassword = reader.readLine().split(": ")[1].trim();  // Read the second line and process it\
            return storedPassword.equals(password);
            }

        } catch (Exception e) {
            Log.e("SignInDebug", "Error: " + e.getMessage(), e);
            e.printStackTrace();
        }
        return false;
    }


    private void authenticateUserAsync(String userEmail, String userPassword) {
        new Thread(() -> {
            boolean isAuthenticated = authenticateUser(userEmail, userPassword);
            runOnUiThread(() -> {
                if (isAuthenticated) {
                    Toast.makeText(SignInActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignInActivity.this, CompaniesActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

}
