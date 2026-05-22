package com.adeshchandra.ultracalc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);

        // 1. Open the drawer when the hamburger icon is clicked
        findViewById(R.id.btnMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // 2. Handle Navigation Menu Clicks
        NavigationView navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawer(GravityCompat.START);

            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already on Home
            } else if (id == R.id.nav_help || id == R.id.nav_manual) {
                startActivity(new Intent(this, ManualActivity.class));
            } else if (id == R.id.nav_invoice) {
                startActivity(new Intent(this, InvoiceHistoryActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, LoginActivity.class));
            } else if (id == R.id.nav_share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out Ultra Calc Pro - The Ultimate Offline Toolkit!");
                startActivity(Intent.createChooser(shareIntent, "Share App via"));
            } else {
                Toast.makeText(this, item.getTitle() + " selected", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        // 3. Routing system for the main Dashboard Grid
        int[] cards = {
            R.id.cardWood, R.id.cardCalc, R.id.cardSci, R.id.cardConv, 
            R.id.cardFin, R.id.cardHealth, R.id.cardMath, R.id.cardDate, R.id.cardHistory
        };

        Class<?>[] acts = {
            WoodActivity.class, CalcActivity.class, ScientificActivity.class, ConverterActivity.class, 
            FinancialActivity.class, HealthActivity.class, MathActivity.class, DateCalcActivity.class, HistoryActivity.class
        };

        for (int i = 0; i < cards.length; i++) {
            final Class<?> act = acts[i];
            findViewById(cards[i]).setOnClickListener(v -> startActivity(new Intent(this, act)));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Dynamically update the Drawer Profile Header if the user has logged in
        SharedPreferences prefs = getSharedPreferences("UltraPrefs", MODE_PRIVATE);
        String savedEmail = prefs.getString("USER_EMAIL", null);
        NavigationView navView = findViewById(R.id.navigationView);
        View headerView = navView.getHeaderView(0);
        
        if (headerView != null && savedEmail != null) {
            TextView tvName = headerView.findViewById(R.id.navHeaderName);
            TextView tvEmail = headerView.findViewById(R.id.navHeaderEmail);
            if (tvName != null) tvName.setText("Pro Account");
            if (tvEmail != null) tvEmail.setText(savedEmail);
        }
    }

    // Ensure the hardware back button closes the drawer if it's open, rather than exiting the app
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
