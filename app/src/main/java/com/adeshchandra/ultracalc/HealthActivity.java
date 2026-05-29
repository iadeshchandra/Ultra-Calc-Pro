package com.adeshchandra.ultracalc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class HealthActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health); 
        
        findViewById(R.id.btnBackHealth).setOnClickListener(v -> finish());
    }
}
