package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class CompaniesActivity extends AppCompatActivity {
    private ListView companyList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private String[] companies = {"Nvidia, NVDA", "Google, GOOG", "Amazon, AMZN", "Facebook, META", "Microsoft, MSFT", "Apple, AAPL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        companyList = findViewById(R.id.company_list);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, companies));

        // Inflate the menu programmatically
        navigationView.inflateMenu(R.menu.navigation_menu);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_forecast_reports) {
                    startActivity(new Intent(CompaniesActivity.this, StockPerformanceActivity.class));
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
}