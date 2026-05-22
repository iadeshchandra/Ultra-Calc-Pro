package com.adeshchandra.ultracalc;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        SharedPreferences prefs = getSharedPreferences("UltraPrefs", MODE_PRIVATE);
        
        findViewById(R.id.btnSkip).setOnClickListener(v -> finish());
        
        findViewById(R.id.btnLogin).setOnClickListener(v -> {
            String email = ((EditText)findViewById(R.id.etEmail)).getText().toString().trim();
            if(email.isEmpty() || !email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("USER_EMAIL", email).apply();
            Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
