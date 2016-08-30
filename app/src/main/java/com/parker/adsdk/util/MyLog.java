package com.parker.adsdk.util;

import android.os.Environment;
import android.os.Process;
import android.text.format.DateFormat;
import android.util.Log;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyLog {
    public static final String TAG = "com.admonet.sdkhelper";
    private static File logfile = new File(Environment.getExternalStorageDirectory() + "/shell.log");
    private static FileWriter logWriter;

    static {
        logfile.delete();
    }

    public MyLog() {
    }

    public static String commonTag() {
        return "(" + Process.myPid() + " " + formatTime(System.currentTimeMillis()) + ")v" + 0 + " ";
    }

    public static void logToFile(String msg) {
        try {
            if(!logfile.exists()) {
                logfile.createNewFile();
            }

            if(logWriter == null) {
                logWriter = new FileWriter(logfile, true);
            }

            String e = "[" + DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.getInstance()).toString() + "]";
            logWriter.append(e).append(msg).append("\n").flush();
        } catch (Exception var2) {
            logWriter = null;
        }

    }

    public static void w(String msg) {
        Log.w(TAG, commonTag() + msg);
    }

    public static void w(String format, Object... args) {
        w(String.format(format, args));
    }

    public static void e(String msg) {
        Log.e(TAG, commonTag() + msg);
    }

    public static void e(String format, Object... args) {
        e(String.format(format, args));
    }

    public static void e(Exception e, String msg) {
        Log.e(TAG, commonTag() + msg);
        e(e);
    }

    public static void e(Exception e, String format, Object... args) {
        e(e, String.format(format, args));
    }

    public static void e(Exception e) {
        try {
            StackTraceElement[] var4;
            int var3 = (var4 = e.getStackTrace()).length;

            for(int var2 = 0; var2 < var3; ++var2) {
                StackTraceElement s = var4[var2];
                Log.e(TAG, commonTag() + s);
            }
        } catch (Exception var5) {
            ;
        }

    }

    public static void i(String msg) {
        Log.i(TAG, commonTag() + msg);
    }

    public static void i(String format, Object... args) {
        i(String.format(format, args));
    }

    public static void d(String msg) {
        Log.d(TAG, commonTag() + msg);
    }

    public static void d(String format, Object... args) {
        d(String.format(format, args));
    }

    public static void v(String msg) {
        Log.v(TAG, commonTag() + msg);
    }

    public static void v(String format, Object... args) {
        v(String.format(format, args));
    }

    public static String formatTime(long time) {
        return (new SimpleDateFormat("MM月dd HH:mm:ss)", Locale.US)).format(new Date(time));
    }
}
