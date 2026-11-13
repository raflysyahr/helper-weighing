package com.helper.weighing;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/** Penyimpanan preset mix design (SharedPreferences, format JSON). */
final class Presets {
    private static final String PREF = "helper_weighing";
    private static final String KEY = "presets";

    private Presets() {}

    private static JSONObject load(Context c) {
        try {
            return new JSONObject(c.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY, "{}"));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    private static void store(Context c, JSONObject o) {
        c.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putString(KEY, o.toString()).apply();
    }

    static List<String> names(Context c) {
        List<String> out = new ArrayList<>();
        Iterator<String> it = load(c).keys();
        while (it.hasNext()) out.add(it.next());
        Collections.sort(out, String.CASE_INSENSITIVE_ORDER);
        return out;
    }

    static void put(Context c, String name, String[] values) {
        JSONObject o = load(c);
        JSONArray a = new JSONArray();
        for (String v : values) a.put(v);
        try {
            o.put(name, a);
        } catch (JSONException ignored) {
            return;
        }
        store(c, o);
    }

    static String[] get(Context c, String name) {
        JSONArray a = load(c).optJSONArray(name);
        if (a == null) return null;
        String[] r = new String[a.length()];
        for (int i = 0; i < r.length; i++) r[i] = a.optString(i, "");
        return r;
    }

    static void remove(Context c, String name) {
        JSONObject o = load(c);
        o.remove(name);
        store(c, o);
    }
}
