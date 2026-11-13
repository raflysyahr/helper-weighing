package com.helper.weighing;

import java.util.Locale;

/** Logika hitung murni (tanpa Android) agar mudah diuji. */
public final class Calc {
    public static final int LOW = -1;
    public static final int OK = 0;
    public static final int HIGH = 1;

    private static final Locale ID = new Locale("id", "ID");

    private Calc() {}

    /** Baca angka dari input; menerima koma maupun titik. Kosong/tidak valid = 0. */
    public static double parse(CharSequence s) {
        if (s == null) return 0;
        String t = s.toString().trim().replace(',', '.');
        if (t.isEmpty() || t.equals(".") || t.equals("-") || t.equals("-.")) return 0;
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String fmt(double v) {
        return fmt(v, 1);
    }

    public static String fmt(double v, int decimals) {
        double r = Math.round(v * Math.pow(10, decimals));
        if (r == 0) v = 0;
        return String.format(ID, "%,." + decimals + "f", v);
    }

    /**
     * Target timbang per bahan: [semen, pasir, batu, air, aditif].
     *
     * @param d  desain per m3 dengan urutan yang sama
     * @param sm kadar air bebas pasir (%), negatif jika kurang dari SSD
     * @param tm kadar air bebas batu (%)
     */
    public static double[] batch(double vol, double[] d, double sm, double tm) {
        double sandDry = d[1] * vol;
        double stoneDry = d[2] * vol;
        double[] t = new double[5];
        t[0] = d[0] * vol;
        t[1] = sandDry * (1 + sm / 100.0);
        t[2] = stoneDry * (1 + tm / 100.0);
        t[3] = Math.max(0, d[3] * vol - waterAdjust(vol, d, sm, tm));
        t[4] = d[4] * vol;
        return t;
    }

    /** Jumlah air (kg) yang dikurangi dari air desain akibat kadar air agregat. */
    public static double waterAdjust(double vol, double[] d, double sm, double tm) {
        return d[1] * vol * sm / 100.0 + d[2] * vol * tm / 100.0;
    }

    public static double lower(double target, double tolPct) {
        return target * (1 - tolPct / 100.0);
    }

    public static double upper(double target, double tolPct) {
        return target * (1 + tolPct / 100.0);
    }

    public static int status(double net, double target, double tolPct) {
        double eps = 1e-9;
        if (net < lower(target, tolPct) - eps) return LOW;
        if (net > upper(target, tolPct) + eps) return HIGH;
        return OK;
    }

    /** Jumlah batch minimum agar total pesanan muat di kapasitas mixer. */
    public static int batchCount(double total, double capacity) {
        if (total <= 0 || capacity <= 0) return 0;
        return Math.max(1, (int) Math.ceil(total / capacity - 1e-9));
    }
}
