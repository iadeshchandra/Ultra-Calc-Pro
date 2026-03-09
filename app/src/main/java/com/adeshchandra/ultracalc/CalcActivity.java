package com.adeshchandra.ultracalc;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class CalcActivity extends AppCompatActivity {
    private TextView tvDisplay, tvExpr;
    private StringBuilder expr = new StringBuilder();
    private boolean justResult = false;
    private HistoryStore history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);
        history = new HistoryStore(getSharedPreferences("uc_prefs", MODE_PRIVATE));
        tvDisplay = findViewById(R.id.tvDisplay);
        tvExpr    = findViewById(R.id.tvExpr);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        setupButtons();
    }

    private void setupButtons() {
        int[] nums = {R.id.b0,R.id.b1,R.id.b2,R.id.b3,R.id.b4,R.id.b5,R.id.b6,R.id.b7,R.id.b8,R.id.b9};
        for (int i = 0; i < nums.length; i++) { final String n = String.valueOf(i); findViewById(nums[i]).setOnClickListener(v->inp(n)); }
        findViewById(R.id.bDot).setOnClickListener(v->inp("."));
        findViewById(R.id.bDbl0).setOnClickListener(v->inp("00"));
        findViewById(R.id.bPlus).setOnClickListener(v->op("+"));
        findViewById(R.id.bMinus).setOnClickListener(v->op("−"));
        findViewById(R.id.bMul).setOnClickListener(v->op("×"));
        findViewById(R.id.bDiv).setOnClickListener(v->op("÷"));
        findViewById(R.id.bPct).setOnClickListener(v->op("%"));
        findViewById(R.id.bPow).setOnClickListener(v->op("^"));
        findViewById(R.id.bEq).setOnClickListener(v->eq());
        findViewById(R.id.bC).setOnClickListener(v->clear());
        findViewById(R.id.bDel).setOnClickListener(v->del());
        findViewById(R.id.bPM).setOnClickListener(v->pm());
        findViewById(R.id.bSqrt).setOnClickListener(v->fnCalc("sqrt"));
        findViewById(R.id.bSq).setOnClickListener(v->sq());
        // Long press C = clear all
        findViewById(R.id.bC).setOnLongClickListener(v->{clear();toast("Cleared");return true;});
    }

    private void inp(String s) {
        if (justResult && !s.equals(".")) { expr.setLength(0); justResult=false; }
        expr.append(s); update();
    }

    private void op(String o) {
        justResult=false;
        if (expr.length()>0) {
            char l = expr.charAt(expr.length()-1);
            if ("+-×÷%^".indexOf(l)>=0) expr.setCharAt(expr.length()-1,o.charAt(0));
            else expr.append(o);
        } else if (o.equals("−")) expr.append("−");
        update();
    }

    private void eq() {
        if (expr.length()==0) return;
        String e = expr.toString();
        String r = CalcEngine.evaluate(e);
        tvExpr.setText(e + " =");
        history.add(e, r, "Calculator");
        expr.setLength(0); expr.append(r);
        tvDisplay.setText(r); justResult=true;
    }

    private void clear() { expr.setLength(0); tvDisplay.setText("0"); tvExpr.setText(""); justResult=false; }

    private void del() {
        if (expr.length()==0) return;
        expr.deleteCharAt(expr.length()-1); update();
    }

    private void pm() {
        if (expr.length()==0) return;
        String s = expr.toString();
        if (s.startsWith("−")) expr.delete(0,1); else expr.insert(0,"−");
        update();
    }

    private void fnCalc(String fn) {
        if (expr.length()==0) return;
        String e = fn+"("+expr.toString()+")";
        String r = CalcEngine.evaluate(e);
        tvExpr.setText(e+" =");
        history.add(e, r, "Calculator");
        expr.setLength(0); expr.append(r);
        tvDisplay.setText(r); justResult=true;
    }

    private void sq() {
        if (expr.length()==0) return;
        String e = expr.toString()+"^2";
        String r = CalcEngine.evaluate(e);
        tvExpr.setText(e+" =");
        history.add(e, r, "Calculator");
        expr.setLength(0); expr.append(r);
        tvDisplay.setText(r); justResult=true;
    }

    private void update() {
        String s = expr.toString();
        tvDisplay.setText(s.isEmpty()?"0":s);
        if (s.length()>1) {
            String p = CalcEngine.evaluate(s);
            tvExpr.setText(p.equals("Error")?"":("= "+p));
        } else tvExpr.setText("");
    }

    private void toast(String msg) { Toast.makeText(this,msg,Toast.LENGTH_SHORT).show(); }
}
