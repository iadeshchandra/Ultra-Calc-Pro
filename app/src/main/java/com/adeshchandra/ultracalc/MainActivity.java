package com.adeshchandra.ultracalc;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int[] cards = {R.id.cardCalc, R.id.cardSci, R.id.cardConv, R.id.cardFin, R.id.cardHealth, R.id.cardMath, R.id.cardDate, R.id.cardHistory};
        Class<?>[] acts = {CalcActivity.class, ScientificActivity.class, ConverterActivity.class, FinancialActivity.class, HealthActivity.class, MathActivity.class, DateCalcActivity.class, HistoryActivity.class};
        for (int i = 0; i < cards.length; i++) {
            final Class<?> act = acts[i];
            findViewById(cards[i]).setOnClickListener(v -> startActivity(new Intent(this, act)));
        }
    }
}
