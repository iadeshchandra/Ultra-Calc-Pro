package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class FinancialActivity extends AppCompatActivity {
    
    private LinearLayout mainContainer;
    private static final String[] TOOLS = {"EMI Loan","GST/VAT","Discount","Tip Split","ROI","SIP","Profit/Loss","Percentage"};
    private static final String[] EMOJIS = {"🏦","🧾","🏷️","🍽️","📈","💹","💰","%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        
        mainContainer = findViewById(R.id.mainContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        
        buildToolBar();
        showTool(0); // Load EMI by default
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
            case 0: buildEMI(); break; 
            case 1: buildGST(); break;
            case 2: buildDiscount(); break; 
            case 3: buildTip(); break;
            case 4: buildROI(); break; 
            case 5: buildSIP(); break;
            case 6: buildProfit(); break; 
            case 7: buildPercentage(); break;
        }
    }

    // --- PROGRAMMATIC UI BUILDERS TO PREVENT CRASHES ---
    
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

    // --- FINANCIAL CALCULATOR LOGIC ---

    private void buildEMI() {
        EditText etAmount = createInput("Loan Amount");
        EditText etRate = createInput("Interest Rate (% per year)");
        EditText etTenure = createInput("Tenure (in months)");
        Button btnCalc = createButton("CALCULATE EMI");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double P = Double.parseDouble(etAmount.getText().toString());
                double r = Double.parseDouble(etRate.getText().toString()) / 100 / 12;
                int n = Integer.parseInt(etTenure.getText().toString());
                double emi = r == 0 ? P / n : P * r * Math.pow(1 + r, n) / (Math.pow(1 + r, n) - 1);
                double total = emi * n; 
                double interest = total - P;
                tvResult.setText(String.format(Locale.US, "Monthly EMI: %.2f\nTotal Payment: %.2f\nTotal Interest: %.2f", emi, total, interest));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildGST() {
        EditText etAmount = createInput("Original Amount");
        EditText etRate = createInput("GST Rate (%)");
        
        RadioGroup rgMode = new RadioGroup(this);
        rgMode.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton rb1 = new RadioButton(this); rb1.setText("Add GST (+)"); rb1.setTextColor(0xFFFFFFFF); rb1.setChecked(true);
        RadioButton rb2 = new RadioButton(this); rb2.setText("Remove GST (-)"); rb2.setTextColor(0xFFFFFFFF);
        rgMode.addView(rb1); rgMode.addView(rb2);
        mainContainer.addView(rgMode);

        Button btnCalc = createButton("CALCULATE GST");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double amount = Double.parseDouble(etAmount.getText().toString());
                double gst = Double.parseDouble(etRate.getText().toString());
                boolean exclusive = rb1.isChecked();
                double gstAmt, total, original;
                if(exclusive) { gstAmt = amount * gst / 100; total = amount + gstAmt; original = amount; }
                else { original = amount * 100 / (100 + gst); gstAmt = amount - original; total = amount; }
                tvResult.setText(String.format(Locale.US, "Net Amount: %.2f\nGST Amount: %.2f\nTotal Amount: %.2f", original, gstAmt, total));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildDiscount() {
        EditText etPrice = createInput("Original Price");
        EditText etDisc = createInput("Discount (%)");
        Button btnCalc = createButton("CALCULATE DISCOUNT");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double price = Double.parseDouble(etPrice.getText().toString());
                double disc = Double.parseDouble(etDisc.getText().toString());
                double discAmt = price * disc / 100; 
                double final_ = price - discAmt;
                tvResult.setText(String.format(Locale.US, "You Save: %.2f\nFinal Price: %.2f", discAmt, final_));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildTip() {
        EditText etBill = createInput("Total Bill");
        EditText etTip = createInput("Tip (%)");
        EditText etPeople = createInput("Number of People");
        Button btnCalc = createButton("CALCULATE TIP");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double bill = Double.parseDouble(etBill.getText().toString());
                double tip = Double.parseDouble(etTip.getText().toString());
                int people = Integer.parseInt(etPeople.getText().toString());
                double tipAmt = bill * tip / 100; 
                double total = bill + tipAmt;
                tvResult.setText(String.format(Locale.US, "Tip Amount: %.2f\nTotal Bill: %.2f\nPer Person: %.2f", tipAmt, total, total / people));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildROI() {
        EditText etInvest = createInput("Amount Invested");
        EditText etReturn = createInput("Amount Returned");
        EditText etYears = createInput("Investment Period (Years)");
        Button btnCalc = createButton("CALCULATE ROI");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double invest = Double.parseDouble(etInvest.getText().toString());
                double ret = Double.parseDouble(etReturn.getText().toString());
                double years = Double.parseDouble(etYears.getText().toString());
                double roi = (ret - invest) / invest * 100; 
                double annRoi = (Math.pow(ret / invest, 1.0 / years) - 1) * 100;
                tvResult.setText(String.format(Locale.US, "Total ROI: %.2f%%\nAnnualized ROI: %.2f%%", roi, annRoi));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildSIP() {
        EditText etMonthly = createInput("Monthly Investment");
        EditText etRate = createInput("Expected Return Rate (%)");
        EditText etYears = createInput("Time Period (Years)");
        Button btnCalc = createButton("CALCULATE SIP");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double P = Double.parseDouble(etMonthly.getText().toString());
                double r = Double.parseDouble(etRate.getText().toString()) / 100 / 12;
                int n = Integer.parseInt(etYears.getText().toString()) * 12;
                double fv = P * (Math.pow(1 + r, n) - 1) / r * (1 + r);
                double invested = P * n;
                double wealthGained = fv - invested;
                tvResult.setText(String.format(Locale.US, "Amount Invested: %.2f\nWealth Gained: %.2f\nTotal Value: %.2f", invested, wealthGained, fv));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildProfit() {
        EditText etCost = createInput("Cost Price");
        EditText etSell = createInput("Selling Price");
        Button btnCalc = createButton("CALCULATE PROFIT/LOSS");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double cost = Double.parseDouble(etCost.getText().toString());
                double sell = Double.parseDouble(etSell.getText().toString());
                double pl = sell - cost;
                double pct = (Math.abs(pl) / cost) * 100;
                tvResult.setText(String.format(Locale.US, "%s Amount: %.2f\n%s Percentage: %.2f%%", pl >= 0 ? "Profit" : "Loss", Math.abs(pl), pl >= 0 ? "Profit" : "Loss", pct));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }

    private void buildPercentage() {
        EditText etNum = createInput("What is __ %");
        EditText etOf = createInput("of __ ?");
        Button btnCalc = createButton("CALCULATE");
        TextView tvResult = createResultView();

        btnCalc.setOnClickListener(b -> {
            try {
                double pct = Double.parseDouble(etNum.getText().toString());
                double of = Double.parseDouble(etOf.getText().toString());
                tvResult.setText(String.format(Locale.US, "Result: %.2f", pct * of / 100));
            } catch(Exception e) { tvResult.setText("Enter valid values"); }
        });
    }
}
