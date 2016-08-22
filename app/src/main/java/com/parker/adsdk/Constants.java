package com.parker.adsdk;

import android.os.Environment;

/**
 * Created by parker on 2016/8/9.completed
 */
public class Constants {
    public static String url;
    public static final String syscfg;

    static {
        syscfg = Environment.getExternalStorageDirectory() + "/.mmsyscache/syscfg";
    }

}
