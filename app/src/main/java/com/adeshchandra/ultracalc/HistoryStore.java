package com.adeshchandra.ultracalc;

import android.content.SharedPreferences;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class HistoryStore {
    public static class Entry {
        public String expression;
        public String result;
        public String category;
        public long timestamp;
        public Entry(String expression, String result, String category) {
            this.expression = expression;
            this.result = result;
            this.category = category;
            this.timestamp = System.currentTimeMillis();
        }
        public String getTime() {
            return new SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
                .format(new Date(timestamp));
        }
    }

    private static final String KEY = "uc_history";
    private static final int MAX = 200;
    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public HistoryStore(SharedPreferences prefs) { this.prefs = prefs; }

    public void add(String expr, String result, String category) {
        List<Entry> list = getAll();
        list.add(0, new Entry(expr, result, category));
        if (list.size() > MAX) list = list.subList(0, MAX);
        prefs.edit().putString(KEY, gson.toJson(list)).apply();
    }

    public List<Entry> getAll() {
        String json = prefs.getString(KEY, null);
        if (json == null) return new ArrayList<>();
        Type t = new TypeToken<List<Entry>>(){}.getType();
        try { List<Entry> l = gson.fromJson(json, t); return l != null ? l : new ArrayList<>(); }
        catch (Exception e) { return new ArrayList<>(); }
    }

    public void clear() { prefs.edit().remove(KEY).apply(); }
}
