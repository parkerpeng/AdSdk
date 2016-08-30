package com.parker.adsdk.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;

import com.parker.adsdk.RootHelper;
import com.parker.adsdk.util.MyLog;

import java.util.Calendar;

/**
 * Created by thinkpad on 2016/8/9.completed
 */
public class WorkAlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            MyLog.i(intent.getAction());
        if(intent.getAction().equals("com.admonet.helper.timing.work.start")) {
                MyLog.i("定时执行开始-->当前时间:" + DateFormat.format("yyyy-MM-dd HH:mm:ss", Calendar.
                        getInstance()) + " ts=" + System.currentTimeMillis());
            new Work(context, intent.getLongExtra("extra_start_time", System.currentTimeMillis()), intent
                    .getLongExtra("extra_end_time", System.currentTimeMillis() + 120000), intent.getStringExtra(
                    "extra_trigger")).run();
        }
    }
}
