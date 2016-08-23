package com.parker.adsdk.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by thinkpad on 2016/8/23.
 */
public class SystemInfo {
    public static boolean iswap(Context context) {
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI || activeNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return false;
            }
            String extraInfo = activeNetworkInfo.getExtraInfo();
            if (extraInfo == null) {
                return false;
            }
            return extraInfo.equalsIgnoreCase("cmwap") || extraInfo.equalsIgnoreCase("ctwap") || extraInfo.equalsIgnoreCase("3gwap") || extraInfo.equalsIgnoreCase("uniwap");
        } catch (Exception e) {
            return false;
        }
    }

}
