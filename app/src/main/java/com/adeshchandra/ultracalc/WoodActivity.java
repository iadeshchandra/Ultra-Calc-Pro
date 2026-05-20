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
    private LinearLayout tableRowsContainer;
    private TextView tvTotalLogs, tvTotalVolume;
    
    // Invoice Views
    private TextView invDate, invTime, invSeller, invCustomer;
    private LinearLayout invTableRows;
    private TextView invTotalVol, invTotalQty, invRate, invTotalAmount;

    private ArrayList<WoodItem> woodList = new ArrayList<>();
    private double currentRate = 0.0;

    class WoodItem {
        int sNo; double length; double girth; int qty; double volume;
        WoodItem(int s, double l, double g, int q, double v) { sNo=s; length=l; girth=g; qty=q; volume=v; }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wood);

        viewFlipper = findViewById(R.id.viewFlipper);
        tableRowsContainer = findViewById(R.id.tableRowsContainer);
        tvTotalLogs = findViewById(R.id.tvTotalLogs);
        tvTotalVolume = findViewById(R.id.tvTotalVolume);

        // Bind Main UI Elements
        EditText etLength = findViewById(R.id.etLength);
        EditText etGirth = findViewById(R.id.etGirth);
        EditText etQty = findViewById(R.id.etQty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        // AR / Guideline Dialog
        findViewById(R.id.btnARGuide).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("📷 AR & Measurement Guide")
                .setMessage("AR Camera Tool (Beta) is initializing offline modules.\n\nTo manually measure standing trees:\n1. Wrap tape around the trunk at chest height (Girth).\n2. Estimate usable log height.\n3. The system will auto-apply Quarter-Girth rules.")
                .setPositiveButton("Got it", null)
                .show();
        });

        // Add Log to Table
        findViewById(R.id.btnAddLog).setOnClickListener(v -> {
            try {
                double length = Double.parseDouble(etLength.getText().toString());
                double girth = Double.parseDouble(etGirth.getText().toString());
                int qty = etQty.getText().toString().isEmpty() ? 1 : Integer.parseInt(etQty.getText().toString());

                // Formula: (Girth/4)^2 * Length / 144
                double volume = ((girth / 4.0) * (girth / 4.0) * length) / 144.0;
                double totalVol = volume * qty;

                int sNo = woodList.size() + 1;
                woodList.add(new WoodItem(sNo, length, girth, qty, totalVol));
                
                etLength.setText(""); etGirth.setText(""); etQty.setText("");
                refreshTable();
            } catch (Exception e) {
                Toast.makeText(this, "Enter valid lengths and girths", Toast.LENGTH_SHORT).show();
            }
        });

        // Add Price & Generate Invoice Dialog
        findViewById(R.id.btnAddPrice).setOnClickListener(v -> showInvoiceDialog());

        // Setup Invoice Layout Bindings
        invDate = findViewById(R.id.invDate);
        invTime = findViewById(R.id.invTime);
        invSeller = findViewById(R.id.invSeller);
        invCustomer = findViewById(R.id.invCustomer);
        invTableRows = findViewById(R.id.invTableRows);
        invTotalVol = findViewById(R.id.invTotalVol);
        invTotalQty = findViewById(R.id.invTotalQty);
        invRate = findViewById(R.id.invRate);
        invTotalAmount = findViewById(R.id.invTotalAmount);

        findViewById(R.id.btnBackToCalc).setOnClickListener(v -> viewFlipper.setDisplayedChild(0));
    }

    private void refreshTable() {
        tableRowsContainer.removeAllViews();
        double sumVol = 0;
        int sumQty = 0;

        for (WoodItem item : woodList) {
            View row = getLayoutInflater().inflate(R.layout.row_wood_item, tableRowsContainer, false);
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(item.length + " ft");
            ((TextView) row.findViewById(R.id.colGirth)).setText(item.girth + " in");
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.2f", item.volume));
            tableRowsContainer.addView(row);

            sumVol += item.volume;
            sumQty += item.qty;
        }
        tvTotalLogs.setText("Total Wood: " + sumQty);
        tvTotalVolume.setText(String.format(Locale.US, "Total (Foot)³: %.2f", sumVol));
    }

    private void showInvoiceDialog() {
        if (woodList.isEmpty()) {
            Toast.makeText(this, "Add logs first!", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_price, null);
        EditText etRate = dialogView.findViewById(R.id.etRate);
        EditText etSeller = dialogView.findViewById(R.id.etSeller);
        EditText etCustomer = dialogView.findViewById(R.id.etCustomer);

        new AlertDialog.Builder(this)
            .setTitle("Generate Invoice")
            .setView(dialogView)
            .setPositiveButton("Create Invoice", (dialog, which) -> {
                try {
                    currentRate = Double.parseDouble(etRate.getText().toString());
                    String seller = etSeller.getText().toString().isEmpty() ? "Adesh Chandra\n01701987744" : etSeller.getText().toString();
                    String customer = etCustomer.getText().toString().isEmpty() ? "Walk-in Client" : etCustomer.getText().toString();
                    generateInvoice(seller, customer);
                } catch (Exception e) {
                    Toast.makeText(this, "Valid Rate Required", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void generateInvoice(String seller, String customer) {
        // Date and Time Formatting
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        Date now = new Date();
        
        invDate.setText("Date : " + sdfDate.format(now));
        invTime.setText("Time : " + sdfTime.format(now));
        invSeller.setText(seller);
        invCustomer.setText(customer);

        invTableRows.removeAllViews();
        double sumVol = 0;
        int sumQty = 0;

        for (WoodItem item : woodList) {
            View row = getLayoutInflater().inflate(R.layout.row_invoice_item, invTableRows, false);
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(item.length + " ft");
            ((TextView) row.findViewById(R.id.colGirth)).setText(item.girth + " in");
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.2f", item.volume));
            invTableRows.addView(row);
            
            sumVol += item.volume;
            sumQty += item.qty;
        }

        invTotalVol.setText(String.format(Locale.US, "%.2f", sumVol));
        invTotalQty.setText(String.valueOf(sumQty));
        invRate.setText(String.valueOf(currentRate));
        invTotalAmount.setText(String.format(Locale.US, "%.0f ৳", sumVol * currentRate));

        // Flip screen to invoice
        viewFlipper.setDisplayedChild(1);
    }
}
