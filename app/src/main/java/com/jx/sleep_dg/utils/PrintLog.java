package com.jx.sleep_dg.utils;

import android.util.Log;

public class PrintLog {

    private static final boolean PRINT = true;
    private static final String TAG = "CMD";

    public static void d(String string) {
        if (PRINT) {
            Log.d(TAG, string);
        }
    }

    public static void e(String string) {
        if (PRINT) {
            Log.e(TAG, string);
        }
    }

    public static void d(byte[] buf, int len) {
        if (PRINT) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String s = Integer.toHexString(buf[i] & 0xff);
                sb.append(s + " ");
            }
            Log.d(TAG, "buf: " + sb.toString());
        }
    }

    public static void e(byte[] buf, int len) {
        if (PRINT) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String s = Integer.toHexString(buf[i] & 0xff);
                sb.append(s + " ");
            }
            Log.e(TAG, "buf: " + sb.toString());
        }
    }

    public static void d(String str, byte[] buf, int len) {
        if (PRINT) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < len; i++) {
                String s = Integer.toHexString(buf[i] & 0xff);
                sb.append(s + " ");
            }
            Log.d("buf", str + ": " + sb.toString());
        }
    }

}
