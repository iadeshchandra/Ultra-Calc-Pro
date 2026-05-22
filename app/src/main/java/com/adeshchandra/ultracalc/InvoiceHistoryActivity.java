package com.adeshchandra.ultracalc;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InvoiceHistoryActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice_history);
        findViewById(R.id.btnBackInv).setOnClickListener(v -> finish());
        loadInvoices();
    }

    private void loadInvoices() {
        LinearLayout container = findViewById(R.id.invoiceListContainer);
        container.removeAllViews();
        File dir = new File(getCacheDir(), "invoices");
        
        if (!dir.exists() || dir.listFiles() == null || dir.listFiles().length == 0) {
            TextView empty = new TextView(this);
            empty.setText("No saved invoices found.");
            empty.setPadding(32, 32, 32, 32);
            empty.setTextSize(16);
            container.addView(empty);
            return;
        }

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".pdf")) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(32, 32, 32, 32);
                row.setBackgroundColor(0xFFFFFFFF);
                
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
                params.setMargins(0, 0, 0, 24);
                row.setLayoutParams(params);

                TextView name = new TextView(this);
                name.setText(file.getName().replace(".pdf", ""));
                name.setTextSize(16);
                name.setTextColor(0xFF0F172A);
                // CORRECTED: Android requires setTypeface to make text bold programmatically
                name.setTypeface(null, Typeface.BOLD); 
                
                TextView date = new TextView(this);
                date.setText("Saved: " + new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US).format(new Date(file.lastModified())));
                date.setTextSize(12);
                date.setTextColor(0xFF64748B);
                date.setPadding(0, 8, 0, 16);

                LinearLayout btnRow = new LinearLayout(this);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);

                Button btnOpen = new Button(this);
                btnOpen.setText("OPEN INVOICE");
                btnOpen.setBackgroundColor(0xFF00897B);
                btnOpen.setTextColor(0xFFFFFFFF);
                btnOpen.setOnClickListener(v -> openPdf(file));
                
                Button btnDel = new Button(this);
                btnDel.setText("DELETE");
                btnDel.setBackgroundColor(0xFFE53935);
                btnDel.setTextColor(0xFFFFFFFF);
                LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(-2, -2);
                btnParams.setMargins(16, 0, 0, 0);
                btnDel.setLayoutParams(btnParams);
                btnDel.setOnClickListener(v -> {
                    new AlertDialog.Builder(this)
                        .setTitle("Delete Invoice")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", (d,w) -> {
                            file.delete(); 
                            loadInvoices();
                        })
                        .setNegativeButton("No", null)
                        .show();
                });

                btnRow.addView(btnOpen); 
                btnRow.addView(btnDel);
                row.addView(name); 
                row.addView(date); 
                row.addView(btnRow);
                container.addView(row);
            }
        }
    }
    
    private void openPdf(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try { 
            startActivity(intent); 
        } catch (Exception e) { 
            Toast.makeText(this, "No PDF viewer installed!", Toast.LENGTH_SHORT).show(); 
        }
    }
}
