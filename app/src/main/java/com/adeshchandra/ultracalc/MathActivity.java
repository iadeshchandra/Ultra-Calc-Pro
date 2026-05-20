package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Arrays;
import java.util.Locale;

public class MathActivity extends AppCompatActivity {
    private LinearLayout mainContainer;
    private static final String[] TOOLS = {"Average","GCF/LCM","Quadratic","Prime","Factorial","Ratio"};
    private static final String[] EMOJIS = {"📊","🔢","📉","🔍","❗","⚖️"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_math);
            mainContainer = findViewById(R.id.mainContainer);
            findViewById(R.id.btnBack).setOnClickListener(v -> finish());
            buildToolBar();
            showTool(0);
        } catch (Exception e) {
            Toast.makeText(this, "Error: Missing activity_math.xml", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void buildToolBar() {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i=0; i<TOOLS.length; i++){
            final int idx=i;
            LinearLayout item=new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(20,14,20,14);
            TextView em=new TextView(this);em.setText(EMOJIS[i]);em.setTextSize(22);em.setGravity(android.view.Gravity.CENTER);
            TextView lb=new TextView(this);lb.setText(TOOLS[i]);lb.setTextSize(10);lb.setTextColor(0xFFB0AECC);lb.setGravity(android.view.Gravity.CENTER);
            item.addView(em);item.addView(lb);item.setOnClickListener(v->showTool(idx));bar.addView(item);
        }
    }

    private void showTool(int idx) {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i=0;i<bar.getChildCount();i++) bar.getChildAt(i).setBackgroundColor(i==idx?0xFF252548:0x00000000);
        mainContainer.removeAllViews();
        switch(idx){
            case 0:buildAvg();break; case 1:buildGCF();break;
            case 2:buildQuad();break; case 3:buildPrime();break;
            case 4:buildFact();break; case 5:buildRatio();break;
        }
    }

    private void showError(String fileName) {
        TextView err = new TextView(this);
        err.setText("⚠️ Layout Error\nMissing or broken file: " + fileName);
        err.setTextColor(0xFFFF1744);
        err.setTextSize(16);
        err.setPadding(40, 40, 40, 40);
        mainContainer.addView(err);
    }

    private void buildAvg() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_average, mainContainer, false); mainContainer.addView(v);
            EditText etNums=v.findViewById(R.id.etNums);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    String[] parts=etNums.getText().toString().split("[,\\s]+");
                    double sum=0; double min=Double.MAX_VALUE; double max=Double.MIN_VALUE;
                    for(String p:parts){double n=Double.parseDouble(p.trim());sum+=n;min=Math.min(min,n);max=Math.max(max,n);}
                    tvResult.setText(String.format(Locale.US,"Average: %.4f\nSum: %.4f",sum/parts.length,sum));
                } catch(Exception e){tvResult.setText("Enter valid numbers");}
            });
        } catch (Exception e) { showError("panel_average.xml"); }
    }

    private void buildGCF() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_gcf, mainContainer, false); mainContainer.addView(v);
            EditText etA=v.findViewById(R.id.etA), etB=v.findViewById(R.id.etB);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    long a=Long.parseLong(etA.getText().toString()), b2=Long.parseLong(etB.getText().toString());
                    long gcf=gcd(a,b2); long lcm=a/gcf*b2;
                    tvResult.setText(String.format(Locale.US,"GCF: %d\nLCM: %d",gcf,lcm));
                } catch(Exception e){tvResult.setText("Enter valid integers");}
            });
        } catch (Exception e) { showError("panel_gcf.xml"); }
    }

    private void buildQuad() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_quad, mainContainer, false); mainContainer.addView(v);
            EditText etA=v.findViewById(R.id.etA), etB=v.findViewById(R.id.etB), etC=v.findViewById(R.id.etC);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    double a=Double.parseDouble(etA.getText().toString()), bv=Double.parseDouble(etB.getText().toString()), c=Double.parseDouble(etC.getText().toString());
                    double disc=bv*bv-4*a*c;
                    if(disc>0){
                        tvResult.setText(String.format(Locale.US,"Roots: %.4f, %.4f",(-bv+Math.sqrt(disc))/(2*a),(-bv-Math.sqrt(disc))/(2*a)));
                    } else if(disc==0) {
                        tvResult.setText(String.format(Locale.US,"Root: %.4f",-bv/(2*a)));
                    } else {
                        tvResult.setText("Complex Roots");
                    }
                } catch(Exception e){tvResult.setText("Enter valid coefficients");}
            });
        } catch (Exception e) { showError("panel_quad.xml"); }
    }

    private void buildPrime() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_prime, mainContainer, false); mainContainer.addView(v);
            EditText etNum=v.findViewById(R.id.etNum);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    long n=Long.parseLong(etNum.getText().toString());
                    boolean p=n>1; for(long i=2;i*i<=n;i++)if(n%i==0)p=false;
                    tvResult.setText(String.format(Locale.US,"%d is %s",n,p?"PRIME ✅":"NOT prime ❌"));
                } catch(Exception e){tvResult.setText("Enter a valid number");}
            });
        } catch (Exception e) { showError("panel_prime.xml"); }
    }

    private void buildFact() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_factorial, mainContainer, false); mainContainer.addView(v);
            EditText etNum=v.findViewById(R.id.etNum);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    int n=Integer.parseInt(etNum.getText().toString());
                    if(n<0){tvResult.setText("Invalid");return;}
                    double r=1;for(int i=2;i<=n;i++)r*=i;
                    tvResult.setText(String.format(Locale.US,"Result: %.4e",r));
                } catch(Exception e){tvResult.setText("Enter an integer");}
            });
        } catch (Exception e) { showError("panel_factorial.xml"); }
    }

    private void buildRatio() {
        try {
            View v = getLayoutInflater().inflate(R.layout.panel_ratio, mainContainer, false); mainContainer.addView(v);
            EditText etA=v.findViewById(R.id.etA), etB=v.findViewById(R.id.etB), etC=v.findViewById(R.id.etC);
            TextView tvResult=v.findViewById(R.id.tvResult);
            v.findViewById(R.id.btnCalc).setOnClickListener(b->{
                try {
                    double a=Double.parseDouble(etA.getText().toString()), bv=Double.parseDouble(etB.getText().toString()), c=Double.parseDouble(etC.getText().toString());
                    tvResult.setText(String.format(Locale.US,"Missing value D = %.4f",c*bv/a));
                } catch(Exception e){tvResult.setText("Enter values A, B, C");}
            });
        } catch (Exception e) { showError("panel_ratio.xml"); }
    }

    private long gcd(long a, long b){return b==0?a:gcd(b,a%b);}
}
