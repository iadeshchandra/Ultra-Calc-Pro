package com.adeshchandra.ultracalc;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class HealthActivity extends AppCompatActivity {
    
    private LinearLayout mainContainer;
    private static final String[] TOOLS = {"BMI Calc", "BMR Calc", "Daily Calories", "Water Intake"};
    private static final String[] EMOJIS = {"⚖️", "🔥", "🍎", "💧"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        
        mainContainer = findViewById(R.id.mainContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        buildToolBar();
        showTool(0); 
    }

    private void buildToolBar() {
        LinearLayout bar = findViewById(R.id.toolBar);
        bar.removeAllViews();
        for (int i = 0; i < TOOLS.length; i++) {
            final int idx = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(30, 14, 30, 14);
            
            TextView em = new TextView(this); 
            em.setText(EMOJIS[i]); 
            em.setTextSize(24); 
            em.setGravity(android.view.Gravity.CENTER);
            
            TextView lb = new TextView(this); 
            lb.setText(TOOLS[i]); 
            lb.setTextSize(12); 
            lb.setTextColor(0xFF94A3B8); 
            lb.setGravity(android.view.Gravity.CENTER);
            
            item.addView(em); 
            item.addView(lb);
            item.setOnClickListener(v -> showTool(idx));
            bar.addView(item);
        }
    }

    private void showTool(int idx) {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i = 0; i < bar.getChildCount(); i++) {
            bar.getChildAt(i).setBackgroundColor(i == idx ? 0xFF1E293B : 0x00000000);
        }
        mainContainer.removeAllViews();
        switch (idx) {
            case 0: buildBMI(); break; 
            case 1: buildBMR(); break;
            case 2: buildCalories(); break; 
            case 3: buildWater(); break;
        }
    }

    private void addContentDescription(String title, String description) {
        TextView tvTitle = new TextView(this);
        tvTitle.setText(title);
        tvTitle.setTextSize(20);
        tvTitle.setTextColor(0xFFFFFFFF);
        // CORRECTED: Using setTypeface instead of setTextStyle
        tvTitle.setTypeface(null, Typeface.BOLD); 
        tvTitle.setPadding(0, 0, 0, 8);
        mainContainer.addView(tvTitle);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(description);
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(0xFF94A3B8);
        tvDesc.setPadding(0, 0, 0, 32);
        mainContainer.addView(tvDesc);
    }

    private EditText createInput(String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setHintTextColor(0xFF64748B);
        et.setTextColor(0xFFF8FAFC);
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 24);
        et.setLayoutParams(params);
        et.setPadding(40, 40, 40, 40);
        et.setBackgroundColor(0xFF1E293B);
        mainContainer.addView(et);
        return et;
    }

    private Button createButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setBackgroundColor(0xFF00897B);
        btn.setTextColor(0xFFFFFFFF);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 20, 0, 40);
        btn.setLayoutParams(params);
        mainContainer.addView(btn);
        return btn;
    }

    private TextView createResultView() {
        TextView tv = new TextView(this);
        tv.setTextSize(18);
        tv.setTextColor(0xFF38BDF8);
        tv.setPadding(20, 20, 20, 20);
        mainContainer.addView(tv);
        return tv;
    }

    private void buildBMI() {
        addContentDescription("Body Mass Index (BMI)", "BMI is a measure of body fat based on height and weight. Maintaining a normal BMI reduces the risk of chronic diseases like diabetes and high blood pressure.");
        
        EditText etWeight = createInput("Weight in KG");
        EditText etHeight = createInput("Height in CM");
        Button btnCalc = createButton("CALCULATE BMI");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double weight = Double.parseDouble(etWeight.getText().toString());
                double heightM = Double.parseDouble(etHeight.getText().toString()) / 100.0;
                double bmi = weight / (heightM * heightM);
                
                String status = "Normal Weight";
                if (bmi < 18.5) status = "Underweight";
                else if (bmi >= 25 && bmi < 29.9) status = "Overweight";
                else if (bmi >= 30) status = "Obese";
                
                tvResult.setText(String.format(Locale.US, "Your BMI: %.1f\nCategory: %s", bmi, status));
            } catch(Exception e) { tvResult.setText("Please enter valid numbers"); }
        });
    }

    private void buildBMR() {
        addContentDescription("Basal Metabolic Rate (BMR)", "BMR represents the total number of calories your body needs to perform basic, life-sustaining functions (like breathing and digestion) while at rest.");

        EditText etAge = createInput("Age (Years)");
        EditText etWeight = createInput("Weight in KG");
        EditText etHeight = createInput("Height in CM");
        
        RadioGroup rgGender = new RadioGroup(this);
        rgGender.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton rbMale = new RadioButton(this); rbMale.setText("Male"); rbMale.setTextColor(0xFFFFFFFF); rbMale.setChecked(true);
        RadioButton rbFemale = new RadioButton(this); rbFemale.setText("Female"); rbFemale.setTextColor(0xFFFFFFFF);
        rgGender.addView(rbMale); rgGender.addView(rbFemale);
        mainContainer.addView(rgGender);

        Button btnCalc = createButton("CALCULATE BMR");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                int age = Integer.parseInt(etAge.getText().toString());
                double w = Double.parseDouble(etWeight.getText().toString());
                double h = Double.parseDouble(etHeight.getText().toString());
                
                double bmr = (10 * w) + (6.25 * h) - (5 * age);
                bmr = rbMale.isChecked() ? bmr + 5 : bmr - 161;
                
                tvResult.setText(String.format(Locale.US, "Your Basal Metabolic Rate:\n%.0f Calories/day", bmr));
            } catch(Exception e) { tvResult.setText("Please enter valid numbers"); }
        });
    }

    private void buildCalories() {
        addContentDescription("Daily Calorie Needs", "Calculate the exact number of calories you need to consume daily to either maintain your current weight, lose weight, or gain muscle mass safely.");

        EditText etAge = createInput("Age (Years)");
        EditText etWeight = createInput("Weight in KG");
        EditText etHeight = createInput("Height in CM");
        Button btnCalc = createButton("CALCULATE DAILY NEEDS");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                int age = Integer.parseInt(etAge.getText().toString());
                double w = Double.parseDouble(etWeight.getText().toString());
                double h = Double.parseDouble(etHeight.getText().toString());
                double bmr = (10 * w) + (6.25 * h) - (5 * age) + 5; 
                
                tvResult.setText(String.format(Locale.US, 
                    "To Maintain Weight: %.0f Cal/day\nTo Lose Weight: %.0f Cal/day\nTo Gain Weight: %.0f Cal/day", 
                    bmr * 1.375, (bmr * 1.375) - 500, (bmr * 1.375) + 500));
            } catch(Exception e) { tvResult.setText("Please enter valid numbers"); }
        });
    }

    private void buildWater() {
        addContentDescription("Hydration Goal", "Proper hydration is critical for joint lubrication, temperature regulation, and overall organ health. Your needs increase with physical exertion.");

        EditText etWeight = createInput("Weight in KG");
        EditText etMinutes = createInput("Daily Exercise (Minutes)");
        Button btnCalc = createButton("CALCULATE HYDRATION");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double weight = Double.parseDouble(etWeight.getText().toString());
                double exercise = Double.parseDouble(etMinutes.getText().toString());
                double liters = (weight * 0.033) + ((exercise / 30.0) * 0.35);
                
                tvResult.setText(String.format(Locale.US, "Daily Water Goal:\n%.1f Liters (approx %.0f glasses)", liters, liters * 4));
            } catch(Exception e) { tvResult.setText("Please enter valid numbers"); }
        });
    }
}
