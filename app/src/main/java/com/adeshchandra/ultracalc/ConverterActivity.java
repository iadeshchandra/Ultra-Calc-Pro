package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class ConverterActivity extends AppCompatActivity {

    private LinearLayout mainContainer;
    private Spinner spinnerFrom, spinnerTo;
    private EditText etInput;
    private TextView tvResult;

    private static final String[] CATEGORIES = {
        "Length", "Weight", "Temperature", "Area", "Volume", 
        "Time", "Data", "Speed", "Pressure", "Energy", 
        "Power", "Force", "Angle", "Fuel"
    };

    private static final String[] EMOJIS = {
        "📏", "⚖️", "🌡️", "🗺️", "🛢️", 
        "⏳", "💾", "🚀", "🎈", "⚡", 
        "🔋", "🏋️", "📐", "⛽"
    };

    private String[][] unitArrays = {
        {"Meter", "Kilometer", "Centimeter", "Millimeter", "Mile", "Yard", "Foot", "Inch"}, // Length
        {"Kilogram", "Gram", "Milligram", "Metric Ton", "Pound", "Ounce"}, // Weight
        {"Celsius", "Fahrenheit", "Kelvin"}, // Temperature
        {"Sq Meter", "Sq Kilometer", "Sq Mile", "Sq Yard", "Sq Foot", "Sq Inch", "Hectare", "Acre"}, // Area
        {"Liter", "Milliliter", "Cubic Meter", "Gallon (US)", "Fluid Ounce (US)"}, // Volume
        {"Second", "Minute", "Hour", "Day", "Week", "Year"}, // Time
        {"Byte", "Kilobyte (KB)", "Megabyte (MB)", "Gigabyte (GB)", "Terabyte (TB)"}, // Data
        {"Meter/Sec", "Km/Hour", "Mile/Hour", "Knot", "Foot/Sec"}, // Speed
        {"Pascal", "Bar", "Atmosphere", "Torr", "PSI"}, // Pressure
        {"Joule", "Kilojoule", "Gram calorie", "Kilocalorie", "Watt hour"}, // Energy
        {"Watt", "Kilowatt", "Megawatt", "Horsepower"}, // Power
        {"Newton", "Kilonewton", "Gram-force", "Kilogram-force"}, // Force
        {"Degree", "Radian", "Milliradian", "Minute of arc"}, // Angle
        {"Miles/Gallon", "Liters/100km", "Km/Liter"} // Fuel
    };

    private double[][] conversionFactors = {
        {1.0, 1000.0, 0.01, 0.001, 1609.34, 0.9144, 0.3048, 0.0254}, // Length (Base: Meter)
        {1.0, 0.001, 0.000001, 1000.0, 0.453592, 0.0283495}, // Weight (Base: Kilogram)
        {1.0, 1.0, 1.0}, // Temp (Handled Specially)
        {1.0, 1000000.0, 2589988.11, 0.836127, 0.092903, 0.00064516, 10000.0, 4046.86}, // Area (Base: Sq Meter)
        {1.0, 0.001, 1000.0, 3.78541, 0.0295735}, // Volume (Base: Liter)
        {1.0, 60.0, 3600.0, 86400.0, 604800.0, 31536000.0}, // Time (Base: Second)
        {1.0, 1024.0, 1048576.0, 1073741824.0, 1099511627776.0}, // Data (Base: Byte)
        {1.0, 0.277778, 0.44704, 0.514444, 0.3048}, // Speed (Base: Meter/Sec)
        {1.0, 100000.0, 101325.0, 133.322, 6894.76}, // Pressure (Base: Pascal)
        {1.0, 1000.0, 4.184, 4184.0, 3600.0}, // Energy (Base: Joule)
        {1.0, 1000.0, 1000000.0, 745.699872}, // Power (Base: Watt)
        {1.0, 1000.0, 0.00980665, 9.80665}, // Force (Base: Newton)
        {1.0, 57.2958, 0.0572958, 0.0166667}, // Angle (Base: Degree)
        {1.0, 1.0, 1.0} // Fuel (Handled Specially)
    };

    private int activeCategoryIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        spinnerFrom = findViewById(R.id.spinnerFrom);
        spinnerTo = findViewById(R.id.spinnerTo);
        etInput = findViewById(R.id.etInput);
        tvResult = findViewById(R.id.tvResult);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        buildToolBar();
        selectCategory(0);

        // Real-time conversion execution
        TextWatcher computeWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { runConversion(); }
            public void afterTextChanged(Editable s) {}
        };
        etInput.addTextChangedListener(computeWatcher);

        AdapterView.OnItemSelectedListener spinnerWatcher = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { runConversion(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
        spinnerFrom.setOnItemSelectedListener(spinnerWatcher);
        spinnerTo.setOnItemSelectedListener(spinnerWatcher);
    }

    private void buildToolBar() {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i = 0; i < CATEGORIES.length; i++) {
            final int idx = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(32, 16, 32, 16);
            
            TextView em = new TextView(this); em.setText(EMOJIS[i]); em.setTextSize(24); em.setGravity(android.view.Gravity.CENTER);
            TextView lb = new TextView(this); lb.setText(CATEGORIES[i]); lb.setTextSize(11); lb.setTextColor(0xFFB0AECC); lb.setGravity(android.view.Gravity.CENTER);
            
            item.addView(em); item.addView(lb);
            item.setOnClickListener(v -> selectCategory(idx));
            bar.addView(item);
        }
    }

    private void selectCategory(int idx) {
        activeCategoryIndex = idx;
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i = 0; i < bar.getChildCount(); i++) {
            bar.getChildAt(i).setBackgroundColor(i == idx ? 0xFF252548 : 0x00000000);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unitArrays[idx]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(adapter);
        spinnerTo.setAdapter(adapter);
        
        if(unitArrays[idx].length > 1) {
            spinnerFrom.setSelection(0);
            spinnerTo.setSelection(1);
        }

        etInput.setText("");
        tvResult.setText("0.00");
    }

    private void runConversion() {
        String inputStr = etInput.getText().toString();
        if (inputStr.isEmpty()) {
            tvResult.setText("0.00");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int fromIdx = spinnerFrom.getSelectedItemPosition();
            int toIdx = spinnerTo.getSelectedItemPosition();
            
            double result = 0.0;

            // Handle Temperature Edge Case (Non-Linear)
            if (activeCategoryIndex == 2) {
                double baseCelsius = inputValue;
                if (fromIdx == 1) baseCelsius = (inputValue - 32) * 5 / 9;
                else if (fromIdx == 2) baseCelsius = inputValue - 273.15;

                if (toIdx == 0) result = baseCelsius;
                else if (toIdx == 1) result = (baseCelsius * 9 / 5) + 32;
                else if (toIdx == 2) result = baseCelsius + 273.15;
            } 
            // Handle Fuel Edge Case (Inversely Proportional)
            else if (activeCategoryIndex == 13) {
                if (fromIdx == toIdx) { result = inputValue; }
                else if (fromIdx == 0 && toIdx == 1) { result = 235.215 / inputValue; } // MPG to L/100km
                else if (fromIdx == 1 && toIdx == 0) { result = 235.215 / inputValue; } // L/100km to MPG
                else if (fromIdx == 0 && toIdx == 2) { result = inputValue * 0.425144; } // MPG to Km/L
                else if (fromIdx == 2 && toIdx == 0) { result = inputValue * 2.35215; } // Km/L to MPG
                else if (fromIdx == 1 && toIdx == 2) { result = 100.0 / inputValue; } // L/100km to Km/L
                else if (fromIdx == 2 && toIdx == 1) { result = 100.0 / inputValue; } // Km/L to L/100km
            } 
            // Standard Linear Factor Parsing
            else {
                double baseValue = inputValue * conversionFactors[activeCategoryIndex][fromIdx];
                result = baseValue / conversionFactors[activeCategoryIndex][toIdx];
            }

            // Format for large/small strings cleanly
            if (result == 0) tvResult.setText("0.00");
            else if (result > 1e7 || result < 1e-4) tvResult.setText(String.format(Locale.US, "%.6e", result));
            else tvResult.setText(String.format(Locale.US, "%.4f", result).replaceAll("0*$", "").replaceAll("\\.$", ""));
            
        } catch (Exception e) {
            tvResult.setText("Error");
        }
    }
}
