package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class ConverterActivity extends AppCompatActivity {
    private LinearLayout catBar;
    private TextView tvTitle, tvResult, tvFormula;
    private Spinner spFrom, spTo;
    private EditText etVal;
    private int selCat = 0;
    private HistoryStore history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_converter);
        history = new HistoryStore(getSharedPreferences("uc_prefs", MODE_PRIVATE));
        catBar    = findViewById(R.id.catBar);
        tvTitle   = findViewById(R.id.tvTitle);
        tvResult  = findViewById(R.id.tvResult);
        tvFormula = findViewById(R.id.tvFormula);
        spFrom    = findViewById(R.id.spFrom);
        spTo      = findViewById(R.id.spTo);
        etVal     = findViewById(R.id.etVal);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
        findViewById(R.id.btnSwap).setOnClickListener(v->swap());
        buildCatBar();
        selectCat(0);
        etVal.addTextChangedListener(new TextWatcher(){
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int b,int c){convert();}
            public void afterTextChanged(Editable s){}
        });
        AdapterView.OnItemSelectedListener l = new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> p,View v,int pos,long id){convert();}
            public void onNothingSelected(AdapterView<?> p){}
        };
        spFrom.setOnItemSelectedListener(l);
        spTo.setOnItemSelectedListener(l);
    }

    private void buildCatBar(){
        for(int i=0;i<UnitConverter.CATS.length;i++){
            final int idx=i;
            UnitConverter.Cat cat=UnitConverter.CATS[i];
            LinearLayout item=new LinearLayout(this);
            item.setOrientation(LinearLayout.VERTICAL);
            item.setGravity(android.view.Gravity.CENTER);
            item.setPadding(22,14,22,14);
            TextView em=new TextView(this);em.setText(cat.emoji);em.setTextSize(22);em.setGravity(android.view.Gravity.CENTER);
            TextView lb=new TextView(this);lb.setText(cat.name.length()>7?cat.name.substring(0,7):cat.name);
            lb.setTextSize(10);lb.setTextColor(0xFFB0AECC);lb.setGravity(android.view.Gravity.CENTER);lb.setMaxLines(1);
            item.addView(em);item.addView(lb);
            item.setOnClickListener(v->selectCat(idx));
            catBar.addView(item);
        }
    }

    private void selectCat(int idx){
        selCat=idx;
        UnitConverter.Cat cat=UnitConverter.CATS[idx];
        tvTitle.setText(cat.emoji+"  "+cat.name);
        for(int i=0;i<catBar.getChildCount();i++)
            catBar.getChildAt(i).setBackgroundColor(i==idx?0xFF252548:0x00000000);
        ArrayAdapter<String> adap=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,cat.units);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFrom.setAdapter(adap); spTo.setAdapter(adap);
        if(cat.units.length>1) spTo.setSelection(1);
        etVal.setText(""); tvResult.setText("—"); tvFormula.setText("");
    }

    private void swap(){
        int f=spFrom.getSelectedItemPosition(),t=spTo.getSelectedItemPosition();
        spFrom.setSelection(t); spTo.setSelection(f); convert();
    }

    private void convert(){
        String input=etVal.getText().toString().trim();
        if(input.isEmpty()){tvResult.setText("—");tvFormula.setText("");return;}
        try{
            double v=Double.parseDouble(input);
            int f=spFrom.getSelectedItemPosition(),t=spTo.getSelectedItemPosition();
            String r=UnitConverter.convert(selCat,v,f,t);
            UnitConverter.Cat cat=UnitConverter.CATS[selCat];
            tvResult.setText(r+" "+cat.units[t]);
            tvFormula.setText(input+" "+cat.units[f]+" = "+r+" "+cat.units[t]);
            history.add(input+" "+cat.units[f],r+" "+cat.units[t],"Converter");
        }catch(Exception e){tvResult.setText("Invalid");}
    }
}
