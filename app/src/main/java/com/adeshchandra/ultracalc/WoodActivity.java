package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class WoodActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    
    // State Tracking
    private int currentMode = 0; 
    // 0: Round(Ft/In), 1: Size(Ft/In), 2: Door(Ft/In)
    // 3: Round(M/cm), 4: Size(M/cm), 5: Door(M/cm)
    private ArrayList<WoodItem> woodList = new ArrayList<>();
    
    // Calculator Views
    private TextView tvCalcTitle, tvTotalLogs, tvTotalVolume;
    private TextView lblParam2, lblParam3;
    private EditText etLength, etParam2, etParam3, etQty;
    private LinearLayout containerParam3;
    private LinearLayout tableRowsContainer;
    
    // Invoice Variables
    private double currentRate = 0.0;

    class WoodItem {
        int sNo; double length; double param2; double param3; int qty; double volume;
        WoodItem(int s, double l, double p2, double p3, int q, double v) { 
            sNo=s; length=l; param2=p2; param3=p3; qty=q; volume=v; 
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wood);

        viewFlipper = findViewById(R.id.viewFlipper);
        
        // Navigation Setup
        findViewById(R.id.btnBackDashboard).setOnClickListener(v -> finish());
        findViewById(R.id.btnBackCalc).setOnClickListener(v -> {
            woodList.clear();
            refreshTable();
            viewFlipper.setDisplayedChild(0);
        });
        findViewById(R.id.btnBackToCalcFromInvoice).setOnClickListener(v -> viewFlipper.setDisplayedChild(1));

        setupDashboardGrid();
        setupCalculatorPad();
    }

    private void setupDashboardGrid() {
        int[] cardIds = {R.id.cardRoundImp, R.id.cardSizeImp, R.id.cardDoorImp, 
                         R.id.cardRoundMet, R.id.cardSizeMet, R.id.cardDoorMet};
        for (int i = 0; i < cardIds.length; i++) {
            final int mode = i;
            findViewById(cardIds[i]).setOnClickListener(v -> openCalculator(mode));
        }
    }

    private void openCalculator(int mode) {
        currentMode = mode;
        woodList.clear();
        refreshTable();
        
        tvCalcTitle = findViewById(R.id.tvCalcTitle);
        lblParam2 = findViewById(R.id.lblParam2);
        lblParam3 = findViewById(R.id.lblParam3);
        containerParam3 = findViewById(R.id.containerParam3);
        
        // Configure UI dynamically based on selection
        if (mode == 0 || mode == 3) {
            tvCalcTitle.setText(mode == 0 ? "Round Wood (Foot/Inch)" : "Round Wood (Meter/cm)");
            lblParam2.setText(mode == 0 ? "Roundness (in)" : "Roundness (cm)");
            containerParam3.setVisibility(View.GONE);
        } else if (mode == 1 || mode == 4) {
            tvCalcTitle.setText(mode == 1 ? "Size Wood (Foot/Inch)" : "Size Wood (Meter/cm)");
            lblParam2.setText(mode == 1 ? "Width (in)" : "Width (cm)");
            lblParam3.setText(mode == 1 ? "Thickness (in)" : "Thickness (cm)");
            containerParam3.setVisibility(View.VISIBLE);
        } else {
            tvCalcTitle.setText(mode == 2 ? "Flush Door (Foot/Inch)" : "Flush Door (Meter/cm)");
            lblParam2.setText(mode == 2 ? "Width (in)" : "Width (cm)");
            containerParam3.setVisibility(View.GONE);
        }
        
        viewFlipper.setDisplayedChild(1);
    }

    private void setupCalculatorPad() {
        tvTotalLogs = findViewById(R.id.tvTotalLogs);
        tvTotalVolume = findViewById(R.id.tvTotalVolume);
        tableRowsContainer = findViewById(R.id.tableRowsContainer);
        
        etLength = findViewById(R.id.etLength);
        etParam2 = findViewById(R.id.etParam2);
        etParam3 = findViewById(R.id.etParam3);
        etQty = findViewById(R.id.etQty);

        findViewById(R.id.btnAdd).setOnClickListener(v -> calculateAndAdd());
        
        findViewById(R.id.btnDelete).setOnClickListener(v -> {
            if (!woodList.isEmpty()) {
                woodList.remove(woodList.size() - 1);
                refreshTable();
            }
        });

        findViewById(R.id.btnAddPrice).setOnClickListener(v -> showInvoiceDialog());
    }

    private void calculateAndAdd() {
        try {
            double l = Double.parseDouble(etLength.getText().toString());
            double p2 = Double.parseDouble(etParam2.getText().toString());
            double p3 = containerParam3.getVisibility() == View.VISIBLE ? Double.parseDouble(etParam3.getText().toString()) : 0;
            int q = etQty.getText().toString().isEmpty() ? 1 : Integer.parseInt(etQty.getText().toString());

            double vol = 0;
            
            // Round Wood Imperial (Quarter Girth Formula)
            if (currentMode == 0) vol = ((p2 / 4.0) * (p2 / 4.0) * l) / 144.0;
            // Size Wood Imperial
            else if (currentMode == 1) vol = (l * p2 * p3) / 144.0;
            // Door Area Imperial
            else if (currentMode == 2) vol = (l * p2) / 12.0; // Assuming Length in Ft, Width in Inches
            // Round Wood Metric
            else if (currentMode == 3) vol = ((p2 / 4.0) * (p2 / 4.0) * l) / 10000.0;
            // Size Wood Metric
            else if (currentMode == 4) vol = (l * p2 * p3) / 10000.0;
            // Door Area Metric
            else if (currentMode == 5) vol = (l * p2) / 100.0; 

            double totalVol = vol * q;
            woodList.add(new WoodItem(woodList.size() + 1, l, p2, p3, q, totalVol));
            
            etLength.setText(""); etParam2.setText(""); etParam3.setText(""); etQty.setText("");
            etLength.requestFocus();
            refreshTable();
        } catch (Exception e) {
            Toast.makeText(this, "Enter valid measurements", Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshTable() {
        tableRowsContainer.removeAllViews();
        double sumVol = 0; int sumQty = 0;

        for (WoodItem item : woodList) {
            View row = getLayoutInflater().inflate(R.layout.row_wood_master, tableRowsContainer, false);
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(String.valueOf(item.length));
            ((TextView) row.findViewById(R.id.colParam2)).setText(String.valueOf(item.param2));
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.3f", item.volume));
            tableRowsContainer.addView(row);

            sumVol += item.volume; sumQty += item.qty;
        }
        
        tvTotalLogs.setText("Total Wood : " + sumQty);
        String unit = (currentMode >= 3) ? "(Meter)³" : "(Foot)³";
        if (currentMode == 2 || currentMode == 5) unit = (currentMode == 5) ? "(Meter)²" : "(Foot)²";
        tvTotalVolume.setText(String.format(Locale.US, "Total %s : %.3f", unit, sumVol));
    }

    private void showInvoiceDialog() {
        if (woodList.isEmpty()) {
            Toast.makeText(this, "Add wood measurements first!", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_price, null);
        EditText etRate = dialogView.findViewById(R.id.etRate);
        EditText etSellerName = dialogView.findViewById(R.id.etSellerName);
        EditText etSellerPhone = dialogView.findViewById(R.id.etSellerPhone);
        EditText etCustomerName = dialogView.findViewById(R.id.etCustomerName);
        EditText etCustomerPhone = dialogView.findViewById(R.id.etCustomerPhone);

        new AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Create Invoice", (dialog, which) -> {
                try {
                    currentRate = Double.parseDouble(etRate.getText().toString());
                    String seller = etSellerName.getText().toString() + "\n" + etSellerPhone.getText().toString();
                    String customer = etCustomerName.getText().toString() + "\n" + etCustomerPhone.getText().toString();
                    generateInvoice(seller, customer);
                } catch (Exception e) {
                    Toast.makeText(this, "Rate is required", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void generateInvoice(String seller, String customer) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        Date now = new Date();
        
        ((TextView) findViewById(R.id.invDate)).setText("Date : " + sdfDate.format(now));
        ((TextView) findViewById(R.id.invTime)).setText("Time : " + sdfTime.format(now));
        ((TextView) findViewById(R.id.invSeller)).setText(seller);
        ((TextView) findViewById(R.id.invCustomer)).setText(customer);

        LinearLayout invTableRows = findViewById(R.id.invTableRows);
        invTableRows.removeAllViews();
        double sumVol = 0; int sumQty = 0;

        for (WoodItem item : woodList) {
            View row = getLayoutInflater().inflate(R.layout.row_invoice_master, invTableRows, false);
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(String.valueOf(item.length));
            ((TextView) row.findViewById(R.id.colParam2)).setText(String.valueOf(item.param2));
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.3f", item.volume));
            invTableRows.addView(row);
            sumVol += item.volume; sumQty += item.qty;
        }

        ((TextView) findViewById(R.id.invTotalVol)).setText(String.format(Locale.US, "%.3f", sumVol));
        ((TextView) findViewById(R.id.invTotalQty)).setText(String.valueOf(sumQty));
        ((TextView) findViewById(R.id.invRate)).setText(String.valueOf(currentRate));
        ((TextView) findViewById(R.id.invTotalAmount)).setText(String.format(Locale.US, "%.0f ৳", sumVol * currentRate));

        viewFlipper.setDisplayedChild(2);
    }
}
