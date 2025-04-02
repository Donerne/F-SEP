package com.example.f_sep;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class CompaniesActivity extends AppCompatActivity {
    private ListView companyList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Button signOut;
    private TextView userName;
    private String intentUserName;
    private String[] companies = {"Nvidia, NVDA", "Google, GOOG", "Amazon, AMZN", "Facebook, META", "Microsoft, MSFT", "Apple, AAPL"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies);

        try {
            // 1. Initialize Toolbar safely
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar == null) {
                throw new RuntimeException("Toolbar not found in layout");
            }
            setSupportActionBar(toolbar);

            // 2. Initialize main views
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            companyList = findViewById(R.id.company_list);

            // Set username safely
            intentUserName = getIntent().getStringExtra("UserName");

            // 3. Safe header view initialization
            if (navigationView != null) {
                View headerView = navigationView.getHeaderView(0);
                if (headerView != null) {
                    signOut = headerView.findViewById(R.id.signoutButton);
                    userName = headerView.findViewById(R.id.userNameText);

                    if (signOut != null) {
                        signOut.setOnClickListener(v -> finishAffinity());
                    }
                    if (userName != null || intentUserName != null) {
                        userName.setText(intentUserName);
                    }
                }
            }

            // 4. Initialize Drawer Toggle
            if (getSupportActionBar() != null) {
                toggle = new ActionBarDrawerToggle(
                        this,
                        drawerLayout,
                        toolbar,
                        R.string.open,
                        R.string.close);
                drawerLayout.addDrawerListener(toggle);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            // 5. Safe ListView setup
            if (companyList != null) {
                companyList.setAdapter(new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        companies != null ? companies : new String[0]));

                companyList.setOnItemClickListener((parent, view, position, id) -> {
                    if (position >= 0 && position < companies.length) {
                        Intent intent = new Intent(this, StockPerformanceActivity.class);
                        intent.putExtra("Selected_Company", companies[position]);
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e) {
            Log.e("CompaniesActivity", "Initialization error", e);
            // Optionally show error to user and finish
            finish();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (toggle != null) {
            toggle.syncState();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle != null && toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//            // nav_forecast_reports
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                // Log the clicked item's ID and title
//                Log.d("NAV_DEBUG", "Clicked Item ID: " + item.getItemId());
//                Log.d("NAV_DEBUG", "Clicked Item Title: " + item.getTitle());
//                if (item.getItemId() == R.id.company_list) {
//                    startActivity(new Intent(CompaniesActivity.this, StockPerformanceActivity.class));
//                }
//                drawerLayout.closeDrawers();
//                return true;
//            }
//        });