package com.parker.adsdk;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.format.DateFormat;

import com.parker.adsdk.entity.rule.Rule;
import com.parker.adsdk.network.RequestUtil;
import com.parker.adsdk.util.LogUtil;
import com.parker.adsdk.work.WorkAlarmBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by parker on 2016/8/9.
 */
public class RootHelper {
    public static boolean DEBUG;
    private Context context;
    public static StringBuilder log;

    static {
        RootHelper.DEBUG = true;
    }

    private void checkTid(){
        //RequestUtil.feedback(this.context, 51, null);
    }



    public void start(Context context) throws Exception {
        Constants.url = "http://" + new JSONObject(new BufferedReader(new InputStreamReader(context.
                getAssets().open("cfg"), "UTF-8")).readLine()).getString("a");
        new URL(Constants.url);
        this.context = context;
        RootHelper.log = new StringBuilder();
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
        LogUtil.append("执行时间:" + DateFormat.format("yyyy-MM-dd HH:mm:ss", rule.getGlobalRules().getStart()
                .getTime()) + " ts=" + rule.getGlobalRules().getStart().getTimeInMillis());
    }


}
