package com.jx.sleep_dg.utils;

import android.util.Log;

/**
 * LB
 * <p>
 * Log工具类
 */
public class LogUtil {

    public static final String ZBKCTAG = "Sleep";

    static final boolean ERROR = true;
    static final boolean INFO = true;
    static final boolean DEBUG = false;

    public static void i(String msg) {
        if (INFO) {
            i(ZBKCTAG, msg);
        }
    }

    public static void d(String msg) {
        if (DEBUG) {
            d(ZBKCTAG, msg);
        }
    }

    public static void e(String msg) {
        if (ERROR) {
            e(ZBKCTAG, msg);
        }
    }

    public static void i(String msg, Throwable th) {
        if (INFO) {
            i(ZBKCTAG, msg, th);
        }
    }

    public static void d(String msg, Throwable th) {
        if (DEBUG) {
            d(ZBKCTAG, msg, th);
        }
    }

    public static void e(String msg, Throwable th) {
        if (ERROR) {
            e(ZBKCTAG, msg, th);
        }
    }

    public static void i(String tag, String msg, Throwable th) {
        if (INFO) {
            Log.i(tag, msg, th);
        }
    }

    public static void e(String tag, String msg, Throwable th) {
        if (ERROR) {
            Log.e(tag, msg, th);
        }
    }

    public static void d(String tag, String msg, Throwable th) {
        if (DEBUG) {
            Log.d(tag, msg, th);
        }
    }

    public static void i(String tag, String msg) {
        if (INFO) {
            Log.i(tag, msg, null);
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR) {
            Log.e(tag, msg, null);
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            Log.d(tag, msg, null);
        }
    }
}
