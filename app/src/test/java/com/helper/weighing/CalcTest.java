package com.helper.weighing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalcTest {
    private static final double EPS = 1e-9;

    @Test
    public void parse_menerimaKomaDanTitik() {
        assertEquals(1.5, Calc.parse("1,5"), EPS);
        assertEquals(2.25, Calc.parse("2.25"), EPS);
        assertEquals(-3.0, Calc.parse("-3"), EPS);
        assertEquals(0, Calc.parse(""), EPS);
        assertEquals(0, Calc.parse("."), EPS);
    }

    @Test
    public void batch_koreksiKadarAir() {
        double[] d = {300, 700, 1000, 180, 2};
        double[] t = Calc.batch(2, d, 5, 0);
        assertEquals(600, t[0], EPS);
        assertEquals(1470, t[1], EPS);   // 1400 * 1.05
        assertEquals(2000, t[2], EPS);
        assertEquals(290, t[3], EPS);    // 360 - 70
        assertEquals(4, t[4], EPS);
    }

    @Test
    public void batch_airTidakNegatif() {
        double[] d = {300, 700, 1000, 10, 0};
        double[] t = Calc.batch(1, d, 10, 10);
        assertEquals(0, t[3], EPS);
    }

    @Test
    public void status_toleransi() {
        assertEquals(Calc.OK, Calc.status(99, 100, 2));
        assertEquals(Calc.OK, Calc.status(102, 100, 2));
        assertEquals(Calc.LOW, Calc.status(97.9, 100, 2));
        assertEquals(Calc.HIGH, Calc.status(102.1, 100, 2));
    }

    @Test
    public void batchCount_dibulatkanKeAtas() {
        assertEquals(3, Calc.batchCount(10, 4));
        assertEquals(2, Calc.batchCount(8, 4));
        assertEquals(1, Calc.batchCount(2, 4));
        assertEquals(0, Calc.batchCount(0, 4));
    }
}
