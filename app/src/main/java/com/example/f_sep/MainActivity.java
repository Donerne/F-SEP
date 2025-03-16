package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Required AWS S3
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.auth.CognitoCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private AmazonS3 s3Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirect to SignInActivity when the app starts
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);

        initializeS3();
        finish(); // Close MainActivity so it doesn't stay in the back stack
    }
    //Initialize AWS S3 service
    private void initializeS3() {
        // AWS IAM Account Access and secret key created for this project
        String accessKey = "AKIA3FRRIP4VGHFMEDP6";
        String secretKey = "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";

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1) // AWS S3 Bucket Region
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .build();

        Toast.makeText(this, "AWS S3 Initialized", Toast.LENGTH_SHORT).show();
    }
}

public void uploadFileToS3(String fileName, String fileContent) {
    try {
        // Create a temporary file
        File file = new File(getCacheDir(), fileName);
        FileOutputStream fos = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fos);
        writer.write(fileContent);
        writer.close();

        // Upload file to S3
        s3Client.putObject(new PutObjectRequest("my-android-app-bucket", fileName, file));
        Toast.makeText(this, "File Uploaded Successfully!", Toast.LENGTH_LONG).show();

    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "Upload Failed!", Toast.LENGTH_LONG).show();
    }
}