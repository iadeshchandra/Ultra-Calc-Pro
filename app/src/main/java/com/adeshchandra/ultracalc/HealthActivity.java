package com.adeshchandra.ultracalc;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Locale;

public class HealthActivity extends AppCompatActivity {
    private LinearLayout mainContainer;
    private HistoryStore history;
    private static final String[] TOOLS = {"BMI","BMR","Calories","Body Fat","Ideal Weight","Water Intake"};
    private static final String[] EMOJIS = {"⚖️","🔥","🥗","📊","💪","💧"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
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
            item.setPadding(20,14,20,14);
            TextView em=new TextView(this);em.setText(EMOJIS[i]);em.setTextSize(22);em.setGravity(android.view.Gravity.CENTER);
            TextView lb=new TextView(this);lb.setText(TOOLS[i]);lb.setTextSize(10);lb.setTextColor(0xFFB0AECC);lb.setGravity(android.view.Gravity.CENTER);
            item.addView(em);item.addView(lb);
            item.setOnClickListener(v->showTool(idx));
            bar.addView(item);
        }
    }

    private void showTool(int idx) {
        LinearLayout bar = findViewById(R.id.toolBar);
        for (int i=0;i<bar.getChildCount();i++) bar.getChildAt(i).setBackgroundColor(i==idx?0xFF252548:0x00000000);
        mainContainer.removeAllViews();
        switch(idx){case 0:buildBMI();break;case 1:buildBMR();break;case 2:buildCal();break;case 3:buildBodyFat();break;case 4:buildIdeal();break;case 5:buildWater();break;}
    }

    private void buildBMI() {
        View v = getLayoutInflater().inflate(R.layout.panel_bmi, mainContainer, false); mainContainer.addView(v);
        EditText etWeight=v.findViewById(R.id.etWeight),etHeight=v.findViewById(R.id.etHeight);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double wt=Double.parseDouble(etWeight.getText().toString());
                double ht=Double.parseDouble(etHeight.getText().toString())/100.0;
                double bmi=wt/(ht*ht);
                String cat; int color;
                if(bmi<18.5){cat="Underweight";color=0xFFFF9100;}
                else if(bmi<25.0){cat="Normal ✅";color=0xFF00E676;}
                else if(bmi<30.0){cat="Overweight";color=0xFFFF9100;}
                else{cat="Obese";color=0xFFFF1744;}
                tvResult.setText(String.format(Locale.US,"BMI: %.1f\nCategory: %s",bmi,cat));
                tvResult.setTextColor(color);
                history.add(String.format(Locale.US,"BMI wt=%.1fkg ht=%.0fcm",wt,ht*100),String.format(Locale.US,"%.1f (%s)",bmi,cat),"Health");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildBMR() {
        View v = getLayoutInflater().inflate(R.layout.panel_bmr, mainContainer, false); mainContainer.addView(v);
        EditText etWeight=v.findViewById(R.id.etWeight),etHeight=v.findViewById(R.id.etHeight),etAge=v.findViewById(R.id.etAge);
        RadioGroup rgGender=v.findViewById(R.id.rgGender);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double wt=Double.parseDouble(etWeight.getText().toString());
                double ht=Double.parseDouble(etHeight.getText().toString());
                int age=Integer.parseInt(etAge.getText().toString());
                boolean male=((RadioButton)rgGender.getChildAt(0)).isChecked();
                double bmr=male?88.362+13.397*wt+4.799*ht-5.677*age:447.593+9.247*wt+3.098*ht-4.330*age;
                tvResult.setText(String.format(Locale.US,"BMR: %.0f cal/day\n\nWith Activity:\nSedentary: %.0f\nLight: %.0f\nModerate: %.0f\nActive: %.0f\nVery Active: %.0f",
                    bmr,bmr*1.2,bmr*1.375,bmr*1.55,bmr*1.725,bmr*1.9));
                history.add(String.format(Locale.US,"BMR wt=%.0f ht=%.0f age=%d",wt,ht,age),String.format(Locale.US,"%.0f cal/day",bmr),"Health");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildCal() {
        View v = getLayoutInflater().inflate(R.layout.panel_cal, mainContainer, false); mainContainer.addView(v);
        EditText etCarbs=v.findViewById(R.id.etCarbs),etProtein=v.findViewById(R.id.etProtein),etFat=v.findViewById(R.id.etFat);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double c=Double.parseDouble(etCarbs.getText().toString());
                double p=Double.parseDouble(etProtein.getText().toString());
                double f=Double.parseDouble(etFat.getText().toString());
                double total=c*4+p*4+f*9;
                tvResult.setText(String.format(Locale.US,"Carbs: %.0fg x 4 = %.0f cal\nProtein: %.0fg x 4 = %.0f cal\nFat: %.0fg x 9 = %.0f cal\n\nTotal: %.0f calories",c,c*4,p,p*4,f,f*9,total));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildBodyFat() {
        View v = getLayoutInflater().inflate(R.layout.panel_bodyfat, mainContainer, false); mainContainer.addView(v);
        EditText etWaist=v.findViewById(R.id.etWaist),etNeck=v.findViewById(R.id.etNeck),etHip=v.findViewById(R.id.etHip),etHeight=v.findViewById(R.id.etHeight);
        RadioGroup rgGender=v.findViewById(R.id.rgGender);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double waist=Double.parseDouble(etWaist.getText().toString());
                double neck=Double.parseDouble(etNeck.getText().toString());
                double height=Double.parseDouble(etHeight.getText().toString());
                boolean male=((RadioButton)rgGender.getChildAt(0)).isChecked();
                double bf;
                if(male){bf=495/(1.0324-0.19077*Math.log10(waist-neck)+0.15456*Math.log10(height))-450;}
                else{double hip=Double.parseDouble(etHip.getText().toString());bf=495/(1.29579-0.35004*Math.log10(waist+hip-neck)+0.22100*Math.log10(height))-450;}
                String cat=male?(bf<6?"Essential":bf<14?"Athletes":bf<18?"Fitness":bf<25?"Average":"Obese"):(bf<14?"Essential":bf<21?"Athletes":bf<25?"Fitness":bf<32?"Average":"Obese");
                tvResult.setText(String.format(Locale.US,"Body Fat: %.1f%%\nCategory: %s",bf,cat));
                history.add("Body Fat Calc",String.format(Locale.US,"%.1f%% (%s)",bf,cat),"Health");
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildIdeal() {
        View v = getLayoutInflater().inflate(R.layout.panel_ideal, mainContainer, false); mainContainer.addView(v);
        EditText etHeight=v.findViewById(R.id.etHeight);
        RadioGroup rgGender=v.findViewById(R.id.rgGender);
        TextView tvResult=v.findViewById(R.id.tvResult);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double ht=Double.parseDouble(etHeight.getText().toString());
                boolean male=((RadioButton)rgGender.getChildAt(0)).isChecked();
                double htIn=ht/2.54;
                double dev=male?50+2.3*(htIn-60):45.5+2.3*(htIn-60);
                double rob=male?52+1.9*(htIn-60):49+1.7*(htIn-60);
                double mil=male?56.2+1.41*(htIn-60):53.1+1.36*(htIn-60);
                double bmiIdeal=(male?22.0:21.0)*Math.pow(ht/100,2);
                tvResult.setText(String.format(Locale.US,"Ideal Weight for %.0fcm:\n\nDevine: %.1f kg\nRobinson: %.1f kg\nMiller: %.1f kg\nBMI-based: %.1f kg",ht,dev,rob,mil,bmiIdeal));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }

    private void buildWater() {
        View v = getLayoutInflater().inflate(R.layout.panel_water, mainContainer, false); mainContainer.addView(v);
        EditText etWeight=v.findViewById(R.id.etWeight);
        Spinner spActivity=v.findViewById(R.id.spActivity);
        TextView tvResult=v.findViewById(R.id.tvResult);
        String[] acts={"Sedentary","Lightly Active","Moderately Active","Very Active"};
        ArrayAdapter<String> adap=new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,acts);
        adap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spActivity.setAdapter(adap);
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                double wt=Double.parseDouble(etWeight.getText().toString());
                int act=spActivity.getSelectedItemPosition();
                double base=wt*0.033;
                double[] mults={1.0,1.1,1.2,1.4};
                double water=base*mults[act];
                tvResult.setText(String.format(Locale.US,"Daily Water Intake:\n%.2f Liters\n%.0f mL\n%.1f glasses (250mL each)",water,water*1000,water*1000/250));
            } catch(Exception e){tvResult.setText("Enter valid values");}
        });
    }
}
