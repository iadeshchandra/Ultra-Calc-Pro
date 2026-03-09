package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ScientificActivity extends AppCompatActivity {
    private TextView tvDisplay, tvExpr;
    private StringBuilder expr = new StringBuilder();
    private boolean justResult = false;
    private HistoryStore history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scientific);
        history = new HistoryStore(getSharedPreferences("uc_prefs", MODE_PRIVATE));
        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpr    = findViewById(R.id.tvExpr);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
        setupButtons();
    }

    private void setupButtons() {
        int[] nums = {R.id.b0,R.id.b1,R.id.b2,R.id.b3,R.id.b4,R.id.b5,R.id.b6,R.id.b7,R.id.b8,R.id.b9};
        for (int i=0;i<nums.length;i++){final String n=String.valueOf(i);findViewById(nums[i]).setOnClickListener(v->app(n));}
        findViewById(R.id.bDot).setOnClickListener(v->app("."));
        findViewById(R.id.bPlus).setOnClickListener(v->appOp("+"));
        findViewById(R.id.bMinus).setOnClickListener(v->appOp("−"));
        findViewById(R.id.bMul).setOnClickListener(v->appOp("×"));
        findViewById(R.id.bDiv).setOnClickListener(v->appOp("÷"));
        findViewById(R.id.bPow).setOnClickListener(v->appOp("^"));
        findViewById(R.id.bPct).setOnClickListener(v->appOp("%"));
        findViewById(R.id.bOB).setOnClickListener(v->app("("));
        findViewById(R.id.bCB).setOnClickListener(v->app(")"));
        // Functions
        int[] fnIds = {R.id.fSin,R.id.fCos,R.id.fTan,R.id.fASin,R.id.fACos,R.id.fATan,
            R.id.fSinh,R.id.fCosh,R.id.fTanh,R.id.fSqrt,R.id.fCbrt,R.id.fLog,R.id.fLn,R.id.fLog2,
            R.id.fExp,R.id.fAbs,R.id.fFact,R.id.fFloor,R.id.fCeil};
        String[] fnStrs = {"sin(","cos(","tan(","asin(","acos(","atan(",
            "sinh(","cosh(","tanh(","sqrt(","cbrt(","log(","ln(","log2(",
            "exp(","abs(","fact(","floor(","ceil("};
        for (int i=0;i<fnIds.length;i++){final String fn=fnStrs[i];findViewById(fnIds[i]).setOnClickListener(v->appFn(fn));}
        // Constants & powers
        findViewById(R.id.fPi).setOnClickListener(v->app("π"));
        findViewById(R.id.fE).setOnClickListener(v->app("ℯ"));
        findViewById(R.id.fSq).setOnClickListener(v->appOp("^2"));
        findViewById(R.id.fCu).setOnClickListener(v->appOp("^3"));
        // Control
        findViewById(R.id.bEq).setOnClickListener(v->eq());
        findViewById(R.id.bC).setOnClickListener(v->clear());
        findViewById(R.id.bDel).setOnClickListener(v->del());
        findViewById(R.id.bPM).setOnClickListener(v->pm());
    }

    private void app(String s){if(justResult&&Character.isDigit(s.charAt(0))){expr.setLength(0);}justResult=false;expr.append(s);update();}
    private void appOp(String o){justResult=false;if(expr.length()>0){expr.append(o);update();}}
    private void appFn(String fn){justResult=false;expr.append(fn);update();}
    private void eq(){
        if(expr.length()==0)return;
        String e=expr.toString(),r=CalcEngine.evaluate(e);
        tvExpr.setText(e+" =");
        history.add(e,r,"Scientific");
        expr.setLength(0);expr.append(r);tvDisplay.setText(r);justResult=true;
    }
    private void clear(){expr.setLength(0);tvDisplay.setText("0");tvExpr.setText("");justResult=false;}
    private void del(){
        if(expr.length()==0)return;
        String s=expr.toString();
        String[] fns={"sinh(","cosh(","tanh(","asin(","acos(","atan(","sin(","cos(","tan(",
            "sqrt(","cbrt(","log2(","log(","ln(","exp(","abs(","fact(","floor(","ceil("};
        for(String fn:fns){if(s.endsWith(fn)){expr.delete(expr.length()-fn.length(),expr.length());update();return;}}
        expr.deleteCharAt(expr.length()-1);update();
    }
    private void pm(){if(expr.length()==0)return;String s=expr.toString();if(s.startsWith("−"))expr.delete(0,1);else expr.insert(0,"−");update();}
    private void update(){
        String s=expr.toString();tvDisplay.setText(s.isEmpty()?"0":s);
        if(s.length()>1){String p=CalcEngine.evaluate(s);tvExpr.setText(p.equals("Error")?"":("= "+p));}else tvExpr.setText("");
    }
}
