package com.adeshchandra.ultracalc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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
        int sNo; 
        double length, param2, param3, volume; 
        int qty;
        String uLen, uP2, uP3; // Stores the unit text (e.g. "ft", "in")
        
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

        setupDashboardGrid();
        setupCalculatorPad();
    }

    private void setupDashboardGrid() {
        int[] cardIds = {R.id.cardRoundImp, R.id.cardSizeImp, R.id.cardDoorImp, R.id.cardRoundMet, R.id.cardSizeMet, R.id.cardDoorMet};
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
        
        // Setup Spinner Adapters based on mode
        boolean isImp = mode < 3;
        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, isImp ? impUnits : metUnits);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLen.setAdapter(adapt); spinP2.setAdapter(adapt); spinP3.setAdapter(adapt);

        if (mode == 0 || mode == 3) {
            tvCalcTitle.setText(isImp ? "Round Wood (Foot/Inch)" : "Round Wood (Meter/cm)");
            lblParam2.setText("Roundness");
            containerParam3.setVisibility(View.GONE);
            spinLen.setSelection(0); spinP2.setSelection(1); // Default: ft & in (or m & cm)
        } else if (mode == 1 || mode == 4) {
            tvCalcTitle.setText(isImp ? "Size Wood (Foot/Inch)" : "Size Wood (Meter/cm)");
            lblParam2.setText("Width"); lblParam3.setText("Thickness");
            containerParam3.setVisibility(View.VISIBLE);
            spinLen.setSelection(0); spinP2.setSelection(1); spinP3.setSelection(1);
        } else {
            tvCalcTitle.setText(isImp ? "Flush Door (Foot/Inch)" : "Flush Door (Meter/cm)");
            lblParam2.setText("Width");
            containerParam3.setVisibility(View.GONE);
            spinLen.setSelection(0); spinP2.setSelection(1);
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

    // Advanced Normalization Formula Engine
    private double calculateNormalizedVolume(double l, String uL, double p2, String uP2, double p3, String uP3, int mode) {
        double normL = l, normP2 = p2, normP3 = p3;
        
        // Imperial Standard is L in Feet, P2/P3 in Inches.
        if (mode < 3) {
            if (uL.equals("in")) normL = l / 12.0;
            if (uP2.equals("ft")) normP2 = p2 * 12.0;
            if (uP3.equals("ft")) normP3 = p3 * 12.0;
            
            if (mode == 0) return ((normP2 / 4.0) * (normP2 / 4.0) * normL) / 144.0;
            if (mode == 1) return (normL * normP2 * normP3) / 144.0;
            if (mode == 2) return (normL * normP2) / 12.0;
        } 
        // Metric Standard is L in Meters, P2/P3 in cm.
        else {
            if (uL.equals("cm")) normL = l / 100.0;
            if (uP2.equals("m")) normP2 = p2 * 100.0;
            if (uP3.equals("m")) normP3 = p3 * 100.0;
            
            if (mode == 3) return ((normP2 / 4.0) * (normP2 / 4.0) * normL) / 10000.0;
            if (mode == 4) return (normL * normP2 * normP3) / 10000.0;
            if (mode == 5) return (normL * normP2) / 100.0; 
        }
        return 0;
    }

    private void calculateAndAdd() {
        try {
            double l = Double.parseDouble(etLength.getText().toString());
            double p2 = Double.parseDouble(etParam2.getText().toString());
            double p3 = containerParam3.getVisibility() == View.VISIBLE ? Double.parseDouble(etParam3.getText().toString()) : 0;
            int q = etQty.getText().toString().isEmpty() ? 1 : Integer.parseInt(etQty.getText().toString());

            String uL = spinLen.getSelectedItem().toString();
            String uP2 = spinP2.getSelectedItem().toString();
            String uP3 = spinP3.getSelectedItem().toString();

            double vol = calculateNormalizedVolume(l, uL, p2, uP2, p3, uP3, currentMode);
            woodList.add(new WoodItem(woodList.size() + 1, l, uL, p2, uP2, p3, uP3, q, vol * q));
            
            etLength.setText(""); etParam2.setText(""); etParam3.setText(""); etQty.setText("1");
            etLength.requestFocus(); refreshTable();
        } catch (Exception e) { Toast.makeText(this, "Enter valid measurements", Toast.LENGTH_SHORT).show(); }
    }

    private void refreshTable() {
        tableRowsContainer.removeAllViews();
        double sumVol = 0; int sumQty = 0;

        for (int i = 0; i < woodList.size(); i++) {
            WoodItem item = woodList.get(i);
            item.sNo = i + 1; 
            View row = getLayoutInflater().inflate(R.layout.row_wood_master, tableRowsContainer, false);
            
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(item.length + " " + item.uLen);
            ((TextView) row.findViewById(R.id.colParam2)).setText(item.param2 + " " + item.uP2);
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.3f", item.volume));
            
            final int index = i;
            row.setOnLongClickListener(v -> { showEditDialog(item, index); return true; });
            
            tableRowsContainer.addView(row);
            sumVol += item.volume; sumQty += item.qty;
        }
        
        tvTotalLogs.setText("Total Wood : " + sumQty);
        String unit = (currentMode >= 3) ? "(Meter)³" : "(Foot)³";
        if (currentMode == 2 || currentMode == 5) unit = (currentMode == 5) ? "(Meter)²" : "(Foot)²";
        tvTotalVolume.setText(String.format(Locale.US, "Total %s : %.3f", unit, sumVol));
    }

    private void showEditDialog(WoodItem item, int index) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_wood, null);
        EditText etEditLen = dialogView.findViewById(R.id.etEditLength);
        EditText etEditP2 = dialogView.findViewById(R.id.etEditParam2);
        EditText etEditQty = dialogView.findViewById(R.id.etEditQty);
        Spinner eSpinL = dialogView.findViewById(R.id.editSpinLen), eSpinP2 = dialogView.findViewById(R.id.editSpinP2);

        dialogView.findViewById(R.id.lblEditParam2).setText(lblParam2.getText().toString());
        
        ArrayAdapter<String> adapt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currentMode < 3 ? impUnits : metUnits);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eSpinL.setAdapter(adapt); eSpinP2.setAdapter(adapt);

        etEditLen.setText(String.valueOf(item.length)); etEditP2.setText(String.valueOf(item.param2)); etEditQty.setText(String.valueOf(item.qty));
        eSpinL.setSelection(item.uLen.equals("ft") || item.uLen.equals("m") ? 0 : 1);
        eSpinP2.setSelection(item.uP2.equals("ft") || item.uP2.equals("m") ? 0 : 1);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        dialogView.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnDeleteRow).setOnClickListener(v -> { woodList.remove(index); refreshTable(); dialog.dismiss(); Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show(); });

        dialogView.findViewById(R.id.btnSaveEdit).setOnClickListener(v -> {
            try {
                item.length = Double.parseDouble(etEditLen.getText().toString());
                item.param2 = Double.parseDouble(etEditP2.getText().toString());
                item.qty = Integer.parseInt(etEditQty.getText().toString());
                item.uLen = eSpinL.getSelectedItem().toString();
                item.uP2 = eSpinP2.getSelectedItem().toString();
                
                double newVol = calculateNormalizedVolume(item.length, item.uLen, item.param2, item.uP2, item.param3, item.uP3, currentMode);
                item.volume = newVol * item.qty;
                refreshTable(); dialog.dismiss();
            } catch (Exception e) { Toast.makeText(this, "Invalid numbers", Toast.LENGTH_SHORT).show(); }
        });
        dialog.show();
    }

    private void showInvoiceDialog() {
        if (woodList.isEmpty()) { Toast.makeText(this, "Add logs first!", Toast.LENGTH_SHORT).show(); return; }
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_price, null);
        EditText etRate = dialogView.findViewById(R.id.etRate), etSellerName = dialogView.findViewById(R.id.etSellerName), etSellerPhone = dialogView.findViewById(R.id.etSellerPhone), etCustomerName = dialogView.findViewById(R.id.etCustomerName), etCustomerPhone = dialogView.findViewById(R.id.etCustomerPhone);

        new AlertDialog.Builder(this).setView(dialogView).setPositiveButton("Create Invoice", (dialog, which) -> {
                try {
                    currentRate = Double.parseDouble(etRate.getText().toString());
                    generateInvoice(etSellerName.getText().toString() + "\n" + etSellerPhone.getText().toString(), etCustomerName.getText().toString() + "\n" + etCustomerPhone.getText().toString());
                } catch (Exception e) { Toast.makeText(this, "Rate is required", Toast.LENGTH_SHORT).show(); }
            }).setNegativeButton("Cancel", null).show();
    }

    private void generateInvoice(String seller, String customer) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy", Locale.US), sdfTime = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        Date now = new Date();
        ((TextView) findViewById(R.id.invDate)).setText("Date : " + sdfDate.format(now));
        ((TextView) findViewById(R.id.invTime)).setText("Time : " + sdfTime.format(now));
        ((TextView) findViewById(R.id.invSeller)).setText(seller); ((TextView) findViewById(R.id.invCustomer)).setText(customer);

        LinearLayout invTableRows = findViewById(R.id.invTableRows); invTableRows.removeAllViews();
        double sumVol = 0; int sumQty = 0;

        for (WoodItem item : woodList) {
            View row = getLayoutInflater().inflate(R.layout.row_invoice_master, invTableRows, false);
            ((TextView) row.findViewById(R.id.colSno)).setText(String.valueOf(item.sNo));
            ((TextView) row.findViewById(R.id.colLength)).setText(item.length + " " + item.uLen);
            ((TextView) row.findViewById(R.id.colParam2)).setText(item.param2 + " " + item.uP2);
            ((TextView) row.findViewById(R.id.colQty)).setText(String.valueOf(item.qty));
            ((TextView) row.findViewById(R.id.colVol)).setText(String.format(Locale.US, "%.3f", item.volume));
            invTableRows.addView(row); sumVol += item.volume; sumQty += item.qty;
        }

        ((TextView) findViewById(R.id.invTotalVol)).setText(String.format(Locale.US, "%.3f", sumVol));
        ((TextView) findViewById(R.id.invTotalQty)).setText(String.valueOf(sumQty));
        ((TextView) findViewById(R.id.invRate)).setText(String.valueOf(currentRate));
        ((TextView) findViewById(R.id.invTotalAmount)).setText(String.format(Locale.US, "%.0f ৳", sumVol * currentRate));

        viewFlipper.setDisplayedChild(2);
    }

    private void exportAndSharePdf() {
        if (invoicePrintArea.getWidth() == 0 || invoicePrintArea.getHeight() == 0) return;
        Bitmap bitmap = Bitmap.createBitmap(invoicePrintArea.getWidth(), invoicePrintArea.getHeight(), Bitmap.Config.ARGB_8888);
        invoicePrintArea.draw(new Canvas(bitmap));
        PdfDocument pdf = new PdfDocument();
        PdfDocument.Page page = pdf.startPage(new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create());
        page.getCanvas().drawBitmap(bitmap, 0, 0, null); pdf.finishPage(page);
        File cachePath = new File(getCacheDir(), "invoices"); if (!cachePath.exists()) cachePath.mkdirs();
        File pdfFile = new File(cachePath, "Wood_Invoice_" + System.currentTimeMillis() + ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(pdfFile); pdf.writeTo(fos); pdf.close(); fos.close();
            Intent shareIntent = new Intent(Intent.ACTION_SEND); shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getPackageName() + ".provider", pdfFile));
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "Share Invoice via"));
        } catch (IOException e) { pdf.close(); Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show(); }
    }
}
