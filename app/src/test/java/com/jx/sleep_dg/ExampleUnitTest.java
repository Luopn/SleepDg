package com.jx.sleep_dg;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void exam() {
        int leftBreathFreq = 96;
        System.out.print(String.format(Locale.getDefault(), "%d次/分钟", (int) Math.ceil((double)leftBreathFreq / 5)));
    }
}