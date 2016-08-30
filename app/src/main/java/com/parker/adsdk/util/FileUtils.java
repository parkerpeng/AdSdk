package com.parker.adsdk.util;

import java.io.File;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * Created by thinkpad on 2016/8/22.completed
 */
public class FileUtils {
    private static Method metSetPermissions;

    static {
        try {
            Class clsFileUtils = Class.forName("android.os.FileUtils");
            metSetPermissions = clsFileUtils.getMethod("setPermissions", new Class[]{String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE});
            metSetPermissions.setAccessible(true);
        } catch (Exception var1) {
            ;
        }

    }

    public FileUtils() {
    }

    public static int setPermissions(File path, int mode) {
        try {
            return ((Integer)metSetPermissions.invoke((Object)null, new Object[]{path.getAbsolutePath(), Integer.valueOf(mode), Integer.valueOf(-1), Integer.valueOf(-1)})).intValue();
        } catch (Exception var3) {
            return -1;
        }
    }

    public static int setPermissions(File path, int mode, int uid, int gid) {
        try {
            return ((Integer)metSetPermissions.invoke((Object)null, new Object[]{path.getAbsolutePath(), Integer.valueOf(mode), Integer.valueOf(uid), Integer.valueOf(gid)})).intValue();
        } catch (Exception var5) {
            return -1;
        }
    }

    public static int setPermissions(String path, int mode) {
        try {
            return ((Integer)metSetPermissions.invoke((Object)null, new Object[]{path, Integer.valueOf(mode), Integer.valueOf(-1), Integer.valueOf(-1)})).intValue();
        } catch (Exception var3) {
            return -1;
        }
    }

    public static int setPermissions(String path, int mode, int uid, int gid) {
        try {
            return ((Integer)metSetPermissions.invoke((Object)null, new Object[]{path, Integer.valueOf(mode), Integer.valueOf(uid), Integer.valueOf(gid)})).intValue();
        } catch (Exception var5) {
            return -1;
        }
    }
}

