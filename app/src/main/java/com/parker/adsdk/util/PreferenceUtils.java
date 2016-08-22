package com.parker.adsdk.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by thinkpad on 2016/8/10.completed
 */
public class PreferenceUtils {
    public static void putDoneTrue(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("done", true).apply();
    }
}
