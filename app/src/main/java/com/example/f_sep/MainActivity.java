package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirect to SignInActivity when the app starts
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity so it doesn't stay in the back stack
    }
}