package com.adeshchandra.ultracalc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WoodActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private int currentMode = 0; 
    private ArrayList<WoodItem> woodList = new ArrayList<>();
    private TextView tvCalcTitle, tvTotalLogs, tvTotalVolume, lblParam2, lblParam3;
    private EditText etLength, etParam2, etParam3, etQty;
    private Spinner spinLen, spinP2, spinP3;
    private LinearLayout containerParam3, tableRowsContainer, invoicePrintArea;
    private double currentRate = 0.0;
    private final String[] impUnits = {"ft", "in"};
    private final String[] metUnits = {"m", "cm"};

    class WoodItem {
        int sNo; double length, param2, param3, volume; int qty; String uLen, uP2, uP3;
        WoodItem(int s, double l, String ul, double p2, String up2, double p3, String up3, int q, double v) { 
            sNo=s; length=l; uLen=ul; param2=p2; uP2=up2; param3=p3; uP3=up3; qty=q; volume=v; 
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wood);

        viewFlipper = findViewById(R.id.viewFlipper);
        invoicePrintArea = findViewById(R.id.invoicePrintArea);

        findViewById(R.id.btnBackDashboard).setOnClickListener(v -> finish());
        findViewById(R.id.btnBackCalc).setOnClickListener(v -> { woodList.clear(); refreshTable(); viewFlipper.setDisplayedChild(0); });
        findViewById(R.id.btnBackToCalcFromInvoice).setOnClickListener(v -> viewFlipper.setDisplayedChild(1));
        findViewById(R.id.btnSharePdf).setOnClickListener(v -> exportAndSharePdf());

        // UI Listeners
        findViewById(R.id.btnHelp).setOnClickListener(v -> startActivity(new Intent(this, ManualActivity.class)));
        findViewById(R.id.btnLang).setOnClickListener(v -> Toast.makeText(this, "Coming Soon!", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnAllSavedRecords).setOnClickListener(v -> Toast.makeText(this, "Check Downloads > Wood calculator", Toast.LENGTH_SHORT).show());

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_bot_help) startActivity(new Intent(this, ManualActivity.class));
            return true;
        });

        setupDashboardGrid();
        setupCalculatorPad();
    }

    private void setupDashboardGrid() {
        // ONLY include the 4 IDs present in activity_wood.xml
        int[] cardIds = {R.id.cardRoundImp, R.id.cardSizeImp, R.id.cardRoundMet, R.id.cardSizeMet};
        for (int i = 0; i < cardIds.length; i++) {
            final int mode = i;
            findViewById(cardIds[i]).setOnClickListener(v -> openCalculator(mode));
        }
    }

    private void openCalculator(int mode) {
        currentMode = mode; woodList.clear(); refreshTable();
        tvCalcTitle = findViewById(R.id.tvCalcTitle);
        lblParam2 = findViewById(R.id.lblParam2); lblParam3 = findViewById(R.id.lblParam3);
        containerParam3 = findViewById(R.id.containerParam3);
        
        boolean isImp = mode < 2; // Adjusted for 4 cards
        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, isImp ? impUnits : metUnits);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLen.setAdapter(adapt); spinP2.setAdapter(adapt); spinP3.setAdapter(adapt);

        if (mode == 0 || mode == 2) {
            tvCalcTitle.setText(isImp ? "Round Wood (Foot/Inch)" : "Round Wood (Meter/cm)");
            lblParam2.setText("Roundness");
            containerParam3.setVisibility(View.GONE);
        } else {
            tvCalcTitle.setText(isImp ? "Size Wood (Foot/Inch)" : "Size Wood (Meter/cm)");
            lblParam2.setText("Width"); lblParam3.setText("Thickness");
            containerParam3.setVisibility(View.VISIBLE);
        }
        viewFlipper.setDisplayedChild(1);
    }

    private void setupCalculatorPad() {
        tvTotalLogs = findViewById(R.id.tvTotalLogs); tvTotalVolume = findViewById(R.id.tvTotalVolume);
        tableRowsContainer = findViewById(R.id.tableRowsContainer);
        etLength = findViewById(R.id.etLength); etParam2 = findViewById(R.id.etParam2);
        etParam3 = findViewById(R.id.etParam3); etQty = findViewById(R.id.etQty);
        spinLen = findViewById(R.id.spinLen); spinP2 = findViewById(R.id.spinP2); spinP3 = findViewById(R.id.spinP3);
        findViewById(R.id.btnAdd).setOnClickListener(v -> calculateAndAdd());
        findViewById(R.id.btnAddPrice).setOnClickListener(v -> showInvoiceDialog());
    }

    // ... (Keep the rest of your methods: calculateNormalizedVolume, calculateAndAdd, refreshTable, showEditDialog, showInvoiceDialog, generateInvoice, exportAndSharePdf)
}
