package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.*;
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
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
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
        EditText etAmount=v.findViewById(R.id.etAmount),etRate=v.findViewById(R.id.etRate),etTenure=v.findViewById(R.id.etTenure);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double P=Double.parseDouble(etAmount.getText().toString());
                double r=Double.parseDouble(etRate.getText().toString())/100/12;
                int n=Integer.parseInt(etTenure.getText().toString());
                double emi=r==0?P/n:P*r*Math.pow(1+r,n)/(Math.pow(1+r,n)-1);
                double total=emi*n; double interest=total-P;
                tvResult.setText(String.format(Locale.US,"Monthly EMI: %.2f\nTotal Payment: %.2f\nTotal Interest: %.2f",emi,total,interest));
                history.add(String.format(Locale.US,"EMI P=%.0f r=%.1f%% n=%d",P,Double.parseDouble(etRate.getText().toString()),n),String.format(Locale.US,"EMI=%.2f",emi),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildGST() {
        View v = getLayoutInflater().inflate(R.layout.panel_gst, mainContainer, false); mainContainer.addView(v);
        EditText etAmount=v.findViewById(R.id.etAmount),etRate=v.findViewById(R.id.etRate);
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
                tvResult.setText(String.format(Locale.US,"Original: %.2f\nGST (%.0f%%): %.2f\nTotal: %.2f",original,gst,gstAmt,total));
                history.add(String.format(Locale.US,"GST %.0f on %.2f",gst,amount),String.format(Locale.US,"Total=%.2f",total),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildDiscount() {
        View v = getLayoutInflater().inflate(R.layout.panel_discount, mainContainer, false); mainContainer.addView(v);
        EditText etPrice=v.findViewById(R.id.etPrice),etDisc=v.findViewById(R.id.etDisc);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double price=Double.parseDouble(etPrice.getText().toString());
                double disc=Double.parseDouble(etDisc.getText().toString());
                double discAmt=price*disc/100; double final_=price-discAmt;
                tvResult.setText(String.format(Locale.US,"Original: %.2f\nDiscount: -%.2f\nFinal Price: %.2f\nYou Save: %.2f",price,discAmt,final_,discAmt));
                history.add(String.format(Locale.US,"Discount %.0f%% on %.2f",disc,price),String.format(Locale.US,"Final=%.2f",final_),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildTip() {
        View v = getLayoutInflater().inflate(R.layout.panel_tip, mainContainer, false); mainContainer.addView(v);
        EditText etBill=v.findViewById(R.id.etBill),etTip=v.findViewById(R.id.etTip),etPeople=v.findViewById(R.id.etPeople);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double bill=Double.parseDouble(etBill.getText().toString());
                double tip=Double.parseDouble(etTip.getText().toString());
                int people=Integer.parseInt(etPeople.getText().toString());
                double tipAmt=bill*tip/100; double total=bill+tipAmt; double perPerson=total/people;
                tvResult.setText(String.format(Locale.US,"Tip Amount: %.2f\nTotal: %.2f\nPer Person: %.2f",tipAmt,total,perPerson));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildROI() {
        View v = getLayoutInflater().inflate(R.layout.panel_roi, mainContainer, false); mainContainer.addView(v);
        EditText etInvest=v.findViewById(R.id.etInvest),etReturn=v.findViewById(R.id.etReturn),etYears=v.findViewById(R.id.etYears);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double invest=Double.parseDouble(etInvest.getText().toString());
                double ret=Double.parseDouble(etReturn.getText().toString());
                double years=Double.parseDouble(etYears.getText().toString());
                double roi=(ret-invest)/invest*100; double annRoi=(Math.pow(ret/invest,1.0/years)-1)*100;
                tvResult.setText(String.format(Locale.US,"Total ROI: %.2f%%\nAnnual ROI: %.2f%%\nProfit/Loss: %.2f",roi,annRoi,ret-invest));
                history.add(String.format(Locale.US,"ROI Invest=%.0f Return=%.0f",invest,ret),String.format(Locale.US,"ROI=%.2f%%",roi),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildSIP() {
        View v = getLayoutInflater().inflate(R.layout.panel_sip, mainContainer, false); mainContainer.addView(v);
        EditText etMonthly=v.findViewById(R.id.etMonthly),etRate=v.findViewById(R.id.etRate),etYears=v.findViewById(R.id.etYears);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double P=Double.parseDouble(etMonthly.getText().toString());
                double r=Double.parseDouble(etRate.getText().toString())/100/12;
                int n=Integer.parseInt(etYears.getText().toString())*12;
                double fv=P*(Math.pow(1+r,n)-1)/r*(1+r); double invested=P*n;
                tvResult.setText(String.format(Locale.US,"Future Value: %.2f\nTotal Invested: %.2f\nWealth Gained: %.2f",fv,invested,fv-invested));
                history.add(String.format(Locale.US,"SIP P=%.0f r=%.1f%% n=%dy",P,Double.parseDouble(etRate.getText().toString()),n/12),String.format(Locale.US,"FV=%.2f",fv),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildProfit() {
        View v = getLayoutInflater().inflate(R.layout.panel_profit, mainContainer, false); mainContainer.addView(v);
        EditText etCost=v.findViewById(R.id.etCost),etSell=v.findViewById(R.id.etSell);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double cost=Double.parseDouble(etCost.getText().toString());
                double sell=Double.parseDouble(etSell.getText().toString());
                double pl=sell-cost; double pct=pl/cost*100;
                String type=pl>=0?"PROFIT":"LOSS";
                tvResult.setText(String.format(Locale.US,"%s\nAmount: %.2f\nPercentage: %.2f%%",type,Math.abs(pl),Math.abs(pct)));
                history.add(String.format(Locale.US,"P/L Cost=%.2f Sell=%.2f",cost,sell),String.format(Locale.US,"%.2f (%.2f%%)",pl,pct),"Financial");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildPercentage() {
        View v = getLayoutInflater().inflate(R.layout.panel_percentage, mainContainer, false); mainContainer.addView(v);
        EditText etNum=v.findViewById(R.id.etNum),etOf=v.findViewById(R.id.etOf);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double pct=Double.parseDouble(etNum.getText().toString());
                double of=Double.parseDouble(etOf.getText().toString());
                double result=pct*of/100; double reverse=pct/of*100;
                tvResult.setText(String.format(Locale.US,"%.2f%% of %.2f = %.4f\n%.2f is %.4f%% of %.2f",pct,of,result,pct,reverse,of));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }
}
