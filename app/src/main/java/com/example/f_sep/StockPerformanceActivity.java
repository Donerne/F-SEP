package com.example.f_sep;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class StockPerformanceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_performance);

        TextView companyTitle = findViewById(R.id.companyTitle);
        TextView forecastText = findViewById(R.id.forecastText);
        ImageView backButton = findViewById(R.id.backButton);

        String companyName = getIntent().getStringExtra("companyName");
        String companySymbol = getIntent().getStringExtra("companySymbol");

        companyTitle.setText(companyName + " (" + companySymbol + ")");
        forecastText.setText("Stock forecast: " + (Math.random() * 5) + "% change tomorrow");

        backButton.setOnClickListener(v -> finish());
    }
}