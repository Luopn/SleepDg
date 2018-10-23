package com.jx.sleep_dg;

import org.junit.Test;

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
        byte[] b = new byte[]{(byte) 0xBB, (byte) 0x14, (byte) 0x31, (byte) 0x02, (byte) 0x64, (byte) 0x64,
                (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64,
                (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x64, (byte) 0x43};
        byte res = 0;
        for (int i = 1; i < b.length-2; i++) {
            res ^= b[i];
        }
        System.out.print(res & 0xff);
    }
}