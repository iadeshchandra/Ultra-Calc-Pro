package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ConverterActivity extends AppCompatActivity {
    private EditText inputPrimary, inputSecondary, inputTertiary, inputPricePerUnit;
    private TextView resultDisplay, historyList;
    private AutoCompleteTextView modeSelector;
    private ArrayList<String> logs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);

        inputPrimary = findViewById(R.id.inputPrimary);
        inputSecondary = findViewById(R.id.inputSecondary);
        inputTertiary = findViewById(R.id.inputTertiary);
        inputPricePerUnit = findViewById(R.id.inputPricePerUnit);
        resultDisplay = findViewById(R.id.resultDisplay);
        historyList = findViewById(R.id.historyList);
        modeSelector = findViewById(R.id.modeSelector);
        Button btnAction = findViewById(R.id.btnAction);
        
        // Back Button to return to Dashboard
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String[] metrics = {
            "Wood: Round Log (CFT)",
            "Wood: Sawn Size Timber (CFT)",
            "Currency: USD to BDT (110)",
            "Land: Bigha to Decimal",
            "Land: Decimal to Sq Ft",
            "Weight: KG to Lbs",
            "Liquid: Liters to Gallons"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, metrics);
        modeSelector.setAdapter(adapter);

        modeSelector.setOnItemClickListener((parent, view, position, id) -> {
            String selected = itemMapping(position);
            adjustFormLayout(selected);
        });

        btnAction.setOnClickListener(v -> executeEngine());
    }

    private String itemMapping(int pos) {
        switch(pos) {
            case 0: return "ROUND";
            case 1: return "SAWN";
            default: return "STANDARD";
        }
    }

    private void adjustFormLayout(String mode) {
        inputPrimary.setText("");
        inputSecondary.setText("");
        inputTertiary.setText("");
        inputPricePerUnit.setText("");
        
        if (mode.equals("ROUND")) {
            inputPrimary.setHint("Length (Feet)");
            inputSecondary.setHint("Girth / Circumference (Inches)");
            inputSecondary.setVisibility(View.VISIBLE);
            inputTertiary.setVisibility(View.GONE);
        } else if (mode.equals("SAWN")) {
            inputPrimary.setHint("Length (Feet)");
            inputSecondary.setHint("Width (Inches)");
            inputTertiary.setHint("Thickness (Inches)");
            inputSecondary.setVisibility(View.VISIBLE);
            inputTertiary.setVisibility(View.VISIBLE);
        } else {
            inputPrimary.setHint("Enter Base Value");
            inputSecondary.setVisibility(View.GONE);
            inputTertiary.setVisibility(View.GONE);
        }
    }

    private void executeEngine() {
        String mode = modeSelector.getText().toString();
        String pStr = inputPrimary.getText().toString();
        
        if (mode.isEmpty() || pStr.isEmpty()) return;
        
        double p = Double.parseDouble(pStr);
        double pricePerUnit = 0;
        String priceStr = inputPricePerUnit.getText().toString();
        if (!priceStr.isEmpty()) {
            pricePerUnit = Double.parseDouble(priceStr);
        }

        double finalVolumeOrMetric = 0;
        String outputLabel = "";

        if (mode.contains("Round Log")) {
            String sStr = inputSecondary.getText().toString();
            if (sStr.isEmpty()) return;
            double girth = Double.parseDouble(sStr);
            finalVolumeOrMetric = ((girth / 4.0) * (girth / 4.0) * p) / 144.0;
            outputLabel = " CFT";
        } 
        else if (mode.contains("Sawn Size")) {
            String sStr = inputSecondary.getText().toString();
            String tStr = inputTertiary.getText().toString();
            if (sStr.isEmpty() || tStr.isEmpty()) return;
            double width = Double.parseDouble(sStr);
            double thickness = Double.parseDouble(tStr);
            finalVolumeOrMetric = (p * width * thickness) / 144.0;
            outputLabel = " CFT";
        }
        else if (mode.contains("USD to BDT")) { finalVolumeOrMetric = p * 110.0; outputLabel = " ৳"; }
        else if (mode.contains("Bigha to Decimal")) { finalVolumeOrMetric = p * 33.06; outputLabel = " Dec"; }
        else if (mode.contains("Decimal to Sq Ft")) { finalVolumeOrMetric = p * 435.6; outputLabel = " sq ft"; }
        else if (mode.contains("KG to Lbs")) { finalVolumeOrMetric = p * 2.204; outputLabel = " lbs"; }
        else if (mode.contains("Liters to Gallons")) { finalVolumeOrMetric = p * 0.264; outputLabel = " gal"; }

        String printResult = String.format("%.2f", finalVolumeOrMetric) + outputLabel;
        
        if (pricePerUnit > 0 && (mode.contains("Round Log") || mode.contains("Sawn Size"))) {
            double totalPrice = finalVolumeOrMetric * pricePerUnit;
            printResult += " | Total Cost: " + String.format("%.2f", totalPrice) + " ৳";
        }

        resultDisplay.setText(printResult);
        logs.add(0, mode.split(":")[0] + " Calc -> " + printResult);
        
        StringBuilder sb = new StringBuilder();
        for (String s : logs) sb.append("✓ ").append(s).append("\n");
        historyList.setText(sb.toString());
    }
}
