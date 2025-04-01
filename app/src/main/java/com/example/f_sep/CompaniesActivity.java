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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

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

        // Set the Toolbar as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        companyList = findViewById(R.id.company_list);

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.open,
                R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        companyList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, companies));

//        // Inflate the menu programmatically
//        navigationView.inflateMenu(R.menu.navigation_menu);

        companyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Check if position is valid
                if (position >= 0 && position < companies.length){
                    // Obtaining item selected
                    String selectedCompany = companies[position];

                    Intent intent = new Intent(CompaniesActivity.this, StockPerformanceActivity.class);
                    intent.putExtra("Selected_Company", selectedCompany);
                    startActivity(intent);
                }
            }
        });

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
    }
}