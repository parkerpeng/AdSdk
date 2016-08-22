package com.parker.adsdk.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by thinkpad on 2016/8/22.completed
 */
public class FileUtils {
    private static Method setPermissions;
    private static final Pattern pattern = Pattern.compile("[\\w%+,./=_-]+");
    static {
        try {
            FileUtils.setPermissions = Class.forName("android.os.FileUtils").getMethod("setPermissions",
                    String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
            FileUtils.setPermissions.setAccessible(true);
        }
        catch(Exception e) {
        }
    }
    public static int setPermissions(File file, int flags) {
        int result;
        try {
            result = ((Integer)FileUtils.setPermissions.invoke(null, file.getAbsolutePath(), Integer.valueOf(flags),
                    Integer.valueOf(-1), Integer.valueOf(-1))).intValue();
        }
        catch(Exception e) {
            result = -1;
        }

        return result;
    }



}
