package com.parker.adsdk.util;

import android.os.Environment;

import com.parker.adsdk.RootHelper;

import java.io.File;

/**
 * Created by parker on 2016/8/9.
 */
public class LogUtil {
    private static File sLogFile;
    static {
        LogUtil.sLogFile = new File(Environment.getExternalStorageDirectory() + "/shell.log");
    }

    public static void append(String str) {
        if(RootHelper.DEBUG) {
            RootHelper.log.append(String.valueOf(str) + "\n");
        }
    }


}
