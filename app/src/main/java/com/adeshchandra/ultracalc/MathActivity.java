package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class MathActivity extends AppCompatActivity {
    private LinearLayout mainContainer;
    private static final String[] TOOLS = {"Average","GCF/LCM","Quadratic","Prime","Factorial","Ratio"};
    private static final String[] EMOJIS = {"📊","🔢","📉","🔍","❗","⚖️"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);
        mainContainer = findViewById(R.id.mainContainer);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
        buildToolBar();
        showTool(0);
    }

    private void buildToolBar() {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i=0;i<TOOLS.length;i++){
            final int idx=i;
            LinearLayout item=new LinearLayout(this);item.setOrientation(LinearLayout.VERTICAL);item.setGravity(android.view.Gravity.CENTER);item.setPadding(20,14,20,14);
            TextView em=new TextView(this);em.setText(EMOJIS[i]);em.setTextSize(22);em.setGravity(android.view.Gravity.CENTER);
            TextView lb=new TextView(this);lb.setText(TOOLS[i]);lb.setTextSize(10);lb.setTextColor(0xFFB0AECC);lb.setGravity(android.view.Gravity.CENTER);
            item.addView(em);item.addView(lb);item.setOnClickListener(v->showTool(idx));bar.addView(item);
        }
    }

    private void showTool(int idx) {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i=0;i<bar.getChildCount();i++) bar.getChildAt(i).setBackgroundColor(i==idx?0xFF252548:0x00000000);
        mainContainer.removeAllViews();
        switch(idx){case 0:buildAvg();break;case 1:buildGCF();break;case 2:buildQuad();break;case 3:buildPrime();break;case 4:buildFact();break;case 5:buildRatio();break;}
    }

    private void buildAvg() {
        View v = getLayoutInflater().inflate(R.layout.panel_average, mainContainer, false);
        mainContainer.addView(v);
        EditText etNums=v.findViewById(R.id.etNums);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                String[] parts=etNums.getText().toString().split("[,\\s]+");
                double sum=0;double min=Double.MAX_VALUE;double max=Double.MIN_VALUE;
                for(String p:parts){double n=Double.parseDouble(p.trim());sum+=n;min=Math.min(min,n);max=Math.max(max,n);}
                double avg=sum/parts.length;
                // Median
                double[] sorted=new double[parts.length];
                for(int i=0;i<parts.length;i++) sorted[i]=Double.parseDouble(parts[i].trim());
                Arrays.sort(sorted);
                double median=sorted.length%2==0?(sorted[sorted.length/2-1]+sorted[sorted.length/2])/2:sorted[sorted.length/2];
                tvResult.setText(String.format(java.util.Locale.US,"Count: %d\nSum: %.4f\nAverage: %.4f\nMedian: %.4f\nMin: %.4f\nMax: %.4f\nRange: %.4f",parts.length,sum,avg,median,min,max,max-min));
            } catch(Exception e){tvResult.setText("Enter comma-separated numbers");}
        });
    }

    private void buildGCF() {
        View v = getLayoutInflater().inflate(R.layout.panel_gcf, mainContainer, false);
        mainContainer.addView(v);
        EditText etA=v.findViewById(R.id.etA),etB=v.findViewById(R.id.etB);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                long a=Long.parseLong(etA.getText().toString());
                long b2=Long.parseLong(etB.getText().toString());
                long gcf=gcd(a,b2);
                long lcm=a/gcf*b2;
                tvResult.setText(String.format("GCF (HCF): %d\nLCM: %d",gcf,lcm));
            } catch(Exception e){tvResult.setText("Enter valid integers");}
        });
    }

    private void buildQuad() {
        View v = getLayoutInflater().inflate(R.layout.panel_quad, mainContainer, false);
        mainContainer.addView(v);
        EditText etA=v.findViewById(R.id.etA),etB=v.findViewById(R.id.etB),etC=v.findViewById(R.id.etC);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double a=Double.parseDouble(etA.getText().toString());
                double bv=Double.parseDouble(etB.getText().toString());
                double c=Double.parseDouble(etC.getText().toString());
                double disc=bv*bv-4*a*c;
                String res = String.format(java.util.Locale.US,"Equation: %.2fx² + %.2fx + %.2f = 0\nDiscriminant: %.4f\n\n",a,bv,c,disc);
                if(disc>0){
                    double x1=(-bv+Math.sqrt(disc))/(2*a), x2=(-bv-Math.sqrt(disc))/(2*a);
                    res+=String.format(java.util.Locale.US,"Two Real Roots:\nx₁ = %.6f\nx₂ = %.6f",x1,x2);
                } else if(disc==0) {
                    res+=String.format(java.util.Locale.US,"One Real Root:\nx = %.6f",-bv/(2*a));
                } else {
                    double re=-bv/(2*a), im=Math.sqrt(-disc)/(2*a);
                    res+=String.format(java.util.Locale.US,"Complex Roots:\nx₁ = %.4f + %.4fi\nx₂ = %.4f - %.4fi",re,im,re,im);
                }
                tvResult.setText(res);
            } catch(Exception e){tvResult.setText("Enter valid coefficients");}
        });
    }

    private void buildPrime() {
        View v = getLayoutInflater().inflate(R.layout.panel_prime, mainContainer, false);
        mainContainer.addView(v);
        EditText etNum=v.findViewById(R.id.etNum);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                long n=Long.parseLong(etNum.getText().toString());
                boolean prime=isPrime(n);
                // Prime factors
                StringBuilder factors=new StringBuilder();
                long temp=n;
                for(long i=2;i*i<=temp;i++) while(temp%i==0){if(factors.length()>0)factors.append(" × ");factors.append(i);temp/=i;}
                if(temp>1){if(factors.length()>0)factors.append(" × ");factors.append(temp);}
                tvResult.setText(String.format("%d is %s\n\nPrime Factors:\n%s",n,prime?"a PRIME number ✅":"NOT prime ❌",factors.length()==0?String.valueOf(n):factors.toString()));
            } catch(Exception e){tvResult.setText("Enter a valid number");}
        });
    }

    private void buildFact() {
        View v = getLayoutInflater().inflate(R.layout.panel_factorial, mainContainer, false);
        mainContainer.addView(v);
        EditText etNum=v.findViewById(R.id.etNum);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                int n=Integer.parseInt(etNum.getText().toString());
                if(n<0){tvResult.setText("Enter non-negative integer");return;}
                if(n>20){tvResult.setText(n+"! is very large\n≈ "+String.format("%.4e",calcFactDouble(n)));return;}
                long f=1; StringBuilder sb=new StringBuilder();
                for(int i=1;i<=n;i++){f*=i;sb.append(i);if(i<n)sb.append(" × ");}
                tvResult.setText(n+"! = "+sb.toString()+"\n= "+f);
            } catch(Exception e){tvResult.setText("Enter a non-negative integer");}
        });
    }

    private void buildRatio() {
        View v = getLayoutInflater().inflate(R.layout.panel_ratio, mainContainer, false);
        mainContainer.addView(v);
        EditText etA=v.findViewById(R.id.etA),etB=v.findViewById(R.id.etB),etC=v.findViewById(R.id.etC);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                String as=etA.getText().toString(),bs=etB.getText().toString(),cs=etC.getText().toString();
                if(!as.isEmpty()&&!bs.isEmpty()&&!cs.isEmpty()){
                    double a=Double.parseDouble(as),bv=Double.parseDouble(bs),c=Double.parseDouble(cs);
                    double d=c*bv/a;
                    tvResult.setText(String.format(java.util.Locale.US,"%.4f : %.4f = %.4f : %.4f\nMissing value D = %.4f",a,bv,c,d,d));
                }
            } catch(Exception e){tvResult.setText("Enter values for A:B = C:D (leave D empty)");}
        });
    }

    private long gcd(long a, long b){return b==0?a:gcd(b,a%b);}
    private boolean isPrime(long n){if(n<2)return false;for(long i=2;i*i<=n;i++)if(n%i==0)return false;return true;}
    private double calcFactDouble(int n){double r=1;for(int i=2;i<=n;i++)r*=i;return r;}
}
