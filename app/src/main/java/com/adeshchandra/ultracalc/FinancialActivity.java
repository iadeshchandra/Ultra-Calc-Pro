package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class FinancialActivity extends AppCompatActivity {
    private LinearLayout mainContainer;
    private HistoryStore history;
    private static final String[] TOOLS = {"EMI Loan","GST/VAT","Discount","Tip Split","ROI","SIP","Profit/Loss","Percentage"};
    private static final String[] EMOJIS = {"🏦","🧾","🏷️","🍽️","📈","💹","💰","%"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial);
        history = new HistoryStore(getSharedPreferences("uc_prefs", MODE_PRIVATE));
        mainContainer = findViewById(R.id.mainContainer);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        buildToolBar();
        showTool(0);
    }

    private void buildToolBar() {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i = 0; i < TOOLS.length; i++) {
            final int idx = i;
            LinearLayout item = new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(20, 14, 20, 14);
            TextView em = new TextView(this); em.setText(EMOJIS[i]); em.setTextSize(22); em.setGravity(android.view.Gravity.CENTER);
            TextView lb = new TextView(this); lb.setText(TOOLS[i]); lb.setTextSize(10); lb.setTextColor(0xFFB0AECC); lb.setGravity(android.view.Gravity.CENTER);
            item.addView(em); item.addView(lb);
            item.setOnClickListener(v -> showTool(idx));
            bar.addView(item);
        }
    }

    private void showTool(int idx) {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i = 0; i < bar.getChildCount(); i++)
            bar.getChildAt(i).setBackgroundColor(i == idx ? 0xFF252548 : 0x00000000);
        mainContainer.removeAllViews();
        switch (idx) {
            case 0: buildEMI(); break; case 1: buildGST(); break;
            case 2: buildDiscount(); break; case 3: buildTip(); break;
            case 4: buildROI(); break; case 5: buildSIP(); break;
            case 6: buildProfit(); break; case 7: buildPercentage(); break;
        }
    }

    private void buildEMI() {
        View v = getLayoutInflater().inflate(R.layout.panel_emi, mainContainer, false); mainContainer.addView(v);
        EditText etAmount=v.findViewById(R.id.etAmount), etRate=v.findViewById(R.id.etRate), etTenure=v.findViewById(R.id.etTenure);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double P=Double.parseDouble(etAmount.getText().toString());
                double r=Double.parseDouble(etRate.getText().toString())/100/12;
                int n=Integer.parseInt(etTenure.getText().toString());
                double emi=r==0?P/n:P*r*Math.pow(1+r,n)/(Math.pow(1+r,n)-1);
                double total=emi*n; double interest=total-P;
                tvResult.setText(String.format(Locale.US,"Monthly EMI: %.2f\nTotal: %.2f\nInterest: %.2f",emi,total,interest));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildGST() {
        View v = getLayoutInflater().inflate(R.layout.panel_gst, mainContainer, false); mainContainer.addView(v);
        EditText etAmount=v.findViewById(R.id.etAmount), etRate=v.findViewById(R.id.etRate);
        TextView tvResult=v.findViewById(R.id.tvResult);
        RadioGroup rgMode=v.findViewById(R.id.rgMode);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double amount=Double.parseDouble(etAmount.getText().toString());
                double gst=Double.parseDouble(etRate.getText().toString());
                boolean exclusive=((RadioButton)rgMode.getChildAt(0)).isChecked();
                double gstAmt,total,original;
                if(exclusive){gstAmt=amount*gst/100;total=amount+gstAmt;original=amount;}
                else{original=amount*100/(100+gst);gstAmt=amount-original;total=amount;}
                tvResult.setText(String.format(Locale.US,"Original: %.2f\nGST: %.2f\nTotal: %.2f",original,gstAmt,total));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildDiscount() {
        View v = getLayoutInflater().inflate(R.layout.panel_discount, mainContainer, false); mainContainer.addView(v);
        EditText etPrice=v.findViewById(R.id.etPrice), etDisc=v.findViewById(R.id.etDisc);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double price=Double.parseDouble(etPrice.getText().toString());
                double disc=Double.parseDouble(etDisc.getText().toString());
                double discAmt=price*disc/100; double final_=price-discAmt;
                tvResult.setText(String.format(Locale.US,"Discount: -%.2f\nFinal: %.2f",discAmt,final_));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildTip() {
        View v = getLayoutInflater().inflate(R.layout.panel_tip, mainContainer, false); mainContainer.addView(v);
        EditText etBill=v.findViewById(R.id.etBill), etTip=v.findViewById(R.id.etTip), etPeople=v.findViewById(R.id.etPeople);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double bill=Double.parseDouble(etBill.getText().toString());
                double tip=Double.parseDouble(etTip.getText().toString());
                int people=Integer.parseInt(etPeople.getText().toString());
                double tipAmt=bill*tip/100; double total=bill+tipAmt;
                tvResult.setText(String.format(Locale.US,"Tip: %.2f\nTotal: %.2f\nPer Person: %.2f",tipAmt,total,total/people));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildROI() {
        View v = getLayoutInflater().inflate(R.layout.panel_roi, mainContainer, false); mainContainer.addView(v);
        EditText etInvest=v.findViewById(R.id.etInvest), etReturn=v.findViewById(R.id.etReturn), etYears=v.findViewById(R.id.etYears);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double invest=Double.parseDouble(etInvest.getText().toString());
                double ret=Double.parseDouble(etReturn.getText().toString());
                double years=Double.parseDouble(etYears.getText().toString());
                double roi=(ret-invest)/invest*100; double annRoi=(Math.pow(ret/invest,1.0/years)-1)*100;
                tvResult.setText(String.format(Locale.US,"ROI: %.2f%%\nAnnual: %.2f%%",roi,annRoi));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildSIP() {
        View v = getLayoutInflater().inflate(R.layout.panel_sip, mainContainer, false); mainContainer.addView(v);
        EditText etMonthly=v.findViewById(R.id.etMonthly), etRate=v.findViewById(R.id.etRate), etYears=v.findViewById(R.id.etYears);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double P=Double.parseDouble(etMonthly.getText().toString());
                double r=Double.parseDouble(etRate.getText().toString())/100/12;
                int n=Integer.parseInt(etYears.getText().toString())*12;
                double fv=P*(Math.pow(1+r,n)-1)/r*(1+r);
                tvResult.setText(String.format(Locale.US,"Future Value: %.2f",fv));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildProfit() {
        View v = getLayoutInflater().inflate(R.layout.panel_profit, mainContainer, false); mainContainer.addView(v);
        EditText etCost=v.findViewById(R.id.etCost), etSell=v.findViewById(R.id.etSell);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double cost=Double.parseDouble(etCost.getText().toString());
                double sell=Double.parseDouble(etSell.getText().toString());
                double pl=sell-cost;
                tvResult.setText(String.format(Locale.US,"%s: %.2f",pl>=0?"PROFIT":"LOSS",Math.abs(pl)));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildPercentage() {
        View v = getLayoutInflater().inflate(R.layout.panel_percentage, mainContainer, false); mainContainer.addView(v);
        EditText etNum=v.findViewById(R.id.etNum), etOf=v.findViewById(R.id.etOf);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double pct=Double.parseDouble(etNum.getText().toString());
                double of=Double.parseDouble(etOf.getText().toString());
                tvResult.setText(String.format(Locale.US,"Result: %.4f",pct*of/100));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }
}
