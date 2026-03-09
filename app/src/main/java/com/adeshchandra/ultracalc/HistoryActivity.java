package com.adeshchandra.ultracalc;

import android.content.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.*;

public class HistoryActivity extends AppCompatActivity {
    private LinearLayout histList;
    private TextView tvEmpty;
    private HistoryStore store;
    private String filterCat = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        store    = new HistoryStore(getSharedPreferences("uc_prefs", MODE_PRIVATE));
        histList = findViewById(R.id.histList);
        tvEmpty  = findViewById(R.id.tvEmpty);
        findViewById(R.id.btnBack).setOnClickListener(v->finish());
        findViewById(R.id.btnClear).setOnClickListener(v->{
            store.clear();
            renderHistory();
            Toast.makeText(this,"History cleared",Toast.LENGTH_SHORT).show();
        });
        buildFilterBar();
        renderHistory();
    }

    private void buildFilterBar() {
        LinearLayout bar = findViewById(R.id.filterBar);
        String[] cats = {"All","Calculator","Scientific","Converter","Financial","Health"};
        for (String cat : cats) {
            Button btn = new Button(this);
            btn.setText(cat);
            btn.setTextSize(11);
            btn.setTextColor(0xFFFFFFFF);
            btn.setBackgroundColor(0xFF1C1C38);
            btn.setPadding(24,12,24,12);
            btn.setOnClickListener(v -> {
                filterCat = cat;
                // highlight
                for (int i=0;i<bar.getChildCount();i++) bar.getChildAt(i).setBackgroundColor(0xFF1C1C38);
                btn.setBackgroundColor(0xFF7C6FFF);
                renderHistory();
            });
            bar.addView(btn);
        }
        ((Button)bar.getChildAt(0)).setBackgroundColor(0xFF7C6FFF);
    }

    private void renderHistory() {
        histList.removeAllViews();
        List<HistoryStore.Entry> all = store.getAll();
        List<HistoryStore.Entry> filtered = new ArrayList<>();
        for (HistoryStore.Entry e : all) {
            if (filterCat.equals("All") || e.category.equals(filterCat)) filtered.add(e);
        }
        if (filtered.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        for (HistoryStore.Entry e : filtered) {
            View item = getLayoutInflater().inflate(R.layout.item_history, histList, false);
            ((TextView)item.findViewById(R.id.tvExpr)).setText(e.expression);
            ((TextView)item.findViewById(R.id.tvResult)).setText("= " + e.result);
            ((TextView)item.findViewById(R.id.tvCat)).setText(e.category);
            ((TextView)item.findViewById(R.id.tvTime)).setText(e.getTime());
            item.findViewById(R.id.btnCopy).setOnClickListener(v -> {
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText("result", e.result));
                Toast.makeText(this,"Copied: "+e.result, Toast.LENGTH_SHORT).show();
            });
            histList.addView(item);
        }
    }
}
