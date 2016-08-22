package com.parker.adsdk.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by thinkpad on 2016/8/9.
 */
public class SystemInfo {
    public static boolean a(Context context) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
                return false;
            }
            String extraInfo = networkInfo.getExtraInfo();
            if(extraInfo == null) {
                return false;
            }
            if(!extraInfo.equalsIgnoreCase("cmwap") && !extraInfo.equalsIgnoreCase("ctwap") && !extraInfo.equalsIgnoreCase(
                    "3gwap") && !extraInfo.equalsIgnoreCase("uniwap")) {
                return false;
            }

            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}
