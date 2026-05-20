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
    private EditText inputPrimary, inputSecondary, inputTertiary, inputPieces, inputPricePerUnit;
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
        inputPieces = findViewById(R.id.inputPieces);
        inputPricePerUnit = findViewById(R.id.inputPricePerUnit);
        resultDisplay = findViewById(R.id.resultDisplay);
        historyList = findViewById(R.id.historyList);
        modeSelector = findViewById(R.id.modeSelector);
        Button btnAction = findViewById(R.id.btnAction);
        
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        String[] metrics = {
            "Wood: Round Log Volume (CFT)",
            "Wood: Sawn Sized Plank (CFT)",
            "Wood: Standing Tree / Air Est (CFT)",
            "Currency: USD to BDT (110 Offline)",
            "Land: Bigha to Decimal",
            "Land: Decimal to Sq Ft",
            "Weight: KG to Lbs",
            "Liquid: Liters to Gallons"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, metrics);
        modeSelector.setAdapter(adapter);

        modeSelector.setOnItemClickListener((parent, view, position, id) -> {
            adjustFormLayout(position);
        });

        btnAction.setOnClickListener(v -> executeEngine());
    }

    private void adjustFormLayout(int pos) {
        inputPrimary.setText("");
        inputSecondary.setText("");
        inputTertiary.setText("");
        inputPieces.setText("1");
        inputPricePerUnit.setText("");
        
        if (pos == 0) { // Round Log
            inputPrimary.setHint("Length (Feet)");
            inputSecondary.setHint("Girth / Circumference (Inches)");
            inputSecondary.setVisibility(View.VISIBLE);
            inputTertiary.setVisibility(View.GONE);
            inputPieces.setVisibility(View.VISIBLE);
            inputPricePerUnit.setVisibility(View.VISIBLE);
        } else if (pos == 1) { // Sawn Size
            inputPrimary.setHint("Length (Feet)");
            inputSecondary.setHint("Width (Inches)");
            inputTertiary.setHint("Thickness (Inches)");
            inputSecondary.setVisibility(View.VISIBLE);
            inputTertiary.setVisibility(View.VISIBLE);
            inputPieces.setVisibility(View.VISIBLE);
            inputPricePerUnit.setVisibility(View.VISIBLE);
        } else if (pos == 2) { // Standing Tree / Air Est
            inputPrimary.setHint("Height / Length (Feet)");
            inputSecondary.setHint("Center Girth Over Bark (Inches)");
            inputSecondary.setVisibility(View.VISIBLE);
            inputTertiary.setVisibility(View.GONE);
            inputPieces.setVisibility(View.VISIBLE);
            inputPricePerUnit.setVisibility(View.VISIBLE);
        } else { // Standard conversions
            inputPrimary.setHint("Enter Input Value");
            inputSecondary.setVisibility(View.GONE);
            inputTertiary.setVisibility(View.GONE);
            inputPieces.setVisibility(View.GONE);
            inputPricePerUnit.setVisibility(View.GONE);
        }
    }

    private void executeEngine() {
        String mode = modeSelector.getText().toString();
        String pStr = inputPrimary.getText().toString();
        
        if (mode.isEmpty() || pStr.isEmpty()) return;
        
        double primaryVal = Double.parseDouble(pStr);
        int pieces = 1;
        String piecesStr = inputPieces.getText().toString();
        if (!piecesStr.isEmpty()) {
            pieces = Integer.parseInt(piecesStr);
        }

        double pricePerUnit = 0;
        String priceStr = inputPricePerUnit.getText().toString();
        if (!priceStr.isEmpty()) {
            pricePerUnit = Double.parseDouble(priceStr);
        }

        double calculatedOutput = 0;
        String outputLabel = "";

        if (mode.contains("Round Log Volume")) {
            String sStr = inputSecondary.getText().toString();
            if (sStr.isEmpty()) return;
            double girth = Double.parseDouble(sStr);
            // Local standard Quarter-Girth formula: ((Girth/4)^2 * Length) / 144
            calculatedOutput = ((girth / 4.0) * (girth / 4.0) * primaryVal) / 144.0;
            calculatedOutput *= pieces;
            outputLabel = " CFT";
        } 
        else if (mode.contains("Sawn Sized Plank")) {
            String sStr = inputSecondary.getText().toString();
            String tStr = inputTertiary.getText().toString();
            if (sStr.isEmpty() || tStr.isEmpty()) return;
            double width = Double.parseDouble(sStr);
            double thickness = Double.parseDouble(tStr);
            // Sawn Plank formula: (Length * Width * Thickness) / 144
            calculatedOutput = (primaryVal * width * thickness) / 144.0;
            calculatedOutput *= pieces;
            outputLabel = " CFT";
        }
        else if (mode.contains("Standing Tree / Air Est")) {
            String sStr = inputSecondary.getText().toString();
            if (sStr.isEmpty()) return;
            double centerGirth = Double.parseDouble(sStr);
            // Standing tree measurement accounting for bark loss and air geometry reductions
            double quarterGirthReduced = (centerGirth * 0.9) / 4.0; 
            calculatedOutput = (quarterGirthReduced * quarterGirthReduced * primaryVal) / 144.0;
            calculatedOutput *= pieces;
            outputLabel = " CFT (Est.)";
        }
        else if (mode.contains("USD to BDT")) { calculatedOutput = primaryVal * 110.0; outputLabel = " ৳"; }
        else if (mode.contains("Bigha to Decimal")) { calculatedOutput = primaryVal * 33.06; outputLabel = " Dec"; }
        else if (mode.contains("Decimal to Sq Ft")) { calculatedOutput = primaryVal * 435.6; outputLabel = " sq ft"; }
        else if (mode.contains("KG to Lbs")) { calculatedOutput = primaryVal * 2.204; outputLabel = " lbs"; }
        else if (mode.contains("Liters to Gallons")) { calculatedOutput = primaryVal * 0.264; outputLabel = " gal"; }

        String printResult = String.format(java.util.Locale.US, "%.3f", calculatedOutput) + outputLabel;
        
        if (pricePerUnit > 0 && (mode.contains("Wood:") || mode.contains("Standing"))) {
            double totalPrice = calculatedOutput * pricePerUnit;
            printResult += " \nTotal Price: " + String.format(java.util.Locale.US, "%.2f", totalPrice) + " ৳";
        }

        resultDisplay.setText(printResult);
        logs.add(0, mode.split(":")[1].trim() + " -> " + printResult.replace("\n", " | "));
        
        StringBuilder sb = new StringBuilder();
        for (String s : logs) sb.append("✓ ").append(s).append("\n\n");
        historyList.setText(sb.toString());
    }
}
