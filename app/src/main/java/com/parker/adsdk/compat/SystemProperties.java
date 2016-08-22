package com.parker.adsdk.compat;

import java.lang.reflect.Method;

/**
 * Created by parker on 2016/8/9.completed
 */
public class SystemProperties {
    private static Class Class;
    private static Method get1;
    private static Method get2;
    private static Method getInt;
    private static Method getLong;
    private static Method getBoolean;
    private static Method set;

    static {
        try {
            SystemProperties.init();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void init() throws ClassNotFoundException, NoSuchMethodException {
        SystemProperties.Class = Class.forName("android.os.SystemProperties");
        SystemProperties.get1 = SystemProperties.Class.getDeclaredMethod("get", String.class);
        SystemProperties.get1.setAccessible(true);
        SystemProperties.get2 = SystemProperties.Class.getDeclaredMethod("get", String.class, String.class);
        SystemProperties.get2.setAccessible(true);
        SystemProperties.getInt = SystemProperties.Class.getDeclaredMethod("getInt", String.class, Integer.TYPE);
        SystemProperties.getInt.setAccessible(true);
        SystemProperties.getLong = SystemProperties.Class.getDeclaredMethod("getLong", String.class, Long.TYPE);
        SystemProperties.getLong.setAccessible(true);
        SystemProperties.getBoolean = SystemProperties.Class.getDeclaredMethod("getBoolean", String.class, Boolean
                .TYPE);
        SystemProperties.getBoolean.setAccessible(true);
        SystemProperties.set = SystemProperties.Class.getDeclaredMethod("set", String.class, String.class);
        SystemProperties.set.setAccessible(true);
    }

    public static String getProp(String key) {
        Object result = null;
        try {
            result = SystemProperties.get1.invoke(null, key);
        }
        catch(Exception e) {
        }
        return ((String)result);
    }
}
