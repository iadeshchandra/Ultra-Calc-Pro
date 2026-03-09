package com.adeshchandra.ultracalc;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DateCalcActivity extends AppCompatActivity {
    private LinearLayout mainContainer;
    private static final String[] TOOLS = {"Age Calc","Date Diff","Add Days","Weekday","Days Left"};
    private static final String[] EMOJIS = {"🎂","📅","➕","📆","⏳"};
    private final SimpleDateFormat SDF = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
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
        switch(idx){case 0:buildAge();break;case 1:buildDiff();break;case 2:buildAdd();break;case 3:buildWeekday();break;case 4:buildDaysLeft();break;}
    }

    private void buildAge() {
        View v = getLayoutInflater().inflate(R.layout.panel_age, mainContainer, false); mainContainer.addView(v);
        TextView tvDOB=v.findViewById(R.id.tvDOB),tvResult=v.findViewById(R.id.tvResult);
        final Calendar[] dob={null};
        v.findViewById(R.id.btnPickDOB).setOnClickListener(b->{
            Calendar c=Calendar.getInstance();
            new DatePickerDialog(this,(dp,y,m,d2)->{dob[0]=Calendar.getInstance();dob[0].set(y,m,d2);tvDOB.setText(SDF.format(dob[0].getTime()));},c.get(Calendar.YEAR)-25,c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
        });
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            if(dob[0]==null){tvResult.setText("Pick date of birth");return;}
            Calendar now=Calendar.getInstance();
            int years=now.get(Calendar.YEAR)-dob[0].get(Calendar.YEAR);
            int months=now.get(Calendar.MONTH)-dob[0].get(Calendar.MONTH);
            int days=now.get(Calendar.DAY_OF_MONTH)-dob[0].get(Calendar.DAY_OF_MONTH);
            if(days<0){months--;days+=30;} if(months<0){years--;months+=12;}
            long totalDays=TimeUnit.MILLISECONDS.toDays(now.getTimeInMillis()-dob[0].getTimeInMillis());
            tvResult.setText(String.format(Locale.US,"Age: %d years, %d months, %d days\n\nTotal Days: %d\nTotal Weeks: %d\nTotal Months: %d",years,months,days,totalDays,totalDays/7,years*12+months));
        });
    }

    private void buildDiff() {
        View v = getLayoutInflater().inflate(R.layout.panel_diff, mainContainer, false); mainContainer.addView(v);
        TextView tvD1=v.findViewById(R.id.tvDate1),tvD2=v.findViewById(R.id.tvDate2),tvResult=v.findViewById(R.id.tvResult);
        final Calendar[] d1={null},d2={null};
        v.findViewById(R.id.btnPick1).setOnClickListener(b->{Calendar c=Calendar.getInstance();new DatePickerDialog(this,(dp,y,m,d)->{d1[0]=Calendar.getInstance();d1[0].set(y,m,d);tvD1.setText(SDF.format(d1[0].getTime()));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        v.findViewById(R.id.btnPick2).setOnClickListener(b->{Calendar c=Calendar.getInstance();new DatePickerDialog(this,(dp,y,m,d)->{d2[0]=Calendar.getInstance();d2[0].set(y,m,d);tvD2.setText(SDF.format(d2[0].getTime()));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            if(d1[0]==null||d2[0]==null){tvResult.setText("Pick both dates");return;}
            long diff=Math.abs(d2[0].getTimeInMillis()-d1[0].getTimeInMillis());
            long days=TimeUnit.MILLISECONDS.toDays(diff);
            tvResult.setText(String.format(Locale.US,"Difference:\n%d days\n%d weeks + %d days\n%.2f months\n%.2f years",days,days/7,days%7,days/30.44,days/365.25));
        });
    }

    private void buildAdd() {
        View v = getLayoutInflater().inflate(R.layout.panel_adddays, mainContainer, false); mainContainer.addView(v);
        TextView tvStart=v.findViewById(R.id.tvStart),tvResult=v.findViewById(R.id.tvResult);
        EditText etDays=v.findViewById(R.id.etDays);
        RadioGroup rgOp=v.findViewById(R.id.rgOp);
        final Calendar[] start={Calendar.getInstance()};
        tvStart.setText(SDF.format(start[0].getTime()));
        v.findViewById(R.id.btnPickStart).setOnClickListener(b->{Calendar c=start[0];new DatePickerDialog(this,(dp,y,m,d)->{start[0].set(y,m,d);tvStart.setText(SDF.format(start[0].getTime()));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            try {
                int days=Integer.parseInt(etDays.getText().toString());
                boolean add=((RadioButton)rgOp.getChildAt(0)).isChecked();
                Calendar result=(Calendar)start[0].clone();
                result.add(Calendar.DAY_OF_YEAR,add?days:-days);
                tvResult.setText(SDF.format(result.getTime())+" ("+result.getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.US)+")");
            } catch(Exception e){tvResult.setText("Enter valid days");}
        });
    }

    private void buildWeekday() {
        View v = getLayoutInflater().inflate(R.layout.panel_weekday, mainContainer, false); mainContainer.addView(v);
        TextView tvDate=v.findViewById(R.id.tvDate),tvResult=v.findViewById(R.id.tvResult);
        final Calendar[] sel={Calendar.getInstance()};
        tvDate.setText(SDF.format(sel[0].getTime()));
        v.findViewById(R.id.btnPick).setOnClickListener(b->{Calendar c=sel[0];new DatePickerDialog(this,(dp,y,m,d)->{sel[0].set(y,m,d);tvDate.setText(SDF.format(sel[0].getTime()));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            String day=sel[0].getDisplayName(Calendar.DAY_OF_WEEK,Calendar.LONG,Locale.US);
            int week=sel[0].get(Calendar.WEEK_OF_YEAR);
            int dayOfYear=sel[0].get(Calendar.DAY_OF_YEAR);
            int year=sel[0].get(Calendar.YEAR);
            boolean leap=new GregorianCalendar().isLeapYear(year);
            tvResult.setText(String.format("Day: %s\nWeek of year: %d\nDay of year: %d\nLeap year: %s",day,week,dayOfYear,leap?"Yes":"No"));
        });
    }

    private void buildDaysLeft() {
        View v = getLayoutInflater().inflate(R.layout.panel_daysleft, mainContainer, false); mainContainer.addView(v);
        TextView tvTarget=v.findViewById(R.id.tvTarget),tvResult=v.findViewById(R.id.tvResult);
        EditText etLabel=v.findViewById(R.id.etLabel);
        final Calendar[] target={null};
        v.findViewById(R.id.btnPickTarget).setOnClickListener(b->{Calendar c=Calendar.getInstance();new DatePickerDialog(this,(dp,y,m,d)->{target[0]=Calendar.getInstance();target[0].set(y,m,d);tvTarget.setText(SDF.format(target[0].getTime()));},c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();});
        v.findViewById(R.id.btnCalc).setOnClickListener(b->{
            if(target[0]==null){tvResult.setText("Pick target date");return;}
            long now=Calendar.getInstance().getTimeInMillis();
            long diff=target[0].getTimeInMillis()-now;
            String label=etLabel.getText().toString().trim();
            if(diff<0){tvResult.setText(String.format(Locale.US,"%s\nPassed %d days ago",label.isEmpty()?"Event":label,TimeUnit.MILLISECONDS.toDays(-diff)));return;}
            long days=TimeUnit.MILLISECONDS.toDays(diff);
            tvResult.setText(String.format(Locale.US,"%s\n%d days remaining\n%d weeks + %d days",label.isEmpty()?"Event":label,days,days/7,days%7));
        });
    }
}
