package com.adeshchandra.ultracalc;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ManualActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual);
        findViewById(R.id.btnBackManual).setOnClickListener(v -> finish());
    }
}
